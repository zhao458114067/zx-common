package com.zx.common.repository.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2023/11/5 16:31
 */
@Configuration
public class SwaggerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String active = environment.getProperty("spring.profiles.active");
        List<String> activeEnvironmentList = Arrays.asList("dev", "fat", "test");
        Map<String, String> enabled = new HashMap<>();
        enabled.put("spring.swagger.enabled", "false");
        if (activeEnvironmentList.contains(active)) {
            enabled.put("spring.swagger.enabled", "true");
        }
        OriginTrackedMapPropertySource originTrackedMapPropertySource = new OriginTrackedMapPropertySource("base_swagger.properties", enabled, true);
        environment.getPropertySources().addLast(originTrackedMapPropertySource);
    }
}