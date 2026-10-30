package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.LoginModel;
import model.RegisterModel;

import java.io.InputStream;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class AutenticacaoService {

    private static final String BASE_URL =
            System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080");

    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public final RegisterModel registerModel = new RegisterModel();
    public final LoginModel loginModel = new LoginModel();
    public Response response;

    public Response registrar() {
        response = post("/api/auth/register", gson.toJson(registerModel));
        return response;
    }

    public Response login() {
        response = post("/api/auth/login", gson.toJson(loginModel));
        return response;
    }

    private Response post(String endpoint, String body) {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Set<ValidationMessage> validarSchema(String schemaPath) {
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaPath);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Schema não encontrado: " + schemaPath);
        }
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaStream);
        return schema.validate(response.getBody().asString(), InputFormat.JSON);
    }
}
