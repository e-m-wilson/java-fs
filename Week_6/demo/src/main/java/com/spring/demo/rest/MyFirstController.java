package com.spring.demo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.demo.Coach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@RestController
public class MyFirstController {

    private static final Logger logger = LoggerFactory.getLogger(MyFirstController.class);

    private Coach myCoach;

    @Autowired 
    public MyFirstController(
        @Qualifier("swimCoach") Coach c
    ) {
        myCoach = c;
    }

    @GetMapping("/workout")
    public String workout() {
        return myCoach.getDailyWorkout();
    }

    @GetMapping("/")
    public String sayHello() {
        logger.info("GET requst on /");
        return "Hello World!";
    }

     @GetMapping("/test")
    public String sayTestingHotReload() {
        return "Hot Reload Works!";
    }

   
    
}
