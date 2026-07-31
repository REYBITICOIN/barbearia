import React from 'react';
import { Appointment } from '../types';
import { Calendar, Clock, User, Phone, CheckCircle2, MessageSquare } from 'lucide-react';
import { sanitizePhone } from '../lib/whatsapp';

interface MyAppointmentsProps {
  appointments: Appointment[];
  onOpenBooking: () => void;
}

export const MyAppointments: React.FC<MyAppointmentsProps> = ({ appointments, onOpenBooking }) => {
  return (
    <div className="py-12 bg-[#0A0A0C]">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="text-2xl font-black text-white">Meus Agendamentos</h2>
            <p className="text-xs text-gray-400">Histórico de horários e confirmações da Barbearia do João</p>
          </div>
          <button
            onClick={onOpenBooking}
            className="px-4 py-2 rounded-xl text-xs font-bold bg-[#D4AF37] text-black hover:bg-[#B38F22]"
          >
            Novo Agendamento
          </button>
        </div>

        {appointments.length === 0 ? (
          <div className="text-center py-16 rounded-2xl bg-[#121316] border border-[#22242B] p-8">
            <Calendar className="w-12 h-12 text-gray-600 mx-auto mb-3" />
            <h3 className="text-base font-bold text-white mb-1">Nenhum agendamento encontrado</h3>
            <p className="text-xs text-gray-400 mb-6">Você ainda não agendou nenhum corte ou serviço conosco.</p>
            <button
              onClick={onOpenBooking}
              className="px-6 py-2.5 rounded-xl text-xs font-bold bg-[#D4AF37] text-black hover:bg-[#B38F22]"
            >
              Agendar Agora
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {appointments.map((apt) => {
              const cleanPhone = sanitizePhone(apt.clientPhone);
              const waMessage = encodeURIComponent(`Olá ${apt.clientName}! Confirmando seu agendamento na Barbearia do João para ${apt.dateTimeStr} com ${apt.barberName}.`);
              const waUrl = `https://wa.me/${cleanPhone}?text=${waMessage}`;

              return (
                <div
                  key={apt.id}
                  className="rounded-2xl bg-[#121316] border border-[#22242B] p-5 flex flex-col md:flex-row md:items-center justify-between gap-4"
                >
                  <div className="space-y-1">
                    <div className="flex items-center space-x-2">
                      <span className="px-2.5 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[10px] font-bold">
                        {apt.status}
                      </span>
                      <span className="text-xs text-gray-400">• R$ {apt.price.toFixed(2)}</span>
                    </div>
                    <h4 className="text-base font-bold text-white">{apt.serviceName}</h4>
                    <p className="text-xs text-gray-300">
                      Cliente: <strong>{apt.clientName}</strong> ({apt.clientPhone})
                    </p>
                    <p className="text-xs text-[#D4AF37] font-semibold">
                      {apt.dateTimeStr} com {apt.barberName}
                    </p>
                  </div>

                  <div className="flex items-center space-x-3">
                    <a
                      href={waUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="px-3.5 py-2 rounded-xl bg-[#25D366]/10 text-[#25D366] hover:bg-[#25D366] hover:text-black border border-[#25D366]/30 text-xs font-bold flex items-center space-x-1.5 transition-all"
                    >
                      <MessageSquare className="w-4 h-4" />
                      <span>WhatsApp</span>
                    </a>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
