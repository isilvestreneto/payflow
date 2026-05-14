# 📋 Plano de Projeto — PayFlow

> Organizador de assinaturas e gastos recorrentes — Aplicativo Android nativo em Jetpack Compose

---

## 🎯 1. Visão geral do projeto

| Item | Definição |
|------|-----------|
| **Nome** | PayFlow |
| **Tema** | Projeto 8 (organizador de assinaturas) |
| **Plataforma** | Android (mínimo SDK 24, alvo SDK 34+) |
| **Linguagem** | Kotlin |
| **UI** | Jetpack Compose + Material 3 (Material Android) |
| **Ícones** | Material Icons (Extended) — biblioteca oficial do Material Android |
| **Arquitetura** | MVVM |
| **Autenticação** | Firebase Authentication (Google Sign-In via OAuth2) |
| **API** | API pública de cotação USD-BRL + catálogo mockado de serviços |
| **Persistência local** | Room (assinaturas) + DataStore (preferências de tema) |
| **Prazo** | 9 dias |

---

## 👥 2. Distribuição sugerida da equipe

Com 4 pessoas já alocadas e telas/responsabilidades restantes, sugiro a seguinte divisão (ajustem conforme integrantes adicionais):

| Pessoa | Responsabilidade principal | Tarefas secundárias |
|--------|---------------------------|---------------------|
| **Ivanildo** | Login + Firebase Auth (OAuth2 Google) | Configuração do projeto Firebase, AndroidManifest, SHA-1 |
| **Ana Caroline** | Tela de Histórico + filtros/ordenação | DAO/Repository de Assinaturas (queries de filtro) |
| **Rafael** | Tela de Cadastro de Assinatura | Validação de formulários, máscaras (valor, data) |
| **Jeferson** | Tela de Detalhe + ações (cancelar/reativar) | DAO/Repository (update de status) |
| **Integrante 5** | Tela Home (Dashboard) + cálculos de resumo | Componentes reutilizáveis (cards, uso de Material Icons) |
| **Integrante 6** | Tela Perfil + Configurações (tema) + README + apresentação | DataStore de tema, navegação geral, integração final |

> 💡 Se forem 5 pessoas, o "Integrante 6" se distribui: tema/DataStore vai pra quem cuida do Perfil; README/apresentação vira responsabilidade compartilhada.

---

## 🗓️ 3. Cronograma de 9 dias

### **Dia 1 — Setup e fundação** (todos)
- [ ] Criar repositório no GitHub e adicionar todos como colaboradores
- [ ] Criar branch `develop` e definir fluxo (feature branches → PR → develop → main)
- [ ] Criar projeto no Android Studio (Empty Activity, Kotlin, Compose, min SDK 24)
- [ ] Configurar `build.gradle` com dependências (Compose BOM, Material 3, Material Icons Extended, Navigation, Hilt, Room, DataStore, Firebase Auth, Retrofit, Coil)
- [ ] Criar projeto no Firebase Console + cadastrar app Android + baixar `google-services.json`
- [ ] Gerar SHA-1 (debug) e cadastrar no Firebase
- [ ] Ativar provedor Google no Firebase Authentication
- [ ] Reunião de 30min: alinhamento da estrutura de pastas e padrões de código

### **Dia 2 — Estrutura base + Login** (Ivanildo + Integrante 5)
- [ ] Criar estrutura de pacotes MVVM (ver seção 4)
- [ ] Configurar `Application` class + Hilt
- [ ] Criar `AppDatabase` (Room), entidades vazias e DAOs com assinaturas
- [ ] Criar `NavGraph` com rotas placeholder
- [ ] Implementar tema (Material 3, cores, tipografia, ícones do Material Android)
- [ ] **Ivanildo:** implementar `LoginScreen` + `LoginViewModel` + `AuthRepository` com Firebase Auth (Google Sign-In)
- [ ] Persistir sessão (auto-login se já autenticado)

### **Dia 3 — Modelos e camada de dados** (Ana Caroline + Rafael)
- [ ] Definir `Subscription` (entity + domain model + enums `SubscriptionStatus`, `SubscriptionType`, `PaymentMethod`)
- [ ] Definir `User` (DataStore para preferências locais)
- [ ] Implementar `SubscriptionDao` (CRUD + queries de filtro/ordenação)
- [ ] Implementar `SubscriptionRepository` com Flow (Room como única fonte de dados)
- [ ] Criar seed de dados (5–10 assinaturas de exemplo) pra apresentação
- [ ] Definir qual API será consumida (ver seção 5)

