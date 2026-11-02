# language: pt
@regressivo
Funcionalidade: Gestão de clientes
  Como operador do AstroTrack
  Quero cadastrar, consultar, alterar e remover clientes
  Para manter atualizada a base de clientes que contratam as viagens

  Contexto:
    Dado que estou autenticado na API

  Cenário: Listar clientes
    Dado que existe um cliente cadastrado
    Quando eu listo os clientes
    Então o status code da resposta deve ser 200
    E a lista deve conter o cliente cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/cliente-lista-response.json"

  Cenário: Buscar cliente por id
    Dado que existe um cliente cadastrado
    Quando eu busco o cliente cadastrado pelo id
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter os dados do cliente cadastrado
    E o corpo da resposta deve ser válido contra o schema "schemas/cliente-response.json"

  Esquema do Cenário: Operação em cliente inexistente retorna 404 (<operacao>)
    Quando eu <operacao> o cliente de id 999999999999
    Então o status code da resposta deve ser 404
    E a mensagem de erro deve ser "Cliente não localizado com id 999999999999"

    Exemplos:
      | operacao |
      | busco    |
      | atualizo |
      | excluo   |

  Cenário: Cadastrar cliente com dados válidos
    Quando eu cadastro um cliente com dados válidos
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter os dados enviados
    E o corpo da resposta deve ser válido contra o schema "schemas/cliente-response.json"

  Esquema do Cenário: Cadastrar cliente com dados inválidos (<campo>)
    Quando eu cadastro um cliente com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser 400
    E o erro de validação do campo "<campoErro>" deve ser "<mensagem>"

    Exemplos:
      | campo | valor               | campoErro | mensagem                                     |
      | nome  | (ausente)           | Nome      | O nome é obrigatório                         |
      | email | email-invalido      | Email     | O e-mail informado é inválido                |
      | cnpj  | 1234567890123456789 | Cnpj      | O CNPJ deve possuir no máximo 18 caracteres  |

  Cenário: Cadastrar cliente com CNPJ já existente é recusado
    Dado que existe um cliente cadastrado
    Quando eu cadastro outro cliente com o mesmo "cnpj"
    Então o status code da resposta deve ser 409
    E a mensagem de erro deve ser "Já existe cliente cadastrado com este CNPJ"

  Cenário: Atualizar cliente
    Dado que existe um cliente cadastrado
    Quando eu atualizo o campo "telefone" do cliente cadastrado para "11912345678"
    Então o status code da resposta deve ser 200
    E o campo "telefone" da resposta deve ser "11912345678"
    E o corpo da resposta deve ser válido contra o schema "schemas/cliente-response.json"
    Quando eu busco o cliente cadastrado pelo id
    Então o campo "telefone" da resposta deve ser "11912345678"

  Cenário: Excluir cliente
    Dado que existe um cliente cadastrado
    Quando eu excluo o cliente cadastrado
    Então o status code da resposta deve ser 204
    Quando eu busco o cliente cadastrado pelo id
    Então o status code da resposta deve ser 404

  # Comportamento atual da API: não há validação no service; a FK com Restrict no banco impede
  # a exclusão e a API responde 500 (o cliente continua cadastrado).
  Cenário: Excluir cliente vinculado a uma viagem é impedido
    Dado que existe uma viagem cadastrada
    Quando eu excluo o cliente vinculado à viagem cadastrada
    Então o status code da resposta deve ser 500
    Quando eu busco o cliente vinculado à viagem cadastrada
    Então o status code da resposta deve ser 200
