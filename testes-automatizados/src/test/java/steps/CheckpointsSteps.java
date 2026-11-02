package steps;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import services.BaseService;
import services.CheckpointService;
import services.ContextoRecursos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CheckpointsSteps {

    private final CheckpointService checkpointService = new CheckpointService();

    private static long idViagemCadastrada() {
        return ContextoRecursos.criado("viagem").get("idViagem").getAsLong();
    }

    @Quando("eu listo os checkpoints da viagem cadastrada")
    public void euListoOsCheckpointsDaViagemCadastrada() {
        checkpointService.listarPorViagem(idViagemCadastrada());
    }

    @Quando("eu listo os checkpoints da viagem de id {long}")
    public void euListoOsCheckpointsDaViagemDeId(long idViagem) {
        checkpointService.listarPorViagem(idViagem);
    }

    @E("todos os checkpoints da lista devem ser da viagem cadastrada")
    public void todosOsCheckpointsDaListaDevemSerDaViagemCadastrada() {
        JsonArray lista = JsonParser.parseString(BaseService.getUltimaResposta().asString()).getAsJsonArray();
        assertTrue("A lista veio vazia", lista.size() > 0);
        for (JsonElement item : lista) {
            assertEquals(idViagemCadastrada(), item.getAsJsonObject().get("idViagem").getAsLong());
        }
    }
}
