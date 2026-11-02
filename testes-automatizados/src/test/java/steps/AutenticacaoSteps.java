package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import org.json.JSONObject;
import services.AutenticacaoService;
import services.BaseService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AutenticacaoSteps {

    private final AutenticacaoService autenticacaoService = new AutenticacaoService();

    @Dado("que estou autenticado na API")
    public void queEstouAutenticadoNaApi() {
        autenticacaoService.autenticar();
    }

    @Dado("que eu tenho um usuário novo com dados únicos")
    public void queEuTenhoUmUsuarioNovoComDadosUnicos() {
        autenticacaoService.prepararUsuarioUnico();
    }

    @E("eu registro esse usuário na API")
    public void euRegistroEsseUsuarioNaApi() {
        autenticacaoService.registrar();
        assertEquals("Falha ao registrar usuário: " + BaseService.getUltimaResposta().asString(),
                201, BaseService.getUltimaResposta().statusCode());
    }

    @Quando("eu faço login com o e-mail e a senha corretos")
    public void euFacoLoginComOEmailEASenhaCorretos() {
        autenticacaoService.loginModel.setEmail(autenticacaoService.registerModel.getEmail());
        autenticacaoService.loginModel.setSenha(autenticacaoService.registerModel.getSenha());
        autenticacaoService.login();
    }

    @Quando("eu faço login com o e-mail correto e uma senha incorreta")
    public void euFacoLoginComOEmailCorretoEUmaSenhaIncorreta() {
        autenticacaoService.loginModel.setEmail(autenticacaoService.registerModel.getEmail());
        autenticacaoService.loginModel.setSenha(autenticacaoService.registerModel.getSenha() + "_incorreta");
        autenticacaoService.login();
    }

    @Quando("eu tento registrar outro usuário com o mesmo e-mail")
    public void euTentoRegistrarOutroUsuarioComOMesmoEmail() {
        autenticacaoService.registerModel.setUsuario(autenticacaoService.registerModel.getUsuario() + "_2");
        autenticacaoService.registrar();
    }

    @Quando("eu chamo o endpoint GET {string} sem token")
    public void euChamoOEndpointGetSemToken(String endpoint) {
        autenticacaoService.getSemToken(endpoint);
    }

    @E("o corpo da resposta deve conter um token")
    public void oCorpoDaRespostaDeveConterUmToken() {
        String token = new JSONObject(BaseService.getUltimaResposta().asString()).optString("token");
        assertFalse("Token ausente ou vazio na resposta", token.isBlank());
    }
}
