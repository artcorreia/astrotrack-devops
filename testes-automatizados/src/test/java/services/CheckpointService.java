package services;

import io.restassured.response.Response;
import model.CheckpointModel;

public class CheckpointService extends BaseService {

    public final CheckpointModel checkpointModel = new CheckpointModel();

    public Response registrar() {
        return post("/api/checkpoints", checkpointModel);
    }
}
