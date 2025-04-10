package maingatewayserver;

import maingatewayserver.PluginMediator;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/*
    *this class get path and monitor the directory and sub directory for each criation
    * the perpus to make every company resource dir of her own to load command from

 */
public class DirMonitor {
    private volatile boolean  watchServiceFlag = true;
    private final PluginMediator plugins;

    //TODO
    private final WatchService watchService;
    private final Path rootPath;
    private final Map<WatchKey, Path> keysmap = new HashMap<>();
    //Ctor
    public DirMonitor(String path , PluginMediator plugins) {
        System.out.println("dirMonitor created");
        try {
            //get the watch service
            this.watchService = FileSystems.getDefault().newWatchService();
            this.plugins = plugins;
            //create nio path from string
            rootPath = Paths.get(path);
            registerAllDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //register the root path and all sub Directory to the watch service
    private void registerAllDirectories(Path rootPath) {
        /*
            * SimpleFileVisitor class that implement File visitor interface visit all files
            * anonymous class for override to add a register each sub directory to the watcher before acsses it
         */
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    //REGISTER EACH PATH
                    registerDirectory (path);
                    //FileVisitResult.CONTINUE is a sign to fisit the sub directory also
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println(" error while register directory");
            throw new RuntimeException(e);
        }
    }
    private void registerDirectory(Path dirPath) throws IOException {
            WatchKey key = dirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            //put the key and associated path in map
            keysmap.put(key, dirPath);
            System.out.println("Dir monitor Register "+dirPath);
    }


/**
 * w
 * */
    protected void startWatcher() {
        while (watchServiceFlag) {
            //take return watch key that modified or wait if havnt
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                //means that interrupted while waiting
                watchServiceFlag = false;
                System.out.println("DirMonitor Shut Down");
                try {
                    watchService.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
            //retrieve the dir path that associated with key
            Path changedDir = keysmap.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {

                //get the relative path that change
                Path relativePath = (Path) event.context();
                // get the full changedDir + relativePath
                Path fullPath = changedDir.resolve(relativePath);
                /*
                 * the event that register is only create
                 * check if its directory and if it is register it and sub directory
                 * for the project means that new company register and new resource directory created
                 */
                if (Files.isDirectory(fullPath)) {
                    registerAllDirectories(fullPath);
                } else {
                    //means added some file
                    //TODO register to the RIGHT company by company ID
                    System.out.println("CHANGED detected at " + fullPath+" " +event.kind());
                    //creation of file can be recognise few times
                    if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                       plugins.load(fullPath.toString());
                    }
                }
                //method return false if the directory is no longer valid
                //or removed and nedded to remove from the keysmap
                if(!key.reset()) {
                    keysmap.remove(key);
                }
            }
        }
    }


    public static void main(String[] args) {
        DirMonitor dirMonitor = new DirMonitor("/home/ido/testFO",null);
        dirMonitor.startWatcher();
    }
}