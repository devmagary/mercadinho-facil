# Configuração do GitHub Actions

Este projeto já está configurado para realizar builds automáticos (CI) usando o GitHub Actions. Toda vez que você fizer um `push` para a branch `main` ou `master`, o GitHub tentará compilar o app.

## ⚠️ Configuração Necessária: Segredos (Secrets)

Como o arquivo `google-services.json` contém informações sensíveis e foi ignorado no Git, o GitHub Actions precisa de uma cópia dele para conseguir compilar o app. Usaremos os "GitHub Secrets" para isso.

### Passo 1: Codificar o arquivo em Base64

Você precisa converter o conteúdo do seu arquivo `app/google-services.json` para uma string Base64.

**No Windows (PowerShell):**
1. Abra o terminal na pasta do projeto.
2. Execute o comando:
   ```powershell
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("app/google-services.json")) | Set-Clipboard
   ```
   *Isso copiará o código Base64 diretamente para sua área de transferência.*

**No Linux/Mac:**
```bash
base64 -i app/google-services.json | pbcopy
# ou apenas base64 app/google-services.json e copie a saída
```

### Passo 2: Adicionar o Segredo no GitHub

1. Vá para a página do seu repositório no GitHub.
2. Clique em **Settings** (Configurações) na barra superior.
3. No menu lateral esquerdo, vá em **Secrets and variables** > **Actions**.
4. Clique no botão verde **New repository secret**.
5. Preencha os campos:
   - **Name**: `GOOGLE_SERVICES_JSON` (tem que ser exatamente assim)
   - **Secret**: Cole o código Base64 que você copiou no Passo 1.
6. Clique em **Add secret**.

## 🚀 Como Verificar o Build

1. Faça um commit e push das suas alterações para o GitHub.
2. Vá para a aba **Actions** no seu repositório.
3. Você verá o workflow "Android Build" rodando.
4. Se ficar verde (✅), o build passou!
5. Você pode clicar no build e baixar o **app-debug** na seção "Artifacts" para testar o APK gerado.
