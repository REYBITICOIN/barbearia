export interface Service {
  id: string;
  name: string;
  price: number;
  durationMinutes: number;
  category: string;
  description: string;
}

export interface Barber {
  id: string;
  name: string;
  specialty: string;
  photoUrl?: string;
  rating: number;
}

export interface Appointment {
  id: string;
  clientName: string;
  clientPhone: string;
  serviceName: string;
  barberName: string;
  dateTimeStr: string;
  price: number;
  status: 'Agendado' | 'Concluído' | 'Cancelado';
  createdAt?: string;
}

export interface BarbershopInfo {
  name: string;
  slogan: string;
  description: string;
  phone: string;
  address: string;
  domainUrl: string;
}
