import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap } from 'rxjs';
import { AUTH_API_BASE_URL, CSRF_TOKEN_URL } from '../services/auth-api';
import { CsrfToken } from '../services/csrf-token';

export const csrfInterceptor: HttpInterceptorFn = (request, next) => {
  if (
    request.url !== AUTH_API_BASE_URL &&
    !request.url.startsWith(`${AUTH_API_BASE_URL}/`)
  ) {
    return next(request);
  }

  const csrfToken = inject(CsrfToken);
  const token = request.url === CSRF_TOKEN_URL ? null : csrfToken.current;
  let apiRequest = request.clone({ withCredentials: true });

  if (token) {
    apiRequest = apiRequest.clone({
      setHeaders: { [token.headerName]: token.value },
    });
  }

  return next(apiRequest).pipe(
    tap((event) => {
      if (
        request.method === 'GET' &&
        request.url === CSRF_TOKEN_URL &&
        event instanceof HttpResponse &&
        !csrfToken.store(event.body)
      ) {
        throw new Error('The CSRF endpoint did not return a token.');
      }
    }),
  );
};