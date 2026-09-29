package com.spring.demo;

import org.springframework.stereotype.Component;

@Component 
public class baseballCoach implements Coach{

    @Override 
    public String getDailyWorkout() {
        return "Play baseball.";
    }

}
