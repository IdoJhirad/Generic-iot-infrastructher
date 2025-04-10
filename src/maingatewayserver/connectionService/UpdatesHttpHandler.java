package maingatewayserver.connectionService;

import com.google.gson.JsonObject;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.nio.ByteBuffer;



public class UpdatesHttpHandler extends AbstractIotHttpHandler {
    //ctor
    public UpdatesHttpHandler(RequestProcessService rps) {
        super(rps);
        super.URL = "/company/product/iot/updates/";
        initHandlers();
    }

    private void initHandlers() {
        methodMap.put("GET", httpExchange -> {
            System.out.println("HTTP -entered to handle Updaets");
            String[] uri = HttpServiceManger.uriParser(httpExchange);
            if(uri.length != 8) {
                System.out.println("HTTP handle updates - Request invalid");
                HttpServiceManger.sendResponse(httpExchange, 400 ,"Bad Request" );
                return;
            }
            JsonObject data =new JsonObject();
            data.addProperty("Company_ID", uri[2]);
            data.addProperty("Product_ID", uri[4]);
            data.addProperty("Iot_ID", uri[6]);

            JsonObject wrapper = HttpServiceManger.createJson("GetIOTDeviceUpdates");
            wrapper.add("Data", data);

            rps.handleRequest(new HttpServiceManger.HttpResponder(httpExchange) , ByteBuffer.wrap(wrapper.toString().getBytes()));
        });


    }
}
