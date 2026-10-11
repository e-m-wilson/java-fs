package com.spring.demo.service;

import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.demo.domain.School;
import com.spring.demo.exceptions.RecordNotFoundException;
import com.spring.demo.persistence.SchoolRepository;

@Service 
public class SchoolService {
    
    private final SchoolRepository schoolRepo;
    
    // TO-DO Implement logging
    //private static final Logger log = LoggerFactory.getLogger(StudentService.class);

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


    @Transactional(readOnly = true)
    public List<School> getAllSchools(Pageable pageable) {
        return schoolRepo.findAll(pageable).getContent();
    }


    @Transactional
    public School insertSchool(School school) {
        return schoolRepo.save(school);
    }
}
