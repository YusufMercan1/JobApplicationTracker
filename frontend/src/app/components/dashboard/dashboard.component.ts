import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { JobService } from '../../services/job.service';
import { JobApplication } from '../../models/job-application.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  applications: JobApplication[] = [];
  errorMessage: string = '';
  currentUser: string = '';

  constructor(private jobService: JobService, private router: Router) {}

  ngOnInit() {
    this.loadApplications();
    this.currentUser = localStorage.getItem('username') || 'Användare';
  }

  loadApplications() {
    this.jobService.getApplications().subscribe({
      next: (data) => {
        this.applications = data;
        this.errorMessage = '';
      },
      error: (err) => {
        console.error('Kunde inte hämta jobb:', err);
        this.errorMessage = 'Kunde inte hämta data. Sessionen kan ha gått ut.';
        if (err.status === 403 || err.status === 401) {
          this.logout();
        }
      }
    });
  }

  onDelete(id: number | undefined) {
    if (confirm('Är du säker på att du vill radera denna ansökan?')) {
      this.jobService.deleteApplication(id!).subscribe({
        next: () => {
          this.applications = this.applications.filter(app => app.id !== id);
        },
        error: (err) => alert('Kunde inte radera jobbet.')
      });
    }
  }

  onStatusChange(id: number | undefined, newStatus: string) {
    this.jobService.updateStatus(id!, newStatus).subscribe({
      next: () => console.log('Status uppdaterad till:', newStatus),
      error: (err) => alert('Kunde inte uppdatera status.')
    });
  }

  logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('username');
    this.router.navigate(['/login']);
  }
}
