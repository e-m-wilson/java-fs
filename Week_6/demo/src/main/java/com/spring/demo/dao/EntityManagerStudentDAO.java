package com.spring.demo.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.spring.demo.domain.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository 
public class EntityManagerStudentDAO implements StudentDAO {

    @PersistenceContext 
    private EntityManager entityManager;

    @Override 
    public Student insert(Student student) {
        entityManager.persist(student);
        return student;
    }

    @Override 
    public List<Student> findAll() {
        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT student FROM Student student ORDER BY student.id",
            Student.class);
            return query.getResultList();
    }

    @Override 
    public Student findById(int id) {
        return entityManager.find(Student.class, id);
    }

    @Override 
    public void delete(Student student) {
        entityManager.remove(student);
    }

    @Override 
    public List<Student> findByLastName(String lastName) {

        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT student FROM Student student " + 
            "WHERE student.lastName = :lastName " + 
            "ORDER BY student.firstName", Student.class);   
        
        query.setParameter("lastName", lastName);

        return query.getResultList();

    }
}
