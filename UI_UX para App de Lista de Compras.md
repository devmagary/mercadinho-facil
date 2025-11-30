

# **Relatório de Inteligência de Design: Modernização de UI/UX para Aplicações de Gestão de Compras e Análise Financeira no Ecossistema Android (2025)**

## **1\. Introdução: O Imperativo da Estética Funcional na Era Pós-Material 3**

O ecossistema de desenvolvimento Android atravessa, em meados da década de 2020, uma transformação paradigmática. A introdução e maturação do Jetpack Compose como toolkit declarativo de UI democratizou a construção de interfaces complexas, permitindo que desenvolvedores implementem o Material Design 3 (M3) com velocidade sem precedentes. No entanto, essa acessibilidade trouxe consigo um efeito colateral paradoxal: a homogeneização estética. Desenvolvedores que aderem estritamente aos componentes padrão do M3, sem uma camada robusta de personalização (*theming*), frequentemente produzem aplicações que, embora funcionalmente competentes e acessíveis, carecem de identidade, sofisticação e apelo emocional. No vernáculo dos usuários finais e críticos de design, essas aplicações são rotuladas como "genéricas" ou, mais asperamente, "feias".

Para o contexto específico de uma aplicação que visa unificar a **Gestão de Listas de Compras** (atividade utilitária, de alta frequência e baixa latência) com a **Análise de Compras de Mercadinho** (atividade analítica, reflexiva e orientada a dados), o desafio de design é exponencialmente complexo. O mercado atual não tolera mais a dicotomia entre "ferramenta útil mas feia" e "app bonito mas inútil". A convergência de expectativas, impulsionada por super-apps financeiros como Revolut e Nubank, e refinada por utilitários de nicho como AnyList e YNAB (*You Need A Budget*), estabeleceu um novo padrão onde a beleza visual é indissociável da usabilidade.

Este relatório analisa exaustivamente o estado da arte do design de interfaces móveis para 2025\. O objetivo é fornecer um compêndio detalhado de estratégias visuais, padrões de arquitetura de informação e implementações técnicas no Jetpack Compose que permitam transcender o visual padrão ("default look") e criar um produto competitivo. A análise abrange desde a psicologia das cores em contextos de varejo e finanças até a implementação técnica de gráficos vetoriais animados e layouts modulares tipo "Bento Grid", focando na eliminação da fricção visual e na maximização do engajamento do usuário.

---

## **2\. O Zeitgeist do Design Móvel em 2025: Além da Utilidade Pura**

A percepção de valor de um aplicativo em 2025 está intrinsecamente ligada à sua apresentação visual. A era do design puramente "Flat" (plano), que dominou a década de 2010 como resposta ao skeumorfismo, evoluiu para uma linguagem mais rica, tátil e profunda. As tendências atuais indicam um movimento pendular em direção a interfaces que convidam ao toque, utilizando profundidade, movimento e personalização extrema para criar uma conexão emocional.

### **2.1 A Evolução do Material Design: Do Padrão ao Expressivo**

O Material Design 3, em sua concepção original, focou fortemente na adaptabilidade (cores dinâmicas extraídas do papel de parede) e na acessibilidade. Contudo, a aplicação crua do M3 resulta em interfaces que parecem "protótipos do Google". A resposta da comunidade de design e da própria evolução do sistema é o **Material Expressive**. Este paradigma não descarta as fundações do M3, mas as expande para permitir uma identidade de marca mais forte.1

A crítica comum de que um app feito em Compose \+ M3 parece "feio" geralmente decorre da falta de intervenção nas propriedades padrão de *Shape* (forma), *Color* (cor) e *Typography* (tipografia). O M3 Expressive encoraja o abandono das formas seguras. Em vez de cantos arredondados uniformes (ex: 12dp), observa-se o uso de "Squircles" (superelipses) e formas morficas que reagem à interação. A física do movimento também mudou: animações lineares são substituídas por curvas de mola (*spring physics*) que dão peso e inércia aos elementos, fazendo com que a interface pareça um organismo vivo e responsivo, não uma série de slides estáticos.4

### **2.2 Neumorfismo 2.0 e a Profundidade Tátil**

Após uma tentativa falha de popularização em 2020 devido a problemas de contraste, o Neumorfismo retornou em 2025 de forma mais madura e sutil, muitas vezes chamado de "Soft UI" ou "Neumorfismo 2.0". Para um app de controle financeiro e compras, isso é vital. Elementos de interface como cartões de despesas ou botões de "check" na lista de compras não são mais apenas retângulos coloridos; eles possuem sombras suaves (*soft shadows*) e luzes especulares que sugerem elevação e depressão tátil.6

