import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  credentials = {
    username: '',
    password: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  onLogin() {
    console.log('Loggar in...', this.credentials);

    this.http.post('http://localhost:8080/auth/login', this.credentials, { responseType: 'text' })
      .subscribe({
        next: (receivedToken: string) => {
          console.log('Inloggning lyckades, token mottagen');


          const cleanToken = receivedToken.replace(/^"|"$/g, '');

          localStorage.setItem('accessToken', cleanToken);

          this.router.navigate(['/dashboard']).then(navSucceeded => {
            if (!navSucceeded) {
              console.error('Navigering misslyckades. Kontrollera dina routes i app.routes.ts');
            } else {
              console.log('Navigerade till dashboard.');
            }
          });
        },
        error: (err) => {
          console.error('Inloggning misslyckades:', err);

          if (err.status === 403 || err.status === 401) {
            alert('Fel användarnamn eller lösenord.');
          } else {
            alert('Ett oväntat fel uppstod vid inloggning. Är backenden igång?');
          }
        }
      });
  }
}
