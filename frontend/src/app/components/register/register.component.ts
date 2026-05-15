import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  user = {
    username: '',
    password: '',
    email: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  onRegister() {
    this.http.post('http://localhost:8080/auth/register', this.user)
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
