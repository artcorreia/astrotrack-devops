package services;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Recursos criados durante o cenário, por nome ("cliente", "viagem", ...), para que steps de classes
 * diferentes possam se referir ao "cliente cadastrado". Limpo antes de cada cenário junto com o BaseService.
 */
public final class ContextoRecursos {

    private static final Map<String, JsonObject> enviados = new HashMap<>();
    private static final Map<String, JsonObject> criados = new HashMap<>();

    private ContextoRecursos() {
    }

    static void registrar(String recurso, JsonObject corpoEnviado, JsonObject corpoCriado) {
        enviados.put(recurso, corpoEnviado);
        criados.put(recurso, corpoCriado);
    }

    /** Corpo enviado no POST que criou o recurso (usado como base para PUT). */
    public static JsonObject enviado(String recurso) {
        return exigir(enviados, recurso).deepCopy();
    }

    /** Corpo retornado pela API ao criar o recurso. */
    public static JsonObject criado(String recurso) {
        return exigir(criados, recurso).deepCopy();
    }

    static void limpar() {
        enviados.clear();
        criados.clear();
    }

    private static JsonObject exigir(Map<String, JsonObject> mapa, String recurso) {
        JsonObject corpo = mapa.get(recurso);
        if (corpo == null) {
            throw new IllegalStateException("Nenhum(a) " + recurso + " foi cadastrado(a) neste cenário");
        }
        return corpo;
    }
}
