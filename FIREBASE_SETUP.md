# Configuração do Firebase - Lista de Compras

Este guia explica passo a passo como configurar o Firebase para o aplicativo de Lista de Compras.

## 1. Criar Projeto no Firebase Console

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Clique em "Adicionar projeto" ou "Create a project"
3. Digite o nome do projeto: **"Shopping List"** (ou outro nome de sua preferência)
4. (Opcional) Ative o Google Analytics se desejar
5. Clique em "Criar projeto"

## 2. Adicionar o Aplicativo Android ao Projeto

1. No painel do projeto, clique no ícone do Android para adicionar um app Android
2. Preencha os campos:
   - **Nome do pacote Android**: `com.shoppinglist`
   - **Apelido do app**: Shopping List (opcional)
   - **Certificado de assinatura SHA-1**: Deixe em branco por enquanto (necessário apenas para autenticação social)
3. Clique em "Registrar app"

## 3. Baixar o arquivo google-services.json

1. Após registrar o app, faça o download do arquivo `google-services.json`
2. **IMPORTANTE**: Coloque este arquivo na pasta `app/` do seu projeto Android
   ```
   APP KOTLIN/
   └── app/
       └── google-services.json  ← AQUI
   ```

## 4. Ativar o Firebase Authentication

1. No menu lateral, vá para **Build > Authentication**
2. Clique em "Get started"
3. Na aba "Sign-in method", ative:
   - **Email/Password** (clique em "Enable" e depois "Save")

## 5. Configurar o Cloud Firestore

1. No menu lateral, vá para **Build > Firestore Database**
2. Clique em "Create database"
3. Escolha o modo:
   - **Production mode** (recomendado) ou **Test mode** (apenas para desenvolvimento)
4. Selecione a localização do servidor (escolha a mais próxima do Brasil, ex: `southamerica-east1`)
5. Clique em "Enable"

## 6. Configurar Regras de Segurança do Firestore

Após criar o banco de dados, configure as regras de segurança:

