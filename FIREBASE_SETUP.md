# Configuração do Firebase

Este documento descreve os passos necessários para configurar o Firebase para o aplicativo Shopping List.

## 1. Crie um Projeto no Firebase

1.  Vá para o [Console do Firebase](https://console.firebase.google.com/).
2.  Clique em **Adicionar projeto** e siga as instruções na tela para criar um novo projeto. Dê um nome sugestivo, como "Shopping List App".

## 2. Adicione o App Android ao seu Projeto Firebase

1.  No painel do seu projeto no Console do Firebase, clique no ícone do Android (⚙️ > **Configurações do projeto** > Geral > Seus apps).
2.  Clique em **Adicionar app** e selecione a plataforma Android.
3.  No campo **Nome do pacote Android**, insira `com.shoppinglist`.
4.  (Opcional) Dê um apelido para o app, como "Shopping List Android App".
5.  (Opcional) Adicione o certificado de assinatura de depuração SHA-1.
    *   Você pode obter o SHA-1 executando o seguinte comando no diretório do seu projeto:
        ```bash
        ./gradlew signingReport
        ```
    *   Procure pela variante "debug" e copie o valor do SHA-1.
6.  Clique em **Registrar app**.

## 3. Adicione o Arquivo de Configuração do Firebase (`google-services.json`)

1.  Após registrar o app, o Firebase fornecerá um arquivo `google-services.json`. Clique em **Fazer o download de google-services.json**.
2.  Mova o arquivo `google-services.json` que você acabou de baixar para o diretório `app/` do seu projeto Android.
    *   **Atenção:** Este arquivo contém informações sensíveis e não deve ser versionado. O `.gitignore` já está configurado para ignorá-lo.

## 4. Habilite os Serviços do Firebase

### Firebase Authentication

1.  No menu de navegação à esquerda do Console do Firebase, vá para a seção **Build** e clique em **Authentication**.
2.  Clique em **Começar**.
3.  Na aba **Método de login**, selecione e habilite o provedor **E-mail/senha**.

### Cloud Firestore

1.  No menu de navegação à esquerda, vá para a seção **Build** e clique em **Cloud Firestore**.
2.  Clique em **Criar banco de dados**.
3.  Selecione **Iniciar em modo de teste**.
    *   **Aviso:** O modo de teste permite acesso aberto aos seus dados por um período limitado. Para produção, você deve configurar regras de segurança mais restritivas.
4.  Clique em **Próximo**.
5.  Escolha um local para o seu Cloud Firestore. A escolha do local é permanente.
6.  Clique em **Habilitar**.

## Conclusão

Após seguir estes passos, seu ambiente do Firebase estará configurado e o aplicativo deverá funcionar corretamente. Certifique-se de que as dependências do Firebase no arquivo `app/build.gradle.kts` estão sincronizadas com o seu projeto.