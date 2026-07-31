import React from 'react';
import { Play } from 'lucide-react';

const MEDIA_ITEMS = [
  {
    id: 1,
    type: 'video',
    thumbnail: 'https://images.unsplash.com/photo-1593702275687-f8b402bf1fb5?auto=format&fit=crop&q=80&w=800',
    title: 'Corte Degradê Perfeito'
  },
  {
    id: 2,
    type: 'image',
    thumbnail: 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&q=80&w=800',
    title: 'Finalização Navalhada'
  },
  {
    id: 3,
    type: 'video',
    thumbnail: 'https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&q=80&w=800',
    title: 'Transformação VIP'
  },
  {
    id: 4,
    type: 'image',
    thumbnail: 'https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&q=80&w=800',
    title: 'Barboterapia'
  }
];

export const MediaSection: React.FC = () => {
  return (
    <section className="py-12 md:py-20 bg-[#060608] border-t border-b border-[#22242B]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <span className="text-xs font-bold text-[#D4AF37] uppercase tracking-wider">
            NOSSO TRABALHO
          </span>
          <h2 className="text-2xl sm:text-3xl font-black text-white mt-2">
            Mídia & Resultados
          </h2>
          <p className="text-sm text-gray-400 mt-3 max-w-2xl mx-auto">
            Confira um pouco do nosso dia a dia, cortes de destaque e o padrão de qualidade da Seu João Barber e Alemão.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {MEDIA_ITEMS.map((item) => (
            <div key={item.id} className="relative group rounded-2xl overflow-hidden aspect-[4/5] bg-[#121316] border border-[#22242B]">
              <img
                src={item.thumbnail}
                alt={item.title}
                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent flex flex-col justify-end p-4">
                {item.type === 'video' && (
                  <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-12 h-12 rounded-full bg-black/50 backdrop-blur-sm border border-[#D4AF37]/50 flex items-center justify-center text-[#D4AF37] group-hover:scale-110 group-hover:bg-[#D4AF37] group-hover:text-black transition-all">
                    <Play className="w-5 h-5 ml-1 fill-current" />
                  </div>
                )}
                <h3 className="text-white font-bold text-sm translate-y-2 group-hover:translate-y-0 transition-transform">
                  {item.title}
                </h3>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
