package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/auth/register (RegisterRequest na API). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterModel {
    @Expose
    private String usuario;
    @Expose
    private String email;
    @Expose
    private String senha;
}
