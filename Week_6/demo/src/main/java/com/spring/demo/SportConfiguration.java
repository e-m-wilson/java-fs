package com.spring.demo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class SportConfiguration {

        @Bean 
        public Coach swimCoach() {
            return new SwimCoach();
        }
}
