package com.cleancode.api;

import com.cleancode.persistence.StudentDAO;
import com.cleancode.persistence.StudentDAOImpl;
import com.cleancode.service.StudentService;
import com.cleancode.service.StudentServiceImpl;

public class Main {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAOImpl();
        StudentService service = new StudentServiceImpl(dao);
        new StudentRepl(service).run();
    }
}