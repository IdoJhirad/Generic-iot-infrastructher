package maingatewayserver.connectionService;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface IotHttpHandler {
    String GetPath();
    void handle(HttpExchange exchange) throws IOException;
}
