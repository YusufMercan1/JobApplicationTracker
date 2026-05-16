import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { JobService } from '../../services/job.service';
import {
  APPLICATION_STATUSES,
  JobApplication,
  STATUS_LABELS
} from '../../models/job-application.model';

@Component({
  selector: 'app-add-application',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './add-application.component.html',
  styleUrl: './add-application.component.scss'
})
export class AddApplicationComponent {
  readonly statuses = APPLICATION_STATUSES;
  readonly statusLabels = STATUS_LABELS;

  newApp: JobApplication = {
    companyName: '',
    role: '',
    status: 'APPLIED',
    appliedDate: new Date().toISOString().split('T')[0],
    deadline: '',
    notes: ''
  };

  errorMessage = '';
  isSubmitting = false;

  constructor(private jobService: JobService, private router: Router) {}

  onSubmit() {
    this.errorMessage = '';
    this.isSubmitting = true;

    const payload: JobApplication = {
      companyName: this.newApp.companyName.trim(),
      role: this.newApp.role.trim(),
      status: this.newApp.status,
      appliedDate: this.newApp.appliedDate || undefined,
      deadline: this.newApp.deadline || undefined,
      notes: this.newApp.notes?.trim() || undefined
    };

    this.jobService.createApplication(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = 'Ett fel uppstod när ansökan skulle sparas.';
      }
    });
  }

  cancel() {
    this.router.navigate(['/dashboard']);
  }
}
