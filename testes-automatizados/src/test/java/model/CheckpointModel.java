package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/checkpoints (CheckpointRequest na API). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckpointModel {
    @Expose
    private Long idViagem;

    @Expose
    private Double latitude;

    @Expose
    private Double longitude;

    @Expose
    private String dataRegistro;

    @Expose
    private Integer botaoPanico;

    @Expose
    private Integer portaAberta;
}
