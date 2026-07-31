import React from 'react';
import { Calendar, Star, Clock, MapPin, CheckCircle2 } from 'lucide-react';

interface HeroProps {
  onOpenBooking: () => void;
}

export const Hero: React.FC<HeroProps> = ({ onOpenBooking }) => {
  return (
    <section className="relative overflow-hidden pt-8 pb-16 md:pt-16 md:pb-24 border-b border-[#22242B]">
      {/* Background Glow Accents */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-7xl h-96 bg-[#D4AF37]/10 blur-[120px] pointer-events-none rounded-full" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          
          {/* Main Info Copy */}
          <div className="lg:col-span-7 space-y-6">
            <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-[#D4AF37]/15 border border-[#D4AF37]/30 text-[#D4AF37] text-xs font-bold uppercase tracking-wider">
              <Star className="w-3.5 h-3.5 fill-[#D4AF37]" />
              <span>Agendamento Online 24/7 • barbearia.art.br</span>
            </div>

            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight text-white leading-[1.1]">
              Seu João Barber e Alemão <br />
              <span className="bg-gradient-to-r from-[#D4AF37] via-[#F3E5AB] to-[#D4AF37] bg-clip-text text-transparent">
                Corte Moderno e Clássico
              </span>
            </h1>

            <p className="text-base sm:text-lg text-gray-300 max-w-2xl leading-relaxed">
              Barbearia especializada em cortes modernos, degradês impecáveis, barba tradicional com toalha quente e cuidados masculinos de alto padrão.
            </p>

            {/* Badges & Trust points */}
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 pt-2">
              <div className="flex items-center space-x-2.5 text-sm text-gray-300">
                <CheckCircle2 className="w-5 h-5 text-[#D4AF37] shrink-0" />
                <span>Confirmação WhatsApp</span>
              </div>
              <div className="flex items-center space-x-2.5 text-sm text-gray-300">
                <CheckCircle2 className="w-5 h-5 text-[#D4AF37] shrink-0" />
                <span>Barbeiros Masters</span>
              </div>
              <div className="flex items-center space-x-2.5 text-sm text-gray-300">
                <CheckCircle2 className="w-5 h-5 text-[#D4AF37] shrink-0" />
                <span>Sem Filas de Espera</span>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center space-y-3 sm:space-y-0 sm:space-x-4 pt-4">
              <button
                onClick={onOpenBooking}
                className="px-8 py-4 rounded-xl font-bold text-base bg-[#D4AF37] hover:bg-[#B38F22] text-black shadow-xl shadow-[#D4AF37]/25 hover:shadow-[#D4AF37]/40 transition-all flex items-center justify-center space-x-3 group active:scale-95"
              >
                <Calendar className="w-5 h-5 group-hover:rotate-12 transition-transform" />
                <span>Agendar Meu Horário</span>
              </button>
            </div>
          </div>

          {/* Right Card Mockup */}
          <div className="lg:col-span-5">
            <div className="relative rounded-2xl bg-[#121316] border border-[#22242B] p-6 shadow-2xl overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-[#D4AF37]/10 rounded-full blur-2xl" />
              
              <div className="flex items-center justify-between border-b border-[#22242B] pb-4 mb-4">
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 rounded-xl bg-[#0A0A0C] border border-[#22242B] flex items-center justify-center text-[#D4AF37]">
                    <Clock className="w-6 h-6" />
                  </div>
                  <div>
                    <h3 className="text-white font-bold text-sm">Horário de Funcionamento</h3>
                    <p className="text-xs text-gray-400">Segunda a Sábado • 09:00 às 20:00</p>
                  </div>
                </div>
                <span className="px-2.5 py-1 rounded-md bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-bold">
                  ABERTO
                </span>
              </div>

              <div className="space-y-3">
                <div className="p-3.5 rounded-xl bg-[#0A0A0C] border border-[#22242B] flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <MapPin className="w-5 h-5 text-[#D4AF37]" />
                    <span className="text-xs text-gray-300 font-medium">Av. Paulista, 1500 - São Paulo/SP</span>
                  </div>
                </div>

                <div className="p-4 rounded-xl bg-gradient-to-r from-[#D4AF37]/10 to-transparent border border-[#D4AF37]/20">
                  <p className="text-xs text-[#D4AF37] font-bold uppercase tracking-wider mb-1">Promoção Combo VIP</p>
                  <p className="text-sm font-bold text-white">Cabelo + Barba com Toalha Quente</p>
                  <div className="flex items-center justify-between mt-2">
                    <span className="text-xs text-gray-400">De R$ 100,00 por</span>
                    <span className="text-lg font-black text-[#D4AF37]">R$ 90,00</span>
                  </div>
                </div>
              </div>

            </div>
          </div>

        </div>
      </div>
    </section>
  );
};
