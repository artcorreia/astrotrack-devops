package services;

import io.restassured.response.Response;
import model.ViagemModel;

public class ViagemService extends BaseService {

    private final ClienteService clienteService = new ClienteService();
    private final MotoristaService motoristaService = new MotoristaService();
    private final VeiculoService veiculoService = new VeiculoService();

    public Response criar(ViagemModel viagem) {
        return post("/api/viagens", viagem);
    }

    public Response buscarPorId(long idViagem) {
        return get("/api/viagens/" + idViagem);
    }

    public Response excluir(long idViagem) {
        return delete("/api/viagens/" + idViagem);
    }

    /**
     * Monta os pré-requisitos (cliente, motorista e veículo novos) e cria uma viagem no status informado.
     * Cada viagem usa motorista e veículo próprios, porque a API não deixa o mesmo motorista ou veículo
     * em duas viagens EM_ANDAMENTO ao mesmo tempo.
     */
    public long criarViagemComStatus(String status) {
        ViagemModel viagem = ViagemModel.builder()
                .idCliente(clienteService.criarClienteAtivo())
                .idMotorista(motoristaService.criarMotoristaAtivo())
                .idVeiculo(veiculoService.criarVeiculoDisponivel())
                .origem("São Paulo - SP")
                .destino("Campinas - SP")
                .dataInicio("2026-01-10T08:00:00")
                // A API exige data de fim para viagens finalizadas
                .dataFim("FINALIZADA".equals(status) ? "2026-01-10T12:00:00" : null)
                .status(status)
                .quilometragemTotal(95.5)
                .build();
        return exigirStatus(criar(viagem), 201, "criar viagem " + status).jsonPath().getLong("idViagem");
    }
}
