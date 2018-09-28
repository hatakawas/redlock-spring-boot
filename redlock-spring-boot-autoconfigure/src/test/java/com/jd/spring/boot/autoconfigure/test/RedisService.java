package com.jd.spring.boot.autoconfigure.test;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
public interface RedisService {
    void executeHold(String lockKey, String lockValue, long expireInMillis);

    void executeRelease(String lockKey, String lockValue);
}
