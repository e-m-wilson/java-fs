package com.spring.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.School;

public interface SchoolRepository extends JpaRepository<School, Integer> {

}
