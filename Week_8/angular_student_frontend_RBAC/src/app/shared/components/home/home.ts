import { Component, inject, OnInit, signal } from '@angular/core';
import { StudentCard } from '../../../features/students/student-card/student-card';
import { Student } from '../../../features/students/student';
import { StudentService } from '../../../features/students/student-service';

import {FormControl, FormGroup, ReactiveFormsModule, FormsModule} from '@angular/forms';

import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSnackBar} from '@angular/material/snack-bar';


@Component({
  imports: [StudentCard, ReactiveFormsModule, FormsModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatProgressSpinnerModule],
  selector: 'app-home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home implements OnInit {

  
  studentService: StudentService = inject(StudentService);
  studentList = signal<Student[] | undefined>(undefined);
  private _snackBar = inject(MatSnackBar);

  studentForm = new FormGroup({
    firstName: new FormControl('First Name', { nonNullable: true }),
    lastName: new FormControl('Last Name', { nonNullable: true }),
    email: new FormControl('email@email.com', { nonNullable: true }),
  });

  emailFilter = new FormControl('');

isLoading = signal(true);
loadError = signal(false);

ngOnInit(): void {
  this.studentService.getStudents().subscribe({
    next: (response) => {
      this.studentList.set(response);
      this.isLoading.set(false);
    },
    error: () => {
      this.loadError.set(true);
      this.isLoading.set(false);
    }
  });
}

  saveNewStudent() {
  const formValue = this.studentForm.getRawValue();

  const newStudent = {
    firstName: formValue.firstName.trim(),
    lastName: formValue.lastName.trim(),
    email: formValue.email.trim(),
  };

  this.studentService.createStudent(newStudent).subscribe({
    next: (createdStudent) => {
      this.studentList.update(students => [
        ...(students ?? []),
        createdStudent
      ]);

      this.studentForm.reset({
        firstName: '',
        lastName: '',
        email: '',
      });

      this.openSuccessSnackBar(`Created Student with ID: ${createdStudent.id}`)

    },
    error: err => {
      console.log(err);
      this.openFailSnackBar(err.error.message);
    }
  });
}

  openSuccessSnackBar(message: string) {
    this._snackBar.open(message, 'Dismiss', {
      duration: 3000,
      panelClass: ['success-snackbar'],
    });
  }

  openFailSnackBar(message: string) {
    this._snackBar.open(message, 'Dismiss', {
      duration: 3000,
      panelClass: ['fail-snackbar'],
    });
  }

  onStudentDeleted(student: Student) {
    this.studentService.deleteStudent(student.id).subscribe({
      next: () => {
        this.studentList.update(students =>
          students?.filter(currentStudent => currentStudent.id !== student.id),
        );
      },
      error: err => console.error('Failed to delete student', err)
    });
  }

  // filterStudents() {
  //   const email = this.emailFilter.value?.trim() ?? '';
  //   this.studentList = this.studentService.getStudentsByEmail(email);
  // }
}
