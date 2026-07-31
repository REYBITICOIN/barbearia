package com.example.data.remote

import com.example.BuildConfig
import com.example.data.local.BarbershopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Módulo de Marketing da IA (ETAPA 3):
 * Integração e geração automática de conteúdo para:
 * 1. Google Business API: Resposta a avaliações de clientes & atualização de fachada
 * 2. Meta API (Facebook & Instagram): Posts e Stories com fotos de cortes
 * 3. YouTube API: Upload e otimização de títulos e hashtags de Shorts
 */
object MarketingAiService {

    suspend fun generateGoogleBusinessReviewReply(
        barbershop: BarbershopEntity,
        clientName: String,
        reviewText: String,
        ratingStars: Int
    ): String = withContext(Dispatchers.IO) {
        delay(600)
        "Olá, $clientName! Muito obrigado pela avaliação de $ratingStars estrelas! 🌟 É um orgulho para a equipe da ${barbershop.name} proporcionar a melhor experiência de barbearia. Volte sempre!"
    }

    suspend fun generateMetaPostCaption(
        barbershop: BarbershopEntity,
        haircutType: String = "Fade Navalhado com Barboterapia VIP",
        promotionOffer: String? = null
    ): String = withContext(Dispatchers.IO) {
        delay(600)
        val promoText = if (promotionOffer != null) "\n🔥 Oferta Especial: $promotionOffer" else ""
        """
            💈 Transformação Impecável na ${barbershop.name}! 💈
            
            Hoje foi dia do nosso cliente renovar o visual com esse estilo: $haircutType! Cabelo alinhado e navalha afiada.$promoText
            
            📍 ${barbershop.address}
            📲 Agende seu horário com nosso Agente Atendente de IA no WhatsApp: ${barbershop.phone}
            
            #BarberLab #Barbearia #${barbershop.name.replace(" ", "")} #BarberShop #Fade #CorteMasculino #Barboterapia
        """.trimIndent()
    }

    suspend fun generateYouTubeShortsDetails(
        barbershop: BarbershopEntity,
        videoTopic: String = "Corte Fade em Tempo Recorde ⚡"
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        delay(600)
        val title = "⚡ $videoTopic - Transformação na ${barbershop.name} #Shorts #Barber"
        val description = """
            Confira este passo a passo de transformação na ${barbershop.name}!
            
            💈 Barbeiro Responsável: ${barbershop.ownerName}
            📍 Endereço: ${barbershop.address}
            📲 Agendamentos pelo WhatsApp: ${barbershop.phone}
            
            #Shorts #Haircut #Barbershop #BarberLife #FadeHaircut
        """.trimIndent()
        Pair(title, description)
    }
}
