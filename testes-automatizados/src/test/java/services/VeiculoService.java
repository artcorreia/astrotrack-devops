package services;

import io.restassured.response.Response;
import model.VeiculoModel;

public class VeiculoService extends BaseService {

    public Response criar(VeiculoModel veiculo) {
        return post("/api/veiculos", veiculo);
    }

    /** Cria um veículo DISPONIVEL com placa única e devolve o id gerado. */
    public long criarVeiculoDisponivel() {
        VeiculoModel veiculo = VeiculoModel.builder()
                .placa(DadosUnicos.placa())
                .modelo("Actros")
                .marca("Mercedes-Benz")
                .ano(2022)
                .status("DISPONIVEL")
                .build();
        return exigirStatus(criar(veiculo), 201, "criar veículo").jsonPath().getLong("idVeiculo");
    }
}
