# App de Lista de Compras em Kotlin com Firebase e Compose

Um aplicativo moderno de lista de compras para Android, construído com as tecnologias mais recentes do ecossistema Jetpack. Este projeto serve como um exemplo prático de implementação de uma arquitetura MVVM com Firebase, Jetpack Compose e Coroutines.

## ✨ Funcionalidades

*   **Autenticação de Usuários:** Login e registro com e-mail e senha.
*   **Listas de Compras Compartilhadas:** Crie listas e adicione itens (a inferir, baseado em `Family.kt`).
*   **Gerenciamento de Itens:** Adicione, edite, remova e marque itens como comprados.
*   **Histórico de Compras:** Visualize listas de compras anteriores.
*   **Analytics:** Tela para visualização de dados e métricas de compras (a inferir, baseado em `AnalyticsScreen.kt`).
*   **UI Moderna:** Interface de usuário construída com Jetpack Compose, incluindo componentes com efeito *Glassmorphism*.

## 🛠️ Tech Stack & Arquitetura

*   **Linguagem:** [Kotlin](https://kotlinlang.org/)
*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Arquitetura:** MVVM (Model-View-ViewModel)
*   **Assincronismo:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://developer.android.com/kotlin/flow)
*   **Navegação:** [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
*   **Backend & Banco de Dados:**
    *   [Firebase Authentication](https://firebase.google.com/docs/auth)
    *   [Cloud Firestore](https://firebase.google.com/docs/firestore)
*   **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/)
*   **Gráficos:** [Vico](https://github.com/patrykandpatrick/vico)
*   **Persistência Local (Configurações):** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)

## 🚀 Começando

Para compilar e executar o projeto, siga os passos abaixo:

1.  **Clone o repositório:**
    ```bash
    git clone <URL_DO_REPOSITORIO>
    ```

2.  **Abra no Android Studio:**
    *   Abra o projeto no Android Studio (versão Flamingo ou superior recomendada).
    *   Aguarde a sincronização do Gradle ser concluída.

3.  **Configure o Firebase:**
    *   Este projeto requer uma configuração do Firebase para funcionar. Siga as instruções detalhadas no arquivo [**`FIREBASE_SETUP.md`**](./FIREBASE_SETUP.md) para configurar seu projeto Firebase e conectar ao app.

4.  **Execute o App:**
    *   Selecione um emulador ou conecte um dispositivo físico.
    *   Clique em "Run" (`Shift` + `F10`).

## 📁 Estrutura do Projeto

O projeto é organizado em pacotes que separam as responsabilidades, seguindo os princípios da Clean Architecture e do padrão MVVM.

```
com.shoppinglist
├── data
│   ├── model         # Modelos de dados (POJOs/data classes)
│   └── repository    # Repositórios que gerenciam as fontes de dados (Firestore)
├── ui
│   ├── components    # Componentes Compose reutilizáveis
│   ├── navigation    # Grafo de navegação e definições de tela
│   ├── screen        # Composables que representam telas inteiras
│   └── theme         # Tema do app (cores, tipografia, formas)
└── viewmodel         # ViewModels que preparam e gerenciam os dados para a UI
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Se você tem alguma ideia para melhorar o app, sinta-se à vontade para abrir uma *issue* para discutir a mudança ou enviar um *pull request*.

1.  Faça um *fork* do projeto.
2.  Crie uma nova *branch* (`git checkout -b feature/nova-feature`).
3.  Faça o *commit* das suas mudanças (`git commit -m 'Adiciona nova feature'`).
4.  Faça o *push* para a *branch* (`git push origin feature/nova-feature`).
5.  Abra um *Pull Request*.

## 📄 Licença

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
(Observação: arquivo `LICENSE` a ser adicionado)
