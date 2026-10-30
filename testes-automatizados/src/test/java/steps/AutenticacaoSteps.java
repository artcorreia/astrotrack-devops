package steps;

import com.networknt.schema.ValidationMessage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.json.JSONObject;
import services.AutenticacaoService;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutenticacaoSteps {

    private static final String SENHA = "Senha@123";

    private final AutenticacaoService autenticacaoService = new AutenticacaoService();

    @Dado("que eu tenho um usuário novo com dados únicos")
    public void queEuTenhoUmUsuarioNovoComDadosUnicos() {
        // Timestamp no usuário/e-mail garante que o cenário possa rodar várias vezes sem 409
        String sufixo = String.valueOf(System.currentTimeMillis());
        autenticacaoService.registerModel.setUsuario("bdd_" + sufixo);
        autenticacaoService.registerModel.setEmail("bdd_" + sufixo + "@astrotrack.com");
        autenticacaoService.registerModel.setSenha(SENHA);
    }

    @E("eu registro esse usuário na API")
    public void euRegistroEsseUsuarioNaApi() {
        autenticacaoService.registrar();
        assertEquals("Falha ao registrar usuário: " + autenticacaoService.response.asString(),
                201, autenticacaoService.response.statusCode());
    }

    @Quando("eu faço login com o e-mail e a senha corretos")
    public void euFacoLoginComOEmailEASenhaCorretos() {
        autenticacaoService.loginModel.setEmail(autenticacaoService.registerModel.getEmail());
        autenticacaoService.loginModel.setSenha(autenticacaoService.registerModel.getSenha());
        autenticacaoService.login();
    }

    @Quando("eu faço login com o e-mail correto e a senha {string}")
    public void euFacoLoginComOEmailCorretoEASenha(String senha) {
        autenticacaoService.loginModel.setEmail(autenticacaoService.registerModel.getEmail());
        autenticacaoService.loginModel.setSenha(senha);
        autenticacaoService.login();
    }

    @Entao("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        assertEquals(statusCode, autenticacaoService.response.statusCode());
    }

    @E("o corpo da resposta deve conter um token")
    public void oCorpoDaRespostaDeveConterUmToken() {
        String token = new JSONObject(autenticacaoService.response.asString()).optString("token");
        assertFalse("Token ausente ou vazio na resposta", token.isBlank());
    }

    @E("o corpo da resposta deve ser válido contra o schema {string}")
    public void oCorpoDaRespostaDeveSerValidoContraOSchema(String schema) {
        Set<ValidationMessage> erros = autenticacaoService.validarSchema(schema);
        assertTrue("Resposta fora do contrato: " + erros, erros.isEmpty());
    }

    @E("a mensagem de erro deve ser {string}")
    public void aMensagemDeErroDeveSer(String mensagem) {
        JSONObject corpo = new JSONObject(autenticacaoService.response.asString());
        assertEquals(mensagem, corpo.getString("mensagem"));
    }
}
