import React, { useState } from 'react';
import { Header } from './components/Header';
import { Hero } from './components/Hero';
import { ServicesSection } from './components/ServicesSection';
import { TeamSection } from './components/TeamSection';
import { MediaSection } from './components/MediaSection';
import { BookingFlowModal } from './components/BookingFlowModal';
import { MyAppointments } from './components/MyAppointments';
import { AdminDashboard } from './components/AdminDashboard';
import { Footer } from './components/Footer';
import { Service, Barber, Appointment } from './types';

const INITIAL_SERVICES: Service[] = [
  { id: '1', name: 'Corte Masculino Premium', price: 55, durationMinutes: 35, category: 'Cabelo', description: 'Corte moderno com lavagem e alinhamento de fios.', imageUrl: 'https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&q=80&w=800' },
  { id: '2', name: 'Barba de Respeito com Toalha Quente', price: 45, durationMinutes: 30, category: 'Barba', description: 'Barboterapia completa com óleos essenciais e toalha quente.', imageUrl: 'https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&q=80&w=800' },
  { id: '3', name: 'Pezinho & Acabamento Navalhado', price: 25, durationMinutes: 15, category: 'Acabamento', description: 'Contorno perfeito de pezinho e barba alinhados na navalha.', imageUrl: 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&q=80&w=800' },
  { id: '4', name: 'Massagem Capilar & Facial', price: 40, durationMinutes: 20, category: 'Massagem', description: 'Massagem relaxante couro cabeludo com óleos essenciais.' },
  { id: '5', name: 'Design de Sobrancelha na Navalha', price: 20, durationMinutes: 15, category: 'Sobrancelha', description: 'Alinhamento preciso de sobrancelhas masculinas.' },
  { id: '6', name: 'Hidratação & Cauterização Capilar', price: 50, durationMinutes: 25, category: 'Hidratação', description: 'Tratamento profundo para brilho e maciez dos fios.' },
  { id: '7', name: 'Combo BarberLab (Cabelo + Barba)', price: 90, durationMinutes: 60, category: 'Combo', description: 'O pacote VIP completo da barbearia com acabamento impecável.', imageUrl: 'https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&q=80&w=800' }
];

const INITIAL_BARBERS: Barber[] = [
  { id: '1', name: 'João Silva', specialty: 'Master Barber & Fundador', rating: 5.0, photoUrl: 'https://images.unsplash.com/photo-1618077360395-f3068be8e001?auto=format&fit=crop&q=80&w=800' },
  { id: '2', name: 'Lucas "Alemão"', specialty: 'Especialista em Fade & Degradê', rating: 4.9, photoUrl: 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&q=80&w=800' }
];

const INITIAL_APPOINTMENTS: Appointment[] = [
  {
    id: 'apt_demo_1',
    clientName: 'Carlos Oliveira',
    clientPhone: '(11) 98877-1122',
    serviceName: 'Combo BarberLab (Cabelo + Barba)',
    barberName: 'João Silva',
    dateTimeStr: 'Hoje às 16:30',
    price: 90.00,
    status: 'Agendado'
  }
];

export function App() {
  const [activeTab, setActiveTab] = useState<'home' | 'my-appointments' | 'admin'>('home');
  const [isBookingOpen, setIsBookingOpen] = useState(false);
  const [preselectedService, setPreselectedService] = useState<Service | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>(INITIAL_APPOINTMENTS);

  const handleOpenBooking = (service?: Service) => {
    if (service) {
      setPreselectedService(service);
    } else {
      setPreselectedService(null);
    }
    setIsBookingOpen(true);
  };

  const handleBookingComplete = (newAppointment: Appointment) => {
    setAppointments(prev => [newAppointment, ...prev]);
  };

  return (
    <div className="min-h-screen bg-[#0A0A0C] text-white flex flex-col justify-between">
      <div>
        <Header
          onOpenBooking={() => handleOpenBooking()}
          activeTab={activeTab}
          setActiveTab={setActiveTab}
        />

        <main>
          {activeTab === 'home' && (
            <>
              <Hero onOpenBooking={() => handleOpenBooking()} />
              <ServicesSection
                services={INITIAL_SERVICES}
                onSelectService={(srv) => handleOpenBooking(srv)}
              />
              <MediaSection />
              <TeamSection barbers={INITIAL_BARBERS} />
            </>
          )}

          {activeTab === 'my-appointments' && (
            <MyAppointments
              appointments={appointments}
              onOpenBooking={() => handleOpenBooking()}
            />
          )}

          {activeTab === 'admin' && (
            <AdminDashboard
              appointments={appointments}
              services={INITIAL_SERVICES}
              barbers={INITIAL_BARBERS}
            />
          )}
        </main>
      </div>

      <Footer />

      <BookingFlowModal
        isOpen={isBookingOpen}
        onClose={() => setIsBookingOpen(false)}
        services={INITIAL_SERVICES}
        barbers={INITIAL_BARBERS}
        preselectedService={preselectedService}
        onBookingComplete={handleBookingComplete}
      />
    </div>
  );
}

export default App;
