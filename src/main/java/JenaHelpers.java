import org.apache.jena.query.*;
import org.apache.jena.rdf.listeners.StatementListener;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Scanner;

enum AxiomFileType {
    TBox,
    ABox,
    RBox
}

class TBoxListener extends StatementListener {
    @Override
    public void addedStatement(Statement s) {
        System.out.println( "[TBox] >> added statement " + s );
    }

    @Override
    public void removedStatement(Statement s) {
        System.out.println( "[TBox] >> removed statement " + s );
    }
}

class RBoxListener extends StatementListener {
    @Override
    public void addedStatement(Statement s) {
        System.out.println( "[RBox] >> added statement " + s );
    }

    @Override
    public void removedStatement(Statement s) {
        System.out.println( "[RBox] >> removed statement " + s );
    }
}

class ABoxListener extends StatementListener {
    @Override
    public void addedStatement(Statement s) {
        System.out.println( "[ABox] >> added statement " + s );
    }

    @Override
    public void removedStatement(Statement s) {
        System.out.println( "[ABox] >> removed statement " + s );
    }
}

public class JenaHelpers {
    private Model model;
    private Model tBoxSchema;
    private Model aBoxFacts;
    private Model rBoxProperties;
    private static String datasetLocation = "target/dataset";

    private static final Logger log = LoggerFactory.getLogger(JenaHelpers.class);
    public JenaHelpers(String tBoxFileName, String aBoxFileName, String rBoxFileName) {
        // https://jena.apache.org/documentation/tdb/datasets.html set default graph as union of named graphs, TODO: check this
        TDB.getContext().set(TDB.symUnionDefaultGraph, true);
        FileManager fm = FileManager.get();

        Dataset dataset = TDBFactory.createDataset(datasetLocation);
        dataset.begin(ReadWrite.WRITE);
        if (!dataset.containsNamedModel("tbox")) {
            fm.readModel(dataset.getNamedModel("tbox"), tBoxFileName);
        }
        if (!dataset.containsNamedModel("abox")) {
            fm.readModel(dataset.getNamedModel("abox"), aBoxFileName);
        }
        if (!dataset.containsNamedModel("rbox")) {
            fm.readModel(dataset.getNamedModel("rbox"), rBoxFileName);
        }
        this.tBoxSchema = dataset.getNamedModel("tbox");
        ModelChangedListener tBoxChangedListener = new TBoxListener();
        this.tBoxSchema.register(tBoxChangedListener);

        this.rBoxProperties = dataset.getNamedModel("rbox");
        ModelChangedListener rBoxChangedListener = new RBoxListener();
        this.rBoxProperties.register(rBoxChangedListener);

        this.aBoxFacts = dataset.getNamedModel("abox");
        ModelChangedListener aBoxChangedListener = new ABoxListener();
        this.aBoxFacts.register(aBoxChangedListener);

        this.model = dataset.getNamedModel("urn:x-arq:UnionGraph");

        if (isOntologyConsistent(AxiomFileType.ABox, this.aBoxFacts)) {
            dataset.commit();
            dataset.end();
        } else {
            dataset.abort();
            log.error("Ontology was not committed because it is not consistent!");
        }
    }

