package com.spring.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.demo.domain.School;
import com.spring.demo.exceptions.RecordNotFoundException;
import com.spring.demo.persistence.SchoolRepository;

@Service 
public class SchoolService {
    
    private final SchoolRepository schoolRepo;
    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepo = schoolRepository;
    }

    // SCHOOLS
    @Transactional(readOnly = true)
    public School getSchoolById(int id) {
          School school = schoolRepo.findById(id)
            .orElseThrow(() -> 
                new RecordNotFoundException(
                    "School not found with id: " + id
                )
            );


        return school;
    }

    @Transactional
    public School insertSchool(School school) {
        return schoolRepo.save(school);
    }
}
