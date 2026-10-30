# language: pt
@regressivo
Funcionalidade: Gestão de checkpoints
  Como operador do AstroTrack
  Quero registrar, consultar, corrigir e remover checkpoints das viagens
  Para ter o histórico de posição e de alertas (pânico e porta aberta) de cada entrega

  Contexto:
    Dado que estou autenticado na API

  Cenário: Listar checkpoints
    Dado que existe um checkpoint cadastrado
    Quando eu listo os checkpoints
    Então o status code da resposta deve ser 200
    E a lista deve conter o checkpoint cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/checkpoint-lista-response.json"

  Cenário: Listar checkpoints de uma viagem
    Dado que existe um checkpoint cadastrado
    Quando eu listo os checkpoints da viagem cadastrada
    Então o status code da resposta deve ser 200
    E todos os checkpoints da lista devem ser da viagem cadastrada
    E a lista deve conter o checkpoint cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/checkpoint-lista-response.json"

  Cenário: Listar checkpoints de viagem inexistente
    Quando eu listo os checkpoints da viagem de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Viagem não localizada com id 999999999999"

  Cenário: Buscar checkpoint por id
    Dado que existe um checkpoint cadastrado
    Quando eu busco o checkpoint cadastrado pelo id
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os dados do checkpoint cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/checkpoint-response.json"

  Esquema do Cenário: Operação em checkpoint inexistente retorna 404 (<operacao>)
    Quando eu <operacao> o checkpoint de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Checkpoint não localizado com id 999999999999"

    Exemplos:
      | operacao |
      | busco    |
      | atualizo |
      | excluo   |

  Cenário: Cadastrar checkpoint com dados válidos
    Quando eu cadastro um checkpoint com dados válidos
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os dados enviados
    E o corpo da resposta deve ser válido contra o schema "schemas/checkpoint-response.json"

  Esquema do Cenário: Cadastrar checkpoint com dados inválidos (<campo>)
    Quando eu cadastro um checkpoint com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser 400
    E o erro de validação do campo "<campoErro>" deve ser "<mensagem>"

    Exemplos:
      | campo       | valor | campoErro   | mensagem                                 |
      | latitude    | 91    | Latitude    | A latitude deve estar entre -90 e 90     |
      | longitude   | -181  | Longitude   | A longitude deve estar entre -180 e 180  |
      | botaoPanico | 2     | BotaoPanico | BotaoPanico deve ser 0 ou 1              |

  Cenário: Cadastrar checkpoint em viagem inexistente é recusado
    Quando eu cadastro um checkpoint com o campo "idViagem" igual a "999999999999"
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Viagem não localizada com id 999999999999"

  Cenário: Atualizar checkpoint
    Dado que existe um checkpoint cadastrado
    Quando eu atualizo o campo "portaAberta" do checkpoint cadastrado para "1"
    Então o status code da resposta deve ser 200
    E o campo "portaAberta" da resposta deve ser "1"
    E o corpo da resposta deve ser válido contra o schema "schemas/checkpoint-response.json"
    Quando eu busco o checkpoint cadastrado pelo id
    Então o campo "portaAberta" da resposta deve ser "1"

  Cenário: Excluir checkpoint
    Dado que existe um checkpoint cadastrado
    Quando eu excluo o checkpoint cadastrado
    Então o status code da resposta deve ser 204
    Quando eu busco o checkpoint cadastrado pelo id
    Então o status code da resposta deve ser 404
