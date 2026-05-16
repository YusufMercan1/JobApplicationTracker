export interface Participant {
  id?: number;
  name: string;
  email: string;
}

export interface Interview {
  id?: number;
  dateTime: string;
  location: string;
  jobApplicationId?: number;
  reminderSent?: boolean;
  meetingApproaching?: boolean;
  participants: Participant[];
}

export interface BookInterviewRequest {
  dateTime: string;
  location: string;
  participants: Participant[];
}
