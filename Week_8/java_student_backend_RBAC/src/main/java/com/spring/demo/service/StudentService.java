package com.spring.demo.service;

import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.spring.demo.persistence.SchoolRepository;
import com.spring.demo.persistence.StudentRepository;
import com.spring.demo.domain.Student;
import com.spring.demo.dto.StudentWriteDto;
import com.spring.demo.domain.School;
import com.spring.demo.exceptions.RecordNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepo;
    private final SchoolRepository schoolRepo;
    
    // TO-DO Implement logging
    //private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    public StudentService(
        StudentRepository studentRepository,
        SchoolRepository schoolRepository
    ) {
        this.studentRepo = studentRepository;
        this.schoolRepo = schoolRepository;
    }

    // STUDENTS

    @Transactional
    public Student insertStudent(StudentWriteDto student) {
        
        School school = null;
        if(student.getSchoolId() != null) {
            school = schoolRepo.findById(student.getSchoolId())
            .orElse(null);
        }
      

        Student newStudent = new Student(
            student.getFirstName(),
            student.getLastName(),
            student.getEmail(),
            school
        );

        return studentRepo.save(newStudent);
    }

    @Transactional(readOnly = true)
    public List<Student> findStudentsBySchoolName(String name) {
        return studentRepo.findBySchool_Name(name);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents(Pageable pageable) {
        return studentRepo.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(int id) {

        Student student = studentRepo.findById(id)
            .orElseThrow(() -> 
                new RecordNotFoundException(
                    "Student not found with id: " + id
                )
            );


        return student;
    }

    @Transactional
    public void deleteStudent(int id) {
        Student student = getStudentById(id);
        studentRepo.delete(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByLastName(String lastName) {
        return studentRepo.findByLastName(lastName);
    }

    // we don't need a call to DAO here
    // since this is in a transaction and existingStudent is a
    // managed entitiy, any changes to it will be tracked
    // before the transaction closes, dirty checking will occur
    // this will detect the change to the managed entity, and the
    // changes will be auto commited
    @Transactional
    public Student updateStudent(int id, Student student) {
        Student existingStudent = getStudentById(id);

        existingStudent.setEmail(student.getEmail());
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());

        return existingStudent;
    }

}