    public Boolean isOntologyConsistent(AxiomFileType axiomFileType, Model targetModel) {
        if (axiomFileType == AxiomFileType.ABox) {
            Reasoner reasonerTBox = ReasonerRegistry.getOWLReasoner().bindSchema(this.tBoxSchema);

            InfModel infModelTBoxABox = ModelFactory.createInfModel(reasonerTBox, targetModel);

            ValidityReport validityReportTBoxABox = infModelTBoxABox.validate();
            if ( !validityReportTBoxABox.isValid() ) {
                log.debug("Ontology TBoxABox is not consistent");
                Iterator<ValidityReport.Report> iter = validityReportTBoxABox.getReports();
                while ( iter.hasNext() ) {
                    ValidityReport.Report report = iter.next();
                    log.info(report.toString());
                }
                return false;
            } else {
                log.debug("Ontology TBoxABox is consistent");
            }

            Reasoner reasonerRBox = ReasonerRegistry.getOWLReasoner().bindSchema(this.rBoxProperties);
            InfModel infModelRBoxABox = ModelFactory.createInfModel(reasonerRBox, targetModel);

            ValidityReport validityReportRBoxABox = infModelRBoxABox.validate();
            if ( !validityReportRBoxABox.isValid() ) {
                log.debug("Ontology RBoxABox is not consistent");
                Iterator<ValidityReport.Report> iter = validityReportRBoxABox.getReports();
                while ( iter.hasNext() ) {
                    ValidityReport.Report report = iter.next();
                    log.info(report.toString());
                }
                return false;
            } else {
                log.debug("Ontology RBoxABox is consistent");
            }
            this.aBoxFacts = targetModel;
            return true;
        }


        Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(targetModel);
        InfModel infModelTBoxRBox = ModelFactory.createInfModel(reasoner, this.aBoxFacts);


        ValidityReport validityReport1 = infModelTBoxRBox.validate();
        if ( !validityReport1.isValid() ) {
            log.warn("Ontology "+axiomFileType+" is not consistent");
            Iterator<ValidityReport.Report> iter = validityReport1.getReports();
            while ( iter.hasNext() ) {
                ValidityReport.Report report = iter.next();
                log.info(report.toString());
            }
            return false;
        } else {
            log.debug("Ontology "+axiomFileType+" is consistent");
            if (axiomFileType == AxiomFileType.TBox) {
                this.tBoxSchema = targetModel;
                return true;
            }
            if (axiomFileType == AxiomFileType.RBox) {
                this.rBoxProperties = targetModel;
                return true;
            }
            log.error("Unexpected state");
            return false;
        }

    }

    public Boolean executeSPARQL(String SPARQLQueryFileLocation, String axiomFileFullPath, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        File file = new File(SPARQLQueryFileLocation);
        Boolean executeSelect = false;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.toLowerCase().contains("select")) {
                    executeSelect = true;
                    break;
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        if (executeSelect) {
            return executeSPARQLSelectQuery(SPARQLQueryFileLocation);
        } else {
            Model targetModel = null;
            AxiomFileType axiomFileType = null;
            if (axiomFileFullPath.contains("tbox")) {
                targetModel = this.tBoxSchema;
                axiomFileType = AxiomFileType.TBox;
            }
            if (axiomFileFullPath.contains("abox")) {
                targetModel = this.aBoxFacts;
                axiomFileType = AxiomFileType.ABox;
            }
            if (axiomFileFullPath.contains("rbox")) {
                targetModel = this.rBoxProperties;
                axiomFileType = AxiomFileType.RBox;
            }
            if (targetModel == null) {
                log.error("No target model chosen!");
            }
            return executeSPARQLUpdateAction(SPARQLQueryFileLocation, ipfsHelpers, axiomFileFullPath, axiomFileType, ethereumHelpers, targetModel);
        }

    }

    private Boolean executeSPARQLSelectQuery(String SPARQLSelectLocation) {
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
        return true;
    }

    // TODO: decide how you are going to split operations on abox, tbox. Can't do updates on UnionGraph
    private Boolean executeSPARQLUpdateAction(String locationOfSPARQL, IPFSHelpers ipfsHelpers, String axiomFileFullPath, AxiomFileType axiomFileType, EthereumHelpers ethereumHelpers, Model targetModel) {

        UpdateRequest request = UpdateFactory.create() ;
        UpdateAction.readExecute(locationOfSPARQL, targetModel) ;
        UpdateAction.execute(request, targetModel);

        if (isOntologyConsistent(axiomFileType, targetModel)) {
            log.debug("Changes were executed");
            uploadChangesToBlockchains(axiomFileFullPath, axiomFileType, targetModel, ipfsHelpers, ethereumHelpers);
            return true;
        } else {
            log.error("Changes were not made because ontology would be no longer consistent");
        }
        return false;

    }

    public void uploadChangesToBlockchains(String axiomFileFullPath, AxiomFileType axiomFileType, Model targetModel, IPFSHelpers ipfsHelpers, EthereumHelpers ethereumHelpers) {
        try {
            RDFDataMgr.write(new FileOutputStream(axiomFileFullPath), targetModel, RDFFormat.TURTLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String xBoxCID = ipfsHelpers.uploadLocalFileToIPFS(axiomFileFullPath).toString();

        if (axiomFileType == AxiomFileType.TBox) {
            ethereumHelpers.updateTBoxInContract(xBoxCID);
        }
        if (axiomFileType == AxiomFileType.ABox) {
            ethereumHelpers.updateABoxInContract(xBoxCID);
        }
        if (axiomFileType == AxiomFileType.RBox) {
            ethereumHelpers.updateRBoxInContract(xBoxCID);
        }

    }

    public void printDatasetToStandardOutput() {
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE) ;
    }
}
