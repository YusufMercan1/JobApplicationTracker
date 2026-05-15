export interface JobApplication {
  id: number;
  companyName: string;
  jobTitle: string;
  status: string;
  applicationDate: string;
  notes?: string;
}