### **Dia 4 — Tela Home + Cadastro** (Integrante 5 + Rafael)
- [ ] **Home:** HomeScreen com cards de resumo (total ativas, gasto mensal, mais cara/barata/usada, média)
- [ ] HomeViewModel: combinar Flow do Room para calcular estatísticas reativas
- [ ] Usar Material Icons para ilustrar cada card (ex: `Icons.Default.AttachMoney`, `Icons.Default.TrendingUp`)
- [ ] **Cadastro:** RegisterSubscriptionScreen com formulário (nome, valor, status, datas, forma pagamento, tipo)
- [ ] Validações inline e botão de salvar habilitado só com campos válidos
- [ ] Navegação Home ↔ Cadastro funcional

### **Dia 5 — Histórico + Detalhe** (Ana Caroline + Jeferson)
- [ ] **Histórico:** HistoryScreen com lista (LazyColumn) + filtros (status, período) + ordenação
- [ ] Componente `SubscriptionCard` reutilizável com Material Icons por categoria (ex: `Icons.Default.PlayCircle` para streaming)
- [ ] **Detalhe:** SubscriptionDetailScreen recebendo `subscriptionId` via navegação
- [ ] Ações: cancelar/reativar (update no Room) + voltar
- [ ] Estado vazio na listagem ("Você ainda não tem assinaturas — cadastre a primeira!")

### **Dia 6 — Perfil + Configurações** (Integrante 6)
- [ ] **Perfil:** ProfileScreen exibindo dados do Firebase Auth (foto via Coil, nome, email)
- [ ] Botão de logout com ícone `Icons.AutoMirrored.Default.Logout`
- [ ] **Configurações:** SettingsScreen com seletor de tema (claro/escuro/sistema) usando `Icons.Default.LightMode`, `Icons.Default.DarkMode`, `Icons.Default.SettingsBrightness`
- [ ] Persistir tema escolhido em DataStore
- [ ] Aplicar tema dinamicamente em toda a app (recompor na mudança)

### **Dia 7 — Integração + API + polimento** (todos)
- [ ] Integrar consumo da API escolhida (ver seção 5)
- [ ] Tratar estados: loading, sucesso, erro, vazio em todas as telas principais
- [ ] Revisar navegação completa de ponta a ponta
- [ ] Garantir uso consistente de Material Icons em toda a app (TopBar, FAB, botões)
- [ ] Testar fluxo completo (login → cadastrar → ver histórico → detalhar → cancelar → voltar)

### **Dia 8 — Testes, ajustes finais e README**
- [ ] Testar em emulador e em dispositivo físico
- [ ] Corrigir bugs encontrados
- [ ] Capturar screenshots/GIFs das telas principais
- [ ] Escrever README completo (ver template em `README.md`)
- [ ] Garantir que `git clone` + sync do Gradle + execução funciona do zero

### **Dia 9 — Apresentação**
- [ ] Ensaiar apresentação (roteiro de 10min)
  - 1min: Problema e proposta
  - 1min: Equipe e tecnologias
  - 4min: Demonstração ao vivo
  - 2min: Arquitetura MVVM, API e persistência
  - 1min: Diferencial e decisões
  - 1min: GitHub/README, aprendizados
- [ ] Preparar backup: vídeo de demonstração caso falhe ao vivo
- [ ] Conferir checklist da seção 6 antes da entrega

---

## 📁 4. Estrutura de pastas e classes (MVVM)

