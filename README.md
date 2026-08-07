# 📋 Enterprise Document Intelligence

[![Tech Stack](https://img.shields.io/badge/Stack-Spring%20Boot%203%20%7C%20Angular%2019%20%7C%20Spring%20AI%20%7C%20Apache%20Camel%20%7C%20Elasticsearch-blue)](#)
[![Status](https://img.shields.io/badge/Status-Active-brightgreen)](#)
[![Security](https://img.shields.io/badge/Security-Sanitizer%20%26%20WAF-orange)](#)

O **Enterprise Document Intelligence** é uma plataforma corporativa avançada para extração, processamento, indexação e busca semântica híbrida em grandes volumes de documentos corporativos (PDF, DOCX, XLSX, TXT). Desenvolvido usando **Java 21**, **Spring Boot 3.4.3**, **Spring AI (OpenAI)**, **Apache Camel 4.10.0**, **Elasticsearch** e **Angular 19.2.2**, o sistema automatiza o pipeline de ingestão e análise semântica de dados corporativos não estruturados.

Ao combinar o poder de processamento do framework de integração **Apache Camel** com a análise profunda de texto do **Apache Tika**, a plataforma extrai de forma assíncrona metadados e conteúdos de documentos salvos em diretórios locais ou tópicos Kafka. Em seguida, os conteúdos são segmentados (chunking) e enriquecidos com representações vetoriais (embeddings) via **Spring AI (OpenAI)**, e finalmente armazenados no **Elasticsearch** para busca híbrida (lexical BM25 combinada com k-NN vetorial).

---

## 📐 1. Arquitetura & Componentes Internos

### Mapeamento da Estrutura de Pastas
```text
enterprise-document-intelligence/
├── backend/                  # Servidor API REST & Pipeline de Processamento (Spring Boot)
│   ├── src/main/java/com/enterprise/docai/
│   │   ├── config/           # Configuração de Spring AI, Camel e Elasticsearch
│   │   ├── controller/       # Endpoints REST para ingestão, consulta e chat RAG
│   │   ├── route/            # Rotas Apache Camel (ingestão automática de arquivos e Kafka)
│   │   ├── service/          # Processamento semântico (Tika Extraction, Spring AI Vector Store)
│   │   └── DocumentIntelligenceApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── frontend/                 # Interface Web Single Page (Angular 19)
│   ├── src/app/
│   │   ├── components/       # Componentes (painel de upload, chat RAG, resultados)
│   │   ├── services/         # Clientes de API REST para comunicação com o backend
│   │   └── app.component.ts
│   ├── package.json          # Angular 19.2.2 & Tailwind 4.0
│   └── tailwind.config.js
└── README.md                 # Guia Geral do Módulo
```

### Diagrama de Fluxo de Ingestão e Consulta (RAG)
```text
[Documento PDF/DOCX]
        │
        ▼ (Pasta Local / Kafka)
+───────────────────────+
|   Apache Camel Route  |  ◄── Ingestão Automática e Assíncrona
+───────────┬───────────+
            │
            ▼ (Tika Parser)
+───────────────────────+
| Apache Tika Extraction|  ◄── Extração de Texto Limpo e Metadados
+───────────┬───────────+
            │
            ▼ (Chunking)
+───────────────────────+
| Text Splitter Service |  ◄── Divisão inteligente em fragmentos de texto
+───────────┬───────────+
            │
            ▼ (OpenAI Embeddings)
+───────────────────────+
| Spring AI Vector Store|  ◄── Geração de vetores multidimensionais
+───────────┬───────────+
            │
            ▼ (Indexação)
+───────────────────────+
|  Elasticsearch Engine |  ◄── Indexação Híbrida (Lexical + Vectorial)
+───────────────────────+
```

### Tabela de Tecnologias & Dependências

| Componente | Tecnologia | Versão Standard | Descrição / Papel no Ecossistema |
|------------|------------|-----------------|----------------------------------|
| **Core Runtime** | Java 21 | JDK 21 | Execução segura e de alto rendimento |
| **Framework API** | Spring Boot | 3.4.3 | Infraestrutura de controle e serviços |
| **Integração** | Apache Camel | 4.10.0 | Orquestração do pipeline de arquivos e streaming |
| **Parse de Arquivos**| Apache Tika | 3.0.0 | Engine robusta para extração de dados estruturados |
| **Inteligência Art.**| Spring AI (OpenAI) | 1.0.0-M6 | Integração nativa com LLMs para Embeddings e RAG |
| **Motor de Busca** | Elasticsearch | 8.x | Armazenamento invertido e busca KNN vetorial |
| **Interface SPA** | Angular | 19.2.2 | Painel administrativo reativo com Signals API |
| **Estilização** | Tailwind CSS | 4.0.0 | Nova geração de estilos utilitários rápidos |

---

## 🚀 2. Guia de Instalação & Pré-requisitos

### Pré-requisitos de Sistema
Para compilar e executar o ecossistema, verifique os pré-requisitos:
* **Java Development Kit (JDK) 21**
* **Node.js** (v18.x ou superior) & **NPM**
* **Apache Maven 3.9+**
* **Docker & Docker Compose** (para provisionar o Elasticsearch localmente)

### Passo a Passo para Instalação Local

1. **Clonar e Acessar o Diretório:**
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd enterprise-document-intelligence
   ```

2. **Iniciar o Elasticsearch via Docker (Se aplicável):**
   ```bash
   docker run -d --name elasticsearch -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" elasticsearch:8.11.1
   ```

3. **Compilar e Rodar o Backend:**
   ```bash
   cd backend
   mvn clean install
   # Certifique-se de configurar a API Key da OpenAI (consulte a seção de configuração)
   mvn spring-boot:run
   ```

4. **Instalar Dependências e Rodar o Frontend:**
   ```bash
   cd ../frontend
   npm install
   npm start
   ```
   O frontend estará disponível em [http://localhost:4200](http://localhost:4200).

---

## ⚙️ 3. Configuração & Variáveis de Ambiente

O processamento inteligente de documentos exige credenciais seguras para comunicação com serviços cognitivos e de persistência.

### Configuração do Backend (`backend/src/main/resources/application.yml`)

| Nome da Variável | Descrição | Valor Padrão | Obrigatória? | Sensível? |
|------------------|-----------|--------------|--------------|-----------|
| `SPRING_AI_OPENAI_API_KEY` | Chave de API secreta do provedor OpenAI | *(Nenhum)* | Sim | Sim |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL` | Modelo de linguagem usado para síntese RAG | `gpt-4o` | Não | Não |
| `SPRING_ELASTICSEARCH_URIS` | Endereço de conexão HTTP do Elasticsearch | `http://localhost:9200` | Sim | Não |
| `CAMEL_INGESTION_DIRECTORY` | Caminho local para monitoramento de novos arquivos | `/tmp/doc-ingest` | Não | Não |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Servidores do broker Kafka para mensageria | `localhost:9092` | Não | Não |

---

## 🛠️ 4. Instruções de Execução e Uso

### Pipeline de Ingestão de Arquivos via Apache Camel
As rotas do Apache Camel são ativadas na inicialização. Para ingerir e indexar automaticamente um documento corporativo:
* Copie qualquer arquivo suportado (ex: `relatorio_anual.pdf`) para a pasta monitorada (`/tmp/doc-ingest`).
* O Camel detectará o arquivo de forma assíncrona, executará a extração do texto com o Apache Tika, fatiará em pedaços pequenos, gerará os embeddings vetoriais com a OpenAI e enviará tudo para o Elasticsearch.

### Exemplos Práticos de Uso da API REST

#### Executar uma Busca Semântica Híbrida via API
```bash
curl -X POST http://localhost:8080/api/documents/search \
     -H "Content-Type: application/json" \
     -d '{
       "query": "Qual foi o faturamento total reportado no terceiro trimestre?",
       "topK": 5,
       "hybridWeight": 0.7
     }'
```

#### Enviar Pergunta em Conversação Contextualizada (Chat RAG)
```bash
curl -X POST http://localhost:8080/api/documents/chat \
     -H "Content-Type: application/json" \
     -d '{
       "message": "Resuma as principais iniciativas de governança corporativa de 2026."
     }'
```

---

## 🧪 5. End-to-End Testing & Qualidade

O projeto dispõe de testes integrados focando no fluxo correto de extração e roteamento de arquivos pelo Apache Camel.

### Executando Testes de Backend
```bash
cd backend
mvn test
```
Esses testes asseguram a integridade dos parsers do Tika, verificação de consistência do payload enviado ao Elasticsearch e comportamento do fallback na ausência da chave OpenAI.

### Executando Testes de Frontend
```bash
cd frontend
npm run test
```

---

## 🛡️ 6. Aspectos de Segurança & Governança (AppSec)

Para garantir conformidade com políticas corporativas de segurança da informação (LGPD e GDPR) e governança de dados:

1. **Sanitização de Cargas Úteis (Payloads):**
   Antes de processar qualquer documento ou requisição de texto pelo motor de IA, o sistema executa varreduras regex para filtrar e mitigar tentativas de **Prompt Injection** estrutural.

2. **Isolamento de Segurança Física de Arquivos:**
   As pastas locais monitoradas pelo Apache Camel possuem privilégios de leitura estritos, impedindo que usuários sem credenciais administrativas gravem arquivos no fluxo comum de processamento da corporação.

3. **Criptografia de Segredos:**
   As chaves de API (`SPRING_AI_OPENAI_API_KEY`) são injetadas em tempo de execução via variáveis de ambiente seguras ou cofres de segredos (Secrets Manager), nunca persistidas em repositórios de código aberto.

---

## 💡 7. Troubleshooting & Solução de Problemas Comuns

| Sintoma de Erro | Causa Provável | Ação Corretiva / Resolução |
|-----------------|----------------|----------------------------|
| `OpenAI API Key is missing or invalid` | A variável `SPRING_AI_OPENAI_API_KEY` não foi exportada no terminal | Defina a chave rodando `export SPRING_AI_OPENAI_API_KEY=sua_chave` ou configure no arquivo `application.yml` antes do boot da aplicação. |
| `ElasticsearchStatusException: Connection refused` | O container ou servidor local do Elasticsearch está desligado | Verifique o status executando `docker ps` ou suba novamente o container utilizando a porta `9200`. |
| `Apache Tika: Extraction Failed` | O documento está corrompido, protegido por senha ou em formato binário não suportado | Verifique a integridade do arquivo. Se protegido por senha, remova a proteção de encriptação antes de enviá-lo ao diretório de ingestão. |
| `Camel Route File Consumer Permission Denied` | O usuário que executou a aplicação Java não possui direitos de leitura na pasta local | Conceda privilégios de leitura à pasta monitorada usando `chmod -R 755 /tmp/doc-ingest`. |
