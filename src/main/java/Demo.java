import io.ipfs.api.IPFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: prepare .ru sparql queries/showcase for toy ontology
// TODO: prepare .ru sparql queries/showcase for dbpedia
// TODO: improve efficiency of checking for consistency for large datasets
// TODO: fix out of RAM when splitting ontologies exception to TBox, ABox - maybe the solution could be reading part of file / loading part of file and dumping it as we go
// TODO: improve Ethereum smart contract to use less gas

// TODO: remove duplicate code...
// TODO: Improve error handling
// TODO: pass CIDs, ontology more compactly as arguments
public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("src/main/java/config.yaml");
        ArrayList<String> loadOntologyFromFiles = (ArrayList<String>)configLoader.getOntology().get("loadFromFiles");
        ArrayList<HashMap<String, String>> splitOntologyToSchemaDataFiles = (ArrayList<HashMap<String, String>>)configLoader.getOntology().get("dumpToFiles");
        String IPFSNodeAddress = (String)configLoader.getIPFS().get("nodeAddress");
        String ethereumNodeAddress = (String)configLoader.getEthereum().get("nodeAddress");
        ArrayList<String> SPARQLQueries = (ArrayList<String>)configLoader.getOntology().get("SPARQLQueries");

        // Separate schema and data
        String aBoxFullPath = getABoxFilePath(splitOntologyToSchemaDataFiles);
        String tBoxFullPath = getTBoxFilePath(splitOntologyToSchemaDataFiles);
        String rBoxFullPath = getRBoxFilePath(splitOntologyToSchemaDataFiles);
        localDataSchemaSplitAndSaveToFile(loadOntologyFromFiles, aBoxFullPath, tBoxFullPath, rBoxFullPath);

        // Upload schema and data files to IPFS
        IPFSHelpers ipfsHelpers = new IPFSHelpers(new IPFS(IPFSNodeAddress));
        String aBoxCID = ipfsHelpers.uploadLocalFileToIPFS(aBoxFullPath).toString();
        String tBoxCID = ipfsHelpers.uploadLocalFileToIPFS(tBoxFullPath).toString();
        String rBoxCID = ipfsHelpers.uploadLocalFileToIPFS(rBoxFullPath).toString();
        log.info("[IPFS upload] aBox CID: "+aBoxCID + " tBox CID: "+tBoxCID + " rBox CID: "+rBoxCID);

        // Store schema and data CIDs to Ethereum
        EthereumHelpers ethereumHelpers = new EthereumHelpers(ethereumNodeAddress, configLoader.isDevelopment());
        ethereumHelpers.loadWalletCredentials(configLoader);
        String contractAddress = storeDataIdentifiersOnEthereumAndGetContractAddress(ethereumHelpers, tBoxCID, aBoxCID, rBoxCID);
        // Retrieve schema and data pointers from Ethereum
        String[] ontology = retrieveIPFSHashesForSchemaAndDataFromEthereum(ethereumHelpers, contractAddress);
        tBoxCID = ontology[0];
        aBoxCID = ontology[1];
        rBoxCID = ontology[2];

        log.info("[ETH retrieve] aBox CID: "+aBoxCID + " tBox CID: "+tBoxCID + " rBox CID" + rBoxCID);
        log.info("[IPFS download and write to files] ");
        // Download data from IPFS to local files
        downloadDataDownstream(ipfsHelpers, aBoxCID, tBoxCID, rBoxCID, aBoxFullPath, tBoxFullPath, rBoxFullPath);
        log.info("[Load files to triplestore and run SPARQL] ");
        // Load the schema and data files into the Apache Jena
        loadABoxToBoxToJenaAndPerformSPARQLOperations(aBoxFullPath, tBoxFullPath, rBoxFullPath, SPARQLQueries);
    }

    public static String storeDataIdentifiersOnEthereumAndGetContractAddress(EthereumHelpers ethereumHelpers, String tBoxCID, String aBoxCID, String rBoxCID) {
        String contractAddress = ethereumHelpers.deployStorageContract();
        TransactionReceipt storeTransactionReceipt = ethereumHelpers.loadStorageContractAndCallStoreMethod(contractAddress, tBoxCID, aBoxCID, rBoxCID);
        String storeTransactionHash = storeTransactionReceipt.getTransactionHash();
        log.info("Store data transaction: "+storeTransactionHash);
        return contractAddress;
    }

    public static String[] retrieveIPFSHashesForSchemaAndDataFromEthereum(EthereumHelpers ethereumHelpers, String contractAddress) {
        String[] contractCIDs = ethereumHelpers.loadStorageContractAndCallRetrieveMethod(contractAddress);
        return contractCIDs;
    }

    public static void localDataSchemaSplitAndSaveToFile(ArrayList<String> loadFromFiles, String aBoxFullPath, String tBoxFullPath, String rBoxFullPath) {
        OntologyHelpers ontologyHelpers = new OntologyHelpers(loadFromFiles);
        // Save to file and upload to IPFS
        ontologyHelpers.saveABoxAxiomsToFile(aBoxFullPath);
        ontologyHelpers.saveTBoxAxiomsToFile(tBoxFullPath);
        ontologyHelpers.saveRBoxAxiomsToFile(rBoxFullPath);
    }

    public static String getABoxFilePath(ArrayList<HashMap<String, String>> dumpToFiles) {
        for (HashMap<String, String> el : dumpToFiles) {
            if (el.containsKey("abox")) {
                return el.get("abox");
            }
        }

        log.error("Dump files must contain rbox, abox, tbox files");
        return null;
    }

    public static String getTBoxFilePath(ArrayList<HashMap<String, String>> dumpToFiles) {
        for (HashMap<String, String> el : dumpToFiles) {
            if (el.containsKey("tbox")) {
                return el.get("tbox");
            }
        }
        log.error("Dump files must contain rbox, abox, tbox files");
        return null;
    }

    public static String getRBoxFilePath(ArrayList<HashMap<String, String>> dumpToFiles) {
        for (HashMap<String, String> el : dumpToFiles) {
            if (el.containsKey("rbox")) {
                return el.get("rbox");
            }
        }
        log.error("Dump files must contain rbox, abox, tbox files");
        return null;
    }

    public static void downloadDataDownstream(IPFSHelpers ipfsHelpers, String aBoxFileHash, String tBoxFileHash, String rBoxFileHash, String aBoxFullPath, String tBoxFullPath, String rBoxFullPath) {
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(aBoxFileHash, aBoxFullPath);
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(tBoxFileHash, tBoxFullPath);
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(rBoxFileHash, rBoxFullPath);
    }

    public static void loadABoxToBoxToJenaAndPerformSPARQLOperations(String aBoxFullPath, String tBoxFullPath, String rBoxFullPath, ArrayList<String> SPARQLQueries) {
        JenaHelpers jenaHelpers = new JenaHelpers(tBoxFullPath, aBoxFullPath, rBoxFullPath);
        jenaHelpers.checkConsistency();
//        JenaHelpers jenaHelpers = new JenaHelpers(inputDBPediaTBoxFullPath, inputDBPediaABoxFullPath);

        for(String query : SPARQLQueries) {
            log.info("[Executing SPARQL from file] "+query);
            jenaHelpers.executeSPARQL(query);
            jenaHelpers.printDatasetToStandardOutput();
            jenaHelpers.checkConsistency();
            // TODO: if jenaHelpers.checkConsistency()
            //          persistChangesToTripleStore
            //          sync changes to blockchain
        }
    }

}
