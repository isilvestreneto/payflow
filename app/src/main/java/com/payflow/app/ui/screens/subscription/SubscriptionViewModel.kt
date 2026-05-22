package com.payflow.app.ui.screens.subscription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.data.local.entity.SubscriptionEntity
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.local.repository.UserRepository
import com.payflow.app.domain.model.PaymentMethod
import com.payflow.app.domain.model.SubscriptionType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var editingId by mutableStateOf<Int?>(null)
    var nomeServico by mutableStateOf("")
    var valorMensal by mutableStateOf("")
    var dataMillis by mutableStateOf<Long?>(System.currentTimeMillis())
    var formaPagamento by mutableStateOf(PaymentMethod.CREDIT_CARD)
    var categoria by mutableStateOf(SubscriptionType.STREAMING)
    var ativo by mutableStateOf(true)
    var exibirDatePicker by mutableStateOf(false)

    val isFormValid: Boolean
        get() = nomeServico.isNotBlank() && parseMoedaParaCentavos(valorMensal) > 0 && dataMillis != null

    private fun parseMoedaParaCentavos(valor: String): Long {
        return try {
            val limpo = valor.replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
            if (limpo.isEmpty()) return 0L
            Math.round(limpo.toDouble() * 100)
        } catch (e: Exception) {
            0L
        }
    }

    fun resetForm() {
        editingId = null
        nomeServico = ""
        valorMensal = ""
        dataMillis = System.currentTimeMillis()
        formaPagamento = PaymentMethod.CREDIT_CARD
        categoria = SubscriptionType.STREAMING
        ativo = true
        exibirDatePicker = false
    }

    fun saveSubscription(onSuccess: () -> Unit) {
        if (!isFormValid) return

        viewModelScope.launch {
            userRepository.ensureDefaultUser()
            
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            
            val entity = SubscriptionEntity(
                id = editingId ?: 0, // id 0 indica auto-incremento para o Room
                nome = nomeServico,
                valorCentavos = parseMoedaParaCentavos(valorMensal),
                status = ativo,
                dataCobrancaMillis = dataMillis ?: System.currentTimeMillis(),
                formaPagamento = formaPagamento.displayName,
                categoria = categoria.displayName,
                usuarioId = "user_default",
                dataCriacao = timestamp,
                dataAtualizacao = timestamp
            )

            if (editingId == null) {
                subscriptionRepository.insertSubscription(entity)
            } else {
                subscriptionRepository.updateSubscription(entity)
            }
            
            resetForm()
            onSuccess()
        }
    }
}
