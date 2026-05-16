import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  user = {
    username: '',
    password: '',
    email: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  onRegister() {
    this.http.post(`${environment.apiUrl}/auth/register`, this.user)
      .subscribe({
        next: () => {
          alert('Registrering lyckades! Du kan nu logga in.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error(err);
          alert('Registrering misslyckades. Användarnamnet kan vara upptaget.');
        }
      });
  }
}
