package maingatewayserver.connectionService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServiceManger {

        private final HttpServer httpServer;

        private final Map<String, IotHttpHandler> urlMap = new HashMap<>();

    public HttpServiceManger() {
        try {
            httpServer = HttpServer.create();
            httpServer.setExecutor(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect(int port, InetAddress ip) {
        if( ip== null ) {
            throw new RuntimeException();
        }
        try {

            httpServer.bind(new InetSocketAddress(ip, port), 0);
            httpServer.createContext("/", new Router());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        System.out.println("http server start");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }


    public void addHandler(String uri, IotHttpHandler handler) {
        urlMap.put(uri,handler);
    }


    static String removeIDs(HttpExchange httpExchange) {
        StringBuilder cleanUri = new StringBuilder("/");
        //System.out.println("\t\t"+cleanUri);
        String[] uriString = uriParser(httpExchange);
        for (int i = 1; i < uriString.length; i += 2) {
            cleanUri.append(uriString[i]).append("/");
        }
        return cleanUri.toString();
    }

        static String[] uriParser(HttpExchange exchange) {
        String uri = exchange.getRequestURI().getPath();
        return uri.split("/");
    }

        static JsonObject createJson(String value) {
        JsonObject json = new JsonObject();
        json.addProperty("Key", value);

        return json;
    }

        static void sendResponse(HttpExchange exchange, int statusCode, String response) {
        System.out.println("HTTP - send responds");

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        try {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        private class Router implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                System.out.println("Http entered to HTTP handler");
                String cleanUri = removeIDs(httpExchange);
                System.out.println("http - clean uri: " + cleanUri);

                IotHttpHandler handler = urlMap.get(cleanUri);
                if (handler != null) {
                    handler.handle(httpExchange);
                    return;
                }
                System.out.println("uri isn't valid");
                sendResponse(httpExchange, 404, "Not Found");
            }
        }

        static class  HttpResponder implements RespondableChannel {

            private final HttpExchange httpExchange;

            //Ctor
            protected HttpResponder(HttpExchange httpExchange) {
                this.httpExchange = httpExchange;
            }


            @Override
            public void respond(ByteBuffer byteBuffer) {

                //get the status code
                String jsonString = new String(byteBuffer.array()).trim();
                System.out.println("HTTP - responder- the message "+jsonString);
                JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);
                String jStatusCode = json.get("Status").getAsString();
                int statusCode;
                if(jStatusCode.equals("Success")){
                    statusCode = 200;
                } else {
                    statusCode = 400;
                }
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                try (OutputStream os = httpExchange.getResponseBody()) {
                    byte[] responseBytes = jsonString.getBytes(StandardCharsets.UTF_8);
                    httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
                    os.write(responseBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }
