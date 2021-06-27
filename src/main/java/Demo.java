import io.ipfs.api.IPFS;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Demo {
    private static final Logger log = Logger.getLogger(Demo.class.getName());
    public static Boolean useReasoner;
    public static Boolean isInitialLoad = true;

    public static void main(String[] args) throws Exception {
        ConfigLoader configLoader = new ConfigLoader("src/main/java/config.yaml");
        ArrayList<String> inputOntologyFiles = (ArrayList<String>) configLoader.getOntology().get("loadFromFiles");
        String IPFSNodeAddress = (String) configLoader.getIPFS().get("nodeAddress");
        String ethereumNodeAddress = (String) configLoader.getEthereum().get("nodeAddress");
        ArrayList<String> SPARQLQueries = (ArrayList<String>) configLoader.getOntology().get("SPARQLQueries");
        String sparqlMigrationDirectory = "ipfs-files/output/sparql-migration-$CID.ru";

        for (String x : SPARQLQueries) {
            if (x.contains("dbpedia")) {
                useReasoner = false;
                break;
            } else {
                useReasoner = true;
            }
        }

        EthereumHelpers ethereumHelpers = new EthereumHelpers(ethereumNodeAddress, configLoader);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(new IPFS(IPFSNodeAddress));

        String inputOntologyCID;
        // TODO: support more input ontology files
        String inputOntologyPath = inputOntologyFiles.get(0);
        String sparqlMigrationCID = "";
        if (isInitialLoad) {
            // Upload ontology to IPFS
            inputOntologyCID = ipfsHelpers.uploadLocalFileToIPFS(inputOntologyPath).toString();
            log.info("[IPFS upload content identifiers] " + inputOntologyCID);

            // Store IPFS content identifiers to Ethereum
            ethereumHelpers.deployStorageContract();
            TransactionReceipt send = ethereumHelpers.getContract().setInitialOntology(inputOntologyCID).send();
            log.info("[ETH] transaction: " + send.getTransactionHash());

        } else {
            // Retrieve IPFS content identifiers from Ethereum
            String smartContractAddress = "0x4b96cd131964d4cf79005aafe0def709b463d5be";
            ethereumHelpers.loadContractAtAddress(smartContractAddress);
            inputOntologyCID = ethereumHelpers.getContract().getInitialOntology().send();

            // Download ontology from IPFS
            // TODO: support more input ontology files
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
            log.info("[IPFS download and write to files]");

            // Download SPARQL migrations
            sparqlMigrationCID = ethereumHelpers.getContract().getSparqlUpdate().send();
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlMigrationCID, sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID));
        }

        // Load the ontology files into the Apache Jena
        JenaHelpers jenaHelpers = new JenaHelpers(inputOntologyPath, useReasoner, isInitialLoad);
        // TODO: support more than one migration
        jenaHelpers.executeSPARQLMigrationForDBSync(sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID));

        for (String query : SPARQLQueries) {
            log.info("[Executing SPARQL from file (.rq)] " + query);
            Boolean successful = jenaHelpers.executeSPARQL(query, inputOntologyPath, ipfsHelpers, ethereumHelpers);
            if (!successful) break;
            // jenaHelpers.printDatasetToStandardOutput();
        }
    }

}
