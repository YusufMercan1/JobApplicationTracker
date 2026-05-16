import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BookInterviewRequest, Interview } from '../models/interview.model';

@Injectable({ providedIn: 'root' })
export class InterviewService {
  private readonly apiUrl = `${environment.apiUrl}/api/interviews`;

  constructor(private http: HttpClient) {}

  getMyMeetings(): Observable<Interview[]> {
    return this.http.get<Interview[]>(this.apiUrl);
  }

  getMeetingsForJob(jobId: number): Observable<Interview[]> {
    return this.http.get<Interview[]>(`${this.apiUrl}/job/${jobId}`);
  }

  bookMeeting(jobId: number, request: BookInterviewRequest): Observable<Interview> {
    return this.http.post<Interview>(`${this.apiUrl}/${jobId}`, request);
  }

  cancelMeeting(interviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${interviewId}`);
  }
}
