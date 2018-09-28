package com.jd.spring.boot.autoconfigure.test;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.lomagicode.redlock.spring.boot.autoconfigure.RedisLock;


/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@Component
public class TaskService {

    @RedisLock(lockKey = "lockKey", expire = 3, timeUnit = TimeUnit.SECONDS)
    public void doTask() {
        System.out.println("=========> Start...");
        sleep(5, TimeUnit.SECONDS);
        System.out.println("=========> Finish...");
    }

    private void sleep(long timeout, TimeUnit timeUnit) {
        try {
            System.out.println("................Just Doing IT.............");
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
