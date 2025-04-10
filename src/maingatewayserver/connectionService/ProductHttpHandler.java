package maingatewayserver.connectionService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ProductHttpHandler extends AbstractIotHttpHandler {

    //ctor
    public ProductHttpHandler(RequestProcessService rps) {
        super(rps);
        super.URL = "/company/product/";
        initHandlers();
    }

    private void initHandlers() {
        methodMap.put("GET", httpExchange -> {
            System.out.println("http entered to GET handle product");
            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if (uri.length != 5) {
                System.out.println("http not valid for Product ");
                HttpServiceManger.sendResponse(httpExchange, 400, "Bad Request");
                return;
            }
            JsonObject data = new JsonObject();
            data.addProperty("Company_ID", uri[2]);
            data.addProperty("Product_ID", uri[4]);
            JsonObject wrapper = HttpServiceManger.createJson("GetProduct");
            wrapper.add("Data", data);
            rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange), ByteBuffer.wrap(wrapper.toString().getBytes()));
        });

        methodMap.put("POST", httpExchange -> {
            System.out.println("entered to handle POST product");

            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if (uri.length != 4) {
                System.out.println("http not valid for Product ");
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
                JsonObject wrapper = HttpServiceManger.createJson("RegisterProduct");

                wrapper.add("Data", data);
                System.out.println("POST product move to rps"+wrapper);

                rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange), ByteBuffer.wrap(wrapper.toString().getBytes()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }
}
