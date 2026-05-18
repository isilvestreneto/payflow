# 📱 Tela Home - PayFlow

## ✨ O que foi criado

Implementei a tela **Home (Dashboard)** do aplicativo PayFlow com design inspirado em **Material You (Material 3)** do Google. A tela exibe métricas importantes sobre as assinaturas do usuário e permite navegação para telas de detalhe.

## 🎨 Estrutura criada

### **1. Modelos de Domínio** (`domain/model/`)

- ✅ `Subscription.kt` - Modelo principal de assinatura
- ✅ `SubscriptionStatus.kt` - Enum: ACTIVE, CANCELED, PAUSED
- ✅ `SubscriptionType.kt` - Enum: STREAMING, SERVICOS, TELEFONIA, BANCOS, IA, OUTROS
- ✅ `PaymentMethod.kt` - Enum: CREDIT_CARD, PIX, BOLETO, DEBIT
- ✅ `HomeSummary.kt` - Modelo de resumo para o dashboard

### **2. Use Cases** (`domain/usecase/`)

- ✅ `GetSubscriptionsUseCase.kt` - Busca lista de assinaturas (mock temporário)
- ✅ `GetHomeSummaryUseCase.kt` - Calcula todas as métricas do dashboard:
  - Quantidade de assinaturas ativas
  - Gasto mensal total
  - Valor médio por assinatura
  - Assinatura mais cara
  - Assinatura mais barata
  - Assinatura mais usada

### **3. Tela Home** (`ui/screens/home/`)

- ✅ `HomeScreen.kt` - Tela principal com cards interativos
- ✅ `HomeViewModel.kt` - Gerencia estado e lógica de negócio
- ✅ `HomeUiState.kt` - Estados: Loading, Success, Error
- ✅ `components/SummaryCard.kt` - Card padrão para métricas
- ✅ `components/HighlightCard.kt` - Card destacado para gasto mensal

### **4. Telas de Detalhe** (`ui/screens/detail/`)

Cada métrica tem sua própria tela de detalhe:

- ✅ `ActiveSubscriptionsScreen.kt` - Mostra quantidade de assinaturas ativas
- ✅ `MonthlySpendingScreen.kt` - Detalha o gasto mensal total
- ✅ `AverageValueScreen.kt` - Exibe a média de valor
- ✅ `MostExpensiveScreen.kt` - Destaca a assinatura mais cara
- ✅ `CheapestScreen.kt` - Mostra a assinatura mais barata
- ✅ `MostUsedScreen.kt` - Apresenta a assinatura mais utilizada

### **5. Navegação** (`ui/navigation/`)

- ✅ `Screen.kt` - Definição de rotas
- ✅ `PayFlowNavGraph.kt` - Grafo de navegação completo

## 🎨 Design Material You

A tela foi projetada usando os princípios do **Material 3** (Material You):

### 🎨 **Cores e Tema**

- ✅ Usa `MaterialTheme.colorScheme` para cores dinâmicas
- ✅ Cards com `primaryContainer`, `secondaryContainer`, `tertiaryContainer`
- ✅ Suporte para tema claro e escuro

### 🎯 **Componentes Material**

- ✅ `TopAppBar` com cores do Material 3
- ✅ `Card` com cores semânticas
- ✅ `FloatingActionButton` para adicionar assinaturas
- ✅ `Icon` com Material Icons oficiais
- ✅ Tipografia Material 3 (`displayLarge`, `titleLarge`, etc.)

### 📱 **Material Icons usados**

- `Icons.Default.AttachMoney` - Gasto mensal
- `Icons.Default.Subscriptions` - Assinaturas ativas
- `Icons.Default.TrendingUp` - Valor médio
- `Icons.Default.ArrowUpward` - Mais cara
- `Icons.Default.ArrowDownward` - Mais barata
- `Icons.Default.Star` - Mais usada
- `Icons.Default.AccountCircle` - Perfil
- `Icons.Default.Add` - Adicionar
- `Icons.AutoMirrored.Filled.ArrowBack` - Voltar

## 📋 Métricas exibidas na Home

1. **📊 Gasto Mensal** (card destacado)
   - Valor total gasto por mês
   - Soma de todas as assinaturas ativas

2. **🔢 Assinaturas Ativas**
   - Quantidade total de serviços ativos

3. **📈 Valor Médio**
   - Média de valor entre todas as assinaturas

4. **💰 Assinatura Mais Cara**
   - Nome e valor da assinatura mais cara

5. **💡 Assinatura Mais Barata**
   - Nome e valor da assinatura mais econômica

6. **⭐ Assinatura Mais Usada**
   - Nome e quantidade de usos no mês

## 🚀 Próximos passos para integração

### **1. Configurar dependências no `build.gradle`**

```kotlin
dependencies {
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Hilt (opcional, mas recomendado)
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
}
```

### **2. Integrar no MainActivity**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PayFlowTheme {
                val navController = rememberNavController()
                // TODO: Injetar via Hilt quando configurado
                val homeViewModel = HomeViewModel(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase()
                    )
                )

                PayFlowNavGraph(
                    navController = navController,
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}
```

### **3. Conectar com Room (quando implementado)**

Substituir o `GetSubscriptionsUseCase` para buscar dados reais:

```kotlin
class GetSubscriptionsUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<Subscription>> {
        return subscriptionRepository.getActiveSubscriptions()
    }
}
```

## 🎯 Funcionalidades implementadas

✅ **Dashboard completo** com 6 métricas principais  
✅ **Navegação** para telas de detalhe de cada métrica  
✅ **Design Material You** com cores dinâmicas  
✅ **Estados de UI** (loading, success, error)  
✅ **Componentes reutilizáveis** (SummaryCard, HighlightCard)  
✅ **Material Icons** em toda a interface  
✅ **Formatação monetária** em pt-BR (R$)  
✅ **Responsive layout** com LazyColumn

## 📸 Layout da Home

A tela Home possui:

- **TopBar** com título "PayFlow" e ícone de perfil
- **Card destacado** de gasto mensal (grande, no topo)
- **Seção "Suas Métricas"** com 2 cards (ativas e média)
- **Seção "Destaques"** com 3 cards (mais cara, mais barata, mais usada)
- **FAB** para adicionar nova assinatura

Cada card é clicável e navega para uma tela de detalhe específica!

---

**💡 Dica**: Os dados são mockados no momento. Conecte com o Room Database para dados reais seguindo a arquitetura MVVM proposta no plano do projeto.
