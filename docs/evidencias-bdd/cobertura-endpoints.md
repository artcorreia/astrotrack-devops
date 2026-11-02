# Cobertura de endpoints — testes BDD (Cucumber + RestAssured + JSON Schema)

Projeto: `testes-automatizados/` · execução: `mvn clean test` · resultado: **86 cenários / 423 passos aprovados**
(ver `mvn-clean-test.txt` e os prints `01` a `09` nesta pasta).

Todos os endpoints exigem token JWT, exceto os de `/api/auth`. Nos cenários, "Esquema" indica um Esquema do Cenário
(entre parênteses, quantos exemplos ele tem).

| Endpoint | Método | Cenários que o cobrem | Arquivo .feature |
|---|---|---|---|
| `/api/auth/register` | POST | Registrar usuário novo (201 + schema) · Registrar usuário com e-mail já cadastrado é recusado (409) · usado como pré-requisito em todos os cenários autenticados | Autenticacao.feature |
| `/api/auth/login` | POST | Login com credenciais válidas retorna token (200 + schema) · Login com senha errada é recusado (401) | Autenticacao.feature |
| `/api/clientes` | GET | Listar clientes (200 + schema de lista) · Esquema: Acessar endpoint protegido sem token (401) | Clientes.feature, Autenticacao.feature |
| `/api/clientes/{id}` | GET | Buscar cliente por id (200 + campos + schema) · Esquema: Operação em cliente inexistente (404) · verificação pós-PUT e pós-DELETE (404) · Excluir cliente vinculado a uma viagem (cliente continua existindo) | Clientes.feature |
| `/api/clientes` | POST | Cadastrar cliente com dados válidos (201 + campos + schema) · Esquema: Cadastrar cliente com dados inválidos (3 exemplos, 400) · Cadastrar cliente com CNPJ já existente (409) | Clientes.feature |
| `/api/clientes/{id}` | PUT | Atualizar cliente (200 + campo alterado + schema) · Esquema: Operação em cliente inexistente (404) | Clientes.feature |
| `/api/clientes/{id}` | DELETE | Excluir cliente (204, depois GET 404) · Esquema: Operação em cliente inexistente (404) · Excluir cliente vinculado a uma viagem é impedido (500) | Clientes.feature |
| `/api/motoristas` | GET | Listar motoristas (200 + schema de lista) · Esquema: Acessar endpoint protegido sem token (401) | Motoristas.feature, Autenticacao.feature |
| `/api/motoristas/{id}` | GET | Buscar motorista por id (200 + campos + schema) · Esquema: Operação em motorista inexistente (404) · verificação pós-PUT e pós-DELETE (404) · Excluir motorista vinculado a uma viagem (motorista continua existindo) | Motoristas.feature |
| `/api/motoristas` | POST | Cadastrar motorista com dados válidos (201 + campos + schema) · Esquema: Cadastrar motorista com dados inválidos (3 exemplos, 400) · Cadastrar motorista com CPF já existente (409) | Motoristas.feature |
| `/api/motoristas/{id}` | PUT | Atualizar motorista (200 + campo alterado + schema) · Esquema: Operação em motorista inexistente (404) | Motoristas.feature |
| `/api/motoristas/{id}` | DELETE | Excluir motorista (204, depois GET 404) · Esquema: Operação em motorista inexistente (404) · Excluir motorista vinculado a uma viagem é impedido (500) | Motoristas.feature |
| `/api/veiculos` | GET | Listar veículos (200 + schema de lista) · Esquema: Acessar endpoint protegido sem token (401) | Veiculos.feature, Autenticacao.feature |
| `/api/veiculos/{id}` | GET | Buscar veículo por id (200 + campos + schema) · Esquema: Operação em veículo inexistente (404) · verificação pós-PUT e pós-DELETE (404) · Excluir veículo vinculado a uma viagem (veículo continua existindo) | Veiculos.feature |
| `/api/veiculos` | POST | Cadastrar veículo com dados válidos (201 + campos + schema) · Esquema: Cadastrar veículo com dados inválidos (3 exemplos, 400) · Cadastrar veículo com placa já existente (409) | Veiculos.feature |
| `/api/veiculos/{id}` | PUT | Atualizar veículo (200 + campo alterado + schema) · Esquema: Operação em veículo inexistente (404) | Veiculos.feature |
| `/api/veiculos/{id}` | DELETE | Excluir veículo (204, depois GET 404) · Esquema: Operação em veículo inexistente (404) · Excluir veículo vinculado a uma viagem é impedido (500) | Veiculos.feature |
| `/api/viagens` | GET | Listar viagens (200 + schema de lista) · Esquema: Acessar endpoint protegido sem token (401) | Viagens.feature, Autenticacao.feature |
| `/api/viagens/status/{status}` | GET | Esquema: Listar viagens por status (AGENDADA, EM_ANDAMENTO, FINALIZADA, CANCELADA → 200 + filtro + schema) · Listar viagens com status inexistente (400) | Viagens.feature |
| `/api/viagens/{id}` | GET | Buscar viagem por id (200 + campos + schema) · Esquema: Operação em viagem inexistente (404) · Consultar viagem inexistente (404) · verificação pós-PUT e pós-DELETE (404) | Viagens.feature, Rastreabilidade.feature |
| `/api/viagens` | POST | Cadastrar viagem com dados válidos (201 + campos + schema) · Cadastrar viagem sem origem (400) · Esquema: Cadastrar viagem violando regra de negócio (5 exemplos: 400 e 404) · Cadastrar viagem em andamento com motorista já em viagem (409) | Viagens.feature |
| `/api/viagens/{id}` | PUT | Atualizar viagem (200 + campo alterado + schema) · Esquema: Operação em viagem inexistente (404) | Viagens.feature |
| `/api/viagens/{id}` | DELETE | Excluir viagem agendada (204, depois GET 404) · Esquema: Operação em viagem inexistente (404) · Excluir viagem em andamento é bloqueado (400) | Viagens.feature, Rastreabilidade.feature |
| `/api/checkpoints` | GET | Listar checkpoints (200 + schema de lista) · Esquema: Acessar endpoint protegido sem token (401) | Checkpoints.feature, Autenticacao.feature |
| `/api/checkpoints/viagem/{idViagem}` | GET | Listar checkpoints de uma viagem (200 + filtro + schema de lista) · Listar checkpoints de viagem inexistente (404) | Checkpoints.feature |
| `/api/checkpoints/{id}` | GET | Buscar checkpoint por id (200 + campos + schema) · Esquema: Operação em checkpoint inexistente (404) · verificação pós-PUT e pós-DELETE (404) | Checkpoints.feature |
| `/api/checkpoints` | POST | Cadastrar checkpoint com dados válidos (201 + campos + schema) · Esquema: Cadastrar checkpoint com dados inválidos (3 exemplos, 400) · Cadastrar checkpoint em viagem inexistente (404) · Registrar checkpoint em viagem em andamento (201 + schema) · Registrar checkpoint em viagem finalizada é recusado (400) | Checkpoints.feature, Rastreabilidade.feature |
| `/api/checkpoints/{id}` | PUT | Atualizar checkpoint (200 + campo alterado + schema) · Esquema: Operação em checkpoint inexistente (404) | Checkpoints.feature |
| `/api/checkpoints/{id}` | DELETE | Excluir checkpoint (204, depois GET 404) · Esquema: Operação em checkpoint inexistente (404) | Checkpoints.feature |

