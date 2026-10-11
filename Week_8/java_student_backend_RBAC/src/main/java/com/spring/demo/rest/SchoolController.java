package com.spring.demo.rest;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.spring.demo.domain.School;
import com.spring.demo.service.SchoolService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @PostMapping
    public ResponseEntity<School> insertSchool(@Valid @RequestBody School school) {

        School savedSchool = schoolService.insertSchool(school);

        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedSchool.getId())
                                .toUri())
                .body(savedSchool);
    }

    @GetMapping("/{id}")
    public School getSchoolById(@PathVariable int id) {
        return schoolService.getSchoolById(id);
    }

    // @GetMapping
    // public List<School> getAllSchools(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "10") int count,
    //         @RequestParam(defaultValue = "true") boolean asc) {

    //     return schoolService.getAllSchools(page, count, asc);
    // }

    @GetMapping
    public List<School> getAllSchools(Pageable pageable) {
        return schoolService.getAllSchools(pageable);
    }


}
