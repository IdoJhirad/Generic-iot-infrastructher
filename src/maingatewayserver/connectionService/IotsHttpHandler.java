package maingatewayserver.connectionService;

import com.google.gson.JsonObject;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.nio.ByteBuffer;


public class IotsHttpHandler extends AbstractIotHttpHandler {

    //ctor
    public IotsHttpHandler(RequestProcessService rps) {
        super(rps);
        super.URL = "/company/product/iots/";
        initHandlers();
    }

    private void initHandlers() {
        methodMap.put("GET", httpExchange -> {
            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if (uri.length != 6) {
                System.out.println("HTTP handlr iots- Request invalid");
                HttpServiceManger.sendResponse(httpExchange, 400, "Bad Request");
                return;
            }
            JsonObject data = new JsonObject();
            data.addProperty("Company_ID", uri[2]);
            data.addProperty("Product_ID", uri[4]);
            JsonObject wrapper = HttpServiceManger.createJson("GetIOTDevices");//TODO change to the command
            wrapper.add("Data", data);
            rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange), ByteBuffer.wrap(wrapper.toString().getBytes()));
        });
    }
}
