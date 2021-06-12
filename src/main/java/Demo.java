import io.ipfs.api.IPFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.util.ArrayList;

public class Demo {
    private static final Logger log = LoggerFactory.getLogger(Demo.class);
    public static Boolean uploadLocalDatabaseToBlockchains = true;
    public static Boolean useReasoner = false;

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("src/main/java/config.yaml");
        ArrayList<String> inputOntologyFiles = (ArrayList<String>) configLoader.getOntology().get("loadFromFiles");
        String IPFSNodeAddress = (String) configLoader.getIPFS().get("nodeAddress");
        String ethereumNodeAddress = (String) configLoader.getEthereum().get("nodeAddress");
        ArrayList<String> SPARQLQueries = (ArrayList<String>) configLoader.getOntology().get("SPARQLQueries");

        EthereumHelpers ethereumHelpers = new EthereumHelpers(ethereumNodeAddress, configLoader.isDevelopment());
        ethereumHelpers.loadWalletCredentials(configLoader);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(new IPFS(IPFSNodeAddress));

        String aBoxCID;
        String tBoxCID;
        String rBoxCID;
        String aBoxFullPath = configLoader.getABoxFilePath();
        String tBoxFullPath = configLoader.getTBoxFilePath();
        String rBoxFullPath = configLoader.getRBoxFilePath();
        if (uploadLocalDatabaseToBlockchains) {
            // Separate ontology to axiom files
            OntologyHelpers ontologyHelpers = new OntologyHelpers(inputOntologyFiles);
            ontologyHelpers.saveABoxAxiomsToFile(aBoxFullPath);
            ontologyHelpers.saveTBoxAxiomsToFile(tBoxFullPath);
            ontologyHelpers.saveRBoxAxiomsToFile(rBoxFullPath);

            // Upload ABox, TBox, RBox files to IPFS
            aBoxCID = ipfsHelpers.uploadLocalFileToIPFS(aBoxFullPath).toString();
            tBoxCID = ipfsHelpers.uploadLocalFileToIPFS(tBoxFullPath).toString();
            rBoxCID = ipfsHelpers.uploadLocalFileToIPFS(rBoxFullPath).toString();
            log.info("[IPFS upload content identifiers] " + aBoxCID + " " + tBoxCID + " " + rBoxCID);

            // Store IPFS content identifiers to Ethereum
            String contractAddress = ethereumHelpers.deployStorageContract();
            log.info("[ETH] contract address: " + contractAddress);
            ethereumHelpers.loadContractAtAddress(contractAddress);
            TransactionReceipt storeTransactionReceipt = ethereumHelpers.callStoreMethodOfContract(tBoxCID, aBoxCID, rBoxCID);
            log.info("[ETH] transaction: " + storeTransactionReceipt.getTransactionHash());

            // Retrieve schema and data pointers from Ethereum
            String[] contractCIDs = ethereumHelpers.callRetrieveTBoxABoxRBoxMethods();
            tBoxCID = contractCIDs[0];
            aBoxCID = contractCIDs[1];
            rBoxCID = contractCIDs[2];
            log.info("[ETH retrieve] ABox CID: " + aBoxCID + " TBox CID: " + tBoxCID + " RBox CID" + rBoxCID);

        } else {
            // Download dataset from blockchain
            String smartContractAddress = "0xbf933943074847bb2c8eb99c5678cd7a3b64f1c6";
            ethereumHelpers.loadContractAtAddress(smartContractAddress);
            String[] contractCIDs = ethereumHelpers.callRetrieveTBoxABoxRBoxMethods();
            tBoxCID = contractCIDs[0];
            aBoxCID = contractCIDs[1];
            rBoxCID = contractCIDs[2];
            log.info("[IPFS download and write to files] ");
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(aBoxCID, aBoxFullPath);
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(tBoxCID, tBoxFullPath);
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(rBoxCID, rBoxFullPath);
        }

        // Load the ABox, TBox, RBox files into the Apache Jena
        JenaHelpers jenaHelpers = new JenaHelpers(tBoxFullPath, aBoxFullPath, rBoxFullPath, useReasoner);

        for (String query : SPARQLQueries) {
            log.info("[Executing SPARQL from query file (.rq)] " + query);
            if (query.contains("tbox")) {
                Boolean successful = jenaHelpers.executeSPARQL(query, tBoxFullPath, ipfsHelpers, ethereumHelpers);
                if (!successful) break;
            }
            if (query.contains("abox")) {
                Boolean successful = jenaHelpers.executeSPARQL(query, aBoxFullPath, ipfsHelpers, ethereumHelpers);
                if (!successful) break;
            }
            if (query.contains("rbox")) {
                Boolean successful = jenaHelpers.executeSPARQL(query, rBoxFullPath, ipfsHelpers, ethereumHelpers);
                if (!successful) break;
            }
            // jenaHelpers.printDatasetToStandardOutput();
        }
    }

}
