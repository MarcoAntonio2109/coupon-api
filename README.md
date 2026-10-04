# Coupon API - Desafio Técnico OneBrain
API REST para gerenciamento de cupons promocionais com Arquitetura Hexagonal (Ports & Adapters), implementando princípios SOLID e garantindo lógica de negócio agnóstica de tecnologia.

## 🎯 Princípios SOLID Aplicados

- **S**ingle Responsibility: Cada UseCase tem uma responsabilidade
- **O**pen/Closed: Aberto para extensão via novas implementações de portas
- **L**iskov Substitution: Portas podem ser substituídas (diferentes adaptadores)
- **I**nterface Segregation: Interfaces específicas por comportamento
- **D**ependency Inversion: Aplicação depende de portas (abstrações), não de implementações

## 📋 Funcionalidades

### UseCases Implementados

- ✅ **CreateCouponUseCase** - Criar cupom com validação de código e data
- ✅ **FindCouponUseCase** - Buscar cupom por código
- ✅ **UpdateCouponUseCase** - Atualizar informações do cupom
- ✅ **DeleteCouponUseCase** - Deletar cupom (soft delete)
- ✅ **ListCouponUseCase** - Listar cupons com paginação

### Regras de Negócio

- Código de cupom deve ter exatamente **6 caracteres alfanuméricos**
- Data de expiração **não pode estar no passado**
- Cupom deletado **não pode ser deletado novamente**
- Código normalizado (remove caracteres especiais automaticamente)
- Cupom deletado é **soft delete** (marcado como deletado, não removido do banco)

## 🛠️ Tecnologias

| Categoria | Tecnologia |
|-----------|-----------|
| **Linguagem** | Java 21 |
| **Framework Web** | Spring Boot 4.1.1 |
| **Web MVC** | Spring Web MVC |
| **Persistência** | Spring Data JPA |
| **ORM** | Hibernate |
| **Segurança** | Spring Security |
| **Banco de Dados** | H2 (em memória) |
| **Documentação API** | Springdoc OpenAPI / Swagger UI |
| **Build** | Maven Wrapper |
| **Containerização** | Docker |
| **Testes** | JUnit 5, Mockito |

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.9+

### Executar Aplicação
```bash
./mvnw spring-boot:run
```

### Executar Testes
```bash
# Todos os testes
./mvnw test

# Apenas testes de domínio
./mvnw test -Dtest=CouponDomainServiceTest

# Com cobertura de testes
./mvnw test jacoco:report
```

### Compilar Sem Testes
```bash
./mvnw clean compile -q
```

### Build Docker
```bash
docker build -t coupon-api:latest .
docker run -p 8080:8080 coupon-api:latest
```

## 📚 Documentação

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### H2 Console
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:coupondb
User: sa
Password: (deixar em branco)
```
## 📖 Endpoints

### Criar Cupom
```http
POST /api/coupons
Content-Type: application/json

{
  "code": "ABC123",
  "description": "Desconto 10%",
  "discountValue": 10.00,
  "expirationDate": "2026-12-31",
  "published": true
}

Response: 201 Created
{
  "id": 1,
  "code": "ABC123",
  "description": "Desconto 10%",
  "discountValue": 10.00,
  "expirationDate": "2026-12-31",
  "published": true,
  "deleted": false,
  "createdAt": "2026-10-04T19:16:43.506-03:00"
}
```

### Buscar Cupom
```http
GET /api/coupons/ABC123

Response: 200 OK
{
  "id": 1,
  "code": "ABC123",
  "description": "Desconto 10%",
  "discountValue": 10.00,
  "expirationDate": "2026-12-31",
  "published": true,
  "deleted": false,
  "createdAt": "2026-10-04T19:16:43.506-03:00"
}
```

### Atualizar Cupom
```http
PUT /api/coupons/ABC123
Content-Type: application/json

{
  "description": "Novo desconto 20%",
  "discountValue": 20.00,
  "expirationDate": "2026-12-31",
  "published": true
}

Response: 200 OK
```

### Deletar Cupom
```http
DELETE /api/coupons/ABC123

Response: 204 No Content
```

### Listar Cupons
```http
GET /api/coupons?page=0&size=10

