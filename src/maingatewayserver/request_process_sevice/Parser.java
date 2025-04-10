package maingatewayserver.request_process_sevice;


import com.google.gson.JsonObject;

import java.nio.ByteBuffer;

public interface Parser{

    JsonObject parse(ByteBuffer dataBuffer);

}
