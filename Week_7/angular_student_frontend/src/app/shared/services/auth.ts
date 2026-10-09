import { Service, signal } from '@angular/core';

@Service()
export class Auth {
  readonly isAuthenticated = signal(false);

  login(): void {
    this.isAuthenticated.set(true);
  }

  logout(): void {
    this.isAuthenticated.set(false);
  }
}
