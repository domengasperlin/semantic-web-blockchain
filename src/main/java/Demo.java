import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

public class Demo {

    private static final Logger log = Logger.getLogger(Demo.class.getName());
    private static final String sparqlMigrationDirectory = "ipfs-files/output/sparql-migration-$CID.ru";
    private static final String basePath = "src/main/java/";
    public static void main(String[] args) throws Exception {
        ScenarioManager.cleanUpIPFS();
        ScenarioManager.cleanRDFDatabase();
        Timer timer = Timer.getInstance();
        String configurationName = args[0];
        Timer.addDataToCSV("Konfiguracija", configurationName, "ime");
        ConfigLoader configLoader = new ConfigLoader(basePath+configurationName);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(configLoader);
        EthereumHelpers ethereumHelpers = new EthereumHelpers(configLoader);

        Optional<ArrayList<String>> inputOntologyFiles = Optional.ofNullable((ArrayList<String>) configLoader.getOntology().get("naloziIzDatotek"));
        ArrayList<String> SPARQLQueries = (ArrayList<String>) configLoader.getOntology().get("poizvedbeSPARQL");

        // Connects to Apache Jena dataset, creates empty one if not existent
        Timer.addDataToCSV("sklepanje", configLoader.uporabiSklepanje().toString(), "boolean");
        JenaHelpers jenaHelpers = new JenaHelpers(configLoader.uporabiSklepanje(), configurationName);
        Optional<String> ethereumContractAddress = jenaHelpers.retrieveEthContractAddressFromRDFDatabase();

        // Save them on blockchains
        if (!ethereumContractAddress.isPresent() && inputOntologyFiles.isPresent()) {
            Timer.addDataToCSV("Scenarij", "zacetni", "tip scenarija");
            jenaHelpers.loadInputOntologiesIFEmptyDatasetIFReasonerConsistencyCheckInfModelAdd(inputOntologyFiles.get());
            String contractAddress = ethereumHelpers.deployStorageContract();
            jenaHelpers.saveEthContractAddressToRDFDatabase(contractAddress);
            jenaHelpers.uploadInputOntologyFilesToBlockchains(inputOntologyFiles.get(), ipfsHelpers, ethereumHelpers);
        } else if (ethereumContractAddress.isPresent()) {
            Optional<String> contractAddress = jenaHelpers.retrieveEthContractAddressFromRDFDatabase();
            ethereumHelpers.loadContractAtAddress(contractAddress.get());
            // FRESH OF RDF DATABASE: Downloads input ontologies from blockchains to folder and then loads them in Jena
            if (!inputOntologyFiles.isPresent()) {
                Timer.addDataToCSV("Scenarij", "sodelovanje", "tip scenarija");
                // Retrieve IPFS content identifiers from Ethereum
                ArrayList<String> inputOntologyFilesFromBlockchains = new ArrayList<>();
                BigInteger inputOntologiesLength = ethereumHelpers.getContract().pridobiDolzinoVhodnihOntologij().send();
                for (BigInteger i = BigInteger.ZERO; i.compareTo(inputOntologiesLength) < 0; i = i.add(BigInteger.ONE)) {
                    // TODO: assuming ttl ending
                    String restoredOntologiesDirectory = "rdf-sparql/ontologija-obnovljena/vhodna-ontologija-$CID.ttl";
                    String timerGetOntology = timer.start("R. Pridobi vhodno ontologijo iz ETH");
                    String inputOntologyCID = ethereumHelpers.getContract().pridobiVhodnoOntologijo(i).send();
                    timer.stop(timerGetOntology);
                    String inputOntologyPath = restoredOntologiesDirectory.replace("$CID", inputOntologyCID);
                    // Download ontology from IPFS
                    String timerGetIPFSFile = timer.start("R. Prenesi vhodno ontologijo iz IPFS");
                    ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
                    timer.stop(timerGetIPFSFile);
                    inputOntologyFilesFromBlockchains.add(inputOntologyPath);
                }
                log.info("[IPFS downloaded and write to files]");
                jenaHelpers.purgeJenaModel();
                jenaHelpers.loadInputOntologiesIFEmptyDatasetIFReasonerConsistencyCheckInfModelAdd(inputOntologyFilesFromBlockchains);
            } else {
                Timer.addDataToCSV("Scenarij", "nadaljevanje", "tip scenarija");
            }
        } else {
            throw new Exception("Neveljavna konfiguracija, podajte vhodne ontologije ali naslov ethereum pogodbe!");
        }

        // Download all not run SPARQL migrations
        BigInteger sparqlMigrations = ethereumHelpers.getContract().pridobiDolzinoMigracij().send();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(sparqlMigrations) < 0; i = i.add(BigInteger.ONE)) {
            String timerGetSPARQLMigrationCID = timer.start("3."+i+" Prenos migracije CID iz ETH");
            String sparqlMigrationCID = ethereumHelpers.getContract().pridobiMigracijo(i).send();
            timer.stop(timerGetSPARQLMigrationCID);
            String sparqlMigrationLocation = sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID);
            if (jenaHelpers.hasMigrationBeenRanAlready(sparqlMigrationCID)) continue;
            String timerRetrieveIPFSCID = timer.start("3."+i+" Prenos IPFS datoteke");
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlMigrationCID, sparqlMigrationLocation);
            timer.stop(timerRetrieveIPFSCID);
            String timerSPARQLMigration = timer.start("3."+i+" Izvedba SPARQL migracije");
            jenaHelpers.executeSPARQLMigrationForDBSync(sparqlMigrationCID, sparqlMigrationLocation);
            timer.stop(timerSPARQLMigration);
        }

        Integer i = 0;
        for (String query : SPARQLQueries) {
            log.info("[Executing SPARQL from file (.rq)] " + query);
            String timerExecuteSPARQLQuery = timer.start("4."+i+" Izvedba SPARQL poizvedbe");
            Boolean successful = jenaHelpers.executeSPARQL(query, inputOntologyFiles.get(), ipfsHelpers, ethereumHelpers);
            timer.stop(timerExecuteSPARQLQuery);
            if (!successful) {
                log.severe("SPARQL query wasn't applied to the dataset. Finishing program execution.");
                break;
            }
            i++;
            // jenaHelpers.printDatasetToStandardOutput();
        }

        Timer.finish();
    }

}
