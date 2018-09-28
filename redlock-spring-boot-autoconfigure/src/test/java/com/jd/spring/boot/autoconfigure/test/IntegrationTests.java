package com.jd.spring.boot.autoconfigure.test;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@SpringBootApplication
public class IntegrationTests {
    public static void main(String[] args) {
        SpringApplication.run(IntegrationTests.class, args);
    }
}
