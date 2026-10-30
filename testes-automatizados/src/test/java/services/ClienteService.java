package services;

import io.restassured.response.Response;
import model.ClienteModel;

public class ClienteService extends BaseService {

    public Response criar(ClienteModel cliente) {
        return post("/api/clientes", cliente);
    }

    /** Cria um cliente ATIVO com CNPJ e e-mail únicos e devolve o id gerado. */
    public long criarClienteAtivo() {
        String sufixo = DadosUnicos.digitos(14);
        ClienteModel cliente = ClienteModel.builder()
                .nome("Cliente BDD " + sufixo)
                .cnpj(sufixo)
                .email("cliente_" + sufixo + "@astrotrack.com")
                .telefone("11999990000")
                .status("ATIVO")
                .build();
        return exigirStatus(criar(cliente), 201, "criar cliente").jsonPath().getLong("idCliente");
    }
}
