package model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corpo de POST /api/auth/login (LoginRequest na API). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginModel {
    @Expose
    private String email;
    @Expose
    private String senha;
}
