package com.example.util

import kotlin.math.max

/**
 * Brazilian Tax Regimes for Barbershops and Beauty Salons
 */
enum class BrazilianTaxRegime(
    val displayName: String,
    val defaultTaxPercent: Double,
    val description: String
) {
    MEI("MEI - Microempreendedor Individual", 0.0, "Taxa fixa mensal (DAS-MEI ~R$ 75,00/mês, 0% por serviço)"),
    SIMPLES_NACIONAL_6("Simples Nacional Anexo III (6%)", 6.0, "Alíquota inicial para salões parceiros e barbearias"),
    SIMPLES_NACIONAL_11("Simples Nacional Faixa 2 (11.2%)", 11.2, "Faturamento anual acima de R$ 180.000"),
    LUCRO_PRESUMIDO("Lucro Presumido (16.33%)", 16.33, "PIS/COFINS/ISS + IRPJ/CSLL para maiores empresas"),
    CPF_ISENTO("Pessoa Física / Sem Nota (0%)", 0.0, "Recebimento direto via PIX / Dinheiro em espécie")
}

/**
 * Payment Methods supported by Brazilian card machines (Stone, PagBank, Ton, Mercado Pago, Rede, Cielo)
 */
enum class PaymentMethodType(
    val displayName: String,
    val defaultRatePercent: Double
) {
    PIX("⚡ PIX", 0.0),
    DEBIT("💳 Cartão Débito", 1.49),
    CREDIT_1X("💳 Crédito à Vista (1x)", 3.19),
    CREDIT_PARCELED_2_6("💳 Crédito Parcelado (2x-6x)", 4.59),
    CREDIT_PARCELED_7_12("💳 Crédito Parcelado (7x-12x)", 10.99)
}

/**
 * Result data class produced by TaxCalculator
 */
data class TaxCalculationResult(
    val servicePrice: Double,
    val chargedToClient: Double,
    val machineFeePercent: Double,
    val machineFeeAmount: Double,
    val taxPercent: Double,
    val taxAmount: Double,
    val netEarnings: Double,
    val passFeeToClient: Boolean,
    val paymentMethodName: String,
    val taxRegimeName: String
)

/**
 * TaxCalculator Utility class for calculating net earnings, card machine fees,
 * and Brazilian tax obligations for barbershops and hair salons.
 */
object TaxCalculator {

    /**
     * Calculates the net earnings after subtracting card machine fees and tax obligations.
     *
     * @param servicePrice The desired base price for the service (e.g., R$ 80.00)
     * @param machineFeePercent The machine transaction fee percentage (e.g., 3.19%)
     * @param taxPercent The Brazilian tax rate percentage (e.g., 6.0% for Simples Nacional)
     * @param passFeeToClient If true, calculates the gross amount to charge on the machine so net earnings equals servicePrice
     * @param paymentMethodName Name of the selected payment method for logging/ui
     * @param taxRegimeName Name of the selected tax regime for logging/ui
     */
    fun calculateNetEarnings(
        servicePrice: Double,
        machineFeePercent: Double,
        taxPercent: Double,
        passFeeToClient: Boolean = false,
        paymentMethodName: String = "Cartão Crédito",
        taxRegimeName: String = "Simples Nacional (6%)"
    ): TaxCalculationResult {
        val safePrice = max(0.0, servicePrice)
        val safeMachineRate = max(0.0, machineFeePercent)
        val safeTaxRate = max(0.0, taxPercent)

        val chargedToClient: Double
        val machineFeeAmount: Double
        val taxAmount: Double
        val netEarnings: Double

        if (passFeeToClient) {
            val totalDeductionFraction = (safeMachineRate + safeTaxRate) / 100.0
            val divisor = if (totalDeductionFraction < 1.0) (1.0 - totalDeductionFraction) else 0.01
            chargedToClient = if (safePrice > 0) safePrice / divisor else 0.0
            machineFeeAmount = chargedToClient * (safeMachineRate / 100.0)
            taxAmount = chargedToClient * (safeTaxRate / 100.0)
            netEarnings = safePrice
        } else {
            chargedToClient = safePrice
            machineFeeAmount = chargedToClient * (safeMachineRate / 100.0)
            taxAmount = chargedToClient * (safeTaxRate / 100.0)
            netEarnings = max(0.0, chargedToClient - machineFeeAmount - taxAmount)
        }

        return TaxCalculationResult(
            servicePrice = safePrice,
            chargedToClient = chargedToClient,
            machineFeePercent = safeMachineRate,
            machineFeeAmount = machineFeeAmount,
            taxPercent = safeTaxRate,
            taxAmount = taxAmount,
            netEarnings = netEarnings,
            passFeeToClient = passFeeToClient,
            paymentMethodName = paymentMethodName,
            taxRegimeName = taxRegimeName
        )
    }

    /**
     * Formats a double currency value into standard Brazilian Real string (R$ 1.234,56).
     */
    fun formatCurrencyBrl(value: Double): String {
        return String.format("R$ %.2f", value).replace(".", ",")
    }
}
