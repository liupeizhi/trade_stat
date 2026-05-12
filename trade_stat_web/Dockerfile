FROM nginx:1.19.2-alpine
WORKDIR /usr/share/nginx/html
COPY dist/** .
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN echo "Asia/shanghai" > /etc/timezone
EXPOSE 80
