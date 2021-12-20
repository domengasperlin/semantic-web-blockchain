import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jena.dboe.base.file.Location;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.listeners.StatementListener;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.update.UpdateAction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModelListener extends StatementListener {
    @Override
    public void addedStatement(Statement s) {
        // Identify type of box UPDATE to know if change is abox,rbox,tbox based
        System.out.println("[Ontology] >> added statement " + s);
    }

    @Override
    public void removedStatement(Statement s) {
        // Identify type of box UPDATE to know if change is abox,rbox,tbox based
        System.out.println("[Ontology] >> removed statement " + s);
    }
}

public class JenaHelpers {
    private static String datasetLocation = "target/dataset";
    private static Boolean useReasoner;
    private Dataset dataset;
    private Model model;

    private static String SPARQLMigrationsNamespace = "http://www.semanticweb.org/domen/sparql/migrations";
    private static String ethContractNamespace = "http://www.semanticweb.org/domen/ethereum/contract";
    private static Property hasAddress = ResourceFactory.createProperty(ethContractNamespace, "hasAddress");


    private static final Logger log = Logger.getLogger(JenaHelpers.class.getName());
    private static Timer timer = Timer.getInstance();

    public JenaHelpers(Boolean useReasoner) {
        this.useReasoner = useReasoner;
        log.setLevel(Level.FINE);
        dataset = TDB2Factory.connectDataset(Location.create(datasetLocation));

        dataset.begin(ReadWrite.READ);
        this.model = dataset.getDefaultModel();
        // Model difference = this.model.difference();
//        ModelChangedListener modelChangedListener = new ModelListener();
//        this.model.register(modelChangedListener);
        dataset.end();
    }

    public void loadInputOntologiesIFEmptyDatasetIFReasonerConsistencyCheckInfModelAdd(ArrayList<String> inputOntologyFiles) throws Exception {
        dataset.begin(ReadWrite.READ);
        boolean isModelEmpty = this.model.isEmpty();
        dataset.end();

        if (isModelEmpty) {
            for (String inputOntologyFileName : inputOntologyFiles) {
                if (inputOntologyFileName != null) {
                    String timerLoadOntology = timer.start("J. Nalozi ontologijo "+inputOntologyFileName+" v Jena bazo");
                    Txn.executeWrite(dataset, () -> RDFDataMgr.read(dataset, inputOntologyFileName));
                    timer.stop(timerLoadOntology);
                }
            }
        }

        if (useReasoner) {
            dataset.begin(ReadWrite.WRITE);
            Boolean isConsistent = isOntologyConsistent(this.model, useReasoner);
            if (isConsistent) {
                log.info("Loaded ontology is consistent");
            } else {
                log.severe("Loaded ontology is not consistent, fix it and try again!");
                throw new Exception("Ontology is not consistent");
            }
        }
    }

    public void purgeJenaModel() {
        String timerPurge = timer.start("P. Brisanje Jena baze");
        dataset.begin(ReadWrite.WRITE);
        this.model.removeAll();
        dataset.commit();
        dataset.end();
        timer.stop(timerPurge);
    }

    public Boolean isOntologyConsistent(Model targetModel, Boolean addInferenceModelFromReasoner) {
        OntModelSpec reasonerSpec = OntModelSpec.RDFS_MEM;
        String timerWouldOntologyBeConsistent = timer.start("J. Pridobi tranzitivne zakonitosti in preveri konsistenostnost ontologij "+reasonerSpec.getLanguage());
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        OntModelSpec ontModelSpec = new OntModelSpec(reasonerSpec);
        ontModelSpec.setReasoner(reasoner);
        InfModel infModel = ModelFactory.createOntologyModel(ontModelSpec, targetModel);
        ValidityReport validityReport = infModel.validate();
        timer.stop(timerWouldOntologyBeConsistent);
        if (!validityReport.isValid()) {
            log.fine("Ontology is not consistent");
            Iterator<ValidityReport.Report> iter = validityReport.getReports();
            while (iter.hasNext()) {
                ValidityReport.Report report = iter.next();
                log.info(report.toString());
            }
            log.severe("Ontology would be no longer consistent if this query is applied to dataset, only consistent changes will be persisted.");
            dataset.abort();
            dataset.end();
            return false;
        } else {
            log.fine("Ontology is consistent");
            dataset.commit();
            dataset.end();

            if (addInferenceModelFromReasoner) {
                String timerAddInferredAxioms = timer.start("J. Dodaj tranzitivne zakonitosti v Jena bazo");
                dataset.begin(ReadWrite.WRITE);
                this.model.add(infModel);
                dataset.commit();
                dataset.end();
                timer.stop(timerAddInferredAxioms);
            }
        }
        return true;
    }

