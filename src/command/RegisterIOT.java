package command;

import admin_dataBase.AdminDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import maingatewayserver.connectionService.RespondableChannel;
import maingatewayserver.request_process_sevice.Command;

import java.nio.ByteBuffer;

public class RegisterIOT implements Command {
    private final JsonObject data;
    private final AdminDB adminDB =AdminDB.getInstance();

    public RegisterIOT(JsonObject data) {
        System.out.println("\tregister IOT Command\n");
        this.data = data;
    }

    @Override
    public void execute(RespondableChannel respondableChannel) {
        System.out.println("Register iot" +data);
        data.addProperty("DBMS_Type","MongoDB");
        JsonObject res = adminDB.handleRequest(data);
        System.out.println("register respond"+res);
        respondableChannel.respond(ByteBuffer.wrap(new Gson().toJson(res).getBytes()));
    }
}