Esta abordagem resolve um problema crítico de UX em apps de lista: a affordance (a dica visual de como um objeto deve ser usado). Em uma lista de compras densa, um item que parece ligeiramente elevado do fundo convida o usuário a interagir (deslizar, tocar), enquanto um design puramente *flat* pode dificultar a distinção entre áreas interativas e informativas. A implementação no Jetpack Compose exige o abandono da elevação padrão baseada em tonalidade (*tonal elevation*) do M3 em favor de sombras customizadas via Modifier.shadow e bordas sutis, criando uma estética mais limpa e moderna.8

### **2.3 Glassmorphism: A Textura da Modernidade**

O Glassmorphism (efeito de vidro fosco) consolidou-se como a textura definidora de interfaces premium em 2025\. Inspirado nas diretrizes do iOS e adotado seletivamente no Android, este estilo utiliza fundos translúcidos com desfoque (*background blur*) para criar hierarquia visual sem bloquear o contexto. Em um aplicativo de "mercadinho", o Glassmorphism pode ser aplicado em painéis flutuantes que mostram o total da compra sobreposta à lista de itens, ou em *bottom sheets* de detalhes do produto.7

A técnica cria uma sensação de espaço tridimensional. Ao invés de cobrir a lista de compras com um painel sólido opaco (que desconecta o usuário da sua tarefa), o painel de vidro fosco mantém a lista visível, porém desfocada, no fundo. Isso mantém a continuidade cognitiva. No Jetpack Compose, a implementação técnica evoluiu de "hacks" complexos para o uso mais direto de Modifier.blur e RenderEffect (no Android 12+), ou simulações visuais com gradientes de alfa e ruído (*noise texture*) para compatibilidade retroativa e performance.10

### **2.4 Minimalismo Exagerado e "Clean Design"**

Paradoxalmente, enquanto as texturas ficam mais ricas, o layout fica mais limpo. A tendência de "Clean Design" em 2025 prega a remoção impiedosa de linhas divisórias (*dividers*), bordas pesadas e cores de fundo contrastantes desnecessárias. A separação entre elementos é feita puramente através do espaço em branco (*whitespace*) generoso. Para uma lista de compras, isso significa que as linhas horizontais que tradicionalmente separam os itens (como em um caderno) são removidas, permitindo que o texto e os ícones respirem. A estrutura é sugerida pelo alinhamento e proximidade, não por linhas explícitas.6

Este minimalismo estende-se à visualização de dados. Gráficos financeiros em apps modernos evitam eixos X e Y visíveis, linhas de grade (*grid lines*) e legendas excessivas. O dado é apresentado como uma "ilustração", focado na tendência e no valor final, reduzindo a carga cognitiva do usuário que apenas quer saber se "gastou muito ou pouco".14

---

## **3\. Arquitetura de Informação Visual: A Revolução das Bento Grids**

Talvez a mudança mais impactante para a estrutura visual de *dashboards* em 2025 seja a onipresença das **Bento Grids**. Inspiradas nas lancheiras japonesas compartimentalizadas, e popularizadas no design digital por materiais promocionais da Apple e interfaces de sistemas como widgets do iOS, as Bento Grids oferecem uma solução elegante para o problema da "tela inicial" em apps multifuncionais.15

### **3.1 O Problema dos Dashboards Tradicionais**

Em aplicativos de gestão tradicionais, a tela inicial geralmente sofre de uma crise de identidade. Ou é uma lista cronológica infinita (difícil de analisar globalmente), ou é uma série de cartões de tamanho idêntico em um carrossel (monótono e com baixa densidade de informação). Para um app que combina "Lista de Compras" (ação imediata) e "Análise Financeira" (reflexão), nenhum desses modelos serve perfeitamente. O usuário precisa de acesso rápido à lista, mas também quer ver o resumo financeiro sem ter que navegar profundamente.

### **3.2 A Solução Bento: Modularidade e Hierarquia**

A Bento Grid resolve isso transformando a tela inicial em um mosaico de blocos retangulares de tamanhos variados, baseados em uma grade subjacente. Cada bloco, ou "célula", contém um tipo diferente de informação ou funcionalidade, hierarquizado pelo tamanho.17

#### **Estrutura Sugerida para App de Mercadinho:**

| Tamanho da Célula | Conteúdo Sugerido | Propósito UX |
| :---- | :---- | :---- |
| **Grande (2x2 ou 2x1)** | **Resumo da Lista Ativa:** "12 itens pendentes no Supermercado X". Exibe os primeiros 3 itens e uma barra de progresso circular. | Foco principal. Permite ver o status da missão atual num relance. |
| **Médio (1x2 Vertical)** | **Gastos do Mês (Gráfico):** Um mini gráfico de barras ou linha mostrando a evolução dos gastos nos últimos 30 dias. | Insight financeiro rápido. Verticalidade aproveita o espaço para mostrar tendências. |
| **Pequeno (1x1)** | **Ação Rápida:** Botão gigante de "+" ou ícone de scanner de código de barras. | Acesso "Thumb-first" para a ação mais comum: adicionar item. |
| **Pequeno (1x1)** | **Insight Flash:** "Leite subiu 10%". Um cartão de alerta com fundo colorido. | Notificação contextual que agrega valor analítico imediato. |
| **Largo (2x1 Horizontal)** | **Orçamento Restante:** Barra de progresso linear mostrando quanto falta para estourar o orçamento do mês. | Controle financeiro passivo. |

