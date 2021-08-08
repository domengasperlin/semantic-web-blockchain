import io.ipfs.api.IPFS;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.math.BigInteger;
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

        for (String query : SPARQLQueries) {
            if (query.contains("dbpedia")) {
                useReasoner = false;
                break;
            } else {
                useReasoner = true;
            }
        }

        EthereumHelpers ethereumHelpers = new EthereumHelpers(ethereumNodeAddress, configLoader);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(new IPFS(IPFSNodeAddress));

        // Load the ontology files into the Apache Jena
        JenaHelpers jenaHelpers = new JenaHelpers(inputOntologyFiles, useReasoner, isInitialLoad);
        // If exception is not thrown till this point and reasoner is active ontology is consistent and we can move on
        if (isInitialLoad) {
            // Upload ontology to IPFS
            ArrayList<String> inputOntologyCIDs = new ArrayList<>();
            for (String inputOntologyPath : inputOntologyFiles) {
                inputOntologyCIDs.add(ipfsHelpers.uploadLocalFileToIPFS(inputOntologyPath).toString());
            }
            log.info("[IPFS upload content identifiers] " + inputOntologyCIDs);

            // Store IPFS content identifiers to Ethereum
            ethereumHelpers.deployStorageContract();
            for (String inputOntologyCID : inputOntologyCIDs) {
                TransactionReceipt transaction = ethereumHelpers.getContract().addInputOntology(inputOntologyCID).send();
                log.info("[ETH] transaction: " + transaction.getTransactionHash());
            }

        } else {
            // Retrieve IPFS content identifiers from Ethereum
            String smartContractAddress = "0x7da519706a64299801f533369a3e36cc7503bd98";
            ethereumHelpers.loadContractAtAddress(smartContractAddress);

            BigInteger inputOntologiesLength = ethereumHelpers.getContract().getInputOntologiesLength().send();
            for(BigInteger i = BigInteger.ZERO; i.compareTo(inputOntologiesLength) < 0; i = i.add(BigInteger.ONE)) {
                String inputOntologyCID = ethereumHelpers.getContract().getInputOntology(i).send();
                // TODO: Here we are making assumption that input files are the same
                String inputOntologyPath = inputOntologyFiles.get(i.intValue());
                // Download ontology from IPFS
                ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
            }
            log.info("[IPFS downloaded and write to files]");

            // Download All SPARQL migrations
            BigInteger sparqlMigrations = ethereumHelpers.getContract().getSUPMigrationsLength().send();
            for(BigInteger i = BigInteger.ZERO; i.compareTo(sparqlMigrations) < 0; i = i.add(BigInteger.ONE)) {
                String sparqlMigrationCID = ethereumHelpers.getContract().getSUPMigration(i).send();
                String sparqlMigrationLocation = sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID);
                ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlMigrationCID, sparqlMigrationLocation);
                jenaHelpers.executeSPARQLMigrationForDBSync(sparqlMigrationLocation);
            }
        }

        for (String query : SPARQLQueries) {
            log.info("[Executing SPARQL from file (.rq)] " + query);
            Boolean successful = jenaHelpers.executeSPARQL(query, inputOntologyFiles, ipfsHelpers, ethereumHelpers);
            if (!successful) {
                log.severe("SPARQL query wasn't applied to the dataset");
                break;
            }
            // jenaHelpers.printDatasetToStandardOutput();
        }
    }

}
