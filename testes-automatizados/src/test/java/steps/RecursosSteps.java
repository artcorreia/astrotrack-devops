package steps;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import services.BaseService;
import services.CheckpointService;
import services.ClienteService;
import services.ContextoRecursos;
import services.CrudService;
import services.MotoristaService;
import services.VeiculoService;
import services.ViagemService;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Steps de CRUD reaproveitados pelas features de clientes, motoristas, veículos, viagens e checkpoints. */
public class RecursosSteps {

    private static final String RECURSO = "(cliente|motorista|veículo|viagem|checkpoint)";
    private static final String RECURSOS = "(clientes|motoristas|veículos|viagens|checkpoints)";
    private static final String CADASTRAD = "cadastrad[oa]";

    /** Valor usado nos Exemplos para remover o campo do corpo (campo obrigatório faltando). */
    private static final String AUSENTE = "(ausente)";

    private static final Map<String, String> SINGULAR = Map.of(
            "clientes", "cliente",
            "motoristas", "motorista",
            "veículos", "veículo",
            "viagens", "viagem",
            "checkpoints", "checkpoint");

    private final Map<String, CrudService> servicos = Map.of(
            "cliente", new ClienteService(),
            "motorista", new MotoristaService(),
            "veículo", new VeiculoService(),
            "viagem", new ViagemService(),
            "checkpoint", new CheckpointService());

    private CrudService servico(String recurso) {
        return servicos.get(recurso);
    }

    private long idCadastrado(String recurso) {
        return ContextoRecursos.criado(recurso).get(servico(recurso).campoId()).getAsLong();
    }

    // ─── Dado ───────────────────────────────────────────────────────────────

    @Dado("^que existe (?:um|uma) " + RECURSO + " " + CADASTRAD + "$")
    public void queExisteUmRecursoCadastrado(String recurso) {
        servico(recurso).cadastrar();
    }

    // ─── Quando ─────────────────────────────────────────────────────────────

    @Quando("^eu listo (?:os|as) " + RECURSOS + "$")
    public void euListoOsRecursos(String recursos) {
        servico(SINGULAR.get(recursos)).listar();
    }

    @Quando("^eu busco (?:o|a) " + RECURSO + " " + CADASTRAD + " pelo id$")
    public void euBuscoORecursoCadastradoPeloId(String recurso) {
        servico(recurso).buscarPorId(idCadastrado(recurso));
    }

    @Quando("^eu (busco|atualizo|excluo) (?:o|a) " + RECURSO + " de id (\\d+)$")
    public void euExecutoOperacaoNoRecursoDeId(String operacao, String recurso, long id) {
        CrudService servico = servico(recurso);
        switch (operacao) {
            case "busco" -> servico.buscarPorId(id);
            case "atualizo" -> servico.atualizar(id, servico.corpoValido());
            case "excluo" -> servico.excluir(id);
            default -> fail("Operação desconhecida: " + operacao);
        }
    }

    @Quando("^eu cadastro (?:um|uma) " + RECURSO + " com dados válidos$")
    public void euCadastroUmRecursoComDadosValidos(String recurso) {
        servico(recurso).criar(servico(recurso).corpoValido());
    }

    @Quando("^eu cadastro (?:um|uma) " + RECURSO + " com o campo \"([^\"]*)\" igual a \"([^\"]*)\"$")
    public void euCadastroUmRecursoComOCampoIgualA(String recurso, String campo, String valor) {
        JsonObject corpo = servico(recurso).corpoValido();
        aplicarValor(corpo, campo, valor);
        servico(recurso).criar(corpo);
    }

    @Quando("^eu cadastro outr[oa] " + RECURSO + " com o mesmo \"([^\"]*)\"$")
    public void euCadastroOutroRecursoComOMesmoCampo(String recurso, String campo) {
        JsonObject corpo = servico(recurso).corpoValido();
        corpo.add(campo, ContextoRecursos.enviado(recurso).get(campo));
        servico(recurso).criar(corpo);
    }

    @Quando("^eu atualizo o campo \"([^\"]*)\" d[oa] " + RECURSO + " " + CADASTRAD + " para \"([^\"]*)\"$")
    public void euAtualizoOCampoDoRecursoCadastrado(String campo, String recurso, String valor) {
        JsonObject corpo = ContextoRecursos.enviado(recurso);
        aplicarValor(corpo, campo, valor);
        servico(recurso).atualizar(idCadastrado(recurso), corpo);
    }

    @Quando("^eu excluo (?:o|a) " + RECURSO + " " + CADASTRAD + "$")
    public void euExcluoORecursoCadastrado(String recurso) {
        servico(recurso).excluir(idCadastrado(recurso));
    }

