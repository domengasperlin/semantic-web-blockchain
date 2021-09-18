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

        // Load the ontology files into the Apache Jena, with active reasoner also checks if input ontology is consistent
        JenaHelpers jenaHelpers = new JenaHelpers(inputOntologyFiles, configLoader.uporabiSklepanje());

        // Save them on blockchains
        String ethereumContractAddress = jenaHelpers.retrieveEthContractAddressFromRDFDatabase();
        EthereumHelpers ethereumHelpers = new EthereumHelpers(configLoader);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(configLoader);
        if (ethereumContractAddress == null) {
            ethereumHelpers.deployStorageContractAndSaveAddressToRDFDatabase(jenaHelpers);
            jenaHelpers.uploadInputOntologyFilesToBlockchains(inputOntologyFiles, ipfsHelpers, ethereumHelpers);
        } else {
            ethereumHelpers.loadContractAtAddress(jenaHelpers.retrieveEthContractAddressFromRDFDatabase());
        }

        // Retrieve IPFS content identifiers from Ethereum
        BigInteger inputOntologiesLength = ethereumHelpers.getContract().pridobiDolzinoVhodnihOntologij().send();
        for(BigInteger i = BigInteger.ZERO; i.compareTo(inputOntologiesLength) < 0; i = i.add(BigInteger.ONE)) {
            String inputOntologyCID = ethereumHelpers.getContract().pridobiVhodnoOntologijo(i).send();
            // TODO: Here we are making assumption that input files are the same
            String inputOntologyPath = inputOntologyFiles.get(i.intValue());
            // Download ontology from IPFS
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(inputOntologyCID, inputOntologyPath);
        }
        log.info("[IPFS downloaded and write to files]");

        // Download All SPARQL migrations
        BigInteger sparqlMigrations = ethereumHelpers.getContract().pridobiDolzinoMigracij().send();
        for(BigInteger i = BigInteger.ZERO; i.compareTo(sparqlMigrations) < 0; i = i.add(BigInteger.ONE)) {
            String sparqlMigrationCID = ethereumHelpers.getContract().pridobiMigracijo(i).send();
            String sparqlMigrationLocation = sparqlMigrationDirectory.replace("$CID", sparqlMigrationCID);
            ipfsHelpers.retrieveFileAndSaveItToLocalSystem(sparqlMigrationCID, sparqlMigrationLocation);
            jenaHelpers.executeSPARQLMigrationForDBSync(sparqlMigrationLocation);
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
