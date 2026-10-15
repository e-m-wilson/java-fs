package com.spring.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}