import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import {AddApplicationComponent} from './components/add-application/add-application.component';
import {authGuard} from './auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'dashboard', component: DashboardComponent },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'add-application', component: AddApplicationComponent, canActivate: [authGuard] }
];