Response: 200 OK
{
  "content": [
    {
      "id": 1,
      "code": "ABC123",
      ...
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1
}
```
## 🧪 Testes

A aplicação conta com **32 testes** organizados em 3 camadas:

### Testes de Domínio (12 testes)
- **Puro Java** - Sem Spring, sem JPA, sem dependências
- Valida todas as regras de negócio
- Arquivo: `CouponDomainServiceTest.java`

**Exemplos**:
- `testCreateNewCouponNormalizesCode()` - Normaliza código
- `testRejectCodeTooShort()` - Rejeita código < 6 caracteres
- `testRejectExpiredDate()` - Rejeita data no passado
- `testDeleteCoupon()` - Marca como deletado
- `testRejectDeletingAlreadyDeletedCoupon()` - Impede dupla deleção

### Testes de Orquestração (15 testes)
- Valida fluxo de UseCases
- Usa Mockito para mockar portas
- Sem lógica de negócio (responsabilidade do domínio)

**Por UseCase**:
- `CreateCouponUseCaseTest` (4 testes)
- `FindCouponUseCaseTest` (2 testes)
- `UpdateCouponUseCaseTest` (3 testes)
- `DeleteCouponUseCaseTest` (3 testes)
- `ListCouponUseCaseTest` (3 testes)

### Testes de Integração (5 testes)
- Fluxo completo com Spring Boot Test
- Banco de dados H2 em memória
- Valida: criar, buscar, atualizar, deletar, listar

**Resultado**: ✅ 32/32 testes passando (0 falhas, 0 erros)

## 🔒 Segurança

- Spring Security ativado (autenticação básica)
- Validação de entrada via anotações (`@NotNull`, `@NotBlank`, etc)
- Tratamento centralizado de exceções (GlobalExceptionHandler)
- Exceções de domínio mapeadas para HTTP status apropriados
- Sem dependências de segurança expostas na camada de domínio

## 📊 Mapeamento de Exceções

| Exceção | HTTP Status | Descrição |
|---------|------------|-----------|
| `CouponNotFoundException` | 404 | Cupom não encontrado |
| `InvalidCouponCodeException` | 400 | Código inválido (não tem 6 caracteres) |
| `InvalidExpirationDateException` | 400 | Data de expiração no passado |
| `CouponAlreadyExistsException` | 409 | Cupom duplicado (código já existe) |
| `CouponAlreadyDeletedException` | 409 | Cupom já foi deletado |

**Exemplos de Respostas**:

```json
// 400 Bad Request
{
  "error": "Código 'ABC' inválido. Deve ter exatamente 6 caracteres..."
}

// 404 Not Found
{
  "error": "Cupom com código 'NOTFOUND' não encontrado."
}

// 409 Conflict
{
  "error": "Cupom com código 'ABC123' já existe."
}
```
## 🏆 Conformidade Hexagonal

### Arquitetura Hexagonal
A aplicação separa a lógica de negócio (domínio) de suas dependências externas (banco de dados, HTTP). Isto permite que:
- ✅ A lógica de negócio seja testável sem frameworks
- ✅ Diferentes adaptadores sejam plugáveis (diferentes bancos, diferentes protocolos)
- ✅ A aplicação seja independente de tecnologia
- ✅ Cada camada tenha responsabilidade clara

### Portas (Ports)
Interfaces que definem contratos entre a aplicação e o mundo externo:
- **`CouponRepositoryPort`** - Como persistir cupons (agnóstico de tecnologia)

### Adaptadores (Adapters)
Implementações concretas das portas:
- **`CouponRepositoryAdapter`** - Implementação JPA (tecnologia específica)
- **`CouponControllerAdapter`** - Implementação REST (tecnologia específica)

**Benefício**: Trocar de JPA para MongoDB é apenas criar um novo adapter sem tocar no domínio!

### UseCases
Orquestradores que coordenam fluxos sem conter lógica de negócio:
- Cada UseCase = Uma intenção do usuário
- Chamam domínio para validação/processamento
- Chamam portas para persistência
- **Sem `if/else` complexos** - apenas coordenação

### Domain Service
Centraliza toda lógica de negócio complexa:
- `CouponDomainService.createNewCoupon()` - Valida e normaliza
- `CouponDomainService.updateCoupon()` - Atualiza com validação
- `CouponDomainService.deleteCoupon()` - Marca como deletado com regra

### Domain Exceptions
Exceções agnósticas de tecnologia:
- Não conhecem HTTP, Spring, JPA
- Herdadas de `DomainException`
- Mapeadas para HTTP apenas no adapter

### A aplicação foi validada para garantir:

- ✅ **Domínio agnóstico** - Sem Spring, sem JPA, sem frameworks
- ✅ **Portas claras** - `CouponRepositoryPort` é a única dependência de aplicação
- ✅ **Adaptadores trocáveis** - Pode-se trocar JPA por MongoDB sem mudança no core
- ✅ **Testes puros** - 12 testes de domínio rodam sem Spring
- ✅ **Sem anotações framework** - Domain nunca vê `@Entity`, `@Component`, etc

## 📝 Licença

Este projeto é um desafio técnico para OneBrain.

---

**Última atualização**: Outubro de 2026  
**Status**: ✅ Arquitetura Hexagonal completa e validada  
**Testes**: ✅ 32/32 passando (100% de cobertura de regras de negócio)  
**Conformidade**: ✅ SOLID + Hexagonal Architecture + DIP

