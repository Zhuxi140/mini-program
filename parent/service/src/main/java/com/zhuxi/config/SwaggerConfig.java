package com.zhuxi.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("你还不能休息哦,牢许")
                        .description("牢许，牢许，牢许！干活干活")
                        .version("v1.0")
                        .license(new License().name("zhuxiBlog").url("http://xizhu.site"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("更多")
                        .url("http://xizhu.site")
                );

    }
}
