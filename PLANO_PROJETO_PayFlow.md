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

| Pessoa                            | Responsabilidade principal | Tarefas secundárias |
|-----------------------------------|---------------------------|---------------------|
| **Ivanildo**                      | Login + Firebase Auth (OAuth2 Google) | Configuração do projeto Firebase, AndroidManifest, SHA-1 |
| **Ana Caroline**                  | Tela de Histórico + filtros/ordenação | DAO/Repository de Assinaturas (queries de filtro) |
| **Rafael**                        | Tela de Cadastro de Assinatura | Validação de formulários, máscaras (valor, data) |
| **Jeferson**                      | Tela de Detalhe + ações (cancelar/reativar) | DAO/Repository (update de status) |
| **Wendel**                        | Tela Home (Dashboard) + cálculos de resumo | Componentes reutilizáveis (cards, uso de Material Icons) |
| **Aleff + quem liberar primeiro** | Tela Perfil + Configurações (tema) + README + apresentação | DataStore de tema, navegação geral, integração final |

> 💡 Se forem 5 pessoas, o "Integrante 6" se distribui: tema/DataStore vai pra quem cuida do Perfil; README/apresentação vira responsabilidade compartilhada.

---

## 🗂️ 3. Planejamento por feature

O fluxo de trabalho é: cada pessoa trabalha na sua branch de feature → abre PR → revisão → merge em `develop` → ao final, `develop` → `main`.

> ⚠️ **Leia a feature 0 e 1 antes de começar qualquer tela.** Elas são pré-requisitos para todo o restante.

---

### 🔧 Feature 0 — Setup e fundação
**Responsável:** todos (fazer juntos ou dividir entre 2 pessoas no início)
**Branch:** `setup/project-foundation` → merge direto na `main`

- [ ] Criar repositório no GitHub e adicionar todos como colaboradores
- [ ] Definir fluxo de Git: `main` (estável) ← `develop` (integração) ← `feature/*` (individual)
- [ ] Criar projeto no Android Studio (Empty Activity, Kotlin, Compose, min SDK 24)
- [ ] Configurar `build.gradle.kts` com todas as dependências (ver seção 9)
- [ ] Criar estrutura de pacotes MVVM completa (ver seção 4) — pastas vazias com `.gitkeep`
- [ ] Configurar `PayFlowApplication.kt` com `@HiltAndroidApp`
- [ ] Configurar `MainActivity.kt` com `@AndroidEntryPoint`
- [ ] Implementar tema base (`Color.kt`, `Theme.kt`, `Type.kt`) com Material 3
- [ ] Criar `NavGraph` com rotas placeholder e `Screen.kt`
- [ ] Criar projeto no Firebase Console + cadastrar app Android + baixar `google-services.json`
- [ ] Gerar SHA-1 (debug **e** release) e cadastrar no Firebase
- [ ] Ativar provedor Google no Firebase Authentication
- [ ] **PR + merge na `main`** — base pronta para todos partirem

---

### 🧱 Feature 1 — Entidade de domínio: Subscription
**Responsável:** Rafael + Ana Caroline + Jeferson (alinhar juntos antes de codar)
**Branch:** `feature/subscription-entity` → merge na `main` antes das telas de cada um

> Esta feature é **bloqueante** para as telas de Cadastro, Histórico e Detalhe. Os três devem alinhar o modelo antes de cada um abrir a branch da sua tela.

- [ ] Reunião rápida (15min) entre Rafael, Ana e Jeferson para validar todos os campos
- [ ] Criar `Subscription.kt` (domain model)
- [ ] Criar enums: `SubscriptionStatus` (ACTIVE, CANCELED, PAUSED), `SubscriptionType` (STREAMING, SERVICOS, TELEFONIA, BANCOS, IA, OUTROS), `PaymentMethod` (CREDIT_CARD, PIX, BOLETO, DEBIT)
- [ ] Criar `SubscriptionEntity.kt` (`@Entity`) com todos os campos + `Converters.kt`
- [ ] Criar `SubscriptionMapper.kt` (Entity ↔ Domain)
- [ ] Criar `SubscriptionDao.kt` com: `insert`, `update`, `delete`, `getAll`, `getById`, `getByStatus`, `getByPeriod`
- [ ] Criar `AppDatabase.kt` com a entidade registrada
- [ ] Criar `SubscriptionRepository.kt` expondo `Flow<List<Subscription>>`
- [ ] Criar seed de dados (5–10 assinaturas de exemplo) para demonstração
- [ ] **PR + merge na `main`** — todos os três fazem rebase antes de abrir suas branches de tela

---

### 🔐 Feature 2 — Tela: Login
**Responsável:** Ivanildo
**Branch:** `feature/login-screen`
**Depende de:** Feature 0

- [ ] `AuthRepository.kt`: login com Google via Firebase Auth, logout, verificar sessão ativa
- [ ] `LoginViewModel.kt` com `LoginUiState` (idle, loading, success, error)
- [ ] `LoginScreen.kt`: botão "Entrar com Google" com logo Google (Material Icons ou drawable oficial)
- [ ] Redirecionar para Home se já autenticado ao abrir o app
- [ ] Tratar erro de autenticação com mensagem visível na tela
- [ ] Navegação Login → Home após sucesso
- [ ] **PR → `develop`**

---

### 🏠 Feature 3 — Tela: Home (Dashboard)
**Responsável:** Integrante 5
**Branch:** `feature/home-screen`
**Depende de:** Feature 0 + Feature 1

