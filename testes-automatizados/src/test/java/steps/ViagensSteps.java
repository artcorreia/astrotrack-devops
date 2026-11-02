package steps;

import io.cucumber.java.pt.Quando;
import model.ViagemModel;
import services.ContextoRecursos;
import services.ViagemService;

public class ViagensSteps {

    private final ViagemService viagemService = new ViagemService();

    @Quando("eu listo as viagens com status {string}")
    public void euListoAsViagensComStatus(String status) {
        viagemService.listarPorStatus(status);
    }

    @Quando("eu cadastro uma nova viagem {string} com o motorista da viagem cadastrada")
    public void euCadastroUmaNovaViagemComOMotoristaDaViagemCadastrada(String status) {
        long idMotorista = ContextoRecursos.criado("viagem").get("idMotorista").getAsLong();
        ViagemModel viagem = viagemService.montarViagem(status);
        viagem.setIdMotorista(idMotorista);
        viagemService.criar(viagem);
    }
}
