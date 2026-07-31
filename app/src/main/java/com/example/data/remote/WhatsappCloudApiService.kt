package com.example.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

object WhatsappCloudApiService {

    private const val TAG = "WhatsappCloudApi"
    
    val phoneId: String = try {
        BuildConfig.WHATSAPP_PHONE_ID
    } catch (_: Throwable) {
        "109876543210987"
    }

    val businessAccountId: String = try {
        BuildConfig.WHATSAPP_BUSINESS_ACCOUNT_ID
    } catch (_: Throwable) {
        "987654321098765"
    }

    val accessToken: String = try {
        BuildConfig.WHATSAPP_ACCESS_TOKEN
    } catch (_: Throwable) {
        "EAAG_sample_whatsapp_cloud_api_token"
    }

    val appDomainUrl: String = try {
        BuildConfig.APP_DOMAIN_URL
    } catch (_: Throwable) {
        "https://barbeariadojoao.com.br"
    }

    private val okHttpClient = OkHttpClient()

    /**
     * Sends confirmation message via Meta WhatsApp Cloud API (Graph API v18.0)
     * Format:
     * "Olá! Seu agendamento na Barbearia do João foi confirmado para [DATA] às [HORÁRIO] com [BARBEIRO]."
     */
    suspend fun sendBookingConfirmationWhatsApp(
        clientPhone: String,
        clientName: String,
        date: String,
        time: String,
        barberName: String,
        shopName: String = "Barbearia do João"
    ): Result<String> = withContext(Dispatchers.IO) {
        val formattedPhone = sanitizePhoneNumber(clientPhone)
        val messageText = "Olá $clientName! Seu agendamento na $shopName foi confirmado para $date às $time com $barberName. Te esperamos!"

        try {
            if (accessToken.isNotBlank() && !accessToken.startsWith("EAAG_sample")) {
                val url = "https://graph.facebook.com/v18.0/$phoneId/messages"

                val payloadJson = JSONObject().apply {
                    put("messaging_product", "whatsapp")
                    put("recipient_type", "individual")
                    put("to", formattedPhone)
                    put("type", "text")
                    put("text", JSONObject().put("body", messageText))
                }

                val body = payloadJson.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    Log.d(TAG, "WhatsApp Cloud API Sent Successfully: $responseStr")
                    return@withContext Result.success("Mensagem enviada com sucesso pelo WhatsApp Cloud API da Meta!")
                } else {
                    Log.e(TAG, "WhatsApp Cloud API Error: ${response.code} $responseStr")
                    return@withContext Result.failure(Exception("Erro na Meta Cloud API (${response.code}): $responseStr"))
                }
            } else {
                // Return success simulation for demo / prototype mode with wa.me URL
                Log.d(TAG, "WhatsApp Cloud API in Simulation Mode (Valid token required in .env)")
                val waLink = generateWhatsAppWebUrl(clientPhone, messageText)
                return@withContext Result.success("Agendamento confirmado! Link WhatsApp gerado: $waLink")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending WhatsApp message", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Generates wa.me or whatsapp:// URL with pre-filled confirmation text
     */
    fun generateWhatsAppWebUrl(phone: String, customMessage: String): String {
        val cleanNumber = sanitizePhoneNumber(phone)
        val encodedMessage = try {
            URLEncoder.encode(customMessage, "UTF-8")
        } catch (_: Exception) {
            customMessage
        }
        return "https://wa.me/$cleanNumber?text=$encodedMessage"
    }

    /**
     * Opens native WhatsApp intent in Android for direct client messaging
     */
    fun openWhatsAppDirectIntent(context: Context, phone: String, message: String) {
        try {
            val url = generateWhatsAppWebUrl(phone, message)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch WhatsApp Intent", e)
        }
    }

    private fun sanitizePhoneNumber(phone: String): String {
        val digits = phone.replace(Regex("[^0-9]"), "")
        return if (!digits.startsWith("55") && digits.length in 10..11) {
            "55$digits"
        } else {
            digits
        }
    }
}
