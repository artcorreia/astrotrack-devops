package steps;

import io.cucumber.java.Before;
import services.BaseService;

public class Hooks {

    @Before
    public void limparContexto() {
        BaseService.limparContexto();
    }
}