### **3.3 Implementação e Estética no Compose**

No Jetpack Compose, a Bento Grid não é um componente único, mas um padrão construído geralmente com LazyVerticalStaggeredGrid. A estética "bonita" vem de três fatores cruciais:

1. **Espaçamento Consistente (Gutter):** O espaço entre as células deve ser rigoroso (ex: 12dp ou 16dp) e idêntico ao *padding* das bordas da tela.  
2. **Arredondamento Uniforme:** Todas as células devem ter o mesmo raio de borda (ex: RoundedCornerShape(24.dp)), criando uma harmonia visual suave.  
3. **Consistência de Cor de Fundo:** As células geralmente compartilham a mesma cor de superfície (um tom levemente mais claro que o fundo da tela no modo escuro, ou *off-white* no modo claro), unificando o mosaico.19

Esta abordagem transforma dados áridos (números e listas) em uma interface convidativa, explorável e visualmente rica, alinhada com as melhores referências de design de 2025 como os widgets do Notion ou o painel de controle do iOS.15

---

## **4\. Análise de Domínio: UX de Listas de Compras de Alta Performance**

Para deixar de ser "feio", o aplicativo deve primeiro deixar de ser "básico". A análise dos líderes de mercado como AnyList, Listonic e Out of Milk revela padrões de UX que elevam a percepção de qualidade do produto. A beleza nestes apps emerge da sua extrema funcionalidade apresentada com elegância.21

### **4.1 O Item de Lista Enriquecido (*Rich List Item*)**

O padrão básico do Material 3 para listas (ListItem) é funcional, mas visualmente pobre para este contexto. O mercado exige "Items Ricos".

* **Iconografia Semântica:** Em vez de um ícone genérico de "comida", o app deve identificar que o usuário digitou "Maçã" e mostrar um ícone específico de maçã. Se não houver ícone, as iniciais coloridas em um círculo (*Avatar*) são o mínimo aceitável. Apps visualmente superiores usam ilustrações 3D ou emojis grandes como ícones, tornando a lista colorida e fácil de escanear visualmente.13  
* **Tipografia Hierárquica:** O nome do produto deve ser proeminente (Bold, tamanho 16-18sp). Detalhes como quantidade, marca ou preço estimado devem estar em uma linha secundária, com cor mais clara e tamanho menor, mas ainda legível.  
* **Micro-interações de Conclusão:** A "feiura" muitas vezes está na estática. Quando um item é marcado, ele não deve simplesmente mudar de estado instantaneamente. A tendência atual é uma animação onde o texto esmaece (*fade out* para cinza), uma linha de risco (*strikethrough*) é desenhada animadamente sobre o texto, e o item desliza suavemente para o final da lista ou para uma seção "Carrinho". Isso fornece feedback tátil e visual de progresso.6

### **4.2 Agrupamento Visual Inteligente**

Uma lista plana de 50 itens é visualmente caótica e intimidante (portanto, "feia"). A organização visual é fundamental.

* **Categorização Automática:** O design deve agrupar itens por "Corredor" (Laticínios, Hortifruti, Limpeza).  
* **Cabeçalhos Expressivos:** Os separadores de categoria não devem ser apenas texto cinza pequeno. Eles podem ser elementos coloridos, com ícones grandes e fundos sutis que codificam a seção por cor (ex: Azul para Congelados, Verde para Vegetais). Isso quebra a monotonia da lista branca e cria ritmo visual.21

### **4.3 Navegação "Thumb-First" (Polegar Primeiro)**

Considerando o uso do app em movimento (dentro do mercado, empurrando carrinho), a ergonomia dita a estética. Elementos cruciais no topo da tela são "feios" do ponto de vista de usabilidade moderna.

* **Bottom Sheets vs. Dialogs:** Evite diálogos modais que aparecem no centro da tela para editar itens. Use *Bottom Sheets* (painéis que deslizam de baixo) com cantos arredondados no topo. Isso é mais elegante e acessível ao polegar.  
* **Floating Action Bar (FAB) Expandido:** O botão de adicionar é o coração do app. Em 2025, a tendência é o FAB expandido ou uma barra de ferramentas flutuante inferior (*Floating Toolbar*) que contém ações contextuais, flutuando acima da lista com uma sombra suave e efeito de vidro.2

