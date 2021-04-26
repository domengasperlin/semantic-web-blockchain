import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class Demo {

    public static void main(String[] args) {

        OntologyHelpers ontologyHelpers = new OntologyHelpers("rdf-sparql/input/input_ontology.owl");
        ontologyHelpers.saveABoxAxiomsToFile();
        ontologyHelpers.saveRBoxAxiomsToFile();
        ontologyHelpers.saveTBoxAxiomsToFile();

        String ontologyOutputName = "rdf-sparql/output/output_ontology.ttl";
        ontologyHelpers.saveOntologyToFile(ontologyOutputName, ontologyHelpers.getOntology(), new TurtleDocumentFormat());

        showcaseSPARQLOperations(ontologyOutputName);
        demoIPFS();
        demoEthereumWeb3();
    }

    public static void showcaseSPARQLOperations(String ontologyOutputName) {
        JenaHelpers jenaHelpers = new JenaHelpers(ontologyOutputName);
        System.out.println("SELECT --------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLQuery("select distinct ?Concept where {[] a ?Concept} LIMIT 10");
        System.out.println("INSERT --------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLInsert();
        jenaHelpers.printDatasetToStandardOutput();
        System.out.println("DELETE --------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLDelete();
        jenaHelpers.printDatasetToStandardOutput();
        System.out.println("UPDATE--------------------------------------------------------------------------------------------");
        jenaHelpers.executeSPARQLUpdate();
        jenaHelpers.printDatasetToStandardOutput();
    }

    public static void demoIPFS() {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        IPFSHelpers ipfsHelpers = new IPFSHelpers(ipfs);

        // Upload file to IPFS
        Multihash aBoxFileHash = ipfsHelpers.uploadLocalFile("rdf-sparql/output/abox-axioms.ttl");
        Multihash rBoxFileHash = ipfsHelpers.uploadLocalFile("rdf-sparql/output/rbox-axioms.ttl");
        Multihash tBoxFileHash = ipfsHelpers.uploadLocalFile("rdf-sparql/output/tbox-axioms.nt");
        System.out.println("tBox hash:"+tBoxFileHash);

        // Get file contents from IPFS
        String aBoxContents = ipfsHelpers.retrieveFileContents(aBoxFileHash.toString());
        String rBoxContents = ipfsHelpers.retrieveFileContents(rBoxFileHash.toString());
        String tBoxContents = ipfsHelpers.retrieveFileContents(tBoxFileHash.toString());

        // Save file contents from IPFS to local file system
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(aBoxFileHash.toString(), "ipfs-files/output/abox-from-ipfs.ttl");
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(rBoxFileHash.toString(), "ipfs-files/output/rbox-from-ipfs.ttl");
        ipfsHelpers.retrieveFileAndSaveItToLocalSystem(tBoxFileHash.toString(), "ipfs-files/output/tbox-from-ipfs.ttl");
    }

    public static void demoEthereumWeb3() {

//        try {
//            TransactionReceipt transactionReceipt = Transfer.sendFunds(web3, credentials, "c261cf8e7283030d0b6fa672b5d15819c8d99aa3", BigDecimal.valueOf(5), Convert.Unit.ETHER).send();
//        } catch (Exception e) {
//            String error = e.getMessage();
//        }

        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials("demo", "ethereum/wallet--c261cf8e7283030d0b6fa672b5d15819c8d99aa3");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String nodeAddress = "https://rinkeby.infura.io/v3/18b69a4069f7455ba4486efd1f5530b1";
        Web3j web3 = Web3j.build(new HttpService(nodeAddress));  // defaults to http://localhost:8545/

        WebHelpers webHelpers = new WebHelpers(web3, credentials);
        webHelpers.deployStorageContract();
        webHelpers.loadStorageContractAndCallRetrieveMethod( "0xB38432E82872312a1Fb43cB731C10D27B05Fd328");
    }

}
