package maingatewayserver.connectionService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractIotHttpHandler implements IotHttpHandler {

    protected String URL;
    protected Map<String, HttpHandler> methodMap = new HashMap<>();
    protected RequestProcessService rps;

    AbstractIotHttpHandler(RequestProcessService rps){
        this.rps = rps;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("http handler handle");
        String cleanUri = HttpServiceManger.removeIDs(exchange);
        HttpHandler handler = methodMap.get(exchange.getRequestMethod());
        if(handler != null) {
            handler.handle(exchange);
            return;
        }
        HttpServiceManger.sendResponse(exchange, 405, "Method Not Allowed");
    }

    @Override
    public String GetPath() {
        return URL;
    }

}

