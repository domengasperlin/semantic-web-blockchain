import org.apache.jena.dboe.base.file.Location;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModelListener extends StatementListener {
    @Override
    public void addedStatement(Statement s)
    {
        // Identify type of box UPDATE to know if change is abox,rbox,tbox based
        System.out.println( "[Ontology] >> added statement " + s );
    }

    @Override
    public void removedStatement(Statement s) {
        // Identify type of box UPDATE to know if change is abox,rbox,tbox based
        System.out.println( "[Ontology] >> removed statement " + s );
    }
}

public class JenaHelpers {
    private static String datasetLocation = "target/dataset";
    private static Boolean useReasoner;
    private static Boolean isInitialLoad;
    private Dataset dataset;
    private Model model;

    private static final Logger log = Logger.getLogger(JenaHelpers.class.getName());
    public JenaHelpers(ArrayList<String> inputOntologyFiles, Boolean useReasoner, Boolean isInitialLoad) throws Exception {
        this.isInitialLoad = isInitialLoad;
        this.useReasoner = useReasoner;
        log.setLevel(Level.FINE);
        dataset = TDB2Factory.connectDataset(Location.create(datasetLocation));

        dataset.begin(ReadWrite.READ);
        Boolean isDataSetEmpty = dataset.isEmpty();
        dataset.end();

        if (isDataSetEmpty) {
            for (String inputOntologyFileName : inputOntologyFiles) {
                if (inputOntologyFileName != null) {
                    Txn.executeWrite(dataset, () -> RDFDataMgr.read(dataset, inputOntologyFileName));
                }
            }
        }

        dataset.begin(ReadWrite.READ);
        this.model = dataset.getDefaultModel();
        ModelChangedListener modelChangedListener = new ModelListener();
        this.model.register(modelChangedListener);

        if (useReasoner) {
            if (isOntologyConsistent(this.model)) {
                log.info("Loaded ontology is consistent");
            } else {
                log.severe( "Loaded ontology is not consistent, fix it and try again!");
                throw new Exception("Ontology is not consistent");
            }
        }
        dataset.end();
    }

    public Boolean isOntologyConsistent(Model targetModel) {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();

        InfModel infModel = ModelFactory.createInfModel(reasoner, targetModel);

        ValidityReport validityReport = infModel.validate();
        if ( !validityReport.isValid() ) {
            log.fine("Ontology is not consistent");
            Iterator<ValidityReport.Report> iter = validityReport.getReports();
            while ( iter.hasNext() ) {
                ValidityReport.Report report = iter.next();
                log.info(report.toString());
            }
            return false;
        } else {
            log.fine( "Ontology is consistent");
        }
        return true;
    }

    public Boolean executeSPARQL(String SPARQLQueryFileLocation, ArrayList<String> inputOntologyFiles, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        File file = new File(SPARQLQueryFileLocation);
        Boolean executeSelect = false;
        String sparqlQueryString = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sparqlQueryString += line;
            }
            if (sparqlQueryString.toLowerCase().contains("select")) {
                executeSelect = true;
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        if (executeSelect) {
            return executeSPARQLSelectQuery(SPARQLQueryFileLocation);
        } else {
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
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }  finally {
            queryExec.close();
        }
        dataset.end();
        return true;
    }

    private Boolean executeSPARQLUpdateAction(String locationOfSPARQL, String sparqlString, IPFSHelpers ipfsHelpers, ArrayList<String> inputOntologyFiles, EthereumHelpers ethereumHelpers) {
        dataset.begin(ReadWrite.WRITE);
        UpdateAction.parseExecute(sparqlString, this.model);

        if (this.useReasoner) {
            if (!isOntologyConsistent(this.model)) {
                log.severe("Ontology would be no longer consistent if this query is applied to dataset, only consistent changes will be persisted.");
                dataset.abort();
                dataset.end();
                return false;
            }
        }
        uploadChangesToBlockchains(inputOntologyFiles, locationOfSPARQL, ipfsHelpers, ethereumHelpers);
        dataset.commit();
        dataset.end();
        return true;

    }

    public void uploadChangesToBlockchains(ArrayList<String> inputOntologyFiles, String locationOfSparql, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        // If it is initial load then save CIDS of axiom files to the IPFS
        if (isInitialLoad) {
            for (String inputOntologyFile : inputOntologyFiles) {
                String xBoxCID = ipfsHelpers.uploadLocalFileToIPFS(inputOntologyFile).toString();

                try {
                    ethereumHelpers.getContract().addInputOntology(xBoxCID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isInitialLoad = false;
        }

        String sparqlQueryCID = ipfsHelpers.uploadLocalFileToIPFS(locationOfSparql).toString();
        try {
            ethereumHelpers.getContract().addSUPMigration(sparqlQueryCID).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printDatasetToStandardOutput() {
        dataset.begin(ReadWrite.READ);
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE);
        dataset.end();
    }

    public void executeSPARQLMigrationForDBSync(String locationOfSparql) {
        File f = new File(locationOfSparql);
        if(f.exists() && !f.isDirectory()) {
            dataset.begin(ReadWrite.WRITE);
            log.info("[Executing SPARQL migration from query file (.rq)] " + locationOfSparql);
            UpdateAction.readExecute(locationOfSparql, dataset);
            dataset.commit();
        }
        log.fine("Update file doesn't exist on initial DB upload");
    }
}
