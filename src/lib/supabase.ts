import { createClient } from '@supabase/supabase-js';

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL || 'https://xyzcompany.supabase.co';
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummy_key';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);

export const DEFAULT_BARBERSHOP = {
  name: 'Seu João Barber e Alemão',
  slogan: 'Corte Moderno e Clássico',
  description: 'Barbearia especializada em cortes modernos, degradês e cortes clássicos tradicionais.',
  phone: '(11) 98877-6655',
  address: 'Av. Paulista, 1500 - São Paulo/SP',
  domainUrl: 'https://barbearia.art.br'
};
