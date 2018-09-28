package com.lomagicode.redlock.spring.boot.autoconfigure;

import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.lettuce.LettuceConverters;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
public class RedisLockServiceBean implements RedisLockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockServiceBean.class);

    private ThreadLocal<String> value = new ThreadLocal<>();

    private RedisTemplate<Object, Object> redisTemplate;


    public RedisLockServiceBean(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private DefaultRedisScript<Boolean> holdScript;
    private DefaultRedisScript<Boolean> releaseScript;


    @PostConstruct
    public void init() {
        holdScript = new DefaultRedisScript<>();
        holdScript.setLocation(new ClassPathResource("lua/hold.lua"));
        holdScript.setResultType(Boolean.class);
        releaseScript = new DefaultRedisScript<>();
        releaseScript.setLocation(new ClassPathResource("lua/release.lua"));
        releaseScript.setResultType(Boolean.class);
    }

    @Override
    public boolean acquire(String lockKey, long expire, TimeUnit timeUnit) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            String uuid = UUID.randomUUID().toString();
            LOGGER.debug("Acquire. lockValue={}", uuid);
            value.set(uuid);
            return connection.stringCommands().set(lockKey.getBytes(), uuid.getBytes(), Expiration.from(expire, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT);
        });
    }

    @Override
    public void hold(String lockKey, long expire, TimeUnit timeUnit) {
        long expireInMillis;
        if (TimeUnit.MILLISECONDS == timeUnit) {
            expireInMillis = expire;
        } else {
            expireInMillis = timeUnit.toMillis(expire);
        }
        long delay = expireInMillis / 3;
        final String lockValue = value.get();
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(() -> {
            boolean result = refreshExpire(lockKey, lockValue, expireInMillis);
            if (!result) {
                LOGGER.debug("Failed to refresh expiration of lockKey, shutdown!");
                exec.shutdown();
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean release(String lockKey) {
        final String lockValue = value.get();
//        return redisTemplate.execute(releaseScript, Collections.singletonList(lockKey), lockValue);
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.scriptingCommands()
                .eval(
                        LettuceConverters.toBytes(releaseScript.getScriptAsString()),
                        ReturnType.BOOLEAN,
                        1,
                        LettuceConverters.toBytes(lockKey),
                        LettuceConverters.toBytes(lockValue)
                ));
    }

    private boolean refreshExpire(String lockKey, String lockValue, long expireInMillis) {
        LOGGER.debug("lockKey={}, lockValue={}, expireInMillis={}", lockKey, lockValue, expireInMillis);
//        Boolean result = redisTemplate.execute(holdScript, Collections.singletonList(lockKey), lockValue, expireInMillis);
        Boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.scriptingCommands()
                .eval(
                        LettuceConverters.toBytes(holdScript.getScriptAsString()),
                        ReturnType.BOOLEAN,
                        1,
                        LettuceConverters.toBytes(lockKey),
                        LettuceConverters.toBytes(lockValue),
                        LettuceConverters.toBytes(expireInMillis)
                ));

        if (LOGGER.isDebugEnabled()) {
            if (result) {
                LOGGER.debug("=========>>>>>>TTL refreshed!");
            } else {
                LOGGER.debug("=========>>>>>>Failed to refresh TTL!");
            }
        }

        return result;
    }
}
