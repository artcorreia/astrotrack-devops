# language: pt
@regressivo
Funcionalidade: Gestão de viagens
  Como operador do AstroTrack
  Quero planejar, consultar, alterar e remover viagens
  Para acompanhar cada entrega do cliente, com motorista e veículo definidos

  Contexto:
    Dado que estou autenticado na API

  Cenário: Listar viagens
    Dado que existe uma viagem cadastrada
    Quando eu listo as viagens
    Então o status code da resposta deve ser 200
    E a lista deve conter a viagem cadastrada
    E o corpo da resposta deve ser válido contra o schema "schemas/viagem-lista-response.json"

  Esquema do Cenário: Listar viagens por status <status>
    Dado que existe uma viagem com status "<status>"
    Quando eu listo as viagens com status "<status>"
    Então o status code da resposta deve ser 200
    E todos os itens da lista devem ter o campo "status" igual a "<status>"
    E a lista deve conter a viagem cadastrada
    E o corpo da resposta deve ser válido contra o schema "schemas/viagem-lista-response.json"

    Exemplos:
      | status       |
      | AGENDADA     |
      | EM_ANDAMENTO |
      | FINALIZADA   |
      | CANCELADA    |

  Cenário: Listar viagens com status inexistente é recusado
    Quando eu listo as viagens com status "XPTO"
    Então o status code da resposta deve ser 400
    E o erro de validação do campo "status" deve ser "The value 'XPTO' is not valid."

  Cenário: Buscar viagem por id
    Dado que existe uma viagem cadastrada
    Quando eu busco a viagem cadastrada pelo id
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os dados da viagem cadastrada
    E o corpo da resposta deve ser válido contra o schema "schemas/viagem-response.json"

  Esquema do Cenário: Operação em viagem inexistente retorna 404 (<operacao>)
    Quando eu <operacao> a viagem de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Viagem não localizada com id 999999999999"

    Exemplos:
      | operacao |
      | busco    |
      | atualizo |
      | excluo   |

  Cenário: Cadastrar viagem com dados válidos
    Quando eu cadastro uma viagem com dados válidos
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os dados enviados
    E o corpo da resposta deve ser válido contra o schema "schemas/viagem-response.json"

  Cenário: Cadastrar viagem sem origem é recusado
    Quando eu cadastro uma viagem com o campo "origem" igual a "(ausente)"
    Então o status code da resposta deve ser 400
    E o erro de validação do campo "Origem" deve ser "A origem é obrigatória"

  Esquema do Cenário: Cadastrar viagem violando regra de negócio (<regra>)
    Quando eu cadastro uma viagem com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser <status>
    E a mensagem de erro deve ser "<mensagem>"

    Exemplos:
      | regra                       | campo       | valor               | status | mensagem                                              |
      | finalizada sem data de fim  | status      | FINALIZADA          | 400    | Viagens finalizadas precisam informar data de fim     |
      | data de fim antes do início | dataFim     | 2026-01-09T08:00:00 | 400    | A data de fim não pode ser anterior à data de início  |
      | cliente inexistente         | idCliente   | 999999999999        | 404    | Cliente não localizado com id 999999999999            |
      | motorista inexistente       | idMotorista | 999999999999        | 404    | Motorista não localizado com id 999999999999          |
      | veículo inexistente         | idVeiculo   | 999999999999        | 404    | Veículo não localizado com id 999999999999            |

  Cenário: Cadastrar viagem em andamento com motorista já em viagem é recusado
    Dado que existe uma viagem com status "EM_ANDAMENTO"
    Quando eu cadastro uma nova viagem "EM_ANDAMENTO" com o motorista da viagem cadastrada
    Então o status code da resposta deve ser 409
    E a mensagem de erro deve ser "Motorista ou veículo já está vinculado a uma viagem em andamento"

  Cenário: Atualizar viagem
    Dado que existe uma viagem cadastrada
    Quando eu atualizo o campo "destino" da viagem cadastrada para "Ribeirão Preto - SP"
    Então o status code da resposta deve ser 200
    E o campo "destino" da resposta deve ser "Ribeirão Preto - SP"
    E o corpo da resposta deve ser válido contra o schema "schemas/viagem-response.json"
    Quando eu busco a viagem cadastrada pelo id
    Então o campo "destino" da resposta deve ser "Ribeirão Preto - SP"

  Cenário: Excluir viagem agendada
    Dado que existe uma viagem cadastrada
    Quando eu excluo a viagem cadastrada
    Então o status code da resposta deve ser 204
    Quando eu busco a viagem cadastrada pelo id
    Então o status code da resposta deve ser 404
