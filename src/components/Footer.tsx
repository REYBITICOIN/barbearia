import React from 'react';
import { Scissors, Phone, MapPin, Globe } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-[#060608] border-t border-[#22242B] py-12 text-gray-400 text-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          
          {/* Brand */}
          <div className="space-y-3">
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 rounded-lg bg-[#D4AF37] text-black flex items-center justify-center font-bold">
                <Scissors className="w-4 h-4" />
              </div>
              <span className="font-black text-sm text-white tracking-wide">
                SEU JOÃO BARBER
              </span>
            </div>
            <p className="text-gray-400">
              Corte Moderno e Clássico • Especializada em cortes modernos, degradês e cortes clássicos tradicionais.
            </p>
          </div>

          {/* Contact */}
          <div className="space-y-2">
            <h4 className="font-bold text-white text-sm">Contato</h4>
            <div className="flex items-center space-x-2">
              <Phone className="w-4 h-4 text-[#D4AF37]" />
              <span>(11) 98877-6655</span>
            </div>
            <div className="flex items-center space-x-2">
              <MapPin className="w-4 h-4 text-[#D4AF37]" />
              <span>Av. Paulista, 1500 - São Paulo/SP</span>
            </div>
          </div>

          {/* Domain */}
          <div className="space-y-2">
            <h4 className="font-bold text-white text-sm">Domínio Web</h4>
            <div className="flex items-center space-x-2">
              <Globe className="w-4 h-4 text-[#D4AF37]" />
              <a href="https://barbearia.art.br" target="_blank" rel="noopener noreferrer" className="hover:text-white underline">
                barbearia.art.br
              </a>
            </div>
            <p className="text-[11px] text-gray-500">Pronto para deploy na Vercel e PWA</p>
          </div>

          {/* Copyright */}
          <div className="space-y-2">
            <h4 className="font-bold text-white text-sm">Sistema SaaS</h4>
            <p>© {new Date().getFullYear()} Seu João Barber e Alemão. Todos os direitos reservados.</p>
          </div>

        </div>
      </div>
    </footer>
  );
};
