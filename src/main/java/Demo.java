import io.ipfs.api.IPFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.util.ArrayList;
import java.util.HashMap;

public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);
    public static Boolean performLocalDatabaseSync = true;
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

        IPFSHelpers ipfsHelpers = new IPFSHelpers(new IPFS(IPFSNodeAddress));
        EthereumHelpers ethereumHelpers = new EthereumHelpers(ethereumNodeAddress, configLoader.isDevelopment());
        ethereumHelpers.loadWalletCredentials(configLoader);
        String aBoxCID;
        String tBoxCID;
        String rBoxCID;
        if (performLocalDatabaseSync) {
            // Upload schema and data files to IPFS
            aBoxCID = ipfsHelpers.uploadLocalFileToIPFS(aBoxFullPath).toString();
            tBoxCID = ipfsHelpers.uploadLocalFileToIPFS(tBoxFullPath).toString();
            rBoxCID = ipfsHelpers.uploadLocalFileToIPFS(rBoxFullPath).toString();
            log.info("[IPFS upload] aBox CID: "+aBoxCID + " tBox CID: "+tBoxCID + " rBox CID: "+rBoxCID);

            // Store schema and data CIDs to Ethereum
            String contractAddress = storeDataIdentifiersOnEthereumAndGetContractAddress(ethereumHelpers, tBoxCID, aBoxCID, rBoxCID);
            log.info("Contract address: " + contractAddress);
            // Retrieve schema and data pointers from Ethereum
            String[] ontology = retrieveIPFSHashesForSchemaAndDataFromEthereum(ethereumHelpers, contractAddress);
            tBoxCID = ontology[0];
            aBoxCID = ontology[1];
            rBoxCID = ontology[2];

        } else {
            aBoxCID = "QmQ3AP2G8UHpvRNRfNr9q4QG2bukNMME5xiNw27awQGhYw";
            tBoxCID = "QmNWbKyVzASXa288oHJqntZbGhWCT62f9V7q3qzRQmUwar";
            rBoxCID = "QmVjjHGYaFBaCrvxp8TTPV3Pz5tB9Sa6sMSyJMkYafN4y7";
            String smartContractAddress = "0xc3d2cf25eDBb8A3eCb06057aeFE4945A87B6b6a6";
            ethereumHelpers.loadContractAtAddress(smartContractAddress);
            downloadDataDownstream(ipfsHelpers, aBoxCID, tBoxCID, rBoxCID, aBoxFullPath, tBoxFullPath, rBoxFullPath);
        }

        log.info("[ETH retrieve] aBox CID: "+aBoxCID + " tBox CID: "+tBoxCID + " rBox CID" + rBoxCID);
        log.info("[IPFS download and write to files] ");
        // Download data from IPFS to local files

        // Load the schema and data files into the Apache Jena
        loadABoxToBoxToJenaAndPerformSPARQLOperations(aBoxFullPath, tBoxFullPath, rBoxFullPath, SPARQLQueries, ipfsHelpers, ethereumHelpers);
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

    public static void loadABoxToBoxToJenaAndPerformSPARQLOperations(String aBoxFullPath, String tBoxFullPath, String rBoxFullPath, ArrayList<String> SPARQLQueries, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        JenaHelpers jenaHelpers = new JenaHelpers(tBoxFullPath, aBoxFullPath, rBoxFullPath);
//        JenaHelpers jenaHelpers = new JenaHelpers(inputDBPediaTBoxFullPath, inputDBPediaABoxFullPath);

        for(String query : SPARQLQueries) {
            log.info("[Executing SPARQL from file] "+query);
            jenaHelpers.executeSPARQL(query, tBoxFullPath, ipfsHelpers, ethereumHelpers);
            jenaHelpers.printDatasetToStandardOutput();
        }
    }

}
