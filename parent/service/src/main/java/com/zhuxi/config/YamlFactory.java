package com.zhuxi.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

public class YamlFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean yPFB = new YamlPropertiesFactoryBean();
        yPFB.setResources(resource.getResource());
        yPFB.afterPropertiesSet();
        Properties object = yPFB.getObject();

        return new PropertiesPropertySource(
                name != null ? name : resource.getResource().getFilename(),
                object
        );

    }
}
