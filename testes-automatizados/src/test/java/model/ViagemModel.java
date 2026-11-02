package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/viagens (ViagemRequest na API). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViagemModel {
    @Expose
    private Long idCliente;

    @Expose
    private Long idMotorista;

    @Expose
    private Long idVeiculo;

    @Expose
    private String origem;

    @Expose
    private String destino;

    @Expose
    private String dataInicio;

    @Expose
    private String dataFim;

    @Expose
    private String status;

    @Expose
    private Double quilometragemTotal;
}
