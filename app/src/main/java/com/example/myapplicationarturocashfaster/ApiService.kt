package com.example.myapplicationarturocashfaster

import kotlinx.coroutines.delay

object ApiService {

    // Simulación de API de pagos
    suspend fun processPayment(paymentData: PaymentData): PaymentResult {
        delay(2000) // Simular delay de red

        return if (paymentData.cardNumber.endsWith("1111")) {
            PaymentResult(false, "Tarjeta rechazada")
        } else {
            PaymentResult(true, "Pago procesado exitosamente")
        }
    }
}

data class PaymentData(
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val cardHolder: String,
    val amount: Double
)

data class PaymentResult(val success: Boolean, val message: String)