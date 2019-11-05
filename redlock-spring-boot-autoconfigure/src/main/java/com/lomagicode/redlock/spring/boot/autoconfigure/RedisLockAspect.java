
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

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@Aspect
public class RedisLockAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockAspect.class);

    private final RedisLockService redisLockService;

    public RedisLockAspect(RedisLockService redisLockService) {
        this.redisLockService = redisLockService;
    }

    @Around("@annotation(com.lomagicode.redlock.spring.boot.autoconfigure.RedisLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.debug("++++++++++Before RedisLock annotated method around!!!++++++++++");

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String lockKey = redisLock.lockKey();
        TimeUnit timeUnit = redisLock.timeUnit();
        boolean lockAquired = redisLockService.acquire(redisLock.lockKey(), redisLock.expire(), timeUnit);
        if (!lockAquired) {
            LOGGER.warn("Failed to acquire redis lock.");
            return null;
        } else {
            LOGGER.info("Acquired redis lock successfully.");
        }
        redisLockService.hold(redisLock.lockKey(), redisLock.expire(), timeUnit);

        // invoke annotated method
        Object rawResult = pjp.proceed();

        boolean lockReleased = redisLockService.release(lockKey);
        if (!lockReleased) {
            LOGGER.warn("Failed to release redis lock!");
            throw new Throwable("Failed to release redis lock!!!");
        } else {
            LOGGER.info("Released redis lock successfully.");
        }

        LOGGER.debug("++++++++++After @RedisLock annotated method around!!!++++++++++");

        return rawResult;
    }
}
