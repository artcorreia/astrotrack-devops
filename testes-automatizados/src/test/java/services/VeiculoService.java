package services;

import model.VeiculoModel;

public class VeiculoService extends CrudService {

    @Override
    public String recurso() {
        return "veículo";
    }

    @Override
    protected String endpoint() {
        return "/api/veiculos";
    }

    @Override
    public String campoId() {
        return "idVeiculo";
    }

    /** Veículo DISPONIVEL com placa única. */
    @Override
    protected VeiculoModel modeloValido() {
        return VeiculoModel.builder()
                .placa(DadosUnicos.placa())
                .modelo("Actros")
                .marca("Mercedes-Benz")
                .ano(2022)
                .status("DISPONIVEL")
                .build();
    }
}