---

## **5\. Análise de Domínio: Fintech e Visualização de Dados (Data Viz)**

A parte de "análise de compras" é onde o app corre o maior risco de parecer uma ferramenta corporativa entediante. A referência aqui não são bancos tradicionais, mas neobancos e apps de gestão pessoal como YNAB, Dime e Revolut.26

### **5.1 A Estética "Fintech Moderna"**

A estética fintech de 2025 afasta-se da frieza dos números puros. Ela humaniza os dados.

* **Tipografia Macro:** O saldo gasto ou a economia mensal deve ser apresentado com tipografia massiva (ex: 40sp+), muitas vezes usando fontes com kerning apertado e pesos pesados (*Inter Tight*, *Satoshi Black*). Isso transforma o número em um elemento gráfico.8  
* **Ilustração de Dados:** Gráficos não são apenas ferramentas de análise; são elementos decorativos. Um gráfico de linha não deve ser uma linha fina e pixelada. Deve ser uma curva Bézier suave (*spline*), com espessura generosa (3dp+), e frequentemente com um preenchimento degradê abaixo dela (*gradient fill*) que vai da cor da linha até a transparência total. Isso ancora o gráfico visualmente.30

### **5.2 Bibliotecas de Gráficos no Jetpack Compose**

A escolha da biblioteca define o limite do que é possível esteticamente. Bibliotecas antigas baseadas em *Views* (como MPAndroidChart) tendem a ter um visual datado e dificuldade de integração com o sistema de temas do Compose.

| Biblioteca | Estilo Visual | Recomendação para 2025 |
| :---- | :---- | :---- |
| **Vico** | Moderno, minimalista, curvas suaves. Suporta *brushes* do Compose (degradês). Eixos customizáveis e limpos. | **Altamente Recomendada.** É a referência atual para gráficos bonitos e performáticos em Kotlin/Compose. Permite criar visuais idênticos aos do iOS Health ou Robinhood. 32 |
| **Compose Charts (Ehsan Narmani)** | Focado em animações ricas e interatividade. Ótimo para gráficos de "Donut" (Pizza) e Barras arredondadas. | Excelente para dashboards visuais e coloridos. Suporta estilos de "curva" e animações de entrada que dão vida à análise. 30 |
| **YCharts** | Funcional e robusta, mas exige mais trabalho de estilização para fugir do padrão "científico". | Útil se precisar de gráficos muito complexos combinados, mas menos "bonita" *out-of-the-box*. 32 |

### **5.3 Padrões de Gráficos para Compras**

* **Donut Chart (Rosca) \> Pie Chart (Pizza):** O gráfico de pizza sólido é considerado antiquado. O gráfico de rosca, com segmentos de pontas arredondadas (*rounded caps*) e espessura grossa, é o padrão moderno. O espaço vazio no centro é usado para exibir o valor total ou o ícone da categoria selecionada, maximizando a densidade de informação.30  
* **Bar Chart com Cantos Arredondados:** Gráficos de barra nunca devem ser retângulos afiados. O topo das barras deve ser totalmente arredondado. O fundo das barras pode ter uma "trilha" (*track*) cinza clara para indicar o máximo possível, criando um visual de medidor.14

---

## **6\. Sistema de Identidade Visual: Cores, Tipografia e Ícones**

Para curar a "feiura", é necessário definir um *Design System* que substitua os padrões do Material 3\.

### **6.1 Psicologia das Cores e Paletas 2025**

A cor padrão do M3 (Roxo/Lavanda) é imediatamente reconhecível e genérica. Para um app híbrido de Compras (Alimento/Frescor) e Finanças (Confiança/Dinheiro), a paleta deve ser intencional.

#### **Paletas Recomendadas (Hex Codes):**

* **Paleta Primária (Frescor & Ação):** Focada na lista de compras.  
  * *Verde Menta Moderno:* \#90CF44 ou \#00C853 (vibrante, mas não neon). Associa-se a produtos frescos e "check" de sucesso.37  
  * *Coral Energético:* \#F16B44 ou \#FF8D27. Usado para destaques, promoções ou botões de ação flutuantes (FAB). O laranja estimula o apetite e a ação impulsiva, útil para compras.38  
* **Paleta Secundária (Confiança & Análise):** Focada na aba de finanças.  
  * *Azul Profundo / Índigo:* \#003366 ou \#5E81D1. Transmite seriedade e calma para análise de dados. Evita a ansiedade causada por vermelhos em gráficos financeiros.40  
  * *Roxo Fintech:* \#6C63FF (blurple). Uma cor muito associada a bancos digitais modernos (Nubank, Revolut), sugerindo tecnologia e inovação.41  
