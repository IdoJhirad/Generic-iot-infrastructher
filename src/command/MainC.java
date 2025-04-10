package command;

import com.google.gson.JsonObject;

public class MainC {
    public static void main(String[] args) {
        GetIOTDeviceUpdates getIOTDeviceUpdates = new GetIOTDeviceUpdates(new JsonObject());
        GetIOTDevices getIOTDevices =new GetIOTDevices(new JsonObject());
        RegisterIOT registerIOT = new RegisterIOT(new JsonObject());
        RegisterIOTUpdate registerIOTUpdate = new RegisterIOTUpdate(new JsonObject());
        RegisterProduct registerProduct = new RegisterProduct(new JsonObject());
    }

}
