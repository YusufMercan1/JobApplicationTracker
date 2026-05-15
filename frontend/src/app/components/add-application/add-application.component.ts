import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-application',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-application.component.html'
})
export class AddApplicationComponent {
  newApp = {
    company: '',
    role: '',
    status: 'APPLIED',
    applicationDate: new Date().toISOString().split('T')[0]
  };
  cancel() {
    this.router.navigate(['/dashboard']);
  }

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    this.http.post('http://localhost:8080/applications', this.newApp)
      .subscribe({
        next: () => {
          alert('Ansökan sparad!');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          console.error('Kunde inte spara:', err);
          alert('Ett fel uppstod när ansökan skulle sparas.');
        }
      });
  }
}
