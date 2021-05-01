import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class Demo {

    private static String aBoxName = "abox-axioms.nt";
    private static String rBoxName = "rbox-axioms.ttl";
    private static String tBoxName = "tbox-axioms.nt";

    private static String rdfSparqlOutputFolder = "rdf-sparql/output";
    private static String rdfSparqlInputFolder = "rdf-sparql/input";
    private static String IPFSOutputFolder = "ipfs-files/output";
    private static String ethereumFolder = "ethereum";

    private static String inputOntologyFullPath = rdfSparqlInputFolder+"/input_ontology.owl";
    private static String inputDBPediaTBoxFullPath = rdfSparqlInputFolder+"/TBox_DBpedia_ontology_type=parsed.xml";
    private static String inputDBPediaABoxFullPath = rdfSparqlInputFolder+"/ABox_DBpedia_instance-types_lang=en_specific.ttl.gz";
    private static String outputOntologyNameFullPath = rdfSparqlOutputFolder+"/output_ontology.ttl";

    private static String aBoxFullPath = rdfSparqlOutputFolder+"/"+aBoxName;
    private static String rBoxFullPath = rdfSparqlOutputFolder+"/"+rBoxName;
    private static String tBoxFullPath = rdfSparqlOutputFolder+"/"+tBoxName;

    private static String IPFSNodeAddress = "/ip4/127.0.0.1/tcp/5001";
    private static String IPFSABoxFullPath = IPFSOutputFolder+"/"+aBoxName;
    private static String IPFSRBoxFullPath = IPFSOutputFolder+"/"+rBoxName;
    private static String IPFSTBoxFullPath = IPFSOutputFolder+"/"+tBoxName;

    private static String SPARQLSelectLocation = rdfSparqlInputFolder+"/select.ru";
    private static String SPARQLInsertLocation = rdfSparqlInputFolder+"/insert.ru";
    private static String SPARQLUpdateLocation = rdfSparqlInputFolder+"/update.ru";
    private static String SPARQLDeleteLocation = rdfSparqlInputFolder+"/delete.ru";

    private static String ethereumNodeAddress = "https://rinkeby.infura.io/v3/18b69a4069f7455ba4486efd1f5530b1";
    private static String ethereumWalletLocation = ethereumFolder+"/wallet--c261cf8e7283030d0b6fa672b5d15819c8d99aa3";
    private static String ethereumWalletPassword = "demo";

    public static void main(String[] args) {
        showcaseOutputOntologyAndOWLAPISchemaDataSeparation();
        demoIPFSFileUploadDownload();
        showcaseJenaSPARQLOperations();
//        demoEthereumStoreAndRetrieveData();
    }

    public static void showcaseOutputOntologyAndOWLAPISchemaDataSeparation() {
        OntologyHelpers ontologyHelpers = new OntologyHelpers(inputOntologyFullPath);
        ontologyHelpers.saveABoxAxiomsToFile(aBoxFullPath);
        ontologyHelpers.saveRBoxAxiomsToFile(rBoxFullPath);
        ontologyHelpers.saveTBoxAxiomsToFile(tBoxFullPath);
        ontologyHelpers.saveOntologyToFile(outputOntologyNameFullPath, ontologyHelpers.getOntology(), new TurtleDocumentFormat());
    }

    public static void demoIPFSFileUploadDownload() {
        IPFS ipfs = new IPFS(IPFSNodeAddress);
        IPFSHelpers ipfsHelpers = new IPFSHelpers(ipfs);

        // Upload file to IPFS
        Multihash aBoxFileHash = ipfsHelpers.uploadLocalFile(aBoxFullPath);
        Multihash rBoxFileHash = ipfsHelpers.uploadLocalFile(rBoxFullPath);
        Multihash tBoxFileHash = ipfsHelpers.uploadLocalFile(tBoxFullPath);
        System.out.println("tBox hash:"+tBoxFileHash);

        // Get file contents from IPFS
        String aBoxContents = ipfsHelpers.retrieveFileContents(aBoxFileHash.toString());
        String rBoxContents = ipfsHelpers.retrieveFileContents(rBoxFileHash.toString());
        String tBoxContents = ipfsHelpers.retrieveFileContents(tBoxFileHash.toString());

        // Save file contents from IPFS to local file system
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(aBoxFileHash.toString(), IPFSABoxFullPath);
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(rBoxFileHash.toString(), IPFSRBoxFullPath);
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(tBoxFileHash.toString(), IPFSTBoxFullPath);
    }

    public static void showcaseJenaSPARQLOperations() {
        JenaHelpers jenaHelpers = new JenaHelpers(outputOntologyNameFullPath, tBoxFullPath, aBoxFullPath);
//        JenaHelpers jenaHelpers = new JenaHelpers(outputOntologyNameFullPath, inputDBPediaTBoxFullPath, inputDBPediaABoxFullPath);
        System.out.println("SELECT --------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLQuery(SPARQLSelectLocation);
        System.out.println("INSERT --------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLInsert(SPARQLInsertLocation);
        jenaHelpers.printDatasetToStandardOutput();
        System.out.println("DELETE --------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLDelete(SPARQLDeleteLocation);
        jenaHelpers.printDatasetToStandardOutput();
        System.out.println("UPDATE--------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLUpdate(SPARQLUpdateLocation);
        jenaHelpers.printDatasetToStandardOutput();
    }



    public static void demoEthereumStoreAndRetrieveData() {

//        try {
//            TransactionReceipt transactionReceipt = Transfer.sendFunds(web3, credentials, "c261cf8e7283030d0b6fa672b5d15819c8d99aa3", BigDecimal.valueOf(5), Convert.Unit.ETHER).send();
//        } catch (Exception e) {
//            String error = e.getMessage();
//        }

        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(ethereumWalletPassword, ethereumWalletLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebHelpers webHelpers = new WebHelpers(ethereumNodeAddress, credentials);
        String contractAddress = webHelpers.deployStorageContract();
        TransactionReceipt storeTransactionReceipt = webHelpers.loadStorageContractAndCallStoreMethod(contractAddress, tBoxFullPath);
        String storeTransactionHash = storeTransactionReceipt.getTransactionHash();
        System.out.println("Store data transaction: "+storeTransactionHash);
        String storedSchema = webHelpers.loadStorageContractAndCallRetrieveMethod(contractAddress);
        System.out.println("Retrieved schema: "+storedSchema);
    }

}
