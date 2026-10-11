import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';

import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import { Auth } from '../services/auth';

@Component({
  imports: [RouterOutlet, RouterLink, MatButtonModule, MatToolbarModule],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {

  authSerivce = inject(Auth);
  router = inject(Router);

  isLoggedIn() {
    return this.authSerivce.isAuthenticated();
  }

  logout() {
    this.authSerivce.logout();
    this.router.navigateByUrl('/login');
  }
}
