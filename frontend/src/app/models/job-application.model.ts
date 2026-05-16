export type ApplicationStatus = 'APPLIED' | 'INTERVIEW' | 'REJECTED';

export interface JobApplication {
  id?: number;
  companyName: string;
  role: string;
  status: ApplicationStatus;
  appliedDate?: string;
  deadline?: string;
  notes?: string;
  reminderSent?: boolean;
}

export interface DashboardStats {
  total: number;
  applied: number;
  interview: number;
  rejected: number;
  overdue: number;
  upcomingDeadlines: number;
  upcomingMeetings: number;
}

export const APPLICATION_STATUSES: ApplicationStatus[] = [
  'APPLIED',
  'INTERVIEW',
  'REJECTED'
];

export const STATUS_LABELS: Record<ApplicationStatus, string> = {
  APPLIED: 'Sökt',
  INTERVIEW: 'Intervju',
  REJECTED: 'Avslagen'
};
