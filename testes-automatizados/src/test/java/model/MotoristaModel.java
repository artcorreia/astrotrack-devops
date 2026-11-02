package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/motoristas (MotoristaRequest na API). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotoristaModel {
    @Expose
    private String nome;

    @Expose
    private String cpf;

    @Expose
    private String cnh;

    @Expose
    private String telefone;

    @Expose
    private String status;
}
