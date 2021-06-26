import io.ipfs.api.IPFS;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Demo {
    private static final Logger log = Logger.getLogger(Demo.class.getName());
    public static Boolean uploadLocalDatabaseToBlockchains;
    public static Boolean useReasoner;

    public static Boolean isInitialLoad = true;

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("src/main/java/config.yaml");
        ArrayList<String> inputOntologyFiles = (ArrayList<String>) configLoader.getOntology().get("loadFromFiles");
        String IPFSNodeAddress = (String) configLoader.getIPFS().get("nodeAddress");
        String ethereumNodeAddress = (String) configLoader.getEthereum().get("nodeAddress");
        ArrayList<String> SPARQLQueries = (ArrayList<String>) configLoader.getOntology().get("SPARQLQueries");

        if (isInitialLoad) {
            uploadLocalDatabaseToBlockchains = true;
        } else {
            uploadLocalDatabaseToBlockchains = false;
        }
        for(String x : SPARQLQueries){
            if(x.contains("dbpedia")){
                useReasoner = false;
                break;
            } else {
                useReasoner = true;
            }
        }

        EthereumHelpers ethereumHelpers = new EthereumHelpers(ethereumNodeAddress, configLoader);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(new IPFS(IPFSNodeAddress));

        String inputOntologyCID = null;
        // TODO: support more input ontology files
        String inputOntologyPath = inputOntologyFiles.get(0);
        String sparqlUpdateCID = null;
        if (uploadLocalDatabaseToBlockchains) {
            // Upload ABox, TBox, RBox files to IPFS
            inputOntologyCID = ipfsHelpers.uploadLocalFileToIPFS(inputOntologyPath).toString();
            log.info("[IPFS upload content identifiers] " + inputOntologyCID);

            // Store IPFS content identifiers to Ethereum
            String contractAddress = ethereumHelpers.deployStorageContract();
            log.info("[ETH] contract address: " + contractAddress);
            ethereumHelpers.loadContractAtAddress(contractAddress);
            TransactionReceipt send;
            try {
                send = ethereumHelpers.getContract().setInitialOntology(inputOntologyCID).send();
                log.info("[ETH] transaction: " + send.getTransactionHash());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // Download dataset from blockchain
            String smartContractAddress = "0x7054c38b58770477d5d86bd6c10dc516042c2622";
            ethereumHelpers.loadContractAtAddress(smartContractAddress);
            try {
                sparqlUpdateCID = ethereumHelpers.getContract().getSparqlUpdate().send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlUpdateCID, "ipfs-files/output/sparql-update-"+sparqlUpdateCID+".ru");

            try {
                inputOntologyCID = ethereumHelpers.getContract().getInitialOntology().send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("[IPFS download and write to files] ");

            // TODO: support more input ontology files
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
        }

        // Load the ABox, TBox, RBox files into the Apache Jena
        JenaHelpers jenaHelpers = new JenaHelpers(inputOntologyPath, useReasoner, isInitialLoad);
        // TODO: support more than one migration
        jenaHelpers.executeSPARQLMigrationForDBSync("ipfs-files/output/sparql-update-"+sparqlUpdateCID+".ru");

        for (String query : SPARQLQueries) {
            log.info("[Executing SPARQL from query file (.rq)] " + query);
            Boolean successful = jenaHelpers.executeSPARQL(query, inputOntologyPath, ipfsHelpers, ethereumHelpers);
            if (!successful) break;
            // jenaHelpers.printDatasetToStandardOutput();
        }
    }

}
