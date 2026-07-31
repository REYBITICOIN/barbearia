import React from 'react';
import { Appointment, Service, Barber } from '../types';
import { Users, DollarSign, Calendar, ShieldCheck, Database, Smartphone } from 'lucide-react';

interface AdminDashboardProps {
  appointments: Appointment[];
  services: Service[];
  barbers: Barber[];
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({ appointments, services, barbers }) => {
  const totalRevenue = appointments.reduce((sum, a) => sum + a.price, 0);

  return (
    <div className="py-12 bg-[#0A0A0C]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        
        {/* Title */}
        <div>
          <span className="text-xs font-bold text-[#D4AF37] uppercase tracking-wider">
            SISTEMA SAAS DE GESTÃO
          </span>
          <h2 className="text-2xl font-black text-white mt-1">
            Painel da Barbearia do João
          </h2>
          <p className="text-xs text-gray-400">Visão geral do estabelecimento, serviços cadastrados e notificações WhatsApp Meta Cloud API</p>
        </div>

        {/* Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="p-5 rounded-2xl bg-[#121316] border border-[#22242B]">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs text-gray-400 font-semibold">Faturamento Total</span>
              <DollarSign className="w-5 h-5 text-[#D4AF37]" />
            </div>
            <p className="text-2xl font-black text-white">R$ {totalRevenue.toFixed(2).replace('.', ',')}</p>
          </div>

          <div className="p-5 rounded-2xl bg-[#121316] border border-[#22242B]">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs text-gray-400 font-semibold">Agendamentos</span>
              <Calendar className="w-5 h-5 text-[#D4AF37]" />
            </div>
            <p className="text-2xl font-black text-white">{appointments.length}</p>
          </div>

          <div className="p-5 rounded-2xl bg-[#121316] border border-[#22242B]">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs text-gray-400 font-semibold">Serviços Ativos</span>
              <Users className="w-5 h-5 text-[#D4AF37]" />
            </div>
            <p className="text-2xl font-black text-white">{services.length}</p>
          </div>

          <div className="p-5 rounded-2xl bg-[#121316] border border-[#22242B]">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs text-gray-400 font-semibold">WhatsApp Cloud API</span>
              <Smartphone className="w-5 h-5 text-emerald-400" />
            </div>
            <p className="text-xs font-bold text-emerald-400">ATIVO & CONECTADO</p>
          </div>
        </div>

        {/* Database & Vercel Info Box */}
        <div className="p-6 rounded-2xl bg-[#121316] border border-[#22242B] space-y-3">
          <h3 className="font-bold text-sm text-white flex items-center space-x-2">
            <Database className="w-4 h-4 text-[#D4AF37]" />
            <span>Configurações para Deploy na Vercel (barbearia.art.br)</span>
          </h3>
          <p className="text-xs text-gray-400 leading-relaxed">
            Este projeto contém a estrutura pronta para deploy na Vercel integrada com banco de dados Supabase e Meta WhatsApp API.
          </p>
          <div className="p-3 rounded-xl bg-[#0A0A0C] border border-[#22242B] font-mono text-xs text-gray-300 overflow-x-auto space-y-1">
            <p><span className="text-[#D4AF37]">VITE_SUPABASE_URL</span>=https://sua-instancia.supabase.co</p>
            <p><span className="text-[#D4AF37]">VITE_SUPABASE_ANON_KEY</span>=eyJhbGciOiJIUzI1NiIsInR... </p>
            <p><span className="text-[#D4AF37]">VITE_WHATSAPP_PHONE_ID</span>=109876543210987</p>
            <p><span className="text-[#D4AF37]">VITE_WHATSAPP_ACCESS_TOKEN</span>=EAAG_token_meta...</p>
          </div>
        </div>

      </div>
    </div>
  );
};
