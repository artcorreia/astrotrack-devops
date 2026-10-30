package services;

import io.restassured.response.Response;
import model.CheckpointModel;

public class CheckpointService extends CrudService {

    private final ViagemService viagemService = new ViagemService();

    public final CheckpointModel checkpointModel = new CheckpointModel();

    @Override
    public String recurso() {
        return "checkpoint";
    }

    @Override
    protected String endpoint() {
        return "/api/checkpoints";
    }

    @Override
    public String campoId() {
        return "idCheckpoint";
    }

    /** Checkpoint em uma viagem AGENDADA nova (a API só recusa checkpoints em viagens encerradas). */
    @Override
    protected CheckpointModel modeloValido() {
        return CheckpointModel.builder()
                .idViagem(viagemService.criarViagemComStatus("AGENDADA"))
                .latitude(-23.5505200)
                .longitude(-46.6333080)
                .dataRegistro("2026-01-10T09:30:00")
                .botaoPanico(0)
                .portaAberta(0)
                .build();
    }

    public Response registrar() {
        return criar(checkpointModel);
    }

    public Response listarPorViagem(long idViagem) {
        return get(endpoint() + "/viagem/" + idViagem);
    }
}
