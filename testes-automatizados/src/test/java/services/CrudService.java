package services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.response.Response;

/** Operações REST comuns (GET lista, GET por id, POST, PUT, DELETE) de um recurso da API. */
public abstract class CrudService extends BaseService {

    /** Nome do recurso no singular, usado como chave no ContextoRecursos (ex.: "cliente"). */
    public abstract String recurso();

    protected abstract String endpoint();

    /** Nome do campo de id na resposta (ex.: "idCliente"). */
    public abstract String campoId();

    /** Modelo válido e com dados únicos para o POST, criando os pré-requisitos via API se precisar. */
    protected abstract Object modeloValido();

    public JsonObject corpoValido() {
        return gson.toJsonTree(modeloValido()).getAsJsonObject();
    }

    public Response listar() {
        return get(endpoint());
    }

    public Response buscarPorId(long id) {
        return get(endpoint() + "/" + id);
    }

    public Response criar(Object corpo) {
        return post(endpoint(), corpo);
    }

    public Response atualizar(long id, Object corpo) {
        return put(endpoint() + "/" + id, corpo);
    }

    public Response excluir(long id) {
        return delete(endpoint() + "/" + id);
    }

    /** Cria um recurso válido (exige 201), guarda no ContextoRecursos e devolve o corpo da resposta. */
    public JsonObject cadastrar() {
        return cadastrar(corpoValido());
    }

    public JsonObject cadastrar(Object corpo) {
        Response response = exigirStatus(criar(corpo), 201, "criar " + recurso());
        JsonObject criado = JsonParser.parseString(response.asString()).getAsJsonObject();
        ContextoRecursos.registrar(recurso(), gson.toJsonTree(corpo).getAsJsonObject(), criado);
        return criado;
    }

    public long cadastrarEObterId() {
        return cadastrar().get(campoId()).getAsLong();
    }
}
