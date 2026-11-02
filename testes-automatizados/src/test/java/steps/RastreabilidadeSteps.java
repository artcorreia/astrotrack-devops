package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import model.CheckpointModel;
import org.json.JSONObject;
import services.BaseService;
import services.CheckpointService;
import services.ViagemService;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RastreabilidadeSteps {

    private final ViagemService viagemService = new ViagemService();
    private final CheckpointService checkpointService = new CheckpointService();

    private long idViagem;

    @Dado("que existe uma viagem com status {string}")
    public void queExisteUmaViagemComStatus(String status) {
        idViagem = viagemService.criarViagemComStatus(status);
    }

    @Quando("eu registro um checkpoint nessa viagem com os dados:")
    public void euRegistroUmCheckpointNessaViagemComOsDados(Map<String, String> dados) {
        CheckpointModel checkpoint = checkpointService.checkpointModel;
        checkpoint.setIdViagem(idViagem);
        checkpoint.setLatitude(Double.parseDouble(dados.get("latitude")));
        checkpoint.setLongitude(Double.parseDouble(dados.get("longitude")));
        checkpoint.setDataRegistro(dados.get("dataRegistro"));
        checkpoint.setBotaoPanico(Integer.parseInt(dados.get("botaoPanico")));
        checkpoint.setPortaAberta(Integer.parseInt(dados.get("portaAberta")));
        checkpointService.registrar();
    }

    @Quando("eu excluo essa viagem")
    public void euExcluoEssaViagem() {
        viagemService.excluir(idViagem);
    }

    @Quando("eu consulto a viagem de id {long}")
    public void euConsultoAViagemDeId(long id) {
        viagemService.buscarPorId(id);
    }

    @E("o corpo da resposta deve conter os dados do checkpoint enviado")
    public void oCorpoDaRespostaDeveConterOsDadosDoCheckpointEnviado() {
        CheckpointModel enviado = checkpointService.checkpointModel;
        JSONObject corpo = new JSONObject(BaseService.getUltimaResposta().asString());

        assertTrue("idCheckpoint deve ser gerado pela API", corpo.getLong("idCheckpoint") > 0);
        assertEquals(idViagem, corpo.getLong("idViagem"));
        assertEquals(enviado.getLatitude(), corpo.getDouble("latitude"), 0.0000001);
        assertEquals(enviado.getLongitude(), corpo.getDouble("longitude"), 0.0000001);
        assertEquals(enviado.getDataRegistro(), corpo.getString("dataRegistro"));
        assertEquals((int) enviado.getBotaoPanico(), corpo.getInt("botaoPanico"));
        assertEquals((int) enviado.getPortaAberta(), corpo.getInt("portaAberta"));
    }
}
