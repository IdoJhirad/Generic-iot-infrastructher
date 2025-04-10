package maingatewayserver.request_process_sevice;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;

public class ParserIMP implements Parser {

    @Override
    public JsonObject parse(ByteBuffer dataBuffer) {
        System.out.println("parser activate");
        String jsonString = new String(dataBuffer.array()).trim();
            return new Gson().fromJson(jsonString, JsonObject.class);

    }

}
