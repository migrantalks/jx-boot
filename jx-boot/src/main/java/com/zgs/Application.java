package com.zgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 主入口
 * @author zgs
 */
@EnableSwagger2
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	System.setProperty("spring.devtools.restart.enabled", "true");
    	SpringApplication.run(Application.class, args);
    }
}