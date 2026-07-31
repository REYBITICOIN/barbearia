import React, { useState } from 'react';
import { Service } from '../types';
import { Scissors, UserCheck, Sparkles, Flame, Eye, Droplet, Crown, Check } from 'lucide-react';

interface ServicesSectionProps {
  services: Service[];
  onSelectService: (service: Service) => void;
}

const CATEGORY_ICONS: Record<string, React.ReactNode> = {
  'Cabelo': <Scissors className="w-5 h-5 text-[#D4AF37]" />,
  'Barba': <UserCheck className="w-5 h-5 text-[#D4AF37]" />,
  'Acabamento': <Sparkles className="w-5 h-5 text-[#D4AF37]" />,
  'Massagem': <Flame className="w-5 h-5 text-[#D4AF37]" />,
  'Sobrancelha': <Eye className="w-5 h-5 text-[#D4AF37]" />,
  'Hidratação': <Droplet className="w-5 h-5 text-[#D4AF37]" />,
  'Combo': <Crown className="w-5 h-5 text-[#D4AF37]" />
};

export const ServicesSection: React.FC<ServicesSectionProps> = ({ services, onSelectService }) => {
  const [selectedCategory, setSelectedCategory] = useState<string>('Todos');

  const categories = ['Todos', 'Cabelo', 'Barba', 'Acabamento', 'Massagem', 'Sobrancelha', 'Hidratação', 'Combo'];

  const filteredServices = selectedCategory === 'Todos' 
    ? services 
    : services.filter(s => s.category.toLowerCase() === selectedCategory.toLowerCase());

  return (
    <section className="py-12 md:py-20 bg-[#0A0A0C]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Title */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-8">
          <div>
            <span className="text-xs font-bold text-[#D4AF37] uppercase tracking-wider">
              CATÁLOGO DE SERVIÇOS
            </span>
            <h2 className="text-2xl sm:text-3xl font-black text-white mt-1">
              Serviços Populares & Cuidados
            </h2>
          </div>
          <p className="text-sm text-gray-400 mt-2 md:mt-0 max-w-md">
            Escolha um dos nossos serviços de alta precisão e garanta seu horário exclusivo.
          </p>
        </div>

        {/* Category Filters */}
        <div className="flex items-center space-x-2 overflow-x-auto pb-4 scrollbar-none mb-8">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all border ${
                selectedCategory === cat
                  ? 'bg-[#D4AF37] text-black border-[#D4AF37] shadow-lg shadow-[#D4AF37]/20'
                  : 'bg-[#121316] text-gray-300 border-[#22242B] hover:border-[#D4AF37]/50'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Services Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredServices.map((service) => (
            <div
              key={service.id}
              className="group relative rounded-2xl bg-[#121316] border border-[#22242B] p-6 hover:border-[#D4AF37]/50 transition-all hover:shadow-xl hover:shadow-[#D4AF37]/10 flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="w-10 h-10 rounded-xl bg-[#0A0A0C] border border-[#22242B] flex items-center justify-center">
                    {CATEGORY_ICONS[service.category] || <Scissors className="w-5 h-5 text-[#D4AF37]" />}
                  </div>
                  <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-[#0A0A0C] border border-[#22242B] text-gray-400">
                    {service.durationMinutes} min
                  </span>
                </div>

                <h3 className="text-lg font-bold text-white group-hover:text-[#D4AF37] transition-colors">
                  {service.name}
                </h3>
                <p className="text-xs text-gray-400 mt-2 line-clamp-2 leading-relaxed">
                  {service.description}
                </p>
              </div>

              <div className="mt-6 pt-4 border-t border-[#22242B] flex items-center justify-between">
                <div>
                  <span className="text-xs text-gray-500 block">Valor</span>
                  <span className="text-xl font-black text-[#D4AF37]">
                    R$ {service.price.toFixed(2).replace('.', ',')}
                  </span>
                </div>

                <button
                  onClick={() => onSelectService(service)}
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-[#D4AF37]/15 hover:bg-[#D4AF37] text-[#D4AF37] hover:text-black border border-[#D4AF37]/30 transition-all flex items-center space-x-1.5"
                >
                  <Check className="w-4 h-4" />
                  <span>Agendar</span>
                </button>
              </div>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
