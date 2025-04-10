package maingatewayserver.request_process_sevice;

import com.google.gson.JsonObject;
import maingatewayserver.connectionService.RespondableChannel;
import maingatewayserver.request_process_sevice.Factory.Factory;
import threadpool.ThreadPool;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class RequestProcessService {

    private final Factory<String, JsonObject, Command> factoryBuilder = new Factory<>();
    private final Parser parser;
    private final ThreadPool threadPool = new ThreadPool();

    public RequestProcessService(Parser parser) {
        this.parser = parser;

    }
    public void handleRequest(RespondableChannel channel, ByteBuffer buffer){
        System.out.println("RPS -handel request submit");
        threadPool.submit(new OperationTask(channel, buffer));
    }

    public void addToFactory(String key, Function< JsonObject , Command> func, int companyID){
        factoryBuilder.add(key,func, companyID);
    }

    public void shutDown() {
        threadPool.shutdown();
    }

    private class OperationTask implements Runnable {
        private final Parser parser = RequestProcessService.this.parser;
        private final RespondableChannel respondableChannel;
        private final ByteBuffer buffer;

        protected OperationTask(RespondableChannel channel, ByteBuffer buffer) {
            this.respondableChannel = channel;
            this.buffer = buffer;
        }
        private Integer getCompanyID(JsonObject jsonObject) {
        JsonObject data = jsonObject.has("Data") ? jsonObject.getAsJsonObject("Data") : null;
        return (data != null && data.has("Company_ID")) ? data.get("Company_ID").getAsInt() : null;
    }
        @Override
        public void run() {
            try{
                System.out.println("OperationTask activate");

                JsonObject jsonObject = parser.parse(buffer);
                System.out.println(jsonObject);


                Integer companyID = getCompanyID(jsonObject);
                if(companyID == null) {
                    companyID = 0;
                }
                System.out.println("rps - the key: "+jsonObject.get("Key").getAsString());
                System.out.println("rps - the data "+jsonObject.get("Data"));

                factoryBuilder.create(jsonObject.get("Key").getAsString() , jsonObject, companyID).execute(respondableChannel);
            }
            catch (Exception e) {
                e.printStackTrace();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Status", 400);
                jsonObject.addProperty("Info", "Invalid Operation");

                respondableChannel.respond(ByteBuffer.wrap(jsonObject.toString().getBytes()));
            }
        }
    }
}
