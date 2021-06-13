
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigLoader {
    Map<String, Object> data;
    private static final Logger log = Logger.getLogger(ConfigLoader.class.getName());
    public ConfigLoader(String path) {
        try {
            InputStream inputStream = new FileInputStream(new File(path));
            Yaml yaml = new Yaml();
            data = yaml.load(inputStream);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public Boolean isDevelopment() {
        return data.get("env").equals("development");
    }

    public Map<String, Object> getOntology() {
        return (Map<String, Object>)data.get("ontology");
    }

    public Map<String, Object> getIPFS() {
        return (Map<String, Object>)data.get("IPFS");
    }

    public Map<String, Object> getEthereum() {
        return (Map<String, Object>)data.get("ethereum");
    }

    public String getABoxFilePath() {
        ArrayList<HashMap<String, String>> dumpToFiles = (ArrayList<HashMap<String, String>>) this.getOntology().get("dumpToFiles");
        for (HashMap<String, String> el : dumpToFiles) {
            if (el.containsKey("abox")) {
                return el.get("abox");
            }
        }

        log.severe("Dump files must contain rbox, abox, tbox files");
        return null;
    }

    public String getTBoxFilePath() {
        ArrayList<HashMap<String, String>> dumpToFiles = (ArrayList<HashMap<String, String>>) this.getOntology().get("dumpToFiles");
        for (HashMap<String, String> el : dumpToFiles) {
            if (el.containsKey("tbox")) {
                return el.get("tbox");
            }
        }
        log.severe( "Dump files must contain rbox, abox, tbox files");
        return null;
    }

    public String getRBoxFilePath() {
        ArrayList<HashMap<String, String>> dumpToFiles = (ArrayList<HashMap<String, String>>) this.getOntology().get("dumpToFiles");
        for (HashMap<String, String> el : dumpToFiles) {
            if (el.containsKey("rbox")) {
                return el.get("rbox");
            }
        }
        log.severe("Dump files must contain rbox, abox, tbox files");
        return null;
    }


}
