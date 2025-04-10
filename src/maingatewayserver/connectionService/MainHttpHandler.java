package maingatewayserver.connectionService;

import maingatewayserver.request_process_sevice.ParserIMP;
import maingatewayserver.request_process_sevice.RequestProcessService;

public class MainHttpHandler {
    public static void main(String[] args) {
        IotHttpHandler h1 = new IOTHttpHandler(new RequestProcessService(new ParserIMP()));
        IotHttpHandler h2 = new IotsHttpHandler(new RequestProcessService(new ParserIMP()));
        IotHttpHandler h3 = new ProductHttpHandler(new RequestProcessService(new ParserIMP()));
        IotHttpHandler h4 = new UpdateHttpHandler(new RequestProcessService(new ParserIMP()));
        IotHttpHandler h5 = new UpdatesHttpHandler(new RequestProcessService(new ParserIMP()));

    }
}
