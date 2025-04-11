import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import { CookieService } from 'ngx-cookie-service';

@NgModule({
  declarations: [AppComponent, LoginComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    JwtInterceptor,
    AppRoutingModule,
    RouterModule.forRoot([]),
  ],
  providers: [CookieService],
  bootstrap: [AppComponent],
})
export class AppModule {}
