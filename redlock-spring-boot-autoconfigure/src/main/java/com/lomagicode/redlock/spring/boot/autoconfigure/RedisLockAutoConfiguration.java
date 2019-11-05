/*
 * Copyright (c) 2019 hatakawas (hatakawas@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lomagicode.redlock.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisLockAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisLockService redisLockService(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisLockServiceBean(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisLockService.class)
    public RedisLockAspect redisLockAspect(RedisLockService redisLockService) {
        return new RedisLockAspect(redisLockService);
    }
}
