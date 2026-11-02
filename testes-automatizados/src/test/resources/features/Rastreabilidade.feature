# language: pt
@regressivo
Funcionalidade: Rastreabilidade das viagens
  Como gestor de frota do AstroTrack
  Quero que cada viagem tenha seus checkpoints registrados e protegidos contra alterações indevidas
  Para garantir a rastreabilidade e a conformidade das entregas

  Esta funcionalidade sustenta o pilar de Governança do ESG: o histórico de posições
  (latitude, longitude, botão de pânico e porta aberta) é a trilha de auditoria de cada entrega.
  Por isso a API só aceita checkpoints em viagens ativas e não permite apagar uma viagem
  que ainda está em andamento, preservando a integridade das evidências.

  Contexto:
    Dado que estou autenticado na API

  Cenário: Registrar checkpoint em viagem em andamento
    Dado que existe uma viagem com status "EM_ANDAMENTO"
    Quando eu registro um checkpoint nessa viagem com os dados:
      | latitude     | -23.5505200         |
      | longitude    | -46.6333080         |
      | dataRegistro | 2026-01-10T09:30:00 |
      | botaoPanico  | 0                   |
      | portaAberta  | 0                   |
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os dados do checkpoint enviado
    E o corpo da resposta deve ser válido contra o schema "schemas/checkpoint-response.json"

  Cenário: Registrar checkpoint em viagem finalizada é recusado
    Dado que existe uma viagem com status "FINALIZADA"
    Quando eu registro um checkpoint nessa viagem com os dados:
      | latitude     | -22.9056400         |
      | longitude    | -47.0608300         |
      | dataRegistro | 2026-01-10T13:00:00 |
      | botaoPanico  | 0                   |
      | portaAberta  | 0                   |
    Então o status code da resposta deve ser 400
    E a mensagem de erro deve ser "Não é possível registrar checkpoints em viagens encerradas"

  Cenário: Excluir viagem em andamento é bloqueado
    Dado que existe uma viagem com status "EM_ANDAMENTO"
    Quando eu excluo essa viagem
    Então o status code da resposta deve ser 400
    E a mensagem de erro deve ser "Não é possível remover uma viagem em andamento"

  Cenário: Consultar viagem inexistente
    Quando eu consulto a viagem de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Viagem não localizada com id 999999999999"
