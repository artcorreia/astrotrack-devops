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
import io.restassured.specification.RequestSpecification;

import java.io.InputStream;
import java.util.Set;

import static io.restassured.RestAssured.given;

/**
 * Base dos services da API. O token JWT e a última resposta ficam em campos estáticos
 * para serem compartilhados entre classes de steps no mesmo cenário (sem injeção de dependência);
 * o hook em steps.Hooks limpa esse estado antes de cada cenário.
 */
public abstract class BaseService {

    protected static final String BASE_URL =
            System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080");

    protected static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private static String token;
    private static Response ultimaResposta;

    public static void definirToken(String novoToken) {
        token = novoToken;
    }

    public static Response getUltimaResposta() {
        return ultimaResposta;
    }

    public static void limparContexto() {
        token = null;
        ultimaResposta = null;
    }

    private RequestSpecification requisicao() {
        RequestSpecification spec = given().baseUri(BASE_URL).contentType(ContentType.JSON);
        if (token != null) {
            spec.header("Authorization", "Bearer " + token);
        }
        return spec;
    }

    protected Response post(String endpoint, Object body) {
        ultimaResposta = requisicao().body(gson.toJson(body)).when().post(endpoint).then().extract().response();
        return ultimaResposta;
    }

    protected Response get(String endpoint) {
        ultimaResposta = requisicao().when().get(endpoint).then().extract().response();
        return ultimaResposta;
    }

    protected Response delete(String endpoint) {
        ultimaResposta = requisicao().when().delete(endpoint).then().extract().response();
        return ultimaResposta;
    }

    /** Garante que um request de pré-requisito deu certo antes de seguir o cenário. */
    protected static Response exigirStatus(Response response, int statusEsperado, String descricao) {
        if (response.statusCode() != statusEsperado) {
            throw new IllegalStateException("Falha ao " + descricao + ": esperado " + statusEsperado
                    + ", recebido " + response.statusCode() + " - " + response.asString());
        }
        return response;
    }

    public static Set<ValidationMessage> validarSchema(String schemaPath) {
        InputStream schemaStream = BaseService.class.getClassLoader().getResourceAsStream(schemaPath);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Schema não encontrado: " + schemaPath);
        }
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaStream);
        return schema.validate(ultimaResposta.getBody().asString(), InputFormat.JSON);
    }
}
