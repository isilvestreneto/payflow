# 💸 PayFlow

> Organize suas assinaturas, controle seus gastos recorrentes e descubra onde está perdendo dinheiro.

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Material%20Android-Icons%20Extended-757575?logo=materialdesign" />
  <img src="https://img.shields.io/badge/Arquitetura-MVVM-success" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-orange" />
</p>

---

## 📖 Sobre o projeto

**PayFlow** é um aplicativo Android nativo, desenvolvido como projeto final da disciplina de Desenvolvimento Android, que ajuda o usuário a **organizar suas assinaturas e gastos recorrentes** (streaming, telefonia, bancos, IA, serviços diversos).

O usuário consegue cadastrar todas as suas assinaturas, visualizar um dashboard com indicadores (gasto mensal, mais cara, mais barata, mais usada) e identificar **assinaturas que está usando pouco e poderia cancelar**.

### 🎯 Problema que resolve
Muita gente perde controle dos gastos recorrentes porque cada serviço cobra em data e cartão diferente, e essas pequenas mensalidades somadas viram um valor expressivo no fim do mês. O PayFlow centraliza tudo num único lugar.

---

## ✨ Funcionalidades

- 🔐 **Login com Google** via OAuth2 (Firebase Authentication)
- 🏠 **Dashboard** com resumo das assinaturas: total ativo, gasto mensal, média, mais cara/barata/usada
- 📜 **Histórico** completo com filtros (status, período) e ordenação (data, nome, status)
- ➕ **Cadastro** de assinatura: nome, valor, status, datas, forma de pagamento e categoria
- 🔎 **Visualização detalhada** de cada assinatura com ações de cancelar/reativar
- 👤 **Perfil** do usuário com dados do Google
- 🎨 **Tema** claro, escuro ou seguindo o sistema (persistido localmente via DataStore)
- 💾 **Funciona offline** após o primeiro login (dados armazenados localmente no Room)

---

## 🖼️ Capturas de tela

> 📌 _Adicionar os screenshots/GIFs aqui antes da apresentação._

| Login | Home | Histórico |
|:-:|:-:|:-:|
| _print_ | _print_ | _print_ |

| Cadastro | Detalhe | Perfil |
|:-:|:-:|:-:|
| _print_ | _print_ | _print_ |

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com separação clara de camadas:

```
┌─────────────────────────────────────────┐
│ UI (Compose Screens + Material Icons)   │  ← observa StateFlow
├─────────────────────────────────────────┤
│ ViewModel (expõe StateFlow<UiState>)    │  ← orquestra
├─────────────────────────────────────────┤
│ UseCase (regras de negócio)             │
├─────────────────────────────────────────┤
│ Repository (única fonte de verdade)     │  ← Flow<T>
├─────────────────────────────────────────┤
│ Data Sources                            │
│  ├─ Room        (assinaturas — local)   │
│  ├─ DataStore   (preferências de tema) │
│  ├─ Retrofit    (API de cotação + mock) │
│  └─ Firebase Auth (apenas autenticação) │
└─────────────────────────────────────────┘
```

### Estrutura de pacotes

```
com.payflow.app/
├── data/
│   ├── local/        # Room (entidades, DAOs, database)
│   ├── preferences/  # DataStore (tema)
│   ├── remote/       # Retrofit (API de cotação e catálogo)
│   ├── repository/   # Repositórios
│   └── mapper/       # Conversão entidade ↔ modelo de domínio
├── domain/
│   ├── model/        # Modelos de domínio + enums
│   └── usecase/      # Casos de uso
├── di/               # Módulos Hilt
└── ui/
    ├── theme/        # Material 3 (Color, Theme, Type)
    ├── navigation/   # NavGraph + rotas
    ├── components/   # UI reutilizável com Material Icons
    └── screens/      # Telas (cada uma com Screen + ViewModel + UiState)
```

---

## 🛠️ Tecnologias e bibliotecas

| Categoria | Tecnologia |
|-----------|-----------|
| Linguagem | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Ícones | Material Icons Extended (Material Android) |
| Navegação | Navigation Compose |
| Injeção de Dependência | Hilt |
| Banco local | Room |
| Preferências | DataStore |
| Autenticação | Firebase Authentication (Google Sign-In) |
| API | Retrofit + Moshi + OkHttp |
| Imagens | Coil |
| Assincronicidade | Kotlin Coroutines + Flow |

---

## 🚀 Como executar

### Pré-requisitos
- Android Studio **Koala (2024.1.1)** ou superior
- JDK 17
- Dispositivo Android (físico ou emulador) com API 24+

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/<usuario>/payflow.git
cd payflow

# 2. Abra no Android Studio
# File → Open → selecione a pasta do projeto

# 3. Sincronize o Gradle (será automático na primeira abertura)

# 4. Rode o app
# Selecione um dispositivo/emulador e clique em Run ▶
```

### ⚙️ Configuração do Firebase
O arquivo `google-services.json` **já está incluído** no repositório (apenas para fins acadêmicos de demonstração). Em produção, ele deve ficar fora do controle de versão.

Se for necessário recriar o projeto Firebase:
1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Crie um projeto e adicione um app Android com package `com.payflow.app`
3. Cadastre o SHA-1 do seu certificado de debug
4. Habilite o provedor **Google** em Authentication → Sign-in method
5. Substitua o arquivo `app/google-services.json` pelo seu

---

## 📚 Decisões de escopo e diferencial

- **Persistência 100% local:** todas as assinaturas ficam no Room (SQLite), sem dependência de backend remoto. O app funciona offline após o primeiro login.
- **Firebase apenas para autenticação:** sem Firestore — o login com Google é o único ponto que requer internet no fluxo principal.
- **Material Android:** toda a iconografia usa o pacote `material-icons-extended`, garantindo visual consistente com o ecossistema Android.
- **Diferencial:** a Home destaca assinaturas pouco utilizadas (sugerindo cancelamento), atendendo ao diferencial proposto pelo tema PayFlow no enunciado.
- **API:** cotação USD-BRL em tempo real na Home + catálogo mockado de serviços populares no Cadastro.
- **Foco em fluxo completo:** priorizamos as 7 telas funcionando ponta a ponta em vez de mais telas incompletas.

---

## 👥 Equipe

| Nome | Papel principal | GitHub |
|------|-----------------|--------|
| Ivanildo | Autenticação (OAuth2) + Setup Firebase | [@usuario](https://github.com/usuario) |
| Ana Caroline | Tela de Histórico + filtros | [@usuario](https://github.com/usuario) |
| Rafael | Tela de Cadastro de Assinatura | [@usuario](https://github.com/usuario) |
| Jeferson | Tela de Detalhe + ações | [@usuario](https://github.com/usuario) |
| _Integrante 5_ | Home (Dashboard) + componentes | [@usuario](https://github.com/usuario) |
| _Integrante 6_ | Perfil + Configurações + Integração | [@usuario](https://github.com/usuario) |

---

## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais. Sem fins comerciais.
