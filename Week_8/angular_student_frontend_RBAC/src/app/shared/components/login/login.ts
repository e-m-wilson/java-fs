import { Component, inject, signal } from '@angular/core';
import { Auth } from '../../services/auth';
import { MatButton } from '@angular/material/button';
import { Router } from '@angular/router';
import {FormControl, FormGroup, ReactiveFormsModule, FormsModule} from '@angular/forms';

import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';



@Component({
  imports: [MatButton, ReactiveFormsModule, FormsModule, MatInputModule, MatFormFieldModule, MatButtonModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {

  authService = inject(Auth);
  router = inject(Router);
  mode = signal<'login' | 'register'>('login');
  registrationComplete = signal(false);

  loginForm = new FormGroup({
    userName: new FormControl('', { nonNullable: true }),
    password: new FormControl('', { nonNullable: true }),
  });

  loginOrRegister(): void {
    const { userName, password } = this.loginForm.getRawValue();

    if (this.mode() === 'register') {
      this.authService.register(userName, password).subscribe(() => {
        this.registrationComplete.set(true);
        this.mode.set('login');
      });
      return;
    }

    this.authService.login(userName, password).subscribe(() => {
      void this.router.navigateByUrl('/');
    });
  }

  toggleMode(): void {
    this.mode.update((mode) => mode === 'login' ? 'register' : 'login');
    this.registrationComplete.set(false);
  }

}
