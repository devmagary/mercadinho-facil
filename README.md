# Lista de Compras Compartilhada - Android

Aplicativo Android para gerenciamento de listas de compras compartilhadas em tempo real entre membros de uma família.

## 📱 Características

- 🔐 **Autenticação de Usuários**: Sistema de login e registro com Firebase Authentication
- 👨‍👩‍👧‍👦 **Sistema de Famílias**: Crie ou entre em famílias usando códigos de convite
- 👤 **Perfil de Usuário**: Visualize seus dados e copie facilmente o código da família
- ✅ **CRUD Completo**: Adicionar, editar, remover e marcar itens como comprados
- 📝 **Listas Nomeadas**: Dê nomes personalizados às suas listas de compras
- 🔄 **Sincronização em Tempo Real**: Firebase Firestore sincroniza automaticamente entre todos os dispositivos
- 👨‍👩‍👧‍👦 **Compartilhamento Familiar**: Múltiplos usuários podem gerenciar a mesma lista
- 📊 **Analytics**: Visualize estatísticas de gastos e histórico de compras
- 🔁 **Histórico e Clonagem**: Clone compras anteriores com um toque
- 🌓 **Dark Mode AMOLED**: Tema escuro otimizado para telas OLED (#000000), com toggle nas configurações
- 📸 **Suporte a Imagens**: Adicione fotos dos produtos (opcional)
- 💰 **Controle de Gastos**: Acompanhe preços e calcule totais automaticamente

## 🛠️ Stack Tecnológica

- **Linguagem**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Arquitetura**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase Firestore + Firebase Authentication
- **Persistência Local**: DataStore Preferences (para configurações de tema)
- **Navegação**: Navigation Compose
- **Imagens**: Coil

## 📁 Estrutura do Projeto

```
app/src/main/java/com/shoppinglist/
├── data/
│   ├── model/
│   │   ├── ShoppingItem.kt      # Modelo de item
│   │   ├── ShoppingList.kt      # Modelo de lista
│   │   ├── Family.kt            # Modelo de família
│   │   └── User.kt              # Modelo de usuário
│   └── repository/
│       ├── AuthRepository.kt    # Autenticação e famílias
│       └── ShoppingRepository.kt # Operações de lista
├── viewmodel/
│   ├── AuthViewModel.kt         # ViewModel de autenticação
│   ├── ShoppingListViewModel.kt # ViewModel principal
│   ├── HistoryViewModel.kt      # ViewModel de histórico
│   ├── AnalyticsViewModel.kt    # ViewModel de analytics
│   ├── ProfileViewModel.kt      # ViewModel de perfil
│   └── ThemeViewModel.kt        # ViewModel de tema
├── ui/
│   ├── theme/                   # Tema Material 3
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── screen/                  # Telas composable
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ShoppingListScreen.kt
│   │   ├── HistoryScreen.kt
│   │   ├── AnalyticsScreen.kt
│   │   ├── ProfileScreen.kt
│   │   └── AddEditItemDialog.kt
│   ├── components/              # Componentes reutilizáveis
│   │   ├── ShoppingItemCard.kt
│   │   └── FinishShoppingDialog.kt
│   └── navigation/
│       ├── Screen.kt
│       └── NavGraph.kt
├── MainActivity.kt
└── ShoppingListApp.kt           # Application class
```

## 🚀 Como Executar

### Pré-requisitos

- Android Studio (versão mais recente recomendada)
- JDK 8 ou superior
- Conta no Firebase

### Passo 1: Configurar o Firebase

Siga o guia detalhado em [FIREBASE_SETUP.md](FIREBASE_SETUP.md):

1. Crie um projeto no Firebase Console
2. Adicione um aplicativo Android com o pacote `com.shoppinglist`
3. Baixe o arquivo `google-services.json` e coloque em `app/`
4. Ative Authentication (Email/Password)
5. Ative Cloud Firestore
6. Configure as regras de segurança

### Passo 2: Abrir no Android Studio

1. Clone ou baixe este projeto
2. Abra o Android Studio
3. Selecione "Open an existing project"
4. Navegue até a pasta do projeto e abra

### Passo 3: Sincronizar Gradle

O Android Studio deve sincronizar automaticamente. Se não:
1. Clique em "File" > "Sync Project with Gradle Files"
2. Aguarde o download das dependências

### Passo 4: Executar

1. Conecte um dispositivo Android ou inicie um emulador
2. Clique em "Run" (▶️) ou pressione `Shift + F10`
3. Aguarde a instalação e abertura do app

## 📖 Como Usar

### Primeira Configuração

1. **Criar Conta**: Na primeira vez, crie uma conta com email e senha
2. **Criar/Entrar em Família**:
   - Crie uma nova família e compartilhe o código de convite
   - Ou entre em uma família existente usando o código

### Gerenciar Lista de Compras

1. **Adicionar Item**: Toque no botão `+` flutuante
   - Preencha nome, quantidade, unidade
   - Opcional: adicione preço e URL de imagem
2. **Nomear Lista**: Toque no campo de texto no topo para dar um nome à lista atual
3. **Marcar como Comprado**: Toque no círculo à esquerda do item
4. **Editar Item**: Toque no ícone de lápis
5. **Deletar Item**: Toque no ícone de lixeira
6. **Finalizar Compra**: Toque em "Finalizar Compra" quando terminar
   - Se a lista já tiver nome, ele será mantido
   - Se não tiver, você pode digitar um nome ou usar a data atual automaticamente

### Ver Histórico

1. Navegue até a aba "Histórico"
2. Veja todas as compras finalizadas com nome, data e valor
3. Toque em "Repetir Compra" para clonar uma lista antiga

### Ver Analytics

1. Navegue até a aba "Análises"
2. Veja estatísticas de gastos:
   - Total gasto em todas as compras
   - Média por compra
   - Histórico detalhado por data

### Gerenciar Perfil e Configurações

1. Toque no ícone de perfil no canto superior direito da tela principal
2. Visualize seus dados (Nome, Email)
3. **Código da Família**: Copie o código de convite com um toque no botão de cópia
4. **Tema**: Alterne entre Modo Claro e Escuro usando o switch

## 🎨 Temas

O aplicativo suporta dois temas:

- **Light Mode**: Tema claro padrão do Material 3
- **Dark Mode AMOLED**: Tema escuro com fundo preto puro (#000000) para economia de bateria em telas OLED

O tema pode ser alternado manualmente na tela de Perfil e a preferência é salva automaticamente.

## 🔐 Segurança e Privacidade

- Todos os dados são armazenados no Firebase Firestore com criptografia em trânsito
- Regras de segurança garantem que apenas membros autenticados da família acessem os dados
- Senhas são gerenciadas pelo Firebase Authentication

## 🔄 Sincronização em Tempo Real

Graças ao Firebase Firestore:
- Quando um membro adiciona/edita/remove um item, todos os outros veem a mudança instantaneamente
- Não é necessário atualizar manualmente a lista
- Funciona mesmo com conexão intermitente (offline-first)

## 🐛 Problemas Conhecidos e Limitações

- A autenticação é básica (apenas email/senha)
- Gráficos de analytics são simples (lista, não gráficos visuais)
- Suporte a imagens via URL (não upload direto)
- Funcionalidade de foto da nota fiscal está apenas estruturada (não implementada)

## 🤝 Contribuindo

Sugestões de melhorias:

1. Adicionar autenticação social (Google, Facebook)
2. Implementar upload de imagens para Firebase Storage
3. Adicionar gráficos visuais na tela de analytics
4. Implementar leitura de QR Code de notas fiscais
5. Adicionar notificações push quando alguém modifica a lista
6. Suporte a múltiplas listas por família

## 📄 Licença

Este projeto foi desenvolvido como exemplo educacional.

## 🙏 Agradecimentos

- Google/Firebase pela excelente plataforma
- Comunidade Jetpack Compose
- Material Design 3

## 📧 Contato

Para dúvidas ou sugestões sobre o projeto, abra uma issue.

---

**Desenvolvido com ❤️ usando Kotlin e Jetpack Compose**
