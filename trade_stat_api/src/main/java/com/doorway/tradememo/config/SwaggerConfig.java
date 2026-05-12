package com.doorway.tradememo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Author: Chenly
 * Date: 2021-03-02 16:58
 * Description:
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static Logger log = LoggerFactory.getLogger(SwaggerConfig.class);
    @Bean
    public Docket createRestApi() {
        log.info("进入到swagger的配置中");
        return new Docket(DocumentationType.SWAGGER_2)
                // 指定构建api文档的详细信息的方法：apiInfo()
                .apiInfo(apiInfo())
                .select()
                // 指定要生成api接口的包路径，这里把controller作为包路径，生成controller中的所有接口
                .apis(RequestHandlerSelectors.basePackage("com.doorway.tradememo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建api文档的详细信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("TradeStat API 文档")
                // 设置接口描述
                .description("股票交易统计分析系统 RESTful API 接口文档")
                // 设置联系方式
                .contact(new Contact("TradeStat","",""))
                // 设置版本
                .version("1.0.0")
                // 构建
                .build();
    }


}