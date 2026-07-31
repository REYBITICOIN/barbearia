import React from 'react';
import { Barber } from '../types';
import { Star } from 'lucide-react';

interface TeamSectionProps {
  barbers: Barber[];
}

export const TeamSection: React.FC<TeamSectionProps> = ({ barbers }) => {
  return (
    <section className="py-12 md:py-20 bg-[#0A0A0C]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-8">
          <div>
            <span className="text-xs font-bold text-[#D4AF37] uppercase tracking-wider">
              NOSSO TIME
            </span>
            <h2 className="text-2xl sm:text-3xl font-black text-white mt-1">
              Profissionais de Excelência
            </h2>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {barbers.map((barber) => (
            <div key={barber.id} className="group relative rounded-2xl bg-[#121316] border border-[#22242B] overflow-hidden hover:border-[#D4AF37]/50 transition-all">
              {barber.photoUrl && (
                <div className="h-64 w-full overflow-hidden">
                  <img
                    src={barber.photoUrl}
                    alt={barber.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                </div>
              )}
              <div className="p-6">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-xl font-bold text-white group-hover:text-[#D4AF37] transition-colors">
                    {barber.name}
                  </h3>
                  <div className="flex items-center space-x-1 bg-[#0A0A0C] px-2 py-1 rounded-lg border border-[#22242B]">
                    <Star className="w-3.5 h-3.5 text-[#D4AF37] fill-[#D4AF37]" />
                    <span className="text-xs font-bold text-white">{barber.rating.toFixed(1)}</span>
                  </div>
                </div>
                <p className="text-sm text-gray-400">{barber.specialty}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
