package com.spring.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserNameAndEnabledTrue(String userName);

    User findByUserName(String userName);

    boolean existsByUserName(String userName);
}