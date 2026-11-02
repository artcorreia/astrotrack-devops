# AstroTrack - Testes automatizados (BDD e API)

Testes BDD (Cucumber + Gherkin) e testes de API da AstroTrack. Cada cenário chama a API real, rodando com o
Oracle, e verifica três coisas:

- **Status code** da resposta (200, 201, 204, 400, 401, 404, 409...);
- **Corpo JSON**: campos e valores esperados (por exemplo, o `destino` da viagem depois de um PUT);
- **Contrato**: a resposta é validada contra um **JSON Schema**, então um campo novo, removido ou com tipo errado
  quebra o teste.

### Ligação com o ESG (Governança)

O AstroTrack rastreia entregas: cada viagem tem checkpoints de posição e alertas (pânico e porta aberta), que formam a
trilha de auditoria da entrega. Os testes garantem que essa trilha é confiável. A API só aceita checkpoints em viagens
ativas e não deixa apagar uma viagem em andamento
([`Rastreabilidade.feature`](src/test/resources/features/Rastreabilidade.feature)). Também não deixa apagar cliente,
motorista ou veículo que tenha viagem vinculada (cenários nas features de cada recurso). Isso sustenta o pilar de
**Governança** do ESG.

---

## Ferramentas e versões

| Ferramenta | Versão | Para quê |
|---|---|---|
| Java (JDK) | 17 | Linguagem dos steps e services |
| Maven | 3.9+ | Build e execução (`mvn clean test`) |
| Cucumber (`cucumber-java`, `cucumber-junit`) | 7.18.1 | Cenários em Gherkin (em português, `# language: pt`) |
| RestAssured | 5.5.0 | Chamadas HTTP à API |
| networknt `json-schema-validator` | 1.5.1 | Validação de contrato (JSON Schema draft-07) |
| JUnit | 4 (via `cucumber-junit`) | Runner que o Maven Surefire executa |
| Gson / Lombok | 2.11.0 / 1.18.34 | Serialização dos models / getters e setters |

---

## Estrutura de pastas

```
testes-automatizados/
├── src/test/resources/
│   ├── features/   # Cenários em Gherkin, um arquivo .feature por funcionalidade
│   └── schemas/    # JSON Schemas de contrato (resposta única e lista, por recurso)
├── src/test/java/
│   ├── steps/      # Step definitions: ligam cada frase do Gherkin ao código (+ Hooks que limpa o contexto)
│   ├── services/   # Chamadas à API com RestAssured, token JWT, validação de schema e geração de dados únicos
│   ├── model/      # Corpos das requisições (cliente, motorista, veículo, viagem, checkpoint, login, registro)
│   └── runner/     # TestRunner: configura o Cucumber (features, glue, tag @regressivo, relatório HTML)
├── Dockerfile          # Imagem dos testes (maven:3.9-eclipse-temurin-17)
├── executar-testes.sh  # Entrypoint: espera a API responder, roda os testes e copia o relatório
└── pom.xml
```

Os dados de teste (CNPJ, CPF, CNH, placa, e-mail) são gerados a partir do timestamp (`DadosUnicos`). Por isso os testes
podem rodar várias vezes no mesmo banco sem bater nas restrições de unicidade.

---

## Como executar

Nas três formas, a API precisa estar no ar com o Oracle. O `.env` da raiz precisa existir (`cp .env.example .env`).

### 1. Só com Docker (não precisa de Java nem Maven na máquina)

Na **raiz** do repositório:

```bash
docker compose --profile testes run --rm testes
```

Esse comando sobe o Oracle e a API, se ainda não estiverem rodando. O container de testes espera a API responder em
`/swagger/v1/swagger.json` (até 300 s) e depois roda `mvn clean test`. Depois de alterar features ou steps, use
`--build` para reconstruir a imagem dos testes:

```bash
docker compose --profile testes run --rm --build testes
```

O serviço `testes` fica no profile `testes`, então um `docker compose up` normal **não** roda os testes.

### 2. Local com Maven

Pré-requisitos: JDK 17 e Maven 3.9+.

```bash
# na raiz: sobe Oracle + API em http://localhost:8080
docker compose up -d

# espere a API responder (http://localhost:8080 abre o Swagger) e rode:
cd testes-automatizados
mvn clean test
```

### 3. Pelo pipeline (GitHub Actions)

O job **"Testes BDD e de API (Cucumber)"** do [`ci-cd.yml`](../.github/workflows/ci-cd.yml) roda em todo push na
`develop`/`main` e em todo pull request para a `main`, depois do job de build e testes unitários. Ele faz o seguinte:

1. cria o `.env` a partir do `.env.example` (ambiente descartável, sem segredos reais);
2. sobe a API e o Oracle com `docker compose` e espera a API responder;
3. roda `docker compose --profile testes run --rm -T --build testes`;
4. publica o artifact **`relatorio-testes-bdd`** (relatório HTML + saída do Maven);
5. em caso de falha, mostra os logs da API e do Oracle;
6. derruba o ambiente com `docker compose down -v`, mesmo se algo falhou.