    @Quando("^eu (busco|excluo) o (cliente|motorista|veículo) vinculado à viagem cadastrada$")
    public void euOperoORecursoVinculadoAViagem(String operacao, String recurso) {
        long id = ContextoRecursos.criado("viagem").get(servico(recurso).campoId()).getAsLong();
        if (operacao.equals("busco")) {
            servico(recurso).buscarPorId(id);
        } else {
            servico(recurso).excluir(id);
        }
    }

    // ─── Então ──────────────────────────────────────────────────────────────

    @E("^o corpo da resposta deve conter os dados enviados$")
    public void oCorpoDaRespostaDeveConterOsDadosEnviados() {
        assertContem(BaseService.getUltimoCorpoEnviado().getAsJsonObject(), corpoDaResposta().getAsJsonObject());
    }

    @E("^o corpo da resposta deve conter os dados d[oa] " + RECURSO + " " + CADASTRAD + "$")
    public void oCorpoDaRespostaDeveConterOsDadosDoRecursoCadastrado(String recurso) {
        assertContem(ContextoRecursos.criado(recurso), corpoDaResposta().getAsJsonObject());
    }

    @E("^a lista deve conter (?:o|a) " + RECURSO + " " + CADASTRAD + "$")
    public void aListaDeveConterORecursoCadastrado(String recurso) {
        String campoId = servico(recurso).campoId();
        long id = idCadastrado(recurso);
        for (JsonElement item : corpoDaResposta().getAsJsonArray()) {
            if (item.getAsJsonObject().get(campoId).getAsLong() == id) {
                assertContem(ContextoRecursos.criado(recurso), item.getAsJsonObject());
                return;
            }
        }
        fail("A lista não contém " + recurso + " com " + campoId + " " + id);
    }

    @E("^o campo \"([^\"]*)\" da resposta deve ser \"([^\"]*)\"$")
    public void oCampoDaRespostaDeveSer(String campo, String valor) {
        JsonElement atual = corpoDaResposta().getAsJsonObject().get(campo);
        assertNotNull("Campo ausente na resposta: " + campo, atual);
        assertIgual(campo, new JsonPrimitive(valor), atual);
    }

    @E("^todos os itens da lista devem ter o campo \"([^\"]*)\" igual a \"([^\"]*)\"$")
    public void todosOsItensDaListaDevemTerOCampoIgualA(String campo, String valor) {
        JsonArray lista = corpoDaResposta().getAsJsonArray();
        assertTrue("A lista veio vazia", lista.size() > 0);
        for (JsonElement item : lista) {
            assertIgual(campo, new JsonPrimitive(valor), item.getAsJsonObject().get(campo));
        }
    }

    // ─── Auxiliares ─────────────────────────────────────────────────────────

    private static JsonElement corpoDaResposta() {
        return JsonParser.parseString(BaseService.getUltimaResposta().asString());
    }

    /** Altera um campo do corpo mantendo o tipo JSON original (número continua número). */
    private static void aplicarValor(JsonObject corpo, String campo, String valor) {
        if (AUSENTE.equals(valor)) {
            corpo.remove(campo);
            return;
        }
        JsonElement original = corpo.get(campo);
        if (original != null && original.isJsonPrimitive() && original.getAsJsonPrimitive().isNumber()) {
            corpo.add(campo, new JsonPrimitive(new BigDecimal(valor)));
        } else {
            corpo.addProperty(campo, valor);
        }
    }

    /** Todo campo do esperado precisa existir no atual com o mesmo valor (números comparados pelo valor). */
    private static void assertContem(JsonObject esperado, JsonObject atual) {
        for (Map.Entry<String, JsonElement> campo : esperado.entrySet()) {
            assertTrue("Campo ausente na resposta: " + campo.getKey(), atual.has(campo.getKey()));
            assertIgual(campo.getKey(), campo.getValue(), atual.get(campo.getKey()));
        }
    }

    private static void assertIgual(String campo, JsonElement esperado, JsonElement atual) {
        if (esperado.isJsonNull() || atual == null || atual.isJsonNull()) {
            assertEquals("Campo " + campo, esperado.isJsonNull(), atual == null || atual.isJsonNull());
            return;
        }
        if (atual.isJsonPrimitive() && atual.getAsJsonPrimitive().isNumber()) {
            BigDecimal valorEsperado = new BigDecimal(esperado.getAsString());
            assertEquals("Campo " + campo + ": esperado " + valorEsperado + ", recebido " + atual,
                    0, valorEsperado.compareTo(atual.getAsBigDecimal()));
            return;
        }
        assertEquals("Campo " + campo, esperado.getAsString(), atual.getAsString());
    }
}
