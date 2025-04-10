package maingatewayserver;

import admin_dataBase.AdminDB;
import com.google.gson.JsonObject;
import maingatewayserver.connectionService.ConnectionService;
import maingatewayserver.connectionService.IotHttpHandler;
import maingatewayserver.PluginMediator;
import maingatewayserver.request_process_sevice.Command;
import maingatewayserver.request_process_sevice.ParserIMP;
import maingatewayserver.request_process_sevice.RequestProcessService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Consumer;




public class GatewayServer {

    private final RequestProcessService requestProcessService = new RequestProcessService(new ParserIMP());
    private final ConnectionService connectionService = new ConnectionService(requestProcessService);
    private final PluginMediator pluginMediator = PluginMediator.getInstance();
    private Thread pluginsThread;
    private final String commandName = Command.class.getName();

    private final String IP ="10.100.102.39";
    public void start() {
        System.out.println(Command.class.getName());
        pluginMediator.addToListner(commandName ,new CommandListenerOperation());
        pluginMediator.addToListner(IotHttpHandler.class.getName(),new HttpListenerOperation());
        pluginsThread = new Thread(pluginMediator::start);
        pluginsThread.start();
        //System.out.println("\tgatewayServer return after starting the plug and play");
        AdminDB adminDB = AdminDB.getInstance();

        adminDB.addDBMS("MongoDB", new AdminDB.MongoDBLHandler());
        try {
            connectionService.addTCPConnection(9090, InetAddress.getByName(IP));
            connectionService.addUDPConnection(9090, InetAddress.getByName(IP));
            connectionService.addHTTPConnection(8000, InetAddress.getByName(IP));
            connectionService.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    public void shutDown() {
        connectionService.stop();
        requestProcessService.shutDown();
        pluginsThread.interrupt();
    }

    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer();
        gatewayServer.start();
    }

    private class HttpListenerOperation implements Consumer<List<Class<?>>>{
        @Override
        public void accept(List<Class<?>> classes) {
            for(Class<?> curr :classes) {
                //System.out.println("try to add class "+curr.getSimpleName()+" HttpService");
                try {
                    IotHttpHandler handler = (IotHttpHandler) curr.getDeclaredConstructor(RequestProcessService.class).newInstance(requestProcessService);
                    connectionService.addHttpHandler(handler.GetPath(), handler);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create instance of class: " + curr.getName(), e);
                }
            }
        }
    }
    /**
     * class that pass to PluginsMediator for handle load  */
    private class CommandListenerOperation implements Consumer<List<Class<?>>> {
        /**
         * iterate over the class list get the company key and register to the factory */
        @Override
        public void accept(List<Class<?>> classes) {
            for(Class<?> curr :classes) {
                if(!Command.class.isAssignableFrom(curr)) {
                    continue;
                }
                int companyKey = getCompanyKey(curr);

                requestProcessService.addToFactory(curr.getSimpleName(), (s) -> {
                    try {
                        // Create a new instance using the default constructor
                        return (Command) curr.getDeclaredConstructor(JsonObject.class).newInstance(s);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create instance of class: " + curr.getName(), e);
                    }
                },companyKey);
                System.out.println("class "+curr.getSimpleName()+" added to factory");
            }
        }

        /**
         * activate command default method getCompanyKey()
         * load the class and activate the method
         * @param loadedClass the class to retrieve the CompanyKey
         */
        private int getCompanyKey(Class<?> loadedClass) {
            try {
                Constructor<?> constructor = loadedClass.getDeclaredConstructor(JsonObject.class);
                Command instance = (Command) constructor.newInstance((JsonObject) null);
                return (int)Command.class.getMethod("getCompanyKey").invoke(instance);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("exeption while invoke the getCompanyKey method");
                throw new RuntimeException();
            }
        }
    }
}

