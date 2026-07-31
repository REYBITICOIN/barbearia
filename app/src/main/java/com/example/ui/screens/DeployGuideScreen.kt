package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BarberLabViewModel
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor

@Composable
fun DeployGuideScreen(viewModel: BarberLabViewModel) {
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    val sqlSchemaScript = """
-- ====================================================================
-- BARBERLAB AI SAAS - SCRIPT DE MIGRAÇÃO BANCO DE DADOS (SUPABASE / POSTGRES / SQLITE)
-- ====================================================================

-- 1. Tabela Multi-Tenant de Barbearias
CREATE TABLE IF NOT EXISTS barbershops (
    tenant_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    facade_photo_url TEXT,
    owner_photo_url TEXT,
    primary_color_hex VARCHAR(20) DEFAULT '#D4AF37',
    secondary_color_hex VARCHAR(20) DEFAULT '#121316',
    logo_url TEXT,
    created_at BIGINT DEFAULT EXTRACT(EPOCH FROM NOW())
);

-- 2. Tabela de Agendamentos por Tenant
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id VARCHAR(100) PRIMARY KEY,
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
    timestamp BIGINT
);

-- 3. Tabela de Configurações dos Agentes de IA (NVIDIA NIM / Gemini)
CREATE TABLE IF NOT EXISTS ai_agent_configs (
    tenant_id VARCHAR(100) PRIMARY KEY REFERENCES barbershops(tenant_id),
    agent_name VARCHAR(255) DEFAULT 'Agente Atendente IA',
    temperature FLOAT DEFAULT 0.2, -- Temperatura estrita para agendamentos
    whatsapp_connected BOOLEAN DEFAULT TRUE,
    auto_scheduling_enabled BOOLEAN DEFAULT TRUE,
    system_prompt TEXT
);

-- 4. Tabela de Logs de Campanhas de Marketing
CREATE TABLE IF NOT EXISTS marketing_logs (
    log_id VARCHAR(100) PRIMARY KEY,
    tenant_id VARCHAR(100) REFERENCES barbershops(tenant_id),
    channel VARCHAR(100) NOT NULL, -- Google Business, Meta API, YouTube Shorts
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    media_url TEXT,
    status VARCHAR(50) DEFAULT 'Publicado',
    timestamp BIGINT
);
    """.trimIndent()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "ETAPA 4 & DEPLOY GRATUITO",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hospedagem & Execução Local",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Instruções completas para deploy em Render/Vercel/Supabase e testes locais no PC.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Environment Variables Status
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🔑 VARIÁVEIS DE AMBIENTE (.env.example)", fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    EnvKeyStatusRow("NVIDIA_NIM_API_KEY", "Chave da API NVIDIA (Llama 3.1 / NeVA com Temp 0.2)")
                    EnvKeyStatusRow("DATABASE_URL", "URL PostgreSQL Supabase / SQLite para multi-tenant")
                    EnvKeyStatusRow("META_API_KEY", "Token de acesso Graph API Meta (FB / Instagram)")
                    EnvKeyStatusRow("GOOGLE_BUSINESS_API_KEY", "Chave API Google Meu Negócio")
                    EnvKeyStatusRow("GEMINI_API_KEY", "Chave Gemini AI Studio para marketing e chat")
                }
            }
        }

        // Local Execution Guide
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "💻 COMO RODAR O PROJETO LOCALMENTE NO PC", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    StepRow("1", "Clone o repositório: git clone https://github.com/LuanPaD/BarberLab.git")
                    StepRow("2", "Abra o projeto no Android Studio (Hedgehog ou mais recente).")
                    StepRow("3", "Copie o arquivo .env.example para .env e preencha suas chaves de API.")
                    StepRow("4", "Aguarde a sincronização do Gradle (Room, KSP e Compose são configurados automaticamente).")
                    StepRow("5", "Execute no Emulador Android ou Dispositivo Físico via USB (Shift + F10).")
                }
            }
        }

        // SQL Migration Script
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1015)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🗄️ SCRIPT SQL DE MIGRAÇÃO", fontWeight = FontWeight.Bold, color = BarberAiCyan)
                        Surface(color = BarberAiCyan.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                            Text("PostgreSQL / Supabase", color = BarberAiCyan, fontSize = 10.sp, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sqlSchemaScript,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF80CBC4),
                            modifier = Modifier
                                .padding(12.dp)
                                .testTag("sql_script_text")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnvKeyStatusRow(keyName: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = keyName, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            Text(text = description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StepRow(stepNumber: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = BarberGold,
            shape = CircleShape,
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = stepNumber, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}
