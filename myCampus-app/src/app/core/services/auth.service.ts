import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { environment } from '../../../../src/environments/environment';
import { UserLoginRequest } from '../models/user-login-request.model';
import { UserLoginResponse } from '../models/user-login-response.model';
import { jwtDecode } from 'jwt-decode';
import { tap, catchError } from 'rxjs/operators';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUser = new BehaviorSubject<UserLoginResponse | null>(null);
  private mockToken =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3NDQzNTc3NTEsImV4cCI6MTc0NDk2MjU1MX0.pIeBce-qgjs9fmrow34A3eNW95Dqi-WVkVfA0T0W4Ec';

  constructor(private http: HttpClient, private cookieService: CookieService) {}

  login(data: UserLoginRequest): Observable<UserLoginResponse> {
    if (environment.mockApi) {
      const mockResponse: UserLoginResponse = {
        token: this.mockToken,
        user: {
          id: 'mock-user-id-123',
          email: data.email,
          role: '',
        },
      };
      return of(mockResponse).pipe(
        tap((response) => {
          if (!response.token) {
            throw new Error('No token received');
          }
          try {
            const decodedToken: any = jwtDecode(response.token);
            const role = decodedToken.role || 'default_role';
            const email = decodedToken.email || data.email;

            response.user = {
              id: decodedToken.sub || 'mock-user-id-123',
              email: email,
              role: role,
            };

            this.cookieService.set('role', role, {
              expires: 1,
              path: '/',
              sameSite: 'Lax',
              secure: false,
            });
            this.cookieService.set('token', response.token, {
              expires: 1,
              path: '/',
              sameSite: 'Lax',
              secure: false,
            });

            console.log('Cookies set:', {
              token: this.cookieService.get('token'),
              role: this.cookieService.get('role'),
            });

            this.currentUser.next(response);
          } catch (e) {
            console.error('Token decode error:', e);
            throw new Error('Invalid token format');
          }
        }),
        catchError((error) => {
          const errorMessage = 'Mock login failed';
          alert(errorMessage);
          return throwError(() => new Error(errorMessage));
        })
      );
    }

    return this.http
      .post<UserLoginResponse>(`${this.apiUrl}/auth/login`, data)
      .pipe(
        tap((response) => {
          if (!response.token) {
            throw new Error('Invalid response from server');
          }
          try {
            const decodedToken: any = jwtDecode(response.token);
            const role = decodedToken.role || 'default_role';
            const email = decodedToken.email || '';

            response.user = {
              id: decodedToken.sub || '',
              email: email,
              role: role,
            };

            this.cookieService.set('role', role, {
              expires: 1,
              path: '/',
              sameSite: 'Lax',
              secure: false,
            });
            this.cookieService.set('token', response.token, {
              expires: 1,
              path: '/',
              sameSite: 'Lax',
              secure: false,
            });

            console.log('Cookies set:', {
              token: this.cookieService.get('token'),
              role: this.cookieService.get('role'),
            });

            this.currentUser.next(response);
          } catch (e) {
            console.error('Token decode error:', e);
            throw new Error('Invalid token format');
          }
        }),
        catchError((error) => {
          const errorMessage =
            error?.error?.metadata?.message || 'An error occurred during login';
          alert(errorMessage);
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getRole(): string | null {
    return this.cookieService.get('role');
  }

  logout(): void {
    this.currentUser.next(null);
    this.cookieService.delete('token', '/');
    this.cookieService.delete('role', '/');
  }

  get isAuthenticated(): boolean {
    return !!this.cookieService.get('token');
  }

  get user$(): Observable<UserLoginResponse | null> {
    return this.currentUser.asObservable();
  }

  getCurrentUser(): UserLoginResponse | null {
    return this.currentUser.value;
  }
}