* **Paleta de Base (Neutros & Dark Mode):**  
  * Evite \#000000 puro. Use *Gunmetal* (\#111920) ou *Charcoal* (\#121212) para fundos escuros. Isso reduz o contraste agressivo e o "smearing" em telas OLED.  
  * Para superfícies (cartões), use um tom levemente mais claro (\#1E1E1E) com uma pitada da cor primária misturada (ex: 2% de azul) para criar harmonia cromática.13

### **6.2 Tipografia: A Voz do Aplicativo**

A fonte padrão do Android, *Roboto*, é funcional mas ubiquamente associada ao "padrão Google". Mudar a fonte é a maneira mais rápida de mudar a personalidade do app.

* **Tendências 2025:** Fontes Sans-Serif Geométricas ou Neo-Grotescas. Elas são limpas, modernas e altamente legíveis em tamanhos pequenos.  
* **Recomendações:**  
  * **Satoshi:** Minimalista, com toques únicos. Excelente para dashboards modernos.44  
  * **General Sans:** Elegante e estruturada, traz um ar mais "profissional" para a parte financeira.44  
  * **Figtree:** Amigável e contemporânea, ótima para listas de compras menos formais.44  
* **Técnica no Compose:** Utilize GoogleFont (disponível no Compose) para carregar essas fontes dinamicamente sem aumentar o tamanho do APK. Configure o Typography do MaterialTheme para usar pesos variados (*Light* para rótulos, *Medium* para corpo, *Bold* para valores).

### **6.3 Ilustrações e Empty States**

Telas vazias (lista zerada, sem histórico) são momentos críticos. O texto "Lista Vazia" é uma oportunidade perdida.

* **Ilustrações 3D/Vetoriais:** Use ilustrações coloridas de cestas de compras, personagens calculando gastos ou vegetais estilizados. O estilo "Claymorphism" (3D fofo e arredondado) está em alta. Isso adiciona "alma" ao aplicativo.13

---

## **7\. Implementação Técnica no Jetpack Compose: O Código da Beleza**

A tradução destes conceitos para código Kotlin exige o domínio de modificadores e APIs específicas do Compose.

### **7.1 Removendo a "Cara de Default"**

O componente Card do M3 vem com sombra e cor baseada no papel de parede.

* **Personalização de Card:** Crie um composable CustomCard. Remova a elevação padrão (elevation \= CardDefaults.cardElevation(defaultElevation \= 0.dp)). Adicione uma borda sutil (border \= BorderStroke(1.dp, Color.Gray.copy(alpha \= 0.1f))) ou use um fundo com baixa opacidade. Isso cria o visual "flat" e limpo desejado.1

### **7.2 Implementando Glassmorphism**

Para criar o efeito de vidro fosco em cartões de destaque:

* Utilize um Box como container.  
* No Android 12+, aplique Modifier.blur(radius \= 16.dp) no conteúdo que ficará *atrás* do vidro, ou use RenderEffect no Modifier do container.  
* Como fallback (e para performance), use um Brush.verticalGradient no background do cartão, indo de Color.White.copy(alpha \= 0.4f) para Color.White.copy(alpha \= 0.1f). Adicione uma borda fina branca com gradiente para simular o reflexo da luz na borda do vidro.12

### **7.3 Animações Expressivas**

A beleza também é sentida. Use animateContentSize nos itens da lista para que, quando expandidos, a mudança seja fluida. Use AnimatedVisibility para mostrar/ocultar gráficos. Configure spring(stiffness \= Spring.StiffnessLow) para que as animações tenham um balanço suave, evitando a rigidez mecânica das animações lineares padrão.5

---

## **8\. Conclusão e Roteiro de Execução**

A percepção de que o aplicativo está "muito feio" é um sintoma de adesão excessiva aos padrões utilitários do Material Design 3 sem a camada necessária de expressão de marca e refinamento visual. O mercado de 2025 exige que ferramentas utilitárias, como listas de compras e gestores financeiros, proporcionem prazer estético e clareza cognitiva.

**Roteiro de Ação Imediata:**

1. **Refatoração da Identidade:** Substitua a fonte Roboto por **Satoshi** ou **General Sans**. Defina uma paleta de cores híbrida (Verde Menta/Azul Índigo) e abandone as cores dinâmicas aleatórias do sistema para elementos chave.  
2. **Reconstrução da Home:** Implemente uma **Bento Grid** usando LazyVerticalStaggeredGrid. Crie widgets modulares para "Resumo da Lista", "Gasto Mensal" e "Ações Rápidas".  
3. **Adoção de Data Viz Moderna:** Integre a biblioteca **Vico**. Substitua gráficos de barras padrão por curvas *spline* suaves com preenchimento gradiente. Adote gráficos de rosca (*Donut*) com cantos arredondados.  
4. **Enriquecimento da Lista:** Transforme o ListItem simples em um cartão interativo com ícones semânticos, tipografia hierárquica e animações de conclusão ricas (risco animado \+ fade).  
5. **Polimento de Textura:** Introduza elementos de **Glassmorphism** em cabeçalhos e barras de navegação flutuantes. Remova sombras pesadas em favor de bordas sutis e cores de fundo suaves.

Ao seguir este roteiro, o aplicativo não apenas deixará de ser genérico, mas se alinhará com as tendências visuais mais sofisticadas do mercado global, oferecendo uma experiência de usuário que inspira confiança, eficiência e satisfação visual.

#### **Referências citadas**

1. Material Design 3 in Compose | Jetpack Compose \- Android Developers, acessado em novembro 29, 2025, [https://developer.android.com/develop/ui/compose/designsystems/material3](https://developer.android.com/develop/ui/compose/designsystems/material3)  
2. Start building with Material 3 Expressive, acessado em novembro 29, 2025, [https://m3.material.io/blog/building-with-m3-expressive](https://m3.material.io/blog/building-with-m3-expressive)  
3. Compose Material 3 Expressive \- YLabZ, acessado em novembro 29, 2025, [https://zoewave.medium.com/compose-material-3-expressive-89f4147df5b8](https://zoewave.medium.com/compose-material-3-expressive-89f4147df5b8)  
4. Material Design 3 \- Google's latest open source design system, acessado em novembro 29, 2025, [https://m3.material.io/](https://m3.material.io/)  
5. WearOS Material 3 shape morphing | Jetpack Compose Tips \- YouTube, acessado em novembro 29, 2025, [https://www.youtube.com/watch?v=qEEo6AwgBjU](https://www.youtube.com/watch?v=qEEo6AwgBjU)  
6. Top Mobile App Design Trends You Should Watch For In 2025 \- Natively, acessado em novembro 29, 2025, [https://natively.dev/blog/top-mobile-app-design-trends-2025](https://natively.dev/blog/top-mobile-app-design-trends-2025)  
7. 12 Mobile App UI/UX Design Trends for 2025, acessado em novembro 29, 2025, [https://www.designstudiouiux.com/blog/mobile-app-ui-ux-design-trends/](https://www.designstudiouiux.com/blog/mobile-app-ui-ux-design-trends/)  
8. Top Mobile App Design Trends to Watch in 2025 | by Carlos Smith \- Medium, acessado em novembro 29, 2025, [https://medium.com/@CarlosSmith24/top-mobile-app-design-trends-to-watch-in-2025-e95f633cd6ef](https://medium.com/@CarlosSmith24/top-mobile-app-design-trends-to-watch-in-2025-e95f633cd6ef)  
9. UI UX Design Trends in 2025 \- Mobivery, acessado em novembro 29, 2025, [https://mobivery.com/en/ui-ux-design-trends-in-2025/](https://mobivery.com/en/ui-ux-design-trends-in-2025/)  
10. Create STUNNING Glassmorphism UI in Android Studio with Jetpack Compose FAST\!, acessado em novembro 29, 2025, [https://www.youtube.com/watch?v=07Ks-3UBAzU](https://www.youtube.com/watch?v=07Ks-3UBAzU)  
11. How to Achieve a Glassmorphic Background in Jetpack Compose Android? \- Stack Overflow, acessado em novembro 29, 2025, [https://stackoverflow.com/questions/78740780/how-to-achieve-a-glassmorphic-background-in-jetpack-compose-android](https://stackoverflow.com/questions/78740780/how-to-achieve-a-glassmorphic-background-in-jetpack-compose-android)  
12. Quick & Easy Glass Effects in Jetpack Compose | by Joseph Sanjaya | Oct, 2025 | Medium, acessado em novembro 29, 2025, [https://medium.com/@sanjayajosep/quick-easy-glass-effects-in-jetpack-compose-9bf0b9f76aa5](https://medium.com/@sanjayajosep/quick-easy-glass-effects-in-jetpack-compose-9bf0b9f76aa5)  
13. UI/UX Design Trends in Mobile Apps for 2025 | Chop Dawg, acessado em novembro 29, 2025, [https://www.chopdawg.com/ui-ux-design-trends-in-mobile-apps-for-2025/](https://www.chopdawg.com/ui-ux-design-trends-in-mobile-apps-for-2025/)  
14. 7 Latest Fintech UX Design Trends & Case Studies for 2025, acessado em novembro 29, 2025, [https://www.designstudiouiux.com/blog/fintech-ux-design-trends/](https://www.designstudiouiux.com/blog/fintech-ux-design-trends/)  
15. Best Bento Grid Design Examples \[2025\] \- Mockuuups Studio, acessado em novembro 29, 2025, [https://mockuuups.studio/blog/post/best-bento-grid-design-examples/](https://mockuuups.studio/blog/post/best-bento-grid-design-examples/)  
16. Embracing the Bento Grid: A Modern Approach to UI Layouts | by Jaco Verdini \- Prototypr, acessado em novembro 29, 2025, [https://blog.prototypr.io/embracing-the-bento-grid-a-modern-approach-to-ui-layouts-4a15f618e751](https://blog.prototypr.io/embracing-the-bento-grid-a-modern-approach-to-ui-layouts-4a15f618e751)  
17. Bento Grid Design Inspiration: 40+ Graphic & Web Design Examples (2025), acessado em novembro 29, 2025, [https://mukeshkdesigns.com/blogs/bento-grid-design-inspiration/](https://mukeshkdesigns.com/blogs/bento-grid-design-inspiration/)  
18. Web design trend: bento box \- Medium, acessado em novembro 29, 2025, [https://medium.com/design-bootcamp/web-design-trend-bento-box-95814d99ac62](https://medium.com/design-bootcamp/web-design-trend-bento-box-95814d99ac62)  
19. Bento Grids designs, themes, templates and downloadable graphic elements on Dribbble, acessado em novembro 29, 2025, [https://dribbble.com/tags/bento-grids](https://dribbble.com/tags/bento-grids)  
20. Bento Grids, acessado em novembro 29, 2025, [https://bentogrids.com/](https://bentogrids.com/)  
21. AnyList: Grocery Shopping List \- Apps on Google Play, acessado em novembro 29, 2025, [https://play.google.com/store/apps/details?id=com.purplecover.anylist\&hl=en\_US](https://play.google.com/store/apps/details?id=com.purplecover.anylist&hl=en_US)  
22. Shopping List \- Listonic \- Apps on Google Play, acessado em novembro 29, 2025, [https://play.google.com/store/apps/details?id=com.l\&hl=en\_US](https://play.google.com/store/apps/details?id=com.l&hl=en_US)  
23. Out of Milk \- Free Grocery Shopping List App, acessado em novembro 29, 2025, [https://outofmilk.com/](https://outofmilk.com/)  
24. Top Data Visualization Trends for 2025 | Fuselab Creative, acessado em novembro 29, 2025, [https://fuselabcreative.com/top-data-visualization-trends-2025/](https://fuselabcreative.com/top-data-visualization-trends-2025/)  
25. Material 3 Expressive Design: A New Era Part 2 | by Stefano Natali | ProAndroidDev, acessado em novembro 29, 2025, [https://proandroiddev.com/material-3-expressive-design-a-new-era-part-2-6a93483c98b0](https://proandroiddev.com/material-3-expressive-design-a-new-era-part-2-6a93483c98b0)  
26. What grocery apps do you love? : r/ynab \- Reddit, acessado em novembro 29, 2025, [https://www.reddit.com/r/ynab/comments/1hq7ow2/what\_grocery\_apps\_do\_you\_love/](https://www.reddit.com/r/ynab/comments/1hq7ow2/what_grocery_apps_do_you_love/)  
27. I built a free SwiftUI app that beautifully combines expense tracking ..., acessado em novembro 29, 2025, [https://www.reddit.com/r/SwiftUI/comments/xtezul/i\_built\_a\_free\_swiftui\_app\_that\_beautifully/](https://www.reddit.com/r/SwiftUI/comments/xtezul/i_built_a_free_swiftui_app_that_beautifully/)  
28. Fintech UX Design Trends in 2025 \- Adam Fard Studio, acessado em novembro 29, 2025, [https://adamfard.com/blog/fintech-ux-trends](https://adamfard.com/blog/fintech-ux-trends)  
29. Bento Grids: The Hottest UI Design Trend for 2025\! \- YouTube, acessado em novembro 29, 2025, [https://www.youtube.com/watch?v=UyJT1E\_vdLY](https://www.youtube.com/watch?v=UyJT1E_vdLY)  
30. Overview \- Compose Charts, acessado em novembro 29, 2025, [https://ehsannarmani.github.io/ComposeCharts/](https://ehsannarmani.github.io/ComposeCharts/)  
31. Building your first Custom Chart in Android with Jetpack Compose \- ProAndroidDev, acessado em novembro 29, 2025, [https://proandroiddev.com/building-your-first-custom-chart-in-android-with-jetpack-compose-a890fb60878b](https://proandroiddev.com/building-your-first-custom-chart-in-android-with-jetpack-compose-a890fb60878b)  
32. jetpack-compose-charts · GitHub Topics, acessado em novembro 29, 2025, [https://github.com/topics/jetpack-compose-charts?o=asc\&s=forks](https://github.com/topics/jetpack-compose-charts?o=asc&s=forks)  
33. How to code Vico charts with Kotlin and Jetpack Compose. The best chart library so far\!, acessado em novembro 29, 2025, [https://www.youtube.com/watch?v=MY290aW8hMQ](https://www.youtube.com/watch?v=MY290aW8hMQ)  
34. Vico: Introduction, acessado em novembro 29, 2025, [https://guide.vico.patrykandpatrick.com/](https://guide.vico.patrykandpatrick.com/)  
35. Best performance Compose Chart library : r/androiddev \- Reddit, acessado em novembro 29, 2025, [https://www.reddit.com/r/androiddev/comments/1ky85q2/best\_performance\_compose\_chart\_library/](https://www.reddit.com/r/androiddev/comments/1ky85q2/best_performance_compose_chart_library/)  
36. Meet “Y-Charts”: an Opensource Jetpack Compose chart library. | by Codeangi \- Medium, acessado em novembro 29, 2025, [https://medium.com/ymedialabs-innovation/meet-ycharts-an-opensource-jetpack-compose-chart-library-2568aeac19fb](https://medium.com/ymedialabs-innovation/meet-ycharts-an-opensource-jetpack-compose-chart-library-2568aeac19fb)  
37. 31 Food Color Palettes for Appetizing Designs, acessado em novembro 29, 2025, [https://www.color-meanings.com/food-color-palettes/](https://www.color-meanings.com/food-color-palettes/)  
38. grocery store Color Palette, acessado em novembro 29, 2025, [https://www.color-hex.com/color-palette/23269](https://www.color-hex.com/color-palette/23269)  
39. Best Colors to Shed Your Delivery App's Image in 2025 \- Zeew, acessado em novembro 29, 2025, [https://zeew.eu/best-colors-to-shed-your-delivery-apps-image-in-2025/](https://zeew.eu/best-colors-to-shed-your-delivery-apps-image-in-2025/)  
40. Stunning Website Color Schemes & CSS Hex Codes for 2025 \- Enveos, acessado em novembro 29, 2025, [https://enveos.com/stunning-website-color-schemes-css-hex-codes-for-2025-trendy-palettes-for-modern-designs/](https://enveos.com/stunning-website-color-schemes-css-hex-codes-for-2025-trendy-palettes-for-modern-designs/)  
41. Best 36 FinTech UI Design Color Palettes | Octet Design Labs, acessado em novembro 29, 2025, [https://octet.design/colors/user-interfaces/fintech-ui-design/](https://octet.design/colors/user-interfaces/fintech-ui-design/)  
42. App Design Color Palette Principles | Ramotion Agency, acessado em novembro 29, 2025, [https://www.ramotion.com/blog/app-design-color-palette/](https://www.ramotion.com/blog/app-design-color-palette/)  
43. 101+ Amazing Website Color Schemes For 2025 (Moodboard) \- Hook Agency, acessado em novembro 29, 2025, [https://hookagency.com/blog/website-color-schemes/](https://hookagency.com/blog/website-color-schemes/)  
44. Best Fonts for Apps in 2025: Top Picks for iOS and Android UI Design \- Frontmatter, acessado em novembro 29, 2025, [https://www.frontmatter.io/blog/best-fonts-for-apps-in-2025-top-picks-for-ios-and-android-ui-design](https://www.frontmatter.io/blog/best-fonts-for-apps-in-2025-top-picks-for-ios-and-android-ui-design)  
45. Best free typefaces for UI design in 2025 \- Top free fonts \- Adham Dannaway, acessado em novembro 29, 2025, [https://www.adhamdannaway.com/blog/ui-design/free-typefaces](https://www.adhamdannaway.com/blog/ui-design/free-typefaces)  
46. android \- Default Style Jetpack Compose \- Stack Overflow, acessado em novembro 29, 2025, [https://stackoverflow.com/questions/66732942/default-style-jetpack-compose](https://stackoverflow.com/questions/66732942/default-style-jetpack-compose)  
47. How to remove border of card view with jetpack compose \- Stack Overflow, acessado em novembro 29, 2025, [https://stackoverflow.com/questions/69842871/how-to-remove-border-of-card-view-with-jetpack-compose](https://stackoverflow.com/questions/69842871/how-to-remove-border-of-card-view-with-jetpack-compose)  
48. What can Advanced / Lesser Known Modifiers do for your UI? — A Comprehensive Exploration in Jetpack Compose | by Nirbhay Pherwani | ProAndroidDev, acessado em novembro 29, 2025, [https://proandroiddev.com/what-can-advanced-lesser-known-modifiers-do-for-your-ui-9c76855bced6](https://proandroiddev.com/what-can-advanced-lesser-known-modifiers-do-for-your-ui-9c76855bced6)