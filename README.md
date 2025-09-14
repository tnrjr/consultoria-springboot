# EY Backend
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
[![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)

## Visão Geral

Este projeto é uma aplicação Java desenvolvida com o framework **Spring Boot**, utilizando **Maven** para gerenciamento de dependências e **PostgreSQL** para persistência de dados (em ambiente local e em produção no **Google Cloud SQL**).  
A aplicação gerencia entidades como `Consultant` e `Project`, permitindo operações de cadastro, atribuição de consultores a projetos e paginação de listagens.

O deploy é feito em **Google Cloud Run** com **Docker**, integrando-se ao **Secret Manager** para gerenciamento seguro de credenciais.

---

## Estrutura do Projeto

A estrutura do projeto é organizada da seguinte forma:

- `src/main/java/com/tary/ey/domain`: Contém as classes de entidade (`Project`, `Consultant`) e enums (`ProjectStatus`).  
- `src/main/java/com/tary/ey/repositories`: Interfaces que estendem `JpaRepository`, responsáveis pelo acesso aos dados.  
- `src/main/java/com/tary/ey/services`: Contém as classes de serviço, encapsulando a lógica de negócios (ex: `ProjectService`).  
- `src/main/java/com/tary/ey/dtos`: Data Transfer Objects (DTOs) usados para validação e transferência de dados.  
- `src/main/java/com/tary/ey/controllers`: Classes controladoras que expõem endpoints REST.  
- `src/main/java/com/tary/ey/config`: Configurações globais, incluindo `GlobalExceptionHandler` e perfis de ambiente.  
- `src/main/resources`: Arquivos de configuração (`application.properties`, `application-prod.yml`).  

---

## Boas Práticas de Desenvolvimento

1. **Organização em Camadas**: O projeto segue o padrão MVC (Model-View-Controller), promovendo separação de responsabilidades.  
<!--2. **Injeção de Dependências**: Utilização de construtores em vez de `@Autowired`, garantindo maior testabilidade.-->
2. **Validação de Dados**: Uso de Bean Validation (`@NotBlank`, `@PastOrPresent`, `@FutureOrPresent`) nos DTOs.  
3. **Tratamento Global de Exceções**: Todas as exceções são tratadas no `GlobalExceptionHandler`, retornando respostas padronizadas.  
4. **Uso de DTOs**: As entidades não são expostas diretamente, evitando acoplamento entre a camada de domínio e a de apresentação.  
5. **Documentação Automática**: Swagger/OpenAPI disponível via `springdoc-openapi`.  
6. **Configurações por Perfil**: Perfis `dev` e `prod` para separar ambientes locais e em nuvem.  
7. **Segurança de Credenciais**: Senhas do banco de dados são armazenadas no Google Secret Manager.  

---

## Padrões de Projeto Utilizados

1. **Repository Pattern**: Separação da camada de persistência usando interfaces JPA.  
2. **Service Layer Pattern**: Camada de serviços encapsula a lógica de negócios.  
3. **DTO Pattern**: Transferência de dados desacoplada da entidade.  
4. **Controller Advice**: Padrão para centralizar tratamento de erros.  

---

## Exemplo de Código

### Entidade Project

```java
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "consultores")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String nome;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
```

### DTO ProjectDTO

```java
public record ProjectDTO (
    Long id,

    @NotBlank(message = "nome é obrigatório")
    String nome,

    @NotBlank(message = "descrição é obrigatória")
    String descricao,

    @NotNull(message = "dataInicio é obrigatória")
    @PastOrPresent(message = "dataInicio não pode ser no futuro")
    LocalDate dataInicio,

    @FutureOrPresent(message = "dataFim não pode ser no passado")
    LocalDate dataFim,

    @NotNull(message = "status é obrigatório")
    ProjectStatus status,

    Set<Long> consultoresIds
){}
```

### Serviço ProjectService

```java
@Service
public class ProjectService {
    private final ProjectRepository projects;
    private final ConsultantRepository consultants;

    public ProjectService(ProjectRepository projects, ConsultantRepository consultants) {
        this.projects = projects;
        this.consultants = consultants;
    }

    public ProjectDTO create(ProjectDTO dto) {
        var project = new Project();
        copy(dto, project);
        return toDTO(projects.save(project));
    }
}
```

### Controlador ProjectController

```java
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDTO create(@RequestBody @Valid ProjectDTO dto) {
        return service.create(dto);
    }
}
```

### Tratamento Global de Exceções

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        // Retorna erros de validação em formato padronizado
    }
}
```

---

## Execução

### Localmente
1. Rodar o PostgreSQL local ou usar H2 em memória.  
2. Executar o projeto:  
   ```bash
   ./mvnw spring-boot:run
   ```
3. Acessar: `http://localhost:8080/swagger-ui.html`

### Com Docker
1. Empacotar o projeto:  
   ```bash
   ./mvnw clean package -DskipTests
   ```
2. Construir a imagem:  
   ```bash
   docker build -t ey-backend .
   ```
3. Rodar o container:  
   ```bash
   docker run -p 8080:8080 ey-backend
   ```

# Exemplos de Requisições

Abaixo estão exemplos de requisições para a API rodando localmente (`http://localhost:8080`).

---

## Criar um Consultor

**POST** `/api/consultants`

```json
{
  "nome": "João Silva",
  "email": "joao.silva@empresa.com",
  "especialidade": "Gestão de Projetos"
}
```

---

## Listar Consultores

**GET** `/api/consultants`

Resposta (exemplo):
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao.silva@empresa.com",
    "especialidade": "Gestão de Projetos"
  }
]
```

---

## Criar um Projeto

**POST** `/api/projects`

```json
{
  "nome": "Implantação ERP",
  "descricao": "Projeto para implantar sistema ERP",
  "dataInicio": "2025-09-01",
  "dataFim": "2026-03-01",
  "status": "EM_ANDAMENTO",
  "consultoresIds": [1]
}
```

---

## Listar Projetos

**GET** `/api/projects`

Resposta (exemplo):
```json
[
  {
    "id": 1,
    "nome": "Implantação ERP",
    "descricao": "Projeto para implantar sistema ERP",
    "dataInicio": "2025-09-01",
    "dataFim": "2026-03-01",
    "status": "EM_ANDAMENTO",
    "consultoresIds": [1]
  }
]
```

---

## Associar Consultor a Projeto

**POST** `/api/projects/1/consultants/1`

Resposta (exemplo):
```json
{
  "id": 1,
  "nome": "Implantação ERP",
  "descricao": "Projeto para implantar sistema ERP",
  "dataInicio": "2025-09-01",
  "dataFim": "2026-03-01",
  "status": "EM_ANDAMENTO",
  "consultoresIds": [1]
}
```

### Deploy no Google Cloud Run
1. Criar e configurar instância do Cloud SQL (PostgreSQL).  
2. Configurar Secret Manager para armazenar `DB_PASSWORD`.  
3. Publicar a imagem no Artifact Registry:  
   ```bash
   docker push us-docker.pkg.dev/<PROJECT_ID>/<REPO>/ey-backend:1
   ```
4. Deploy no Cloud Run:  
   ```bash
   gcloud run deploy ey-backend --image=us-docker.pkg.dev/<PROJECT_ID>/<REPO>/ey-backend:1 --region=us-central1 --allow-unauthenticated
   ```

---

## Conclusão

O **EY Backend** segue boas práticas de desenvolvimento, utilizando padrões reconhecidos para garantir modularidade, testabilidade e segurança. A arquitetura em camadas, uso de DTOs e tratamento global de exceções tornam o sistema robusto e pronto para escalabilidade em produção na nuvem.