```
app/src/main/java/com/payflow/app/
│
├── PayFlowApplication.kt           # @HiltAndroidApp
├── MainActivity.kt                 # @AndroidEntryPoint + setContent { PayFlowApp() }
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt              # @Database(entities = [SubscriptionEntity::class])
│   │   ├── converter/
│   │   │   └── Converters.kt           # Date, Enum
│   │   ├── dao/
│   │   │   └── SubscriptionDao.kt      # @Dao
│   │   └── entity/
│   │       └── SubscriptionEntity.kt   # @Entity(tableName = "subscriptions")
│   │
│   ├── preferences/
│   │   └── ThemePreferences.kt         # DataStore<Preferences> — tema do app
│   │
│   ├── remote/
│   │   ├── api/
│   │   │   ├── ExchangeRateApi.kt      # Cotação USD-BRL (economia.awesomeapi.com.br)
│   │   │   └── ServicesCatalogApi.kt   # Catálogo mockado de serviços populares
│   │   └── dto/
│   │       ├── ExchangeRateDto.kt
│   │       └── ServiceCatalogDto.kt
│   │
│   ├── repository/
│   │   ├── AuthRepository.kt           # Firebase Auth (Google Sign-In)
│   │   ├── SubscriptionRepository.kt   # Room — única fonte de dados de assinaturas
│   │   └── ServicesCatalogRepository.kt # API remota de catálogo + cotação
│   │
│   └── mapper/
│       └── SubscriptionMapper.kt       # Entity ↔ Domain model
│
├── domain/
│   ├── model/
│   │   ├── Subscription.kt
│   │   ├── SubscriptionStatus.kt       # enum: ACTIVE, CANCELED, PAUSED
│   │   ├── SubscriptionType.kt         # enum: STREAMING, SERVICOS, TELEFONIA, BANCOS, IA, OUTROS
│   │   ├── PaymentMethod.kt            # enum: CREDIT_CARD, PIX, BOLETO, DEBIT
│   │   └── User.kt
│   │
│   └── usecase/
│       ├── GetSubscriptionsUseCase.kt
│       ├── GetHomeSummaryUseCase.kt     # Calcula gasto mensal, mais cara, etc
│       ├── SaveSubscriptionUseCase.kt
│       ├── ToggleSubscriptionStatusUseCase.kt
│       └── GetServicesCatalogUseCase.kt
│
├── di/
│   ├── DatabaseModule.kt               # @Module @InstallIn(SingletonComponent::class)
│   ├── NetworkModule.kt                # Retrofit, OkHttp, Moshi
│   ├── RepositoryModule.kt
│   └── FirebaseModule.kt               # FirebaseAuth instance
│
└── ui/
    ├── theme/
    │   ├── Color.kt                    # Paleta Material 3
    │   ├── Theme.kt                    # MaterialTheme com light/dark dinâmico
    │   └── Type.kt                     # Tipografia Material 3
    │
    ├── navigation/
    │   ├── PayFlowNavGraph.kt          # NavHost com todas as rotas
    │   └── Screen.kt                   # sealed class com rotas tipadas
    │
    ├── components/                     # Componentes reutilizáveis com Material Icons
    │   ├── PayFlowTopBar.kt            # TopAppBar Material 3
    │   ├── PayFlowBottomNav.kt         # NavigationBar Material 3 (opcional)
    │   ├── SubscriptionCard.kt         # Card com ícone por categoria (Material Icons)
    │   ├── EmptyState.kt               # Ícone + mensagem quando lista vazia
    │   ├── LoadingIndicator.kt         # CircularProgressIndicator Material 3
    │   ├── ErrorMessage.kt
    │   └── PayFlowTextField.kt         # OutlinedTextField Material 3
    │
    └── screens/
        ├── login/
        │   ├── LoginScreen.kt
        │   ├── LoginViewModel.kt       # @HiltViewModel
        │   └── LoginUiState.kt
        │
        ├── home/
        │   ├── HomeScreen.kt
        │   ├── HomeViewModel.kt
        │   ├── HomeUiState.kt
        │   └── components/
        │       ├── SummaryCard.kt      # Card com Icons.Default.AttachMoney etc
        │       └── HighlightCard.kt
        │
        ├── history/
        │   ├── HistoryScreen.kt
        │   ├── HistoryViewModel.kt
        │   ├── HistoryUiState.kt
        │   └── components/
        │       └── FilterBottomSheet.kt  # ModalBottomSheet Material 3
        │
        ├── register/
        │   ├── RegisterSubscriptionScreen.kt
        │   ├── RegisterSubscriptionViewModel.kt
        │   └── RegisterSubscriptionUiState.kt
        │
        ├── detail/
        │   ├── SubscriptionDetailScreen.kt
        │   ├── SubscriptionDetailViewModel.kt
        │   └── SubscriptionDetailUiState.kt
        │
        ├── profile/
        │   ├── ProfileScreen.kt
        │   ├── ProfileViewModel.kt
        │   └── ProfileUiState.kt
        │
        └── settings/
            ├── SettingsScreen.kt
            ├── SettingsViewModel.kt
            └── SettingsUiState.kt
```

### 🔑 Padrões a seguir
- **UI State unificado** por tela (`sealed interface` ou `data class` com `isLoading`, `error`, `data`)
- **ViewModels** expõem `StateFlow<UiState>` (nunca `LiveData` em projeto novo com Compose)
- **Repositories** retornam `Flow<T>` quando possível (reatividade automática com Room)
- **Room é a única fonte de verdade** para assinaturas — sem Firestore
- **Use cases** opcionais: comece sem eles; extraia só se a lógica do ViewModel crescer demais
- **Hilt** para injeção de dependência
- **Material Icons** para toda iconografia — nunca imagens bitmap para ícones de UI

---

## 🌐 5. Como atender ao requisito de API

O PDF aceita: pública, mockada, própria ou Firebase/Supabase/JSON Server. Duas opções combinadas:

### Opção A — **API pública de cotação USD-BRL**
- URL: `https://economia.awesomeapi.com.br/json/last/USD-BRL`
- Gratuita, sem chave de API
- Usar na Home para exibir o gasto mensal convertido em dólares
- Consumir via Retrofit + Moshi

