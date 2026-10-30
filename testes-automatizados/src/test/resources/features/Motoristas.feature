# language: pt
@regressivo
Funcionalidade: Gestão de motoristas
  Como operador do AstroTrack
  Quero cadastrar, consultar, alterar e remover motoristas
  Para saber quem está habilitado a conduzir cada viagem

  Contexto:
    Dado que estou autenticado na API

  Cenário: Listar motoristas
    Dado que existe um motorista cadastrado
    Quando eu listo os motoristas
    Então o status code da resposta deve ser 200
    E a lista deve conter o motorista cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/motorista-lista-response.json"

  Cenário: Buscar motorista por id
    Dado que existe um motorista cadastrado
    Quando eu busco o motorista cadastrado pelo id
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os dados do motorista cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/motorista-response.json"

  Esquema do Cenário: Operação em motorista inexistente retorna 404 (<operacao>)
    Quando eu <operacao> o motorista de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Motorista não localizado com id 999999999999"

    Exemplos:
      | operacao |
      | busco    |
      | atualizo |
      | excluo   |

  Cenário: Cadastrar motorista com dados válidos
    Quando eu cadastro um motorista com dados válidos
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os dados enviados
    E o corpo da resposta deve ser válido contra o schema "schemas/motorista-response.json"

  Esquema do Cenário: Cadastrar motorista com dados inválidos (<campo>)
    Quando eu cadastro um motorista com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser 400
    E o erro de validação do campo "<campoErro>" deve ser "<mensagem>"

    Exemplos:
      | campo | valor        | campoErro | mensagem                                     |
      | nome  | A            | Nome      | O nome deve possuir entre 2 e 100 caracteres |
      | cpf   | (ausente)    | Cpf       | O CPF é obrigatório                          |
      | cnh   | 123456789012 | Cnh       | A CNH deve possuir 11 dígitos                |

  Cenário: Cadastrar motorista com CPF já existente é recusado
    Dado que existe um motorista cadastrado
    Quando eu cadastro outro motorista com o mesmo "cpf"
    Então o status code da resposta deve ser 409
    E a mensagem de erro deve ser "Já existe motorista cadastrado com este CPF"

  Cenário: Atualizar motorista
    Dado que existe um motorista cadastrado
    Quando eu atualizo o campo "status" do motorista cadastrado para "BLOQUEADO"
    Então o status code da resposta deve ser 200
    E o campo "status" da resposta deve ser "BLOQUEADO"
    E o corpo da resposta deve ser válido contra o schema "schemas/motorista-response.json"
    Quando eu busco o motorista cadastrado pelo id
    Então o campo "status" da resposta deve ser "BLOQUEADO"

  Cenário: Excluir motorista
    Dado que existe um motorista cadastrado
    Quando eu excluo o motorista cadastrado
    Então o status code da resposta deve ser 204
    Quando eu busco o motorista cadastrado pelo id
    Então o status code da resposta deve ser 404

  # Comportamento atual da API: não há validação no service; a FK com Restrict no banco impede
  # a exclusão e a API responde 500 (o motorista continua cadastrado).
  Cenário: Excluir motorista vinculado a uma viagem é impedido
    Dado que existe uma viagem cadastrada
    Quando eu excluo o motorista vinculado à viagem cadastrada
    Então o status code da resposta deve ser 500
    Quando eu busco o motorista vinculado à viagem cadastrada
    Então o status code da resposta deve ser 200
