redlock-spring-boot
===================

[![Build Status](https://travis-ci.org/hatakawas/redlock-spring-boot-starter.svg?branch=master)](https://travis-ci.org/hatakawas/redlock-spring-boot)
[![codecov](https://codecov.io/gh/hatakawas/redlock-spring-boot-starter/branch/master/graph/badge.svg)](https://codecov.io/gh/hatakawas/redlock-spring-boot)

Distributed redis lock implemented and integrated with spring boot.

## Usage

### Maven dependency

To use redlock-spring-boot-starter, you import dependency into your POM.xml:
````xml
<dependency>
    <groupId>com.lomagicode.spring.boot</groupId>
    <artifactId>redlock-spring-boot-starter</artifactId>
    <version>${spring-boot-redlock.version}</version>
</dependency>
````

### Use in code

Redlock-spring-boot is very easy-to-use, it exposed a @RedisLock annotation which you can just add to your method that you want to be executed in a redis lock. For example:

```java
@Component
public class Example {
    @RedisLock(lockKey="your-lock-key", expire=10, timeUnit=TimeUnit.SECONDS)
    public void doTask() {
        try {
            // Do some task...
        } catch (Exception e) {
            // Handle exception.
        }
    } 
}
```

With above code, when you call `Example#doTask()`, it will try to aquire and hold the lock before really executing the method.
if it failed to aquire the lock, the method wouldn't be executed which was as expected.

### What should be noticed

> Because `@RedisLock` is implemented with spring aop, the `Example` class
should must be a spring containered bean.

> Exception in code wrapped with `@RedisLock` annotation MUST be caught and handled.

That's all, enjoy it!