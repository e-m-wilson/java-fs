package com.spring.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.School;

public interface SchoolRepository extends JpaRepository<School, Integer> {

}
