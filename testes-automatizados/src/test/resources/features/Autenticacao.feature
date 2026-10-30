# language: pt
@regressivo
Funcionalidade: Autenticação de usuários
  Como usuário do AstroTrack
  Quero me registrar e fazer login na API
  Para obter um token JWT e acessar os recursos protegidos

  Contexto:
    Dado que eu tenho um usuário novo com dados únicos
    E eu registro esse usuário na API

  Cenário: Login com credenciais válidas retorna token
    Quando eu faço login com o e-mail e a senha corretos
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter um token
    E o corpo da resposta deve ser válido contra o schema "schemas/auth-response.json"

  Cenário: Login com senha errada é recusado
    Quando eu faço login com o e-mail correto e a senha "SenhaErrada@999"
    Então o status code da resposta deve ser 401
    E a mensagem de erro deve ser "Credenciais inválidas"
