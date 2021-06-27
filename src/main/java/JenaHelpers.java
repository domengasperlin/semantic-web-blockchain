import org.apache.jena.dboe.base.file.Location;
import org.apache.jena.query.*;
import org.apache.jena.rdf.listeners.StatementListener;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelChangedListener;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.update.UpdateAction;
import java.io.File;
import java.io.FileNotFoundException;
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
    public JenaHelpers(String inputOntologyFileName, Boolean useReasoner, Boolean isInitialLoad) {
        this.isInitialLoad = isInitialLoad;
        this.useReasoner = useReasoner;
        log.setLevel(Level.FINE);
        dataset = TDB2Factory.connectDataset(Location.create(datasetLocation));

        dataset.begin(ReadWrite.READ);
        Boolean isDataSetEmpty = dataset.isEmpty();
        dataset.end();

        if (inputOntologyFileName != null && isDataSetEmpty) {
            Txn.executeWrite(dataset, () -> {
                RDFDataMgr.read(dataset, inputOntologyFileName) ;
            });
        }

        dataset.begin(ReadWrite.READ);
        this.model = dataset.getDefaultModel();
        ModelChangedListener modelChangedListener = new ModelListener();
        this.model.register(modelChangedListener);

        if (useReasoner) {
            // TODO: implement reasoner
//            if (isOntologyConsistent(AxiomFileType.ABox, this.aBoxFacts)) {
//                log.fine("Ontology is consistent");
//            } else {
//                log.severe( "Ontology is not consistent!");
//            }
        }
        dataset.end();
    }

//    public Boolean isOntologyConsistent(AxiomFileType axiomFileType, Model targetModel) {
//        if (axiomFileType == AxiomFileType.ABox) {
//            Reasoner reasonerTBox = ReasonerRegistry.getOWLReasoner().bindSchema(this.tBoxSchema);
//
//            InfModel infModelTBoxABox = ModelFactory.createInfModel(reasonerTBox, targetModel);
//
//            ValidityReport validityReportTBoxABox = infModelTBoxABox.validate();
//            if ( !validityReportTBoxABox.isValid() ) {
//                log.fine("Ontology TBoxABox is not consistent");
//                Iterator<ValidityReport.Report> iter = validityReportTBoxABox.getReports();
//                while ( iter.hasNext() ) {
//                    ValidityReport.Report report = iter.next();
//                    log.info(report.toString());
//                }
//                return false;
//            } else {
//                log.fine( "Ontology TBoxABox is consistent");
//            }
//
//            Reasoner reasonerRBox = ReasonerRegistry.getOWLReasoner().bindSchema(this.rBoxProperties);
//            InfModel infModelRBoxABox = ModelFactory.createInfModel(reasonerRBox, targetModel);
//
//            ValidityReport validityReportRBoxABox = infModelRBoxABox.validate();
//            if ( !validityReportRBoxABox.isValid() ) {
//                log.fine("Ontology RBoxABox is not consistent");
//                Iterator<ValidityReport.Report> iter = validityReportRBoxABox.getReports();
//                while ( iter.hasNext() ) {
//                    ValidityReport.Report report = iter.next();
//                    log.info(report.toString());
//                }
//                return false;
//            } else {
//                log.fine("Ontology RBoxABox is consistent");
//            }
//            return true;
//        }
//
//
//        Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(targetModel);
//        InfModel infModelTBoxRBox = ModelFactory.createInfModel(reasoner, this.aBoxFacts);
//
//
//        ValidityReport validityReport1 = infModelTBoxRBox.validate();
//        if ( !validityReport1.isValid() ) {
//            log.warning("Ontology "+axiomFileType+" is not consistent");
//            Iterator<ValidityReport.Report> iter = validityReport1.getReports();
//            while ( iter.hasNext() ) {
//                ValidityReport.Report report = iter.next();
//                log.info(report.toString());
//            }
//            return false;
//        } else {
//            log.fine("Ontology "+axiomFileType+" is consistent");
//            if (axiomFileType == AxiomFileType.TBox) {
//                return true;
//            }
//            if (axiomFileType == AxiomFileType.RBox) {
//                return true;
//            }
//            log.severe("Unexpected state");
//            return false;
//        }
//
//    }

    public Boolean executeSPARQL(String SPARQLQueryFileLocation, String axiomFileFullPath, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
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
            return executeSPARQLUpdateAction(SPARQLQueryFileLocation, sparqlQueryString, ipfsHelpers, axiomFileFullPath, ethereumHelpers);
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

    // TODO: decide how you are going to split operations on abox, tbox. Can't do updates on UnionGraph
    private Boolean executeSPARQLUpdateAction(String locationOfSPARQL, String sparqlString, IPFSHelpers ipfsHelpers, String axiomFileFullPath, EthereumHelpers ethereumHelpers) {
        dataset.begin(ReadWrite.WRITE);
        UpdateAction.parseExecute(sparqlString, this.model);
        dataset.commit();

        dataset.begin(ReadWrite.READ);
//        if (this.useReasoner) {
//            if (isOntologyConsistent(axiomFileType, toUpdate)) {
//                uploadChangesToBlockchains(axiomFileFullPath, locationOfSPARQL, axiomFileType, toUpdate, ipfsHelpers, ethereumHelpers);
//                dataset.end();
//                return true;
//            } else {
//                log.severe("Ontology is no longer consistent");
//                dataset.end();
//                return false;
//            }
//        }

        uploadChangesToBlockchains(axiomFileFullPath, locationOfSPARQL, this.model, ipfsHelpers, ethereumHelpers);
        dataset.end();
        return true;

    }

    public void uploadChangesToBlockchains(String axiomFileFullPath, String locationOfSparql, Model targetModel, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        // If it is initial load then save CIDS of axiom files to the IPFS
        if (isInitialLoad) {
            String xBoxCID = ipfsHelpers.uploadLocalFileToIPFS(axiomFileFullPath).toString();

            try {
                ethereumHelpers.getContract().setInitialOntology(xBoxCID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isInitialLoad = false;
        }

        String sparqlQueryCID = ipfsHelpers.uploadLocalFileToIPFS(locationOfSparql).toString();
        try {
            ethereumHelpers.getContract().setSparqlUpdate(sparqlQueryCID).send();
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
