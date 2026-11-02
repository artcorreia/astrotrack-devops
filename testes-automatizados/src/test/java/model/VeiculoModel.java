package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/veiculos (VeiculoRequest na API). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoModel {
    @Expose
    private String placa;

    @Expose
    private String modelo;

    @Expose
    private String marca;

    @Expose
    private Integer ano;

    @Expose
    private String status;
}
