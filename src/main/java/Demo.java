import io.ipfs.api.IPFS;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Demo {
    private static final Logger log = Logger.getLogger(Demo.class.getName());
    public static Boolean useReasoner;
    public static Boolean isInitialLoad = true;
    public static long programStarted = System.nanoTime();

    public static void main(String[] args) throws Exception {
        int toMsConvert = 1000000;
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
        long startTimeJena = System.nanoTime();
        JenaHelpers jenaHelpers = new JenaHelpers(inputOntologyFiles, useReasoner, isInitialLoad);
        log.info("Jena load: " + (System.nanoTime() - startTimeJena) / toMsConvert + " ms");
        // If exception is not thrown till this point and reasoner is active ontology is consistent and we can move on
        if (isInitialLoad) {
            // Upload ontology to IPFS
            ArrayList<String> inputOntologyCIDs = new ArrayList<>();
            for (String inputOntologyPath : inputOntologyFiles) {
                inputOntologyCIDs.add(ipfsHelpers.uploadLocalFileToIPFS(inputOntologyPath).toString());
            }
            log.info("[IPFS upload content identifiers] " + inputOntologyCIDs);

            // Store IPFS content identifiers to Ethereum
            long deployContractStart = System.nanoTime();
            ethereumHelpers.deployStorageContract();
            log.info("Deploy contract: " + (System.nanoTime() - deployContractStart) / toMsConvert + " ms");

            for (String inputOntologyCID : inputOntologyCIDs) {
                long contractMethodCallStart = System.nanoTime();
                TransactionReceipt transaction = ethereumHelpers.getContract().addInputOntology(inputOntologyCID).send();
                log.info("[ETH] transaction: " + transaction.getTransactionHash());
                log.info("Contract method addInputOntology call: " + (System.nanoTime() - contractMethodCallStart) / toMsConvert + " ms");
            }

        } else {
            // Retrieve IPFS content identifiers from Ethereum
            String smartContractAddress = "0xd9ff39f2395ebc672e187dcb2d30b82a4ca38570";
            long ethereumOperationsStart = System.nanoTime();
            ethereumHelpers.loadContractAtAddress(smartContractAddress);
            BigInteger inputOntologiesLength = ethereumHelpers.getContract().getInputOntologiesLength().send();
            log.info("ethereum size of input ontologies: " + (System.nanoTime() - ethereumOperationsStart) / toMsConvert + " ms");
            for (BigInteger i = BigInteger.ZERO; i.compareTo(inputOntologiesLength) < 0; i = i.add(BigInteger.ONE)) {
                long ethereumGetInputOntologyStart = System.nanoTime();
                String inputOntologyCID = ethereumHelpers.getContract().getInputOntology(i).send();
                log.info("ethereum get input ontology: " + (System.nanoTime() - ethereumGetInputOntologyStart) / toMsConvert + " ms");

                // TODO: Here we are making assumption that input files are the same
                String inputOntologyPath = inputOntologyFiles.get(i.intValue());
                // Download ontology from IPFS
                long ipfsInputOnlogyStart = System.nanoTime();
                ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
                log.info("IPFS get input ontology: " + (System.nanoTime() - ipfsInputOnlogyStart) / toMsConvert + " ms");

            }
            log.info("[IPFS downloaded and write to files]");

            // Download All SPARQL migrations
            long ethereumGetSUPMigrationsStart = System.nanoTime();
            BigInteger sparqlMigrations = ethereumHelpers.getContract().getSUPMigrationsLength().send();
            log.info("ethereum getSUPMigrationsLength ontologies: " + (System.nanoTime() - ethereumGetSUPMigrationsStart) / toMsConvert + " ms");
            for (BigInteger i = BigInteger.ZERO; i.compareTo(sparqlMigrations) < 0; i = i.add(BigInteger.ONE)) {
                long ethereumGetSUPMigrationStart = System.nanoTime();
                String sparqlMigrationCID = ethereumHelpers.getContract().getSUPMigration(i).send();
                log.info("ethereum getSUPMigration: " + (System.nanoTime() - ethereumGetSUPMigrationStart) / toMsConvert + " ms");

                String sparqlMigrationLocation = sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID);

                long ipfsRetrieveMigrationStart = System.nanoTime();
                ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlMigrationCID, sparqlMigrationLocation);
                log.info("ipfs retrieve migration: " + (System.nanoTime() - ipfsRetrieveMigrationStart) / toMsConvert + " ms");

                long sparqlMigrationStart = System.nanoTime();
                jenaHelpers.executeSPARQLMigrationForDBSync(sparqlMigrationLocation);
                log.info("sparql migration: " + (System.nanoTime() - sparqlMigrationStart) / toMsConvert + " ms");
            }
        }

        for (String query : SPARQLQueries) {

            long executeQueryStart = System.nanoTime();
            log.info("[Executing SPARQL from file (.rq)] " + query);
            Boolean successful = jenaHelpers.executeSPARQL(query, inputOntologyFiles, ipfsHelpers, ethereumHelpers);
            if (!successful) {
                log.severe("SPARQL query wasn't applied to the dataset");
                break;
            }
            log.info("Execute sparql query: " + (System.nanoTime() - executeQueryStart) / toMsConvert + " ms");
            // jenaHelpers.printDatasetToStandardOutput();
        }
        log.info("Program finished in: " + (System.nanoTime() - programStarted) / toMsConvert + " ms");

    }

}
