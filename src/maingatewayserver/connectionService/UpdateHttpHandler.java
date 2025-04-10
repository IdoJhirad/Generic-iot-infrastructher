package maingatewayserver.connectionService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class UpdateHttpHandler extends AbstractIotHttpHandler {

    //ctor
    public UpdateHttpHandler(RequestProcessService rps) {
        super(rps);
        super.URL = "/company/product/iot/update/";
        initHandlers();
    }

    private void initHandlers() {
        methodMap.put("GET", httpExchange -> {
            System.out.println("entered to handle Get update");
            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if (uri.length != 9) {
                HttpServiceManger.sendResponse(httpExchange, 400, "Bad Request");
                return;
            }
            JsonObject data = new JsonObject();
            data.addProperty("Company_ID", uri[2]);
            data.addProperty("Product_ID", uri[4]);
            data.addProperty("Iot_ID", uri[6]);
            data.addProperty("Update_ID", uri[8]);

            JsonObject wrapper = HttpServiceManger.createJson("GetIot");//TODO change to the command
            wrapper.add("Data", data);
            rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange), ByteBuffer.wrap(wrapper.toString().getBytes()));
        });

        methodMap.put("POST", httpExchange -> {
            System.out.println("entered to handle Post update");

            String[] uri = HttpServiceManger.uriParser(httpExchange);
            System.out.println("HTTP uri len " + uri.length);
            if (uri.length != 8) {
                HttpServiceManger.sendResponse(httpExchange, 400, "Bad Request");
                return;
            }
            InputStream inputStream = httpExchange.getRequestBody();
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            try {
                inputStream.read(buffer.array());
                String body = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                JsonObject data = new Gson().fromJson(body, JsonObject.class);
                data.addProperty("Company_ID", uri[2]);
                data.addProperty("Product_ID", uri[4]);
                data.addProperty("Iot_ID", uri[6]);
                JsonObject wrapper = HttpServiceManger.createJson("RegisterIOTUpdate");

                wrapper.add("Data", data);

                System.out.println("HTTP-  Post upadte move to rps");
                rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange), ByteBuffer.wrap(wrapper.toString().getBytes()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
