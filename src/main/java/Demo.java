import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Demo {

    private static final Logger log = Logger.getLogger(Demo.class.getName());

    public static void main(String[] args) throws Exception {
        Timer timer = Timer.getInstance();
        String totalTimeOfProgram = timer.start("Skupni cas izvajanja programa");

        ConfigLoader configLoader = new ConfigLoader("src/main/java/konfiguracija.yaml");
        ArrayList<String> inputOntologyFiles = (ArrayList<String>) configLoader.getOntology().get("naloziIzDatotek");
        ArrayList<String> SPARQLQueries = (ArrayList<String>) configLoader.getOntology().get("poizvedbeSPARQL");
        String sparqlMigrationDirectory = "ipfs-files/output/sparql-migration-$CID.ru";

        IPFSHelpers ipfsHelpers = new IPFSHelpers(configLoader);
        EthereumHelpers ethereumHelpers = new EthereumHelpers(configLoader);

        // Connects to Apache Jena dataset, creates empty one if not existent
        JenaHelpers jenaHelpers = new JenaHelpers(configLoader.uporabiSklepanje());
        String ethereumContractAddress = jenaHelpers.retrieveEthContractAddressFromRDFDatabase();

        // Save them on blockchains
        if (ethereumContractAddress == null && inputOntologyFiles != null) {
            jenaHelpers.loadInputOntologiesIFEmptyDatasetIFReasonerConsistencyCheckInfModelAdd(inputOntologyFiles);
            String contractAddress = ethereumHelpers.deployStorageContract();
            jenaHelpers.saveEthContractAddressToRDFDatabase(contractAddress);

            jenaHelpers.uploadInputOntologyFilesToBlockchains(inputOntologyFiles, ipfsHelpers, ethereumHelpers);
        } else if (ethereumContractAddress != null) {
            String contractAddress = jenaHelpers.retrieveEthContractAddressFromRDFDatabase();
            ethereumHelpers.loadContractAtAddress(contractAddress);
            // FRESH OF RDF DATABASE: Downloads input ontologies from blockchains to folder and then loads them in Jena
            if (inputOntologyFiles == null) {
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
            Boolean successful = jenaHelpers.executeSPARQL(query, inputOntologyFiles, ipfsHelpers, ethereumHelpers);
            timer.stop(timerExecuteSPARQLQuery);
            if (!successful) {
                log.severe("SPARQL query wasn't applied to the dataset. Finishing program execution.");
                break;
            }
            i++;
            // jenaHelpers.printDatasetToStandardOutput();
        }

        timer.stop(totalTimeOfProgram);
        Timer.closeWriter();
    }

}
