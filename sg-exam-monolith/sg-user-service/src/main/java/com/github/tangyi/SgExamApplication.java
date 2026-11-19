/*
 * Copyright 2024 The sg-exam authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 */

package com.github.tangyi;

import com.github.tangyi.config.PrometheusInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.TimeZone;

/**
 * SgExam 在线考试系统 - 单体版本启动类
 *
 * 合并自: sg-user-service + sg-exam-service + sg-api + sg-common + sg-generator + sg-job
 * 技术栈: Spring Boot 2.7.1 + JDK 17 + MyBatis + MySQL/H2 + Redis
 */
@EnableAsync
@EnableCaching
@SpringBootApplication
@MapperScan(basePackages = {"com.github.tangyi.*.mapper"})
public class SgExamApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        // 设置时区
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(SgExamApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PrometheusInterceptor()).addPathPatterns("/**");
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }
}
