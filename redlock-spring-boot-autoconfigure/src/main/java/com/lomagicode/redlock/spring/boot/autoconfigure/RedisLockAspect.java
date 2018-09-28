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
            LOGGER.warn("Failed to aquire redis lock.");
            return null;
        } else  {
            LOGGER.info("Aquired redis lock successfully.");
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
