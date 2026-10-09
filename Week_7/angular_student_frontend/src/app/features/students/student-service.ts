import { inject, Service } from '@angular/core';
import { Student } from './student';
import { NewStudent } from './new-student';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Service()
export class StudentService {

    private API_URL = 'http://localhost:8080/api/students';
    
    private http = inject(HttpClient);


    getStudents(): Observable<Student[]> {
        return this.http.get<Student[]>(this.API_URL);
    }

    getStudent(id: number): Observable<Student> {
        return this.http.get<Student>(`${this.API_URL}/${id}`);
    }

    createStudent(student: NewStudent): Observable<Student> {
        return this.http.post<Student>(this.API_URL, student);
    }

    updateStudent(id: number, student: NewStudent): Observable<Student> {
        return this.http.put<Student>(`${this.API_URL}/${id}`, student);
    }

    deleteStudent(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}