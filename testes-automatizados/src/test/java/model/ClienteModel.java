package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/clientes (ClienteRequest na API). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteModel {
    @Expose
    private String nome;

    @Expose
    private String cnpj;

    @Expose
    private String email;

    @Expose
    private String telefone;

    @Expose
    private String status;
}
