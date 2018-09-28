package com.lomagicode.redlock.spring.boot.autoconfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * @return 锁键
     */
    String lockKey();

    /**
     * 单位由 timeUnit 指定
     *
     * @return 锁过期时间
     */
    long expire();

    /**
     * 默认时间单位为毫秒
     *
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
