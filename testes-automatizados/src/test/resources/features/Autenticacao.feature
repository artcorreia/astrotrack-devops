# language: pt
@regressivo
Funcionalidade: Autenticação de usuários
  Como usuário do AstroTrack
  Quero me registrar e fazer login na API
  Para obter um token JWT e acessar os recursos protegidos

  Cenário: Registrar usuário novo
    Dado que eu tenho um usuário novo com dados únicos
    Quando eu registro esse usuário na API
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter um token
    E o corpo da resposta deve ser válido contra o schema "schemas/auth-response.json"

  Cenário: Registrar usuário com e-mail já cadastrado é recusado
    Dado que eu tenho um usuário novo com dados únicos
    E eu registro esse usuário na API
    Quando eu tento registrar outro usuário com o mesmo e-mail
    Então o status code da resposta deve ser 409
    E a mensagem de erro deve ser "Já existe um usuário cadastrado com este e-mail"

  Cenário: Login com credenciais válidas retorna token
    Dado que eu tenho um usuário novo com dados únicos
    E eu registro esse usuário na API
    Quando eu faço login com o e-mail e a senha corretos
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter um token
    E o corpo da resposta deve ser válido contra o schema "schemas/auth-response.json"

  Cenário: Login com senha errada é recusado
    Dado que eu tenho um usuário novo com dados únicos
    E eu registro esse usuário na API
    Quando eu faço login com o e-mail correto e uma senha incorreta
    Então o status code da resposta deve ser 401
    E a mensagem de erro deve ser "Credenciais inválidas"

  Esquema do Cenário: Acessar endpoint protegido sem token é recusado
    Quando eu chamo o endpoint GET "<endpoint>" sem token
    Então o status code da resposta deve ser 401

    Exemplos:
      | endpoint         |
      | /api/clientes    |
      | /api/motoristas  |
      | /api/veiculos    |
      | /api/viagens     |
      | /api/checkpoints |
