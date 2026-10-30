package services;

import model.MotoristaModel;

public class MotoristaService extends CrudService {

    @Override
    public String recurso() {
        return "motorista";
    }

    @Override
    protected String endpoint() {
        return "/api/motoristas";
    }

    @Override
    public String campoId() {
        return "idMotorista";
    }

    /** Motorista ATIVO com CPF e CNH únicos. */
    @Override
    protected MotoristaModel modeloValido() {
        return MotoristaModel.builder()
                .nome("Motorista BDD")
                .cpf(DadosUnicos.digitos(11))
                .cnh(DadosUnicos.digitos(11))
                .telefone("11988880000")
                .status("ATIVO")
                .build();
    }
}
