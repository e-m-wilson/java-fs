import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Student } from '../student';

import { ActivatedRoute } from '@angular/router';
import { StudentService } from '../student-service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NewStudent } from '../new-student';

@Component({
  imports: [MatCardModule, MatButtonModule, MatInputModule, MatFormFieldModule, ReactiveFormsModule],
  selector: 'app-student-details',
  styleUrl: './student-details.css',
  templateUrl: './student-details.html',
})
export class StudentDetails implements OnInit {
  route = inject(ActivatedRoute);
  studentService = inject(StudentService);

  student = signal<Student | undefined>(undefined);
  editMode = signal(false);

  readonly studentForm = new FormGroup({
    firstName: new FormControl('', { nonNullable: true }),
    lastName: new FormControl('', { nonNullable: true }),
    email: new FormControl('', { nonNullable: true }),
  });

  ngOnInit() {
    const studentId = Number(this.route.snapshot.params['id']);

    this.studentService.getStudent(studentId).subscribe({
      next: (student) => {
        this.student.set(student);
        this.studentForm.patchValue(student);
      },
      error: (error) => console.error('Failed to load student', error),
    });
  }

  toggleEditForm() {
    this.editMode.update((value) => !value);
  }

  saveStudent() {
    const currentStudent = this.student();

    if (!currentStudent || this.studentForm.invalid) {
      return;
    }

    const updatedStudent: NewStudent = {
      ...currentStudent,
      ...this.studentForm.getRawValue(),
      id: currentStudent.id
    };

    this.studentService.updateStudent(Number(updatedStudent.id), updatedStudent).subscribe({
      next: (student) => {
        this.student.set(student);
        this.studentForm.patchValue(student);
        this.editMode.set(false);
      },
      error: (error) => {
        console.error('Failed to update student', error);
      },
    });
  }
}
