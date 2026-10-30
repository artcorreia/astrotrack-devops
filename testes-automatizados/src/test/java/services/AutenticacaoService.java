package services;

import io.restassured.response.Response;
import model.LoginModel;
import model.RegisterModel;

public class AutenticacaoService extends BaseService {

    private static final String SENHA_PADRAO = "Senha@123";

    public final RegisterModel registerModel = new RegisterModel();
    public final LoginModel loginModel = new LoginModel();

    /** Preenche o registerModel com usuário e e-mail únicos por execução. */
    public void prepararUsuarioUnico() {
        String sufixo = String.valueOf(DadosUnicos.proximo());
        registerModel.setUsuario("bdd_" + sufixo);
        registerModel.setEmail("bdd_" + sufixo + "@astrotrack.com");
        registerModel.setSenha(SENHA_PADRAO);
    }

    public Response registrar() {
        return post("/api/auth/register", registerModel);
    }

    public Response login() {
        return post("/api/auth/login", loginModel);
    }

    /** GET em um endpoint protegido sem enviar o header Authorization. */
    public Response getSemToken(String endpoint) {
        definirToken(null);
        return get(endpoint);
    }

    /** Registra um usuário novo, faz login e guarda o token para os próximos requests. */
    public void autenticar() {
        prepararUsuarioUnico();
        exigirStatus(registrar(), 201, "registrar usuário");

        loginModel.setEmail(registerModel.getEmail());
        loginModel.setSenha(registerModel.getSenha());
        Response response = exigirStatus(login(), 200, "fazer login");

        definirToken(response.jsonPath().getString("token"));
    }
}