**29 de 29 endpoints cobertos.**

## Observações

- **Exclusão de cliente, motorista ou veículo vinculado a viagem:** a API responde **500**. Não há validação no service; quem
  impede a exclusão é a FK com `DeleteBehavior.Restrict` no banco, e a mensagem devolvida é a da exceção do EF Core. Os cenários
  registram o comportamento atual e confirmam que o registro continua existindo. Se a API passar a tratar esse caso
  (por exemplo, com 409), esses três cenários precisam ser atualizados.
- **Erros de validação dos DTOs** (`[Required]`, `[StringLength]`, `[Range]`, `[RegularExpression]`) voltam no formato
  `ProblemDetails` do ASP.NET (`errors.<Campo>`), e não no `ErroResponse` do `GlobalExceptionMiddleware`. Esse formato é usado só
  para as exceções de regra de negócio (400, 404 e 409).
- **Segurança sem token:** foi testada nos 5 endpoints de listagem. Os demais endpoints dos mesmos controllers ficam
  protegidos pelo mesmo `[Authorize]` declarado na classe.
- **Schemas** (`testes-automatizados/src/test/resources/schemas`): um de resposta única e um de lista por recurso. Os de lista
  reaproveitam o de resposta única via `$ref`. Os de resposta única usam `additionalProperties: false`, então um campo novo
  ou removido na API quebra o contrato.
