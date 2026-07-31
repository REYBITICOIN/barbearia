import React, { useState } from 'react';
import { Scissors, Calendar, Shield, Menu, X, Phone, MapPin } from 'lucide-react';

interface HeaderProps {
  onOpenBooking: () => void;
  activeTab: 'home' | 'my-appointments' | 'admin';
  setActiveTab: (tab: 'home' | 'my-appointments' | 'admin') => void;
}

export const Header: React.FC<HeaderProps> = ({ onOpenBooking, activeTab, setActiveTab }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 bg-[#0A0A0C]/90 backdrop-blur-md border-b border-[#22242B]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20">
          
          {/* Logo & Title */}
          <div 
            className="flex items-center space-x-3 cursor-pointer group"
            onClick={() => setActiveTab('home')}
          >
            <div className="w-11 h-11 rounded-xl bg-gradient-to-br from-[#D4AF37] to-[#B38F22] flex items-center justify-center text-black shadow-lg shadow-[#D4AF37]/20 group-hover:scale-105 transition-transform">
              <Scissors className="w-6 h-6 stroke-[2.5]" />
            </div>
            <div>
              <h1 className="text-lg font-black tracking-wide text-white group-hover:text-[#D4AF37] transition-colors">
                SEU JOÃO BARBER
              </h1>
              <p className="text-xs font-semibold text-[#D4AF37] tracking-wider uppercase">
                Corte Moderno e Clássico
              </p>
            </div>
          </div>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-8">
            <button
              onClick={() => setActiveTab('home')}
              className={`text-sm font-semibold transition-colors ${
                activeTab === 'home' ? 'text-[#D4AF37]' : 'text-gray-300 hover:text-white'
              }`}
            >
              Início & Serviços
            </button>
            <button
              onClick={() => setActiveTab('my-appointments')}
              className={`text-sm font-semibold flex items-center space-x-2 transition-colors ${
                activeTab === 'my-appointments' ? 'text-[#D4AF37]' : 'text-gray-300 hover:text-white'
              }`}
            >
              <Calendar className="w-4 h-4" />
              <span>Meus Agendamentos</span>
            </button>
            <button
              onClick={() => setActiveTab('admin')}
              className={`text-sm font-semibold flex items-center space-x-2 transition-colors ${
                activeTab === 'admin' ? 'text-[#D4AF37]' : 'text-gray-400 hover:text-white'
              }`}
            >
              <Shield className="w-4 h-4" />
              <span>Painel Gestão</span>
            </button>
          </nav>

          {/* CTA Button */}
          <div className="hidden md:flex items-center space-x-4">
            <button
              onClick={onOpenBooking}
              className="px-5 py-2.5 rounded-xl font-bold text-sm bg-[#D4AF37] hover:bg-[#B38F22] text-black shadow-lg shadow-[#D4AF37]/25 hover:shadow-[#D4AF37]/40 transition-all active:scale-95"
            >
              Agendar Horário
            </button>
          </div>

          {/* Mobile Menu Button */}
          <div className="md:hidden flex items-center">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-gray-300 hover:text-white hover:bg-white/5"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#121316] border-b border-[#22242B] px-4 pt-3 pb-6 space-y-4">
          <button
            onClick={() => { setActiveTab('home'); setMobileMenuOpen(false); }}
            className="block w-full text-left py-2 text-base font-semibold text-white hover:text-[#D4AF37]"
          >
            Início & Serviços
          </button>
          <button
            onClick={() => { setActiveTab('my-appointments'); setMobileMenuOpen(false); }}
            className="block w-full text-left py-2 text-base font-semibold text-gray-300 hover:text-[#D4AF37]"
          >
            Meus Agendamentos
          </button>
          <button
            onClick={() => { setActiveTab('admin'); setMobileMenuOpen(false); }}
            className="block w-full text-left py-2 text-base font-semibold text-gray-400 hover:text-[#D4AF37]"
          >
            Painel Gestão
          </button>
          <button
            onClick={() => { onOpenBooking(); setMobileMenuOpen(false); }}
            className="w-full py-3 rounded-xl font-bold text-sm bg-[#D4AF37] text-black text-center"
          >
            Agendar Horário Agora
          </button>
        </div>
      )}
    </header>
  );
};
