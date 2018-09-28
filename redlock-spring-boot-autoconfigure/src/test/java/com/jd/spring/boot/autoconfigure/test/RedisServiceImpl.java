package com.jd.spring.boot.autoconfigure.test;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.lettuce.LettuceConverters;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@Service
public class RedisServiceImpl implements RedisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
//    @Qualifier(value = "defaultRedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;

    private DefaultRedisScript<Boolean> holdScript;
    private DefaultRedisScript<Long> releaseScript;

    @PostConstruct
    public void init() throws IOException {
        holdScript = new DefaultRedisScript<>();
        holdScript.setLocation(new ClassPathResource("lua/hold.lua"));
        holdScript.setResultType(Boolean.class);
        releaseScript = new DefaultRedisScript<>();
        releaseScript.setLocation(new ClassPathResource("lua/release.lua"));
        releaseScript.setResultType(Long.class);
    }


    @Override
    public void executeHold(String lockKey, String lockValue, long expireInMillis) {
        // CODE 1
//        Boolean result = redisTemplate.execute(holdScript, Collections.singletonList(lockKey), lockValue, expireInMillis);
        // END of CODE 1
        // CODE 2
        Boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.scriptingCommands()
                .eval(
                        LettuceConverters.toBytes(holdScript.getScriptAsString()),
                        ReturnType.BOOLEAN,
                        1,
                        LettuceConverters.toBytes(lockKey),
                        LettuceConverters.toBytes(lockValue),
                        LettuceConverters.toBytes(expireInMillis)
                ));
        // END of CODE 2
        System.out.println(holdScript.getSha1());
        LOGGER.debug("Execute holdScript, result={}, content=\n{}", result, holdScript.getScriptAsString());
    }

    @Override
    public void executeRelease(String lockKey, String lockValue) {
        Long result = redisTemplate.execute(releaseScript, Collections.singletonList(lockKey), lockValue);
        LOGGER.debug("Execute releaseScript, result={}, content=\n{}", result, releaseScript.getScriptAsString());
    }
}
