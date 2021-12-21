import java.io.IOException;
import java.util.logging.Logger;

public class ScenarioManager {
    private static final Logger log = Logger.getLogger(ScenarioManager.class.getName());

    private static final String path = "/Users/domengasperlin/Development/sb/src/main/java";
    public static Boolean cleanUpIPFS() {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash",path+"/cleanUpIPFSBeforeExecution.sh");
        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            log.severe("Failed Clearing IPFS cache");
            e.printStackTrace();
            return false;
        }
        log.info("Clearing IPFS cache");
        return true;
    }

    public static Boolean cleanRDFDatabase() {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash",path+"/cleanUpRDFDatabase.sh");
        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Failed Clearing RDFS database");
            return false;
        }
        log.info("Clearing RDFS database");
        return true;
    }
}
