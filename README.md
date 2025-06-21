# Sistema de Pedidos para E-commerce com Microsserviços e Eventos 🚀

![Status](https://img.shields.io/badge/status-conclu%C3%ADdo-brightgreen)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring](https://img.shields.io/badge/Spring%20Boot-green)

## Um sistema de pedidos de e-commerce com microsserviços e eventos assíncronos usando Spring Boot, RabbitMQ e Keycloak.

## 📖 Sobre o Projeto



Este projeto implementa o backend de um sistema de pedidos para e-commerce, projetado com uma arquitetura de microsserviços orientada a eventos. O principal objetivo foi resolver a problemática de sistemas monolíticos que sofrem com picos de acesso (como em uma Black Friday), resultando em lentidão no checkout, falhas e perda de vendas.

A solução foca em **resiliência, escalabilidade e segurança**, desacoplando o fluxo crítico de criação de pedidos de seu processamento subsequente, garantindo uma experiência de usuário rápida e que nenhuma venda seja perdida.

---

## 🏛️ Arquitetura do Sistema

A arquitetura está organizada em um **monorepo** para facilitar a configuração e a visualização integrada dos componentes. Todos os serviços são orquestrados via **Docker Compose**.

### Componentes Principais

* **📦 API Gateway (`api-gateway`):** Ponto de entrada único que centraliza o roteamento e a segurança. Ele valida tokens JWT emitidos pelo Keycloak usando o fluxo **OAuth2/OIDC**, protegendo todos os serviços internos.
* **📝 Serviço de Pedidos (`order-service`):** Microsserviço síncrono e de alta performance responsável por receber a requisição do pedido, validá-la, persistir o estado inicial e publicar um evento no RabbitMQ. A qualidade da sua lógica é garantida por **testes unitários com JUnit e Mockito**.
* **⚙️ Serviço de Processamento (`order-processings-service`):** Microsserviço assíncrono que consome os eventos da fila. Ele é responsável pela lógica de "pós-venda", registrando o resultado do processamento. Sua lógica de **idempotência** previne o processamento duplicado de pedidos.
* **🐇 RabbitMQ com DLQ:** Atua como o broker de mensageria, garantindo a comunicação assíncrona e a resiliência do sistema. Mensagens que falham repetidamente são enviadas a uma **Dead Letter Queue (DLQ)** para análise, mantendo o fluxo principal sempre saudável.
* **🔑 Keycloak:** Atua como o Provedor de Identidade e Acesso (IdP) externo, gerenciando usuários e a emissão de tokens.

---

## 🛠️ Stack de Tecnologias

| Categoria | Tecnologias |
| :--- | :--- |
| **Backend** | `Java`, `Spring Boot`, `Spring Web`, `Spring Data JPA` |
| **Segurança** | `Spring Security`, `OAuth2`, `JWT` |
| **Gateway** | `Spring Cloud Gateway` (baseado em WebFlux) |
| **Mensageria**| `Spring AMQP`, `RabbitMQ` |
| **Banco de Dados**| `PostgreSQL` |
| **Testes** | `JUnit`, `Mockito`, `k6` (Teste de Carga) |
| **Infraestrutura**| `Docker`, `Docker Compose` |
| **Build** | `Maven` |

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar todo o ecossistema localmente.

### Pré-requisitos
* **Git**
* **Java (JDK) 21**
* **Docker** e **Docker Compose**

### 1. Clonando o Repositório
```bash
git clone https://github.com/GustavoeDev/sistema-pedidos-ecommerce.git

cd sistema-pedidos-ecommerce
```

### 2. Executando a Aplicação
Agora que o Keycloak está configurado, suba o restante da infraestrutura.

1.  Execute o Docker Compose na raiz do projeto:
    ```bash
    docker-compose up --build -d
    ```
2.  Abra cada projeto de microsserviço (`api-gateway`, `order-service`, `order-processings-service`) em sua IDE.
3.  Execute a classe principal de cada um deles. O Spring Boot se conectará automaticamente aos serviços rodando no Docker.

### 3. Configuração do Keycloak
O Keycloak precisa de uma configuração inicial para funcionar com o projeto.

1.  Acesse o **Console de Administração** em `http://localhost:8080`.
2.  Faça login com as credenciais que voce definiu no .env, exemplo: `admin` / `admin`.
3.  **Crie um Realm:** No canto superior esquerdo, clique em "master" e depois em "Create Realm". Exemplo: `gustavoedev`.
4.  **Crie um Client:** No menu à esquerda, vá em "Clients" e clique em "Create client".
    * **Client ID:** `gateway`
    * **Client authentication:** Deixe como `On`.
    * Na próxima tela, em **Valid redirect URIs**, adicione `http://localhost:9000/*` para testes locais. Salve.
5.  **Crie um Usuário:** No menu à esquerda, vá em "Users" e "Add user".
    * Preencha o **Username**.
    * Na aba "Credentials", defina uma senha para o usuário e desmarque a opção "Temporary".

**Portas Padrão:**
* **API Gateway:** `http://localhost:9000`
* **Keycloak:** `http://localhost:8080`
* **RabbitMQ Management:** `http://localhost:15672`

---

## 🧪 Como Testar

### Teste de Carga com k6

O roteiro de teste `teste-de-carga-pedidos.js` está na raiz do projeto.

1.  **Obtenha um Access Token:** Primeiro, você precisa obter um token do Keycloak para um usuário válido. Você pode fazer isso via uma chamada `curl` ou usando o Postman:
    ```bash
    # Substitua com os dados do seu client e usuário
    curl --location 'http://localhost:8080/realms/gustavoedev/protocol/openid-connect/token' \
      --header 'Content-Type: application/x-www-form-urlencoded' \
      --data-urlencode 'client_id=gateway' \
      --data-urlencode 'client_secret=sua_client_secret' \
      --data-urlencode 'username=seu_username' \
      --data-urlencode 'password=sua_password' \
      --data-urlencode 'grant_type=password' \
      --data-urlencode 'scope=openid profile email'
    ```
    Copie o valor do `access_token` da resposta.

2.  **Execute o k6:**
    Primeiro altere o access_token para o access_token obtido. Depois execute:
    ```bash
    k6 run teste-de-carga-pedidos.js
    ```
    
---

## API - Criação de Pedido

O principal endpoint da aplicação é o de criação de pedidos. Ele é a porta de entrada para o fluxo de negócio e foi projetado para ser simples e rápido.

* **Método HTTP:** `POST`
* **URL:** `http://localhost:9000/order/`
* **Descrição:** Cria um novo pedido no sistema. Em vez de processar o pedido imediatamente, ele valida os dados de entrada, persiste a ordem com um status inicial e publica um evento para que um serviço assíncrono continue o processamento. Isso garante uma resposta quase instantânea para o cliente.
* **Autenticação:** Na arquitetura final, esta rota será protegida e exigirá um `Bearer Token` (JWT) no cabeçalho `Authorization`. Durante o desenvolvimento (sem a camada de segurança ativa), o `clientId` deve ser enviado no corpo da requisição.

### Corpo da Requisição (`Request Body`)

**Exemplo de JSON:**
```json
{
    "clientId": "0acd22a5-d384-410c-88a8-bac6d407ceba",
    "items": [
        {
            "productId": "d13c44c2-2395-4d3c-8e3a-b65ab8589097",
            "quantity": 4,
            "unitPrice": 3
        },
        {
            "productId": "f0e9d8c7-b6a5-4321-fedc-ba0987654321",
            "quantity": 1,
            "unitPrice": 120.50
        }
    ]
}
```

### Resposta de Sucesso

* **Código de Status:** `202 Accepted`
* **Motivo:** O status `202 Accepted` é utilizado para indicar que a requisição foi aceita, mas o processamento ainda não foi concluído (ele acontecerá de forma assíncrona). Isso informa ao cliente que está tudo certo e que ele pode esperar por uma notificação futura sobre o status do pedido.

---

## 👤 Contato

**Gustavo Emanuel**

* **LinkedIn:** www.linkedin.com/in/gustavoedev
