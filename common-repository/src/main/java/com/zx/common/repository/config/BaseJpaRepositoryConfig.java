package com.zx.common.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author : zhaoxu
 * 重写requestMapping
 */
@Configuration
@EnableJpaRepositories(basePackages = {"**.repository"}, repositoryFactoryBeanClass = BaseJpaRepositoryFactoryBean.class)
@EntityScan(basePackages = {"**.entity"})
public class BaseJpaRepositoryConfig {

}
