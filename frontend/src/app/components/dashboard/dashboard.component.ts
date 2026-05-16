import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Chart, ArcElement, Tooltip, Legend, DoughnutController } from 'chart.js';
import { JobService } from '../../services/job.service';
import { InterviewService } from '../../services/interview.service';
import {
  APPLICATION_STATUSES,
  DashboardStats,
  JobApplication,
  STATUS_LABELS
} from '../../models/job-application.model';
import { BookInterviewRequest, Interview, Participant } from '../../models/interview.model';
import { isDeadlineApproaching, isDeadlineOverdue } from '../../utils/reminder.utils';

Chart.register(ArcElement, Tooltip, Legend, DoughnutController);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, AfterViewInit {

  @ViewChild('donutChart') donutChartRef!: ElementRef<HTMLCanvasElement>;
  private chart: Chart<'doughnut'> | null = null;

  applications: JobApplication[] = [];
  stats: DashboardStats | null = null;
  errorMessage = '';
  currentUser = '';

  editingApp: JobApplication | null = null;
  interviewApp: JobApplication | null = null;
  interviews: Interview[] = [];
  interviewLoading = false;
  interviewError = '';

  newMeeting: BookInterviewRequest = this.emptyMeeting();
  newParticipant: Participant = { name: '', email: '' };

  readonly statuses = APPLICATION_STATUSES;
  readonly statusLabels = STATUS_LABELS;

  constructor(
    private jobService: JobService,
    private interviewService: InterviewService,
    private router: Router
  ) {}

  ngOnInit() {
    this.currentUser = localStorage.getItem('username') || 'Användare';
    this.loadDashboard();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      if (!this.donutChartRef) return;
      this.chart = new Chart(this.donutChartRef.nativeElement, {
        type: 'doughnut',
        data: {
          labels: ['Sökt', 'Intervju', 'Avslagen'],
          datasets: [{
            data: [0, 0, 0],
            backgroundColor: ['#3b82f6', '#f59e0b', '#6b7280'],
            borderColor: 'transparent',
            hoverOffset: 6
          }]
        },
        options: {
          responsive: false,
          maintainAspectRatio: true,
          cutout: '70%',
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                color: '#94a3b8',
                padding: 16,
                font: { size: 13 }
              }
            },
            tooltip: {
              callbacks: {
                label: (ctx) => ` ${ctx.label}: ${ctx.parsed}`
              }
            }
          }
        }
      });
    }, 0);
  }

  private updateChart(stats: DashboardStats) {
    if (!this.chart) {
      setTimeout(() => this.updateChart(stats), 100);
      return;
    }
    this.chart.data.datasets[0].data = [stats.applied, stats.interview, stats.rejected];
    this.chart.update();
  }

  loadDashboard() {
    this.jobService.getApplications().subscribe({
      next: (data) => {
        this.applications = data;
        this.errorMessage = '';
      },
      error: () => {
        this.errorMessage = 'Kunde inte hämta ansökningar.';
      }
    });

    this.jobService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.updateChart(data);
      },
      error: () => {
        this.errorMessage = 'Kunde inte hämta statistik.';
      }
    });
  }

  openEdit(app: JobApplication) {
    this.editingApp = { ...app };
  }

  closeEdit() {
    this.editingApp = null;
  }

  saveEdit() {
    if (!this.editingApp?.id) {
      return;
    }

    this.jobService.updateApplication(this.editingApp.id, this.editingApp).subscribe({
      next: () => {
        this.closeEdit();
        this.loadDashboard();
      },
      error: () => alert('Kunde inte spara ändringar.')
    });
  }

  openInterviews(app: JobApplication) {
    this.interviewApp = app;
    this.newMeeting = this.emptyMeeting();
    this.interviewError = '';
    this.loadInterviews(app.id!);
  }

  closeInterviews() {
    this.interviewApp = null;
    this.interviews = [];
    this.newMeeting = this.emptyMeeting();
  }

  loadInterviews(jobId: number) {
    this.interviewLoading = true;
    this.interviewService.getMeetingsForJob(jobId).subscribe({
      next: (data) => {
        this.interviews = data;
        this.interviewLoading = false;
      },
      error: () => {
        this.interviewError = 'Kunde inte hämta möten.';
        this.interviewLoading = false;
      }
    });
  }

  addParticipant() {
    if (!this.newParticipant.name.trim()) {
      return;
    }
    this.newMeeting.participants.push({ ...this.newParticipant });
    this.newParticipant = { name: '', email: '' };
  }

  removeParticipant(index: number) {
    this.newMeeting.participants.splice(index, 1);
  }

  bookMeeting() {
    if (!this.interviewApp?.id || !this.newMeeting.dateTime) {
      alert('Ange datum och tid för mötet.');
      return;
    }

    this.interviewService.bookMeeting(this.interviewApp.id, this.newMeeting).subscribe({
      next: () => {
        this.newMeeting = this.emptyMeeting();
        this.loadInterviews(this.interviewApp!.id!);
        this.loadDashboard();
      },
      error: () => alert('Kunde inte boka mötet.')
    });
  }

  cancelMeeting(interviewId: number | undefined) {
    if (!interviewId || !confirm('Avboka detta möte?')) {
      return;
    }

    this.interviewService.cancelMeeting(interviewId).subscribe({
      next: () => {
        this.loadInterviews(this.interviewApp!.id!);
        this.loadDashboard();
      },
      error: () => alert('Kunde inte avboka mötet.')
    });
  }

  onDelete(id: number | undefined) {
    if (!id || !confirm('Är du säker på att du vill radera denna ansökan?')) {
      return;
    }

    this.jobService.deleteApplication(id).subscribe({
      next: () => this.loadDashboard(),
      error: () => alert('Kunde inte radera ansökan.')
    });
  }

  onStatusChange(app: JobApplication) {
    if (!app.id) {
      return;
    }

    this.jobService.updateApplication(app.id, app).subscribe({
      next: () => this.loadDashboard(),
      error: () => {
        alert('Kunde inte uppdatera status.');
        this.loadDashboard();
      }
    });
  }

  triggerEmail(id: number) {
    this.jobService.sendTestEmail(id).subscribe({
      next: () => alert('Ett påminnelsemail har skickats!'),
      error: () => alert('Kunde inte skicka mail.')
    });
  }

  isOverdue(app: JobApplication): boolean {
    return isDeadlineOverdue(app.deadline);
  }

  isApproaching(app: JobApplication): boolean {
    return isDeadlineApproaching(app.deadline);
  }

  showDeadlineReminder(app: JobApplication): boolean {
    return this.isApproaching(app) || this.isOverdue(app);
  }

  logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('username');
    this.router.navigate(['/login']);
  }

  private emptyMeeting(): BookInterviewRequest {
    return {
      dateTime: '',
      location: '',
      participants: []
    };
  }
}