A imagem Docker e os deploys (staging e produção) dependem desse job: **se um cenário BDD falhar, não há deploy.**

---

## Configuração e relatório

| Variável | Padrão | Descrição |
|---|---|---|
| `API_BASE_URL` | `http://localhost:8080` | URL base da API testada. No docker-compose é `http://api:8080` (nome do serviço na rede `astrotrack-net`). |
| `API_WAIT_TIMEOUT` | `300` | Só no container: segundos máximos de espera pela API antes de desistir. |

Exemplo apontando para outra instância: `API_BASE_URL=https://minha-api.exemplo.com mvn clean test`.

Onde fica o **relatório HTML do Cucumber** (`cucumber-reports.html`):

| Forma de execução | Local |
|---|---|
| Docker | `testes-automatizados/relatorios/cucumber-reports.html` (+ `saida-testes.txt` com a saída do Maven) |
| Maven local | `testes-automatizados/target/cucumber-reports.html` |
| Pipeline | artifact `relatorio-testes-bdd` na página da execução do workflow |

As pastas `target/` e `relatorios/` são ignoradas pelo git.

---

## Cobertura

**29 de 29 endpoints** da API cobertos por **86 cenários** (423 passos), todos aprovados. A tabela com cada endpoint, o
método e os cenários que o cobrem está em
[`docs/evidencias-bdd/cobertura-endpoints.md`](../docs/evidencias-bdd/cobertura-endpoints.md). As evidências de
execução (prints do relatório, `mvn clean test` e execução pelo Docker) estão em
[`docs/evidencias-bdd/`](../docs/evidencias-bdd/).

### Features

Todas as features têm a tag `@regressivo`, que é a tag executada pelo `TestRunner`.

| Feature | Cenários | O que cobre |
|---|---|---|
| [`Autenticacao.feature`](src/test/resources/features/Autenticacao.feature) | 9 | Registro e login (token JWT), credenciais inválidas e recusa (401) dos endpoints protegidos chamados sem token. |
| [`Clientes.feature`](src/test/resources/features/Clientes.feature) | 13 | CRUD de clientes, validações (400), CNPJ duplicado (409), cliente inexistente (404) e exclusão de cliente com viagem. |
| [`Motoristas.feature`](src/test/resources/features/Motoristas.feature) | 13 | CRUD de motoristas, validações (400), CPF duplicado (409), motorista inexistente (404) e exclusão de motorista com viagem. |
| [`Veiculos.feature`](src/test/resources/features/Veiculos.feature) | 13 | CRUD de veículos, validações (400), placa duplicada (409), veículo inexistente (404) e exclusão de veículo com viagem. |
| [`Viagens.feature`](src/test/resources/features/Viagens.feature) | 20 | CRUD de viagens, filtro por status e regras de negócio na criação (datas, vínculos inexistentes, motorista já em viagem). |
| [`Checkpoints.feature`](src/test/resources/features/Checkpoints.feature) | 14 | CRUD de checkpoints, listagem por viagem, validações (latitude, longitude, botão de pânico) e viagem inexistente (404). |
| [`Rastreabilidade.feature`](src/test/resources/features/Rastreabilidade.feature) | 4 | Trilha de auditoria (ESG): checkpoint aceito em viagem em andamento e recusado em finalizada; viagem em andamento não pode ser excluída. |

---

## Defeitos encontrados pelos testes

### Excluir cliente, motorista ou veículo com viagem vinculada devolve 500

| | |
|---|---|
| **Endpoints** | `DELETE /api/clientes/{id}`, `DELETE /api/motoristas/{id}`, `DELETE /api/veiculos/{id}` |
| **Como reproduzir** | Cadastrar uma viagem e tentar excluir o cliente, o motorista ou o veículo dela |
| **Esperado** | **409 Conflict** com uma mensagem própria da API (ex.: "Cliente possui viagens vinculadas e não pode ser excluído") |
| **Obtido** | **500 Internal Server Error** com `"erro": "Erro interno do servidor"` e, em `mensagem`, o texto interno da exceção do Entity Framework Core (`DbUpdateException`) |

**Causa:** os services não verificam se existem viagens vinculadas antes de excluir. Quem impede a exclusão é a FK com
`DeleteBehavior.Restrict` no banco. A `DbUpdateException` não é tratada e cai no caso genérico do
`GlobalExceptionMiddleware`, que responde 500 e repassa `exception.Message`. A integridade dos dados está garantida (o
registro continua existindo), mas o cliente da API recebe um erro de servidor em vez de um conflito de negócio, e a
resposta expõe um detalhe interno da implementação.

**Correção sugerida:** antes do `Remove`, verificar no service se há viagens vinculadas e lançar `ConflictException`. O
middleware já converte essa exceção em 409.

**Nos testes:** os três cenários "Excluir … vinculado a uma viagem é impedido" registram o comportamento **atual** (500) e
confirmam que o registro não foi apagado. Quando a correção for feita, eles devem passar a esperar **409**.
