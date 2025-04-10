package test.plaginstest;

import com.google.gson.JsonObject;
import gatewayServer.connectionService.RespondableChannel;
import gatewayServer.request_process_sevice.Command;

import java.nio.ByteBuffer;

public class RegCompany implements Command {
    String name;
    int numberOfProduct;


    public RegCompany(JsonObject obj){
        name = obj.get("Name").getAsString();
        numberOfProduct = obj.get("Number of products").getAsInt();

    }

    @Override
    public void execute(RespondableChannel respondableChannel) {
        System.out.println("Register Sucsess");
        System.out.println("Name of the Company: "+name);
        System.out.println("Number of the Product: "+numberOfProduct);

        JsonObject respond = new JsonObject();
        respond.addProperty("Status", 200);
        respond.addProperty("Info", "RegCompany");

        respondableChannel.respond(ByteBuffer.wrap(respond.toString().getBytes()));
    }
}