    public Boolean executeSPARQL(String SPARQLQueryFileLocation, ArrayList<String> inputOntologyFiles, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) throws IOException {
        File file = new File(SPARQLQueryFileLocation);
        Boolean executeSelect = false;
        String sparqlQueryString = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sparqlQueryString += line;
                if (scanner.hasNextLine()) sparqlQueryString += "\n";
            }
            if (sparqlQueryString.toLowerCase().contains("select")) {
                executeSelect = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (executeSelect) {
            return executeSPARQLSelectQuery(SPARQLQueryFileLocation);
        } else {

            String migration = "# MIGRACIJA" + '\n';
            if (!sparqlQueryString.contains(migration)) {
                // Adds metadata to SPARQL migrations
                String dateString = "# DATUM: " + new Date() + '\n';
                String saltString = "# SOL: " + RandomStringUtils.randomAlphanumeric(30) + '\n';
                String migrationFileContents = migration + dateString + saltString + sparqlQueryString;
                Files.write(Paths.get(SPARQLQueryFileLocation), migrationFileContents.getBytes(StandardCharsets.UTF_8));
            } else {
                // Skipping SPARQL query because it has already been executed on the database
                log.warning("Migration was already executed");
                return false;
            }

            return executeSPARQLUpdateAction(SPARQLQueryFileLocation, sparqlQueryString, ipfsHelpers, inputOntologyFiles, ethereumHelpers);
        }

    }

