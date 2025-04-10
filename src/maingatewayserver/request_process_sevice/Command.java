package maingatewayserver.request_process_sevice;


import maingatewayserver.connectionService.RespondableChannel;

public interface Command {
    default int getCompanyKey() {
        return 0;
    }
    void execute(RespondableChannel respondableChannel);
}



