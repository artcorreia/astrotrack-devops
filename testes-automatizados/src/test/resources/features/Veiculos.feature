# language: pt
@regressivo
Funcionalidade: Gestão de veículos
  Como operador do AstroTrack
  Quero cadastrar, consultar, alterar e remover veículos da frota
  Para controlar quais veículos estão disponíveis para as viagens

  Contexto:
    Dado que estou autenticado na API

  Cenário: Listar veículos
    Dado que existe um veículo cadastrado
    Quando eu listo os veículos
    Então o status code da resposta deve ser 200
    E a lista deve conter o veículo cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/veiculo-lista-response.json"

  Cenário: Buscar veículo por id
    Dado que existe um veículo cadastrado
    Quando eu busco o veículo cadastrado pelo id
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os dados do veículo cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/veiculo-response.json"

  Esquema do Cenário: Operação em veículo inexistente retorna 404 (<operacao>)
    Quando eu <operacao> o veículo de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Veículo não localizado com id 999999999999"

    Exemplos:
      | operacao |
      | busco    |
      | atualizo |
      | excluo   |

  Cenário: Cadastrar veículo com dados válidos
    Quando eu cadastro um veículo com dados válidos
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os dados enviados
    E o corpo da resposta deve ser válido contra o schema "schemas/veiculo-response.json"

  Esquema do Cenário: Cadastrar veículo com dados inválidos (<campo>)
    Quando eu cadastro um veículo com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser 400
    E o erro de validação do campo "<campoErro>" deve ser "<mensagem>"

    Exemplos:
      | campo  | valor     | campoErro | mensagem                                                 |
      | placa  | abc1d23   | Placa     | A placa deve seguir o padrão Mercosul ou antigo sem hífen |
      | ano    | 1970      | Ano       | O ano deve estar entre 1980 e 2100                       |
      | modelo | (ausente) | Modelo    | O modelo é obrigatório                                   |

  Cenário: Cadastrar veículo com placa já existente é recusado
    Dado que existe um veículo cadastrado
    Quando eu cadastro outro veículo com o mesmo "placa"
    Então o status code da resposta deve ser 409
    E a mensagem de erro deve ser "Já existe veículo cadastrado com esta placa"

  Cenário: Atualizar veículo
    Dado que existe um veículo cadastrado
    Quando eu atualizo o campo "status" do veículo cadastrado para "EM_MANUTENCAO"
    Então o status code da resposta deve ser 200
    E o campo "status" da resposta deve ser "EM_MANUTENCAO"
    E o corpo da resposta deve ser válido contra o schema "schemas/veiculo-response.json"
    Quando eu busco o veículo cadastrado pelo id
    Então o campo "status" da resposta deve ser "EM_MANUTENCAO"

  Cenário: Excluir veículo
    Dado que existe um veículo cadastrado
    Quando eu excluo o veículo cadastrado
    Então o status code da resposta deve ser 204
    Quando eu busco o veículo cadastrado pelo id
    Então o status code da resposta deve ser 404

  # Comportamento atual da API: não há validação no service; a FK com Restrict no banco impede
  # a exclusão e a API responde 500 (o veículo continua cadastrado).
  Cenário: Excluir veículo vinculado a uma viagem é impedido
    Dado que existe uma viagem cadastrada
    Quando eu excluo o veículo vinculado à viagem cadastrada
    Então o status code da resposta deve ser 500
    Quando eu busco o veículo vinculado à viagem cadastrada
    Então o status code da resposta deve ser 200