    private Boolean executeSPARQLSelectQuery(String SPARQLSelectLocation) {
        dataset.begin(ReadWrite.READ);
        Query query = QueryFactory.read(SPARQLSelectLocation);

        QueryExecution queryExec = QueryExecutionFactory.create(query, this.model);
        try {
            ResultSet results = queryExec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                log.info(soln.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            queryExec.close();
        }
        dataset.end();
        return true;
    }

    private Boolean executeSPARQLUpdateAction(String locationOfSPARQL, String sparqlString, IPFSHelpers ipfsHelpers, ArrayList<String> inputOntologyFiles, EthereumHelpers ethereumHelpers) {
        dataset.begin(ReadWrite.WRITE);
        String timerRdfExecuteSPARQL = timer.start("4.x Izvedi SPARQL posodobitev nad bazo RDF");
        UpdateAction.parseExecute(sparqlString, this.model);

        if (this.useReasoner) {
            // TODO: handle duplicate inferences that would be added to the model if we pass true here. UPDATE?
            Boolean isConsistent = isOntologyConsistent(this.model, false);
            if (!isConsistent) {
                return false;
            }
        } else {
            dataset.commit();
            dataset.end();
        }
        timer.stop(timerRdfExecuteSPARQL);
        uploadChangesToBlockchains(locationOfSPARQL, ipfsHelpers, ethereumHelpers);
        return true;

    }

    public void saveEthContractAddressToRDFDatabase(String ethereumContractAddress) {
        dataset.begin(ReadWrite.WRITE);
        Resource ethereumContract = this.model.createResource(ethContractNamespace);
        ethereumContract.addProperty(hasAddress, ethereumContractAddress);
        dataset.commit();
        dataset.end();
    }

    public void saveRanSparqlMigrationToRDFDatabase(String migrationCID) {
        dataset.begin(ReadWrite.WRITE);
        Resource SPARQLMigrationsResource = this.model.createResource(SPARQLMigrationsNamespace);
        Bag migrationsBag = model.getBag(SPARQLMigrationsResource);
        migrationsBag.add(migrationCID);
        dataset.commit();
        dataset.end();

        dataset.begin(ReadWrite.READ);
        Iterator bagStatements = SPARQLMigrationsResource.listProperties();
        ArrayList<String> migrationCIDsInBag = new ArrayList<>();
        while (bagStatements.hasNext()) {
            Statement next = (Statement) bagStatements.next();
            migrationCIDsInBag.add(next.getLiteral().toString());
        }
        log.info("Migrations already ran will be skipped: " + migrationCIDsInBag);
        dataset.end();
    }

    public Boolean hasMigrationBeenRanAlready(String migrationCID) {
        dataset.begin(ReadWrite.READ);
        Bag migrationsBag = model.getBag(SPARQLMigrationsNamespace);
        Boolean migrationRan = migrationsBag.contains(migrationCID);
        dataset.end();
        return migrationRan;
    }

    public Optional<String> retrieveEthContractAddressFromRDFDatabase() {
        String ethereumContractAddress = null;
        dataset.begin(ReadWrite.READ);
        Statement property = this.model.getResource(ethContractNamespace).getProperty(hasAddress);
        dataset.end();
        if (property != null) {
            ethereumContractAddress =  property.getLiteral().toString();
        }
        return Optional.ofNullable(ethereumContractAddress);
    }

    public void uploadInputOntologyFilesToBlockchains(ArrayList<String> inputOntologyFiles, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        int i = 0;
        String timerInputOntologyOnBlockchain = timer.start("2. Vhodna ontologije na verige blokov");
        for (String inputOntologyFile : inputOntologyFiles) {
            String timerInputOntologyToIPFS = timer.start("2."+i+" Objava vhodne ontologije na IPFS");
            String xBoxCID = ipfsHelpers.uploadLocalFileToIPFS(inputOntologyFile).toString();
            timer.stop(timerInputOntologyToIPFS);
            log.info("[IPFS upload content identifier] " + xBoxCID);
            try {
                String timerPostCIDToETH = timer.start("2."+i+" Objava CID-a vhodne ontologije na ETH");
                TransactionReceipt transaction = ethereumHelpers.getContract().dodajVhodnoOntologijo(xBoxCID).send();
                timer.stop(timerPostCIDToETH);
                log.info("[ETH] transaction: " + transaction.getTransactionHash());
                Timer.addDataToCSV("2."+i+" Objava CID-a vhodne ontologije na ETH", transaction.getGasUsed().toString(), "plin");
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        timer.stop(timerInputOntologyOnBlockchain);
    }

    public void uploadChangesToBlockchains(String locationOfSparql, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        String timerUploadMigrationToIPFS = timer.start("4.x Nalozi migracijo na IPFS");
        String sparqlQueryCID = ipfsHelpers.uploadLocalFileToIPFS(locationOfSparql).toString();
        timer.stop(timerUploadMigrationToIPFS);
        if (hasMigrationBeenRanAlready(sparqlQueryCID)) {
            return;
        }
        try {
            String addMigrationCIDToETH = timer.start("4.x Nalozi CID migracije na ETH");
            ethereumHelpers.getContract().dodajMigracijo(sparqlQueryCID).send();
            timer.stop(addMigrationCIDToETH);
            saveRanSparqlMigrationToRDFDatabase(sparqlQueryCID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printDatasetToStandardOutput() {
        dataset.begin(ReadWrite.READ);
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE);
        dataset.end();
    }

    public void executeSPARQLMigrationForDBSync(String sparqlMigrationCID, String locationOfSparql) {
        File f = new File(locationOfSparql);
        if (f.exists() && !f.isDirectory()) {
            if (hasMigrationBeenRanAlready(sparqlMigrationCID)) return;
            dataset.begin(ReadWrite.WRITE);
            log.info("[Executing SPARQL migration from query file (.rq)] " + locationOfSparql);
            UpdateAction.readExecute(locationOfSparql, dataset);
            dataset.commit();
            saveRanSparqlMigrationToRDFDatabase(sparqlMigrationCID);
        }
        log.fine("Update file doesn't exist on initial DB upload");
    }
}
