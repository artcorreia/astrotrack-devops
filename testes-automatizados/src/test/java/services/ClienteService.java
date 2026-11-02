package services;

import model.ClienteModel;

public class ClienteService extends CrudService {

    @Override
    public String recurso() {
        return "cliente";
    }

    @Override
    protected String endpoint() {
        return "/api/clientes";
    }

    @Override
    public String campoId() {
        return "idCliente";
    }

    /** Cliente ATIVO com CNPJ e e-mail únicos. */
    @Override
    protected ClienteModel modeloValido() {
        String sufixo = DadosUnicos.digitos(14);
        return ClienteModel.builder()
                .nome("Cliente BDD " + sufixo)
                .cnpj(sufixo)
                .email("cliente_" + sufixo + "@astrotrack.com")
                .telefone("11999990000")
                .status("ATIVO")
                .build();
    }
}
