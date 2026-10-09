import { Component, inject } from '@angular/core';
import { Auth } from '../../services/auth';
import { MatButton } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
  imports: [MatButton],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {

  authService = inject(Auth);
  router = inject(Router);

  login() {
    this.authService.login();
    this.router.navigateByUrl('/');
  }

}
