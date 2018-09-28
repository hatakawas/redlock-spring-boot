package com.jd.spring.boot.autoconfigure.test;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created on Sep 18, 2018
 *
 * @author Chuan Qin
 */
@SpringBootApplication
public class IntegrationTests {
    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {

        String[] beans = context.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
            System.out.println(bean);
        }

    }
}
