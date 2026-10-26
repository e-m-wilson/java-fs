import { HttpClient } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';
import { Observable, switchMap, tap } from 'rxjs';
import { CSRF_TOKEN_URL, LOGIN_URL, LOGOUT_URL, ME_URL, REGISTER_URL} from './auth-api';

export interface AuthUser {
  username: string;
  roles: string[];
}

@Service()
export class Auth {
  readonly isAuthenticated = signal(false);
  readonly currentUser = signal<AuthUser | null>(null);
  readonly canCreateOrEditStudents = computed(() =>
    this.currentUser()?.roles.some((role) =>
      role === 'ROLE_ADMIN' || role === 'ROLE_MANAGER',
    ) ?? false,
  );
  readonly canDeleteStudents = computed(() =>
    this.currentUser()?.roles.includes('ROLE_ADMIN') ?? false,
  );
  private readonly http = inject(HttpClient);

  login(userName: string, password: string): Observable<unknown> {
    return this.http.get<unknown>(CSRF_TOKEN_URL).pipe(
      switchMap(() =>
        this.http.post<unknown>(LOGIN_URL, { userName, password }),
      ),
      switchMap(() => this.loadCurrentUser()),
    );
  }

  loadCurrentUser(): Observable<AuthUser> {
    return this.http.get<AuthUser>(ME_URL).pipe(
      tap((user) => {
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
      }),
    );
  }

  register(userName: string, password: string): Observable<unknown> {
    return this.http.get<unknown>(CSRF_TOKEN_URL).pipe(
      switchMap(() =>
        this.http.post<unknown>(REGISTER_URL, { userName, password }),
      ),
    );
  }

  logout(): Observable<unknown> {
    return this.http.get<unknown>(CSRF_TOKEN_URL).pipe(
      switchMap(() =>
        this.http.post<unknown>(LOGOUT_URL, {}),
      ),
      tap(() => {
        this.currentUser.set(null);
        this.isAuthenticated.set(false);
      }),
    ); 
  }
}
