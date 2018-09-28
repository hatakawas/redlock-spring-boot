package com.jd.spring.boot.autoconfigure.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lomagicode.redlock.spring.boot.autoconfigure.RedisLockService;


/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationTests.class)
@SpringBootConfiguration
public class RedisLockTests {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisLockService redisLockService;
    @Autowired
    private RedisService redisService;

    @Test
    public void testRedisLock() {
        taskService.doTask();
    }

    @Test
    public void testHoldScript() {
        redisService.executeHold("lockKey", "lockValue", 10000);
    }


}
