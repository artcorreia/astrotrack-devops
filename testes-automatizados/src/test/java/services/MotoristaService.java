package services;

import io.restassured.response.Response;
import model.MotoristaModel;

public class MotoristaService extends BaseService {

    public Response criar(MotoristaModel motorista) {
        return post("/api/motoristas", motorista);
    }

    /** Cria um motorista ATIVO com CPF e CNH únicos e devolve o id gerado. */
    public long criarMotoristaAtivo() {
        MotoristaModel motorista = MotoristaModel.builder()
                .nome("Motorista BDD")
                .cpf(DadosUnicos.digitos(11))
                .cnh(DadosUnicos.digitos(11))
                .telefone("11988880000")
                .status("ATIVO")
                .build();
        return exigirStatus(criar(motorista), 201, "criar motorista").jsonPath().getLong("idMotorista");
    }
}
