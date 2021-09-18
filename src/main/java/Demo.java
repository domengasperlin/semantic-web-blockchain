import java.math.BigInteger;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Demo {

    private static final Logger log = Logger.getLogger(Demo.class.getName());

    public static void main(String[] args) throws Exception {
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
            jenaHelpers.saveEthContractAddressToRDFDatabase(ethereumHelpers.deployStorageContract());
            jenaHelpers.uploadInputOntologyFilesToBlockchains(inputOntologyFiles, ipfsHelpers, ethereumHelpers);
        } else if (ethereumContractAddress != null) {
            ethereumHelpers.loadContractAtAddress(jenaHelpers.retrieveEthContractAddressFromRDFDatabase());
            // FRESH OF RDF DATABASE: Downloads input ontologies from blockchains to folder and then loads them in Jena
            if (inputOntologyFiles == null) {
                // TODO: assuming ttl ending
                String restoredOntologiesDirectory = "rdf-sparql/ontologija-obnovljena/vhodna-ontologija-$CID.ttl";
                // Retrieve IPFS content identifiers from Ethereum
                ArrayList<String> inputOntologyFilesFromBlockchains = new ArrayList<>();
                BigInteger inputOntologiesLength = ethereumHelpers.getContract().pridobiDolzinoVhodnihOntologij().send();
                for(BigInteger i = BigInteger.ZERO; i.compareTo(inputOntologiesLength) < 0; i = i.add(BigInteger.ONE)) {
                    String inputOntologyCID = ethereumHelpers.getContract().pridobiVhodnoOntologijo(i).send();
                    String inputOntologyPath = restoredOntologiesDirectory.replace("$CID", inputOntologyCID);
                    // Download ontology from IPFS
                    ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
                    inputOntologyFilesFromBlockchains.add(inputOntologyPath);
                }
                log.info("[IPFS downloaded and write to files]");
                jenaHelpers.purgeJenaModel();
                jenaHelpers.loadInputOntologiesIFEmptyDatasetIFReasonerConsistencyCheckInfModelAdd(inputOntologyFilesFromBlockchains);
            }
        } else {
            throw new Exception("Neveljavna konfiguracija, podajte vhodne ontologije ali naslov ethereum pogodbe!");
        }

        // Download All SPARQL migrations
        BigInteger sparqlMigrations = ethereumHelpers.getContract().pridobiDolzinoMigracij().send();
        for(BigInteger i = BigInteger.ZERO; i.compareTo(sparqlMigrations) < 0; i = i.add(BigInteger.ONE)) {
            String sparqlMigrationCID = ethereumHelpers.getContract().pridobiMigracijo(i).send();
            String sparqlMigrationLocation = sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID);
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlMigrationCID, sparqlMigrationLocation);
            jenaHelpers.executeSPARQLMigrationForDBSync(sparqlMigrationCID, sparqlMigrationLocation);
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
