package command;

import admin_dataBase.AdminDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import maingatewayserver.connectionService.RespondableChannel;
import maingatewayserver.request_process_sevice.Command;

import java.nio.ByteBuffer;

public class GetIOTDevices implements Command {
    private final JsonObject data;
    private final AdminDB adminDB =AdminDB.getInstance();

    public GetIOTDevices(JsonObject data) {
        System.out.println("\tGetIOTDevices Command\n");
        this.data = data;
    }

    @Override
    public void execute(RespondableChannel respondableChannel) {
        data.addProperty("DBMS_Type","MongoDB");
        JsonObject res = adminDB.handleRequest(data);
        System.out.println("GET ioTS respond"+res);
        respondableChannel.respond(ByteBuffer.wrap(new Gson().toJson(res).getBytes()));
    }
}
