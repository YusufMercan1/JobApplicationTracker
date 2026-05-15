import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JobApplication } from '../models/job-application.model';

@Injectable({ providedIn: 'root' })
export class JobService {
  private apiUrl = 'http://localhost:8080/applications';

  constructor(private http: HttpClient) {}

  getApplications(): Observable<JobApplication[]> {
    return this.http.get<JobApplication[]>(this.apiUrl);
    // i job.service.ts
  }

    deleteApplication(id: number) {
      return this.http.delete(`http://localhost:8080/applications/${id}`);
    }

    updateStatus(id: number, status: string) {
      return this.http.patch(`http://localhost:8080/applications/${id}/status`, status);
    }
}
