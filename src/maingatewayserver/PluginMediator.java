package maingatewayserver;


import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginMediator {
    private final DynamicJarLoader jarLoader = new DynamicJarLoader();
    private final List<Map.Entry<String,Consumer<List<Class<?>>>>> subscribersList = new ArrayList<>();

    private final String PATH = "/home/ido/git/projects/GenericIOT 2.0/GenericIoTInfrastructure/resource";
    private final Path resDir;
    private  boolean isStart = false;

    private PluginMediator() {
        System.out.println("plugins created");
        resDir = Paths.get(PATH);
    }

    public void start() {
        if(isStart) {
            throw new RejectedExecutionException("plugin mediator already start");
        }
        isStart = true;
        initLoad();
        DirMonitor dirMonitor = new DirMonitor(PATH, this);
        dirMonitor.startWatcher();
    }

    private static class Holder {
        private static final PluginMediator INSTANCE = new PluginMediator();
    }

    public static PluginMediator getInstance() {
        return Holder.INSTANCE;
    }

    public void addToListner(String className, Consumer<List<Class<?>>> consumer) {
        System.out.println("P&P : listener is added");
        subscribersList.add(new AbstractMap.SimpleEntry<>(className, consumer));
    }

    private void initLoad(){
        try {
            Files.walkFileTree(resDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(resDir, "*.jar")) {
                        System.out.println(stream);
                        for (Path currPath : stream) {
                            load(currPath.toAbsolutePath().toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void load(String path) {
        //System.out.println("entered to Plugins load "+subscribersList);
        for(Map.Entry<String,Consumer<List<Class<?>>>> entry : subscribersList) {
            List<Class<?>> newClass = jarLoader.load(path, entry.getKey());
            entry.getValue().accept(newClass);
        }
    }

    private static class DynamicJarLoader {

        public DynamicJarLoader() {
        }

        protected List<Class<?>> load(String path, String interfaceName) {
            Class<?> interfaceClass;
            try {
                //System.out.println("\t\t"+interfaceName);
                interfaceClass = Class.forName(interfaceName); // Load the interface class by name
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            List<Class<?>> classes = new ArrayList<>();
            try (JarFile jar = new JarFile(path)) {
                URL[] jarURL = new URL[]{new URL("jar:file:" + path + "!/")};
                try (URLClassLoader classLoader = new URLClassLoader(jarURL)) {
                    Enumeration<JarEntry> jarEntry = jar.entries();
                    while (jarEntry.hasMoreElements()) {
                        JarEntry curr = jarEntry.nextElement();
                        if (curr.getName().endsWith(".class")) {
                            String classToUpload = (curr.getName().substring(0, curr.getName().length() - ".class".length())).replace('/', '.');
                            //System.out.println("\n\n\n"+classToUpload+"\n\n\n");
                            Class<?> aClass = classLoader.loadClass(classToUpload);

                            if (interfaceClass.isAssignableFrom(aClass) && !aClass.isInterface() && !Modifier.isAbstract(aClass.getModifiers())) {
                                classes.add(aClass);
                                //System.out.println("added class  " + aClass.getSimpleName() + " to list");
                            }
                        }
                    }
                }
            } catch(IOException | ClassNotFoundException e){

                throw new RuntimeException(e);
            }
            return classes;

        }
    }
}
