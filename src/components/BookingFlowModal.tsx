import React, { useState } from 'react';
import { Service, Barber, Appointment } from '../types';
import { sendWhatsAppConfirmation } from '../lib/whatsapp';
import { X, Calendar, Clock, User, Phone, CheckCircle2, Scissors, ArrowRight, ArrowLeft } from 'lucide-react';

interface BookingFlowModalProps {
  isOpen: boolean;
  onClose: () => void;
  services: Service[];
  barbers: Barber[];
  preselectedService?: Service | null;
  onBookingComplete: (newAppointment: Appointment) => void;
}

const AVAILABLE_TIMES = ['09:00', '10:00', '11:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00'];

export const BookingFlowModal: React.FC<BookingFlowModalProps> = ({
  isOpen,
  onClose,
  services,
  barbers,
  preselectedService,
  onBookingComplete
}) => {
  const [step, setStep] = useState<number>(1);
  const [selectedService, setSelectedService] = useState<Service | null>(preselectedService || services[0] || null);
  const [selectedBarber, setSelectedBarber] = useState<Barber | null>(barbers[0] || null);
  const [selectedDate, setSelectedDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [selectedTime, setSelectedTime] = useState<string>('14:00');
  const [clientName, setClientName] = useState<string>('');
  const [clientPhone, setClientPhone] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [bookingSuccess, setBookingSuccess] = useState<boolean>(false);
  const [whatsappResultMsg, setWhatsappResultMsg] = useState<string>('');
  const [waDirectUrl, setWaDirectUrl] = useState<string>('');

  if (!isOpen) return null;

  const handleConfirmBooking = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!clientName.trim() || !clientPhone.trim() || !selectedService || !selectedBarber) return;

    setIsSubmitting(true);

    const formattedDate = selectedDate.split('-').reverse().join('/');
    const dateTimeStr = `${formattedDate} às ${selectedTime}`;

    const newAppointment: Appointment = {
      id: `apt_web_${Date.now()}`,
      clientName: clientName.trim(),
      clientPhone: clientPhone.trim(),
      serviceName: selectedService.name,
      barberName: selectedBarber.name,
      dateTimeStr: dateTimeStr,
      price: selectedService.price,
      status: 'Agendado',
      createdAt: new Date().toISOString()
    };

    // Send WhatsApp Meta Cloud API notification
    const waResponse = await sendWhatsAppConfirmation({
      clientPhone: clientPhone.trim(),
      clientName: clientName.trim(),
      serviceName: selectedService.name,
      dateStr: formattedDate,
      timeStr: selectedTime,
      barberName: selectedBarber.name
    });

    setWhatsappResultMsg(waResponse.message);
    setWaDirectUrl(waResponse.waLink);

    onBookingComplete(newAppointment);
    setIsSubmitting(false);
    setBookingSuccess(true);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
      <div className="relative w-full max-w-lg bg-[#121316] border border-[#22242B] rounded-2xl p-6 shadow-2xl text-white max-h-[90vh] overflow-y-auto">
        
        {/* Header */}
        <div className="flex items-center justify-between border-b border-[#22242B] pb-4 mb-4">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-[#D4AF37] text-black flex items-center justify-center font-bold">
              <Scissors className="w-4 h-4" />
            </div>
            <div>
              <h3 className="font-bold text-sm text-white">Barbearia do João</h3>
              <p className="text-xs text-gray-400">Novo Agendamento Online</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg text-gray-400 hover:text-white hover:bg-white/10">
            <X className="w-5 h-5" />
          </button>
        </div>

        {!bookingSuccess ? (
          <div>
            {/* Steps indicator */}
            <div className="flex items-center justify-between text-xs font-semibold text-gray-400 mb-6 px-2">
              <span className={step >= 1 ? 'text-[#D4AF37]' : ''}>1. Serviço</span>
              <span className={step >= 2 ? 'text-[#D4AF37]' : ''}>2. Barbeiro & Data</span>
              <span className={step >= 3 ? 'text-[#D4AF37]' : ''}>3. Seus Dados</span>
            </div>

            {/* STEP 1: Choose Service */}
            {step === 1 && (
              <div className="space-y-4">
                <h4 className="text-sm font-bold text-white mb-2">Selecione o Serviço:</h4>
                <div className="space-y-2 max-h-60 overflow-y-auto pr-1">
                  {services.map((srv) => (
                    <div
                      key={srv.id}
                      onClick={() => setSelectedService(srv)}
                      className={`p-3.5 rounded-xl border cursor-pointer flex items-center justify-between transition-all ${
                        selectedService?.id === srv.id
                          ? 'bg-[#D4AF37]/15 border-[#D4AF37] text-white'
                          : 'bg-[#0A0A0C] border-[#22242B] text-gray-300 hover:border-[#D4AF37]/40'
                      }`}
                    >
                      <div>
                        <p className="font-bold text-sm">{srv.name}</p>
                        <p className="text-xs text-gray-400">{srv.durationMinutes} min • {srv.category}</p>
                      </div>
                      <span className="font-black text-sm text-[#D4AF37]">
                        R$ {srv.price.toFixed(2).replace('.', ',')}
                      </span>
                    </div>
                  ))}
                </div>

                <div className="pt-4 flex justify-end">
                  <button
                    disabled={!selectedService}
                    onClick={() => setStep(2)}
                    className="px-6 py-2.5 rounded-xl font-bold text-xs bg-[#D4AF37] text-black hover:bg-[#B38F22] flex items-center space-x-2"
                  >
                    <span>Avançar</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* STEP 2: Choose Barber & Date/Time */}
            {step === 2 && (
              <div className="space-y-4">
                <div>
                  <label className="block text-xs font-bold text-gray-300 mb-2">Barbeiro de Preferência:</label>
                  <div className="grid grid-cols-2 gap-2">
                    {barbers.map((b) => (
                      <div
                        key={b.id}
                        onClick={() => setSelectedBarber(b)}
                        className={`p-3 rounded-xl border cursor-pointer text-center transition-all ${
                          selectedBarber?.id === b.id
                            ? 'bg-[#D4AF37]/15 border-[#D4AF37] text-white'
                            : 'bg-[#0A0A0C] border-[#22242B] text-gray-300'
                        }`}
                      >
                        <p className="font-bold text-xs">{b.name}</p>
                        <p className="text-[10px] text-gray-400">{b.specialty}</p>
                      </div>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-300 mb-2">Data do Agendamento:</label>
                  <input
                    type="date"
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    className="w-full bg-[#0A0A0C] border border-[#22242B] rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-[#D4AF37]"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-300 mb-2">Horário Disponível:</label>
                  <div className="grid grid-cols-5 gap-1.5">
                    {AVAILABLE_TIMES.map((t) => (
                      <button
                        key={t}
                        type="button"
                        onClick={() => setSelectedTime(t)}
                        className={`py-1.5 rounded-lg text-xs font-bold transition-all border ${
                          selectedTime === t
                            ? 'bg-[#D4AF37] text-black border-[#D4AF37]'
                            : 'bg-[#0A0A0C] text-gray-300 border-[#22242B] hover:border-[#D4AF37]/50'
                        }`}
                      >
                        {t}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="pt-4 flex justify-between">
                  <button
                    onClick={() => setStep(1)}
                    className="px-4 py-2 rounded-xl text-xs font-bold bg-[#0A0A0C] border border-[#22242B] text-gray-300 flex items-center space-x-1"
                  >
                    <ArrowLeft className="w-4 h-4" />
                    <span>Voltar</span>
                  </button>
                  <button
                    onClick={() => setStep(3)}
                    className="px-6 py-2.5 rounded-xl font-bold text-xs bg-[#D4AF37] text-black hover:bg-[#B38F22] flex items-center space-x-2"
                  >
                    <span>Avançar</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* STEP 3: Client Info */}
            {step === 3 && (
              <form onSubmit={handleConfirmBooking} className="space-y-4">
                <div className="p-3 rounded-xl bg-[#0A0A0C] border border-[#22242B] space-y-1 text-xs text-gray-300 mb-2">
                  <p><span className="text-gray-500">Serviço:</span> <strong className="text-white">{selectedService?.name}</strong> (R$ {selectedService?.price.toFixed(2)})</p>
                  <p><span className="text-gray-500">Barbeiro:</span> <strong className="text-white">{selectedBarber?.name}</strong></p>
                  <p><span className="text-gray-500">Data e Hora:</span> <strong className="text-[#D4AF37]">{selectedDate.split('-').reverse().join('/')} às {selectedTime}</strong></p>
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-300 mb-1">Seu Nome Completo *</label>
                  <input
                    type="text"
                    required
                    placeholder="Ex: Carlos Oliveira"
                    value={clientName}
                    onChange={(e) => setClientName(e.target.value)}
                    className="w-full bg-[#0A0A0C] border border-[#22242B] rounded-xl px-3 py-2.5 text-xs text-white focus:outline-none focus:border-[#D4AF37]"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-300 mb-1">Telefone WhatsApp (com DDD) *</label>
                  <input
                    type="tel"
                    required
                    placeholder="(11) 99999-8888"
                    value={clientPhone}
                    onChange={(e) => setClientPhone(e.target.value)}
                    className="w-full bg-[#0A0A0C] border border-[#22242B] rounded-xl px-3 py-2.5 text-xs text-white focus:outline-none focus:border-[#D4AF37]"
                  />
                  <p className="text-[10px] text-gray-500 mt-1">Enviaremos a confirmação instantânea diretamente no seu WhatsApp.</p>
                </div>

                <div className="pt-4 flex justify-between">
                  <button
                    type="button"
                    onClick={() => setStep(2)}
                    className="px-4 py-2 rounded-xl text-xs font-bold bg-[#0A0A0C] border border-[#22242B] text-gray-300 flex items-center space-x-1"
                  >
                    <ArrowLeft className="w-4 h-4" />
                    <span>Voltar</span>
                  </button>
                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="px-6 py-2.5 rounded-xl font-bold text-xs bg-[#D4AF37] text-black hover:bg-[#B38F22] flex items-center space-x-2"
                  >
                    {isSubmitting ? 'Confirmando...' : 'Confirmar Agendamento'}
                  </button>
                </div>
              </form>
            )}
          </div>
        ) : (
          /* SUCCESS SCREEN */
          <div className="text-center py-6 space-y-4">
            <div className="w-16 h-16 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 flex items-center justify-center mx-auto">
              <CheckCircle2 className="w-10 h-10" />
            </div>
            <h3 className="text-xl font-black text-white">Agendamento Confirmado!</h3>
            <p className="text-xs text-gray-300 max-w-sm mx-auto">
              Seu horário na <strong className="text-white">Barbearia do João</strong> foi registrado com sucesso.
            </p>

            <div className="p-3 rounded-xl bg-[#0A0A0C] border border-[#22242B] text-xs text-emerald-400 font-semibold">
              {whatsappResultMsg}
            </div>

            {waDirectUrl && (
              <a
                href={waDirectUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center space-x-2 px-6 py-3 rounded-xl bg-[#25D366] text-black font-bold text-xs hover:bg-[#20bd5a] transition-all"
              >
                <span>Abrir Mensagem no WhatsApp</span>
              </a>
            )}

            <div className="pt-2">
              <button
                onClick={onClose}
                className="px-6 py-2.5 rounded-xl font-bold text-xs bg-[#22242B] text-white hover:bg-white/10"
              >
                Fechar
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};