- [ ] `GetHomeSummaryUseCase.kt`: calcular total de ativas, gasto mensal, média, mais cara, mais barata, mais usada
- [ ] `HomeViewModel.kt` consumindo Flow do Room via use case
- [ ] `HomeScreen.kt` com cards de resumo (Material Icons: `AttachMoney`, `TrendingUp`, `TrendingDown`, `Star`)
- [ ] Exibir cotação USD-BRL consumida via Retrofit (API pública — ver seção 5)
- [ ] Estados: loading (skeleton ou `CircularProgressIndicator`), dados, vazio (nenhuma assinatura ainda)
- [ ] FAB com `Icons.Default.Add` navegando para Cadastro
- [ ] **PR → `develop`**

---

### 📜 Feature 4 — Tela: Histórico
**Responsável:** Ana Caroline
**Branch:** `feature/history-screen`
**Depende de:** Feature 0 + Feature 1

- [ ] `HistoryViewModel.kt` com filtros reativos via `StateFlow` (status + período) sobre o Flow do Room
- [ ] `HistoryScreen.kt` com `LazyColumn` de assinaturas
- [ ] Componente `SubscriptionCard.kt` reutilizável com ícone por tipo (`PlayCircle` streaming, `Phone` telefonia, `AccountBalance` banco, etc.)
- [ ] `FilterBottomSheet.kt` (ModalBottomSheet Material 3) com filtros: status (chip group) + período (date picker)
- [ ] Ordenação por data, nome e status
- [ ] Estado vazio: ícone + mensagem sugerindo cadastrar a primeira assinatura
- [ ] Navegação para Detalhe ao tocar em um card
- [ ] **PR → `develop`**

---

### ➕ Feature 5 — Tela: Cadastro de Assinatura
**Responsável:** Rafael
**Branch:** `feature/register-subscription-screen`
**Depende de:** Feature 0 + Feature 1

- [ ] `RegisterSubscriptionViewModel.kt` com validação de cada campo como `StateFlow`
- [ ] `RegisterSubscriptionScreen.kt` com `OutlinedTextField` (Material 3) para: nome, valor, data de inclusão, data de vencimento
- [ ] Seletores: status (dropdown), tipo (dropdown com ícone por categoria), forma de pagamento (dropdown)
- [ ] Campo de nome com autocomplete: ao digitar, sugerir serviços do catálogo mockado via API (ver seção 5)
- [ ] Botão "Salvar" habilitado somente com todos os campos obrigatórios válidos
- [ ] Máscara de valor (ex: `R$ 0,00`) e validação de datas
- [ ] Navegar de volta ao Histórico ou Home após salvar
- [ ] **PR → `develop`**

---

### 🔎 Feature 6 — Tela: Detalhe da Assinatura
**Responsável:** Jeferson
**Branch:** `feature/subscription-detail-screen`
**Depende de:** Feature 0 + Feature 1

- [ ] `SubscriptionDetailViewModel.kt` recebendo `subscriptionId` e buscando no Room via `getById`
- [ ] `SubscriptionDetailScreen.kt` exibindo todos os campos da assinatura com ícones do Material Android
- [ ] Botão "Cancelar assinatura" (visível quando status = ACTIVE) com confirmação em `AlertDialog`
- [ ] Botão "Reativar assinatura" (visível quando status = CANCELED) com confirmação em `AlertDialog`
- [ ] Atualização de status refletida imediatamente via Flow no Room
- [ ] `TopAppBar` com botão de voltar (`Icons.AutoMirrored.Default.ArrowBack`)
- [ ] **PR → `develop`**

---

### 👤 Feature 7 — Tela: Perfil
**Responsável:** Integrante 6
**Branch:** `feature/profile-screen`
**Depende de:** Feature 0 + Feature 2 (Login)

- [ ] `ProfileViewModel.kt` expondo dados do `FirebaseAuth.currentUser`
- [ ] `ProfileScreen.kt` com foto de perfil via Coil (`AsyncImage`), nome e e-mail
- [ ] Botão de logout com `Icons.AutoMirrored.Default.Logout` → limpar sessão e navegar para Login
- [ ] **PR → `develop`**

---

### ⚙️ Feature 8 — Tela: Configurações
**Responsável:** Integrante 6
**Branch:** `feature/settings-screen`
**Depende de:** Feature 0

- [ ] `ThemePreferences.kt` com DataStore para persistir escolha de tema
- [ ] `SettingsViewModel.kt` lendo e gravando preferência de tema
- [ ] `SettingsScreen.kt` com seletor de tema: claro (`Icons.Default.LightMode`), escuro (`Icons.Default.DarkMode`), sistema (`Icons.Default.SettingsBrightness`)
- [ ] Aplicar tema dinamicamente em `MainActivity` ao mudar a preferência (sem reiniciar o app)
- [ ] **PR → `develop`**

---

### 🔗 Feature 9 — Integração, polimento e entrega
**Responsável:** todos
**Branch:** `develop` → `main`

- [ ] Garantir que todos os PRs de telas foram mergeados em `develop`
- [ ] Revisar navegação completa de ponta a ponta no fluxo principal
- [ ] Tratar estados de loading/erro/vazio em todas as telas que ainda estiverem faltando
- [ ] Garantir uso consistente de Material Icons (sem misturar com drawables bitmap)
- [ ] Testar em emulador e dispositivo físico
- [ ] Capturar screenshots/GIFs para o README
- [ ] Preencher o README com integrantes, prints e instruções finais
- [ ] Garantir que `git clone` + sync do Gradle + execução funciona do zero
- [ ] Tag `v1.0` no commit final na `main`
- [ ] Ensaiar apresentação (roteiro de 10min):
  - 1min: Problema e proposta
  - 1min: Equipe e tecnologias
  - 4min: Demonstração ao vivo
  - 2min: Arquitetura MVVM, API e persistência
  - 1min: Diferencial e decisões
  - 1min: GitHub/README, aprendizados
- [ ] Gravar vídeo de backup da demonstração

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
