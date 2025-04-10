package maingatewayserver.connectionService;

import java.nio.ByteBuffer;

public interface RespondableChannel {
        void respond(ByteBuffer byteBuffer);
}