package com.spring.demo;


import org.springframework.stereotype.Component;

@Component
public class CoachImpl implements Coach{

    @Override
    public String getDailyWorkout() {
        return "Play basketball.";
    }

}
