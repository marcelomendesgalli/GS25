# 💡 Pitch: Green Light Monitor - Protegendo Nossos Estudantes do Calor Extremo

## O Problema: Uma Ameaça Crescente nas Escolas Públicas

Com o avanço das mudanças climáticas, as ondas de calor no Brasil tornaram-se mais frequentes, intensas e prolongadas. Essa realidade impõe um desafio crítico às escolas públicas, especialmente na cidade de São Paulo, onde a maioria das instituições não possui sistemas de climatização adequados. Milhares de estudantes e profissionais da educação são expostos diariamente a temperaturas elevadas, resultando em:

-   **Riscos à Saúde**: Aumento de casos de desidratação, insolação, mal-estar e agravamento de condições respiratórias, impactando diretamente o bem-estar físico dos alunos.
-   **Prejuízo ao Aprendizado**: Ambientes desconfortáveis afetam a concentração, o desempenho cognitivo e a capacidade de retenção de informações, comprometendo a qualidade do ensino.
-   **Falta de Informação e Preparo**: Gestores escolares, professores e coordenadores frequentemente carecem de dados em tempo real e de ferramentas proativas para identificar situações de risco e tomar decisões preventivas eficazes.
-   **Tomada de Decisão Reativa**: A ausência de um sistema de monitoramento integrado leva a ações tardias e improvisadas, que podem não ser suficientes para mitigar os riscos de forma adequada.

Em suma, a falta de climatização e de um sistema de alerta eficiente nas escolas públicas brasileiras representa uma vulnerabilidade significativa para a saúde e o desenvolvimento educacional de nossas crianças.

## A Solução: Green Light Monitor - Inteligência e Acessibilidade a Serviço da Educação

O **Green Light Monitor** é um sistema inteligente e acessível, desenvolvido para enfrentar o desafio dos riscos climáticos em escolas públicas, com foco inicial nas altas temperaturas. Nossa plataforma integrada de monitoramento ambiental oferece uma solução viável e abrangente:

### 🎯 Ideia Principal

Desenvolver uma plataforma que monitora as condições ambientais em tempo real dentro das escolas, enviando alertas automáticos e inteligentes em situações de calor extremo. O objetivo é capacitar gestores escolares com informações precisas para a tomada de decisões preventivas, garantindo um ambiente escolar seguro e propício ao aprendizado.

### ✨ Como Funciona

1.  **Monitoramento Contínuo**: Sensores de temperatura e umidade são instalados estrategicamente nas escolas, coletando dados em tempo real.
2.  **Coleta de Dados Robusta**: As leituras dos sensores são enviadas de forma assíncrona e segura para o sistema central via **RabbitMQ**, garantindo a integridade e a disponibilidade dos dados.
3.  **Análise Inteligente e Alerta Proativo**: O sistema processa as leituras e, ao identificar valores críticos (ex: temperatura acima de 30°C ou 35°C), gera alertas. Utilizando **Spring AI**, esses alertas são enriquecidos com mensagens personalizadas e recomendações de ações preventivas, tornando-os mais eficazes e acionáveis.
4.  **Interface Intuitiva para Gestão**: Gestores, professores e coordenadores acessam um **dashboard web** (desenvolvido com Spring MVC e Thymeleaf) que oferece uma visualização clara das condições ambientais, históricos de leituras e alertas ativos. A plataforma permite o gerenciamento completo (CRUD) de escolas e sensores.
5.  **Segurança e Acesso Controlado**: O acesso ao sistema é seguro, com autenticação via **OAuth2** (Google, GitHub) e controle de acesso baseado em perfis (ADMIN, GESTOR, USUARIO).

### 🚀 Benefícios e Impacto

-   **Proteção da Saúde dos Alunos**: Redução significativa dos riscos de desidratação, insolação e outros problemas de saúde relacionados ao calor.
-   **Melhora do Ambiente de Aprendizado**: Criação de um espaço mais confortável e seguro, favorecendo a concentração e o desempenho acadêmico.
-   **Empoderamento dos Gestores**: Ferramentas e informações em tempo real para decisões proativas, como a liberação antecipada de alunos, a intensificação da hidratação ou a suspensão de atividades ao ar livre.
-   **Comunicação Eficaz**: Alertas claros e direcionados que facilitam a comunicação com a comunidade escolar.
-   **Otimização de Recursos**: Foco na prevenção e na ação direcionada, evitando medidas reativas e custosas.
-   **Escalabilidade e Acessibilidade**: Uma solução desenvolvida com tecnologias modernas e acessíveis, pronta para ser implementada em larga escala em toda a rede pública de ensino.

O **Green Light Monitor** não é apenas uma ferramenta tecnológica; é um investimento no futuro e na saúde de nossas crianças, garantindo que o ambiente escolar seja sempre um local de segurança e bem-estar, independentemente das condições climáticas.

