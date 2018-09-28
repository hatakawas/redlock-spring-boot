package com.lomagicode.redlock.spring.boot.autoconfigure;

import java.util.concurrent.TimeUnit;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
public interface RedisLockService {

    boolean acquire(String lockKey, long expire, TimeUnit timeUnit);

    void hold(String lockKey, long expire, TimeUnit timeUnit);

    boolean release(String lockKey);

}
