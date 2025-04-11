// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login/login.component'; // Pfad anpassen

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];
