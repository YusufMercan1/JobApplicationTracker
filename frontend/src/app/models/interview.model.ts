export interface Interview {
  id?: number;
  dateTime: string;
  location: string;
  participants: Participant[];
}

export interface Participant {
  name: string;
  email: string;
}
