package com.spring.demo;


public class SwimCoach implements Coach{

    @Override 
    public String getDailyWorkout() {
        return "Swim for 20 minutes.";
    }

}