### Opção B — **Catálogo mockado de serviços (JSON estático)**
- Hospedar um `db.json` no [Glitch](https://glitch.com) ou [MockAPI.io](https://mockapi.io) (ambos gratuitos)
- Conteúdo: lista de serviços populares com preço-base (Netflix R$ 21,90, Spotify R$ 11,90, etc)
- Usar na tela de Cadastro para sugerir serviços conforme o usuário digita o nome
- Consumir via Retrofit + Moshi

> ✅ **Sugestão final:** usar **ambas** — são independentes, o esforço é baixo e aparecem visivelmente no app. Cumpre o requisito com clareza.

---

## ✅ 6. Checklist final (antes da apresentação)

### Funcional
- [ ] Login com Google funciona em dispositivo limpo
- [ ] App lembra sessão (não pede login toda vez)
- [ ] Cadastro de assinatura persiste e aparece no histórico
- [ ] Filtros do histórico funcionam
- [ ] Detalhe permite cancelar/reativar e reflete na Home
- [ ] Home recalcula totais ao adicionar/remover assinatura
- [ ] Tema claro/escuro/sistema funciona e persiste
- [ ] Logout funciona

### Qualidade
- [ ] App não trava em nenhum fluxo principal
- [ ] Estados de loading/erro/vazio presentes nas telas principais
- [ ] Sem hardcoded strings em português no código (usar `strings.xml`)
- [ ] Material Icons usados consistentemente (sem misturar com drawables bitmap)
- [ ] Build de release roda sem erros (`./gradlew assembleRelease`)

### Entrega
- [ ] README com: descrição, funcionalidades, prints/GIFs, arquitetura, dependências, instruções, integrantes
- [ ] `.gitignore` correto (não commitar `/build`, `.idea`, `local.properties`)
- [ ] Branches limpas, último commit na `main`
- [ ] Tag de versão `v1.0` no commit final
- [ ] Apresentação ensaiada e em 10 minutos

---

## 💡 7. Sugestões de enriquecimento (se sobrar tempo)

Em ordem de custo-benefício:

1. **Notificações locais** de vencimento próximo (WorkManager) — alto impacto visual
2. **Gráfico de pizza** por categoria na Home (biblioteca `Vico` ou `MPAndroidChart`)
3. **Animações de transição** entre telas com Compose `AnimatedContent`
4. **Splash Screen** com a API SplashScreen (Android 12+)
5. **Diferencial PDF (gasto com pouco uso):** campo "última vez que usei" no Cadastro → Home destaca assinaturas não usadas há >30 dias
6. **FAB animado** na Home/Histórico com `Icons.Default.Add` para acesso rápido ao Cadastro
7. **BottomNavigationBar** com Material Icons para navegar entre Home, Histórico e Perfil

> ⚠️ Não tente fazer tudo. Escolham 1-2 itens que combinem com o diferencial que querem destacar na apresentação.

---

## 🚨 8. Riscos e mitigações

| Risco | Mitigação |
|-------|-----------|
| Firebase Auth não funcionar em release devido a SHA-1 errado | Cadastrar **SHA-1 de debug E release** desde o dia 1 |
| Conflitos de merge no Git | Feature branches pequenas + PRs revisados |
| ViewModel acessando contexto direto | Code review garantindo separação de camadas |
| Apresentação ao vivo falhar (rede, Firebase) | Gravar vídeo de backup no dia 8 |
| Integrante atrasado/ausente | Pareamento desde o dia 1; tarefas independentes |
| Tema dinâmico complicando a navegação | Implementar tema só no dia 6, depois das telas principais |

---

## 📚 9. Dependências sugeridas (`build.gradle.kts` do módulo app)

```kotlin
dependencies {
    // Compose + Material 3 (Material Android)
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended") // Material Icons completo
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")

    // Room (persistência local — única fonte de dados de assinaturas)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore (preferências de tema)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Firebase (apenas autenticação — sem Firestore)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Retrofit + Moshi (API de cotação e catálogo mockado)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coil (foto de perfil do Google)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

> **Plugins necessários no `build.gradle.kts` do projeto e do módulo:**
> - `id("com.google.gms.google-services")`
> - `id("dagger.hilt.android.plugin")`
> - `id("com.google.devtools.ksp")`

### 💡 Nota sobre Material Icons Extended
O pacote `material-icons-extended` contém mais de 2.000 ícones prontos do Material Design. Use-os assim:
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.automirrored.filled.Logout

Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "Gasto")
Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Streaming")
```
Consulte todos os ícones disponíveis em: https://fonts.google.com/icons
