FROM azul/zulu-openjdk-alpine:17-jre

# Install timezone data and curl for health check
RUN apk add --no-cache tzdata curl bash

# Set timezone
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create non-root user
RUN addgroup -g 1000 app && adduser -u 1000 -G app -s /bin/bash -D app

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/trade_stat_api-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown -R app:app /app

# Switch to non-root user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM settings
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:G1HeapRegionSize=4m -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}

# Run application - override datasource URL via command line
CMD sh -c "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -Dspring.datasource.url=jdbc:mysql://trade_stat_db:3306/trade_memo?serverTimezone=Asia/Shanghai\\&useUnicode=true\\&characterEncoding=utf8\\&useSSL=false\\&allowPublicKeyRetrieval=true\\&allowMultiQueries=true -jar /app/app.jar"
