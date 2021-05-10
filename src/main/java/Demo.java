import contracts.Storage;
import io.ipfs.api.IPFS;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

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

//    private static String ethereumNodeAddress = "https://rinkeby.infura.io/v3/18b69a4069f7455ba4486efd1f5530b1";
    private static String ethereumNodeAddress = "http://localhost:7545";

    private static String ethereumWalletLocation = ethereumFolder+"/wallet--c261cf8e7283030d0b6fa672b5d15819c8d99aa3";
    private static String ethereumWalletPassword = "demo";

    // TODO: handle rBox in model
    public static void main(String[] args) {
        IPFSHelpers ipfs = new IPFSHelpers(new IPFS(IPFSNodeAddress));
        String aBoxCID;
        String tBoxCID;

        // Get hashes from Ethereum for aBox, tBox, rBox
        OntologyHelpers ontologyHelpers = localDataSchemaSplitAndUploadToIPFS(ipfs);
        aBoxCID = ontologyHelpers.getaBoxHash();
        tBoxCID = ontologyHelpers.gettBoxHash();
        System.out.println("[IPFS upload] aBox CID: "+aBoxCID + " tBox CID: "+tBoxCID);
        String contractAddress = storeDataOnEthereumAndGetContractAddress(tBoxCID, aBoxCID);
        String[] ontology = retrieveIPFSHashesForSchemaAndDataFromEthereum(contractAddress);
        tBoxCID = ontology[0];
        aBoxCID = ontology[1];

        System.out.println("[ETH retrieve] aBox CID: "+aBoxCID + " tBox CID: "+tBoxCID);
        System.out.println("[IPFS download and write to files] ");
        downloadDataDownstream(ipfs, aBoxCID, tBoxCID);
        System.out.println("[Load files to triplestore and run SPARQL] ");
        showcaseJenaSPARQLOperations();
    }


    public static String storeDataOnEthereumAndGetContractAddress(String tBoxCID, String aBoxCID) {

        Credentials credentials = null;

        if (ethereumNodeAddress.contains("localhost")) {
            String  privateKey= "1d0c3d2b4ed66c966bc477ed68ece830a19b66ed06362cf22626d6e16e761dab"; // Add a private key here
            credentials = Credentials.create(privateKey);
        } else {
            try {
                credentials = WalletUtils.loadCredentials(ethereumWalletPassword, ethereumWalletLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        WebHelpers webHelpers = new WebHelpers(ethereumNodeAddress, credentials);
        String contractAddress = webHelpers.deployStorageContract();
        TransactionReceipt storeTransactionReceipt = webHelpers.loadStorageContractAndCallStoreMethod(contractAddress, tBoxCID, aBoxCID);
        String storeTransactionHash = storeTransactionReceipt.getTransactionHash();
        System.out.println("Store data transaction: "+storeTransactionHash);
        return contractAddress;
    }

    public static String[] retrieveIPFSHashesForSchemaAndDataFromEthereum(String contractAddress) {
        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(ethereumWalletPassword, ethereumWalletLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebHelpers webHelpers = new WebHelpers(ethereumNodeAddress, credentials);
        String[] contractCIDs = webHelpers.loadStorageContractAndCallRetrieveMethod(contractAddress);
        return contractCIDs;
    }

    public static OntologyHelpers localDataSchemaSplitAndUploadToIPFS(IPFSHelpers ipfsHelpers) {
        OntologyHelpers ontologyHelpers = new OntologyHelpers(inputOntologyFullPath);
        ontologyHelpers.saveABoxAxiomsToFile(aBoxFullPath);
        ontologyHelpers.setaBoxHash(ipfsHelpers.uploadLocalFile(aBoxFullPath).toString());
        ontologyHelpers.saveTBoxAxiomsToFile(tBoxFullPath);
        ontologyHelpers.settBoxHash(ipfsHelpers.uploadLocalFile(tBoxFullPath).toString());
        return ontologyHelpers;
    }

    public static void downloadDataDownstream(IPFSHelpers ipfsHelpers, String aBoxFileHash, String tBoxFileHash) {
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(aBoxFileHash, aBoxFullPath);
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(tBoxFileHash, tBoxFullPath);
    }

    public static void showcaseJenaSPARQLOperations() {
        JenaHelpers jenaHelpers = new JenaHelpers(tBoxFullPath, aBoxFullPath);
//        JenaHelpers jenaHelpers = new JenaHelpers(inputDBPediaTBoxFullPath, inputDBPediaABoxFullPath);
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

}
