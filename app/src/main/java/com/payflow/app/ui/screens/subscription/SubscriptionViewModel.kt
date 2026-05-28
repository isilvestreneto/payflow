package com.payflow.app.ui.screens.subscription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payflow.app.data.local.entity.SubscriptionEntity
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.local.repository.AuthRepository
import com.payflow.app.domain.model.PaymentMethod
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import com.payflow.app.domain.model.SubscriptionType
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var editingId by mutableStateOf<String?>(null)
    var nomeServico by mutableStateOf("")
    var valorMensal by mutableStateOf("")
    var dataMillis by mutableStateOf<Long?>(System.currentTimeMillis())
    var formaPagamento by mutableStateOf(PaymentMethod.CREDIT_CARD)
    var categoria by mutableStateOf(SubscriptionType.STREAMING)
    var status by mutableStateOf(SubscriptionStatus.ACTIVE)
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
            (limpo.toDouble() * 100).roundToLong()
        } catch (_: Exception) {
            0L
        }
    }

    fun loadSubscription(subscription: Subscription) {
        editingId = subscription.id
        nomeServico = subscription.name
        valorMensal = String.format("%.2f", subscription.value).replace(".", ",")
        dataMillis = subscription.startDate
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        formaPagamento = subscription.paymentMethod
        categoria = subscription.type
        status = subscription.status
        exibirDatePicker = false
    }

    fun resetForm() {
        editingId = null
        nomeServico = ""
        valorMensal = ""
        dataMillis = System.currentTimeMillis()
        formaPagamento = PaymentMethod.CREDIT_CARD
        categoria = SubscriptionType.STREAMING
        status = SubscriptionStatus.ACTIVE
        exibirDatePicker = false
    }

    fun saveSubscription(onSuccess: () -> Unit) {
        if (!isFormValid) return

        viewModelScope.launch {
            val user = authRepository.usuarioLogado()
            val usuarioLogadoId = user?.id ?: return@launch

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val entity = if (editingId == null) {
                SubscriptionEntity(
                    nome = nomeServico,
                    valorCentavos = parseMoedaParaCentavos(valorMensal),
                    status = status,
                    dataCobrancaMillis = dataMillis ?: System.currentTimeMillis(),
                    formaPagamento = formaPagamento.displayName,
                    categoria = categoria.displayName,
                    usuarioId = usuarioLogadoId,
                    dataCriacao = timestamp,
                    dataAtualizacao = timestamp
                )
            } else {
                SubscriptionEntity(
                    id = editingId!!,
                    nome = nomeServico,
                    valorCentavos = parseMoedaParaCentavos(valorMensal),
                    status = status,
                    dataCobrancaMillis = dataMillis ?: System.currentTimeMillis(),
                    formaPagamento = formaPagamento.displayName,
                    categoria = categoria.displayName,
                    usuarioId = usuarioLogadoId,
                    dataCriacao = timestamp,
                    dataAtualizacao = timestamp
                )
            }

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