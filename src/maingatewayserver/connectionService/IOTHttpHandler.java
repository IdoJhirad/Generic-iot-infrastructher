package maingatewayserver.connectionService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class IOTHttpHandler extends AbstractIotHttpHandler {

    //ctor
    public IOTHttpHandler(RequestProcessService rps) {
        super(rps);
        super.URL = "/company/product/iot/";
        initHandlers();
    }

    private void initHandlers() {
        methodMap.put("GET", httpExchange -> {
            System.out.println("entered to handle GET Iot");
            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if (uri.length != 7) {
                System.out.println("http invalid for Iot post");
                HttpServiceManger.sendResponse(httpExchange, 400, "Bad Request");
                return;
            }
            JsonObject data =new JsonObject();
            data.addProperty("Company_ID", uri[2]);
            data.addProperty("Product_ID", uri[4]);
            data.addProperty("Iot_ID", uri[6]);

            JsonObject wrapper = HttpServiceManger.createJson("GetIot");//TODO change to the command
            wrapper.add("Data", data);

            rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange), ByteBuffer.wrap(wrapper.toString().getBytes()));   });

        methodMap.put("POST", httpExchange -> {
            System.out.println("entered to handle POST Iot");
            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if (uri.length != 6) {
                System.out.println("http valid for Iot post");
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

                JsonObject wrapper = HttpServiceManger.createJson("RegisterIOT");

                wrapper.add("Data", data);
                rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange) , ByteBuffer.wrap(wrapper.toString().getBytes()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
