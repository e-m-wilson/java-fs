import { Component, inject, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Student } from '../student';
import { Auth } from '../../../shared/services/auth';

import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';

@Component({
  imports: [RouterLink, MatButtonModule, MatCardModule],
  selector: 'app-student-card',
  styleUrl: './student-card.css',
  templateUrl: './student-card.html',
})
export class StudentCard {
  readonly auth = inject(Auth);
  student = input.required<Student>();

  deleted = output<Student>();

  deleteStudent() {
    this.deleted.emit(this.student());
  }
}