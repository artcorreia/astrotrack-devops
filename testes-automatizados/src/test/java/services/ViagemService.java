package services;

import io.restassured.response.Response;
import model.ViagemModel;

public class ViagemService extends CrudService {

    private final ClienteService clienteService = new ClienteService();
    private final MotoristaService motoristaService = new MotoristaService();
    private final VeiculoService veiculoService = new VeiculoService();

    @Override
    public String recurso() {
        return "viagem";
    }

    @Override
    protected String endpoint() {
        return "/api/viagens";
    }

    @Override
    public String campoId() {
        return "idViagem";
    }

    /** Viagem AGENDADA: pode ser alterada e excluída sem deixar dados presos no banco. */
    @Override
    protected ViagemModel modeloValido() {
        return montarViagem("AGENDADA");
    }

    public Response listarPorStatus(String status) {
        return get(endpoint() + "/status/" + status);
    }

    /**
     * Monta uma viagem no status informado com pré-requisitos novos (cliente, motorista e veículo).
     * Cada viagem usa motorista e veículo próprios, porque a API não deixa o mesmo motorista ou veículo
     * em duas viagens EM_ANDAMENTO ao mesmo tempo.
     */
    public ViagemModel montarViagem(String status) {
        return ViagemModel.builder()
                .idCliente(clienteService.cadastrarEObterId())
                .idMotorista(motoristaService.cadastrarEObterId())
                .idVeiculo(veiculoService.cadastrarEObterId())
                .origem("São Paulo - SP")
                .destino("Campinas - SP")
                .dataInicio("2026-01-10T08:00:00")
                // A API exige data de fim para viagens finalizadas
                .dataFim("FINALIZADA".equals(status) ? "2026-01-10T12:00:00" : null)
                .status(status)
                .quilometragemTotal(95.5)
                .build();
    }

    public long criarViagemComStatus(String status) {
        return cadastrar(montarViagem(status)).get(campoId()).getAsLong();
    }
}
