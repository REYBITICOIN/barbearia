/**
 * WhatsApp Meta Cloud API Integration & Direct wa.me Fallback Link Utility
 */

export interface SendWhatsAppParams {
  clientPhone: string;
  clientName: string;
  serviceName: string;
  dateStr: string;
  timeStr: string;
  barberName: string;
  shopName?: string;
}

export const sendWhatsAppConfirmation = async (params: SendWhatsAppParams): Promise<{ success: boolean; message: string; waLink: string }> => {
  const {
    clientPhone,
    clientName,
    dateStr,
    timeStr,
    barberName,
    shopName = 'Barbearia do João'
  } = params;

  const cleanPhone = sanitizePhone(clientPhone);
  const messageText = `Olá ${clientName}! Seu agendamento na ${shopName} foi confirmado para ${dateStr} às ${timeStr} com ${barberName}. Te esperamos!`;
  const encodedText = encodeURIComponent(messageText);
  const waLink = `https://wa.me/${cleanPhone}?text=${encodedText}`;

  // Check for Meta Cloud API environment keys
  const phoneId = import.meta.env.VITE_WHATSAPP_PHONE_ID || import.meta.env.WHATSAPP_PHONE_ID;
  const accessToken = import.meta.env.VITE_WHATSAPP_ACCESS_TOKEN || import.meta.env.WHATSAPP_ACCESS_TOKEN;

  if (phoneId && accessToken && !accessToken.includes('sample')) {
    try {
      const response = await fetch(`https://graph.facebook.com/v18.0/${phoneId}/messages`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          messaging_product: 'whatsapp',
          recipient_type: 'individual',
          to: cleanPhone,
          type: 'text',
          text: { body: messageText }
        })
      });

      if (response.ok) {
        return { success: true, message: 'Notificação enviada via WhatsApp Meta Cloud API!', waLink };
      }
    } catch (e) {
      console.warn('Meta API error, falling back to direct link', e);
    }
  }

  // Fallback to client link execution
  return {
    success: true,
    message: 'Agendamento confirmado! Notificação WhatsApp gerada.',
    waLink
  };
};

export const sanitizePhone = (phone: string): string => {
  const digits = phone.replace(/[^0-9]/g, '');
  if (!digits.startsWith('55') && (digits.length === 10 || digits.length === 11)) {
    return `55${digits}`;
  }
  return digits;
};