1. Vá para a aba **Rules** no Firestore Database
2. Substitua as regras padrão pelas seguintes:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Permitir leitura/escrita apenas para usuários autenticados
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
    
    // Regras específicas para usuários
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Regras para famílias
    match /families/{familyId} {
      allow read: if request.auth != null && 
                     request.auth.uid in resource.data.memberIds;
      allow write: if request.auth != null;
    }
    
    // Regras para lista atual
    match /currentLists/{familyId} {
      allow read, write: if request.auth != null;
    }
    
    // Regras para histórico
    match /history/{historyId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

3. Clique em "Publish" para salvar as regras

## 7. ⚠️ **IMPORTANTE**: Criar Índice Composto do Firestore

Para que o histórico de compras funcione corretamente, você **PRECISA** criar um índice composto no Firestore:

### Por que é necessário?

O aplicativo precisa:
1. Filtrar o histórico por `familyId` (para mostrar apenas compras da sua família)
2. Ordenar por `completedAt` (data de conclusão, mais recente primeiro)

O Firestore exige um índice para consultas que combinam filtros com ordenação.

### Como criar o índice

**Opção 1: Link Automático (Recomendado)**

1. Execute o aplicativo e tente visualizar o Histórico
2. O Firestore detectará a consulta e mostrará um erro no Logcat com um **link direto**
3. Clique no link para criar o índice automaticamente
4. Aguarde alguns minutos para o índice ficar pronto

**Opção 2: Criação Manual**

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Vá para **Firestore Database → Indexes**
3. Clique em **Create Index**
4. Configure:
   - **Collection ID**: `history`
   - **Fields to index**:
     - Campo 1: `familyId` → **Ascending**
     - Campo 2: `completedAt` → **Descending**
   - **Query scopes**: Collection
5. Clique em **Create**
6. Aguarde o status mudar para "Enabled" (pode levar alguns minutos)

**Exemplo visual da configuração:**

```
Collection ID:    history
Fields indexed:   
  - familyId      [Ascending]
  - completedAt   [Descending]
Query scope:      Collection
```

### Verificar se funcionou

Após criar o índice:
1. Finalize uma nova compra no app
2. Vá para a aba "Histórico"
3. A compra deve aparecer com a data correta

## 8. Estrutura das Coleções no Firestore

> **⚠️ IMPORTANTE: Você NÃO precisa criar as coleções manualmente!**
>
> No Firestore, as coleções são criadas **automaticamente** quando o aplicativo salva o primeiro documento nelas. 
>
> Basta executar o aplicativo e usar as funcionalidades (criar conta, criar família, adicionar itens) que as coleções aparecerão automaticamente no Firebase Console conforme você usa o app.

### Como verificar se as coleções foram criadas

1. Execute o aplicativo no Android Studio
2. Crie uma conta de teste no app
3. Crie uma família ou entre em uma
4. Adicione alguns itens à lista
5. Abra o Firebase Console → **Firestore Database**
6. Você verá as coleções aparecerem automaticamente: `users`, `families`, `currentLists`

---

### Estrutura de Dados (Referência)

Abaixo está a estrutura de cada coleção que o aplicativo criará automaticamente:

### `users`
Armazena informações dos usuários
```json
{
  "email": "usuario@example.com",
  "name": "Nome do Usuário",
  "familyId": "id_da_familia"
}
```

### `families`
Grupos familiares que compartilham listas
```json
{
  "name": "Família Silva",
  "memberIds": ["userId1", "userId2"],
  "inviteCode": "ABC123",
  "ownerId": "userId1"
}
```

### `currentLists`
Lista de compras ativa (uma por família)
```json
{
  "familyId": "family123",
  "items": [
    {
      "name": "Arroz",
      "quantity": 2,
      "unit": "KG",
      "price": 15.50,
      "imageUrl": null,
      "isChecked": false,
      "createdAt": "timestamp"
    }
  ],
  "status": "ACTIVE",
  "createdAt": "timestamp",
  "totalValue": 15.50
}
```

### `history`
Histórico de compras finalizadas
```json
{
  "familyId": "family123",
  "items": [...],
  "status": "COMPLETED",
  "createdAt": "timestamp",
  "completedAt": "timestamp",
  "totalValue": 150.00
}
```

## 9. Testando a Configuração

1. Abra o projeto no Android Studio
2. Sincronize o Gradle (Sync Project with Gradle Files)
3. Execute o aplicativo em um emulador ou dispositivo físico
4. Crie uma conta de teste
5. Verifique no Firebase Console se:
   - O usuário aparece em **Authentication > Users**
   - Os documentos são criados em **Firestore Database**

## 10. Solução de Problemas

### Erro: "google-services.json não encontrado"
- Certifique-se de que o arquivo está em `app/google-services.json`
- Sincronize o Gradle novamente

### Erro de autenticação
- Verifique se o Email/Password está ativado no Firebase Authentication
- Verifique se o nome do pacote corresponde: `com.shoppinglist`

### Erro de permissão no Firestore
- Verifique as regras de segurança
- Para testes, você pode temporariamente usar regras permissivas (NÃO recomendado para produção):
```javascript
allow read, write: if true;
```

### Histórico vazio ou sem datas
- **SOLUÇÃO**: Crie o índice composto conforme descrito na seção 7
- Verifique se o índice está com status "Enabled" no Firebase Console

## 11. Próximos Passos

Após concluir a configuração, você pode:
- Adicionar mais métodos de autenticação (Google, Facebook, etc.)
- Configurar índices compostos adicionais para consultas complexas
- Implementar Cloud Functions para lógica server-side
- Adicionar Firebase Storage para armazenar imagens de produtos

## 12. Recursos Adicionais

- [Documentação oficial do Firebase](https://firebase.google.com/docs)
- [Firebase Authentication](https://firebase.google.com/docs/auth/android/start)
- [Cloud Firestore](https://firebase.google.com/docs/firestore/quickstart)
- [Firestore Indexes](https://firebase.google.com/docs/firestore/query-data/indexing)
