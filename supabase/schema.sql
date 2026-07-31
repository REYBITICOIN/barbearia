-- ====================================================================
-- BARBEARIA DO JOÃO - BANCO DE DADOS SUPABASE / POSTGRESQL (REAL-TIME)
-- DOMÍNIO: barbearia.art.br
-- ====================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabela Multi-Tenant de Barbearias
CREATE TABLE IF NOT EXISTS barbershops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    slogan VARCHAR(255) DEFAULT 'Corte Moderno e Clássico',
    description TEXT,
    owner_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    primary_color_hex VARCHAR(20) DEFAULT '#D4AF37',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Tabela de Serviços
CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_id VARCHAR(100) UNIQUE NOT NULL,
    tenant_id VARCHAR(100) REFERENCES barbershops(tenant_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Tabela de Barbeiros
CREATE TABLE IF NOT EXISTS barbers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    barber_id VARCHAR(100) UNIQUE NOT NULL,
    tenant_id VARCHAR(100) REFERENCES barbershops(tenant_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    specialty VARCHAR(255) DEFAULT 'Master Barber',
    photo_url TEXT,
    rating FLOAT DEFAULT 5.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Tabela de Agendamentos (Appointments)
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    appointment_id VARCHAR(100) UNIQUE NOT NULL,
    tenant_id VARCHAR(100) REFERENCES barbershops(tenant_id) ON DELETE CASCADE,
    client_name VARCHAR(255) NOT NULL,
    client_phone VARCHAR(50) NOT NULL,
    service_name VARCHAR(255) NOT NULL,
    barber_name VARCHAR(255) NOT NULL,
    date_time_str VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'Agendado',
    created_by_ai BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Seed Data: Barbearia do João (Tenant Principal)
INSERT INTO barbershops (tenant_id, name, slogan, description, owner_name, phone, address, primary_color_hex)
VALUES (
    'tenant_barberlab_master',
    'Barbearia do João',
    'Corte Moderno e Clássico',
    'Barbearia especializada em cortes modernos, degradês e cortes clássicos tradicionais.',
    'João Silva',
    '(11) 98877-6655',
    'Av. Paulista, 1500 - São Paulo/SP',
    '#D4AF37'
) ON CONFLICT (tenant_id) DO NOTHING;

-- Seed Data: Serviços Principais
INSERT INTO services (service_id, tenant_id, name, price, duration_minutes, category, description)
VALUES 
    ('srv_1', 'tenant_barberlab_master', 'Corte Masculino Premium', 55.00, 35, 'Cabelo', 'Corte moderno com lavagem e alinhamento de fios.'),
    ('srv_2', 'tenant_barberlab_master', 'Barba de Respeito com Toalha Quente', 45.00, 30, 'Barba', 'Barboterapia completa com óleos essenciais e toalha quente.'),
    ('srv_3', 'tenant_barberlab_master', 'Pezinho & Acabamento Navalhado', 25.00, 15, 'Acabamento', 'Contorno perfeito de pezinho e barba alinhados na navalha.'),
    ('srv_4', 'tenant_barberlab_master', 'Massagem Capilar & Facial', 40.00, 20, 'Massagem', 'Massagem relaxante couro cabeludo com óleos essenciais.'),
    ('srv_5', 'tenant_barberlab_master', 'Design de Sobrancelha na Navalha', 20.00, 15, 'Sobrancelha', 'Alinhamento preciso de sobrancelhas masculinas.'),
    ('srv_6', 'tenant_barberlab_master', 'Hidratação & Cauterização Capilar', 50.00, 25, 'Hidratação', 'Tratamento profundo para brilho e maciez dos fios.'),
    ('srv_7', 'tenant_barberlab_master', 'Combo BarberLab (Cabelo + Barba)', 90.00, 60, 'Combo', 'O pacote VIP completo da barbearia com acabamento impecável.')
ON CONFLICT (service_id) DO NOTHING;

-- Seed Data: Barbeiro Principal
INSERT INTO barbers (barber_id, tenant_id, name, specialty, rating, is_active)
VALUES 
    ('barb_1', 'tenant_barberlab_master', 'João Silva', 'Master Barber & Fundador', 5.0, true),
    ('barb_2', 'tenant_barberlab_master', 'Lucas "Alemão"', 'Especialista em Fade & Degradê', 4.9, true)
ON CONFLICT (barber_id) DO NOTHING;
