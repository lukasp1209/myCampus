import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
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

  constructor(private http: HttpClient, private cookieService: CookieService) {}

  login(data: UserLoginRequest): Observable<UserLoginResponse> {
    return this.http
      .post<UserLoginResponse>(`${this.apiUrl}/auth/login`, data)
      .pipe(
        tap((response) => {
          const decodedToken: any = jwtDecode(response.token);
          const role = decodedToken.role;

          this.cookieService.set('role', role, { expires: 1 });
          this.cookieService.set('token', response.token, { expires: 1 });

          this.currentUser.next(response);
        }),
        catchError((error) => {
          const errorMessage =
            error?.error?.metadata?.message || 'An error occurred during login';
          alert(errorMessage);
          return throwError(errorMessage);
        })
      );
  }

  getRole(): string | null {
    return this.cookieService.get('role');
  }

  logout(): void {
    this.currentUser.next(null);
    this.cookieService.delete('token');
    this.cookieService.delete('role');
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
