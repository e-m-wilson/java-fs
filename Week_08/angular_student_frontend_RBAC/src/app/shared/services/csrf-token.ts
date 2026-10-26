import { Service } from '@angular/core';

export interface CsrfTokenValue {
  value: string;
  headerName: string;
}

@Service()
export class CsrfToken {
  private token: CsrfTokenValue | null = null;

  get current(): CsrfTokenValue | null {
    return this.token;
  }

  store(response: unknown): boolean {
    if (typeof response !== 'object' || response === null) {
      return false;
    }

    const body = response as { token: string; headerName: string };
    if (body?.token?.length === 0) {
      return false;
    }

    this.token = {
      value: body.token,
      headerName: body.headerName,
    };
    return true;
  }
}