package steps;

import com.networknt.schema.ValidationMessage;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import org.json.JSONObject;
import services.BaseService;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Validações genéricas sobre a última resposta da API, usadas por todas as features. */
public class ComumSteps {

    @Entao("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        assertEquals("Corpo: " + BaseService.getUltimaResposta().asString(),
                statusCode, BaseService.getUltimaResposta().statusCode());
    }

    @E("o corpo da resposta deve ser válido contra o schema {string}")
    public void oCorpoDaRespostaDeveSerValidoContraOSchema(String schema) {
        Set<ValidationMessage> erros = BaseService.validarSchema(schema);
        assertTrue("Resposta fora do contrato: " + erros, erros.isEmpty());
    }

    @E("a mensagem de erro deve ser {string}")
    public void aMensagemDeErroDeveSer(String mensagem) {
        JSONObject corpo = new JSONObject(BaseService.getUltimaResposta().asString());
        assertEquals(mensagem, corpo.getString("mensagem"));
    }
}
