import org.apache.jena.dboe.base.file.Location;
import org.apache.jena.query.*;
import org.apache.jena.rdf.listeners.StatementListener;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb2.TDB2;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.update.*;
import org.apache.jena.util.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static String datasetLocation = "target/dataset";
    private static Boolean useReasoner;
    private Dataset dataset;
    private Model model;
    private Model tBoxSchema;
    private Model aBoxFacts;
    private Model rBoxProperties;

    private static final Logger log = Logger.getLogger(JenaHelpers.class.getName());
    public JenaHelpers(String tBoxFileName, String aBoxFileName, String rBoxFileName, Boolean useReasoner) {
        this.useReasoner = useReasoner;
        log.setLevel(Level.FINE);
        FileManager fm = FileManager.get();
        // https://jena.apache.org/documentation/tdb/datasets.html set default graph as union of named graphs, TODO: check this
        TDB2.getContext().set(TDB2.symUnionDefaultGraph, true);
        dataset = TDB2Factory.connectDataset(Location.create(datasetLocation));

        dataset.begin(ReadWrite.READ);
        Boolean containsTBox = dataset.containsNamedModel("tbox");
        Boolean containsABox = dataset.containsNamedModel("abox");
        Boolean containsRBox = dataset.containsNamedModel("rbox");
        dataset.end();

        if (!containsTBox && tBoxFileName != null) {
            dataset.begin(ReadWrite.WRITE);
            fm.readModel(dataset.getNamedModel("tbox"), tBoxFileName);
            dataset.commit();
        }
        if (!containsABox && aBoxFileName != null) {
            dataset.begin(ReadWrite.WRITE);
            fm.readModel(dataset.getNamedModel("abox"), aBoxFileName);
            dataset.commit();
        }
        if (!containsRBox && rBoxFileName != null) {
            dataset.begin(ReadWrite.WRITE);
            fm.readModel(dataset.getNamedModel("rbox"), rBoxFileName);
            dataset.commit();
        }

        dataset.begin(ReadWrite.READ);
        this.tBoxSchema = dataset.getNamedModel("tbox");
        this.aBoxFacts = dataset.getNamedModel("abox");
        this.rBoxProperties = dataset.getNamedModel("rbox");
        this.model = dataset.getUnionModel();

        if (!tBoxFileName.contains("dbpedia")) {
            ModelChangedListener tBoxChangedListener = new TBoxListener();
            this.tBoxSchema.register(tBoxChangedListener);
            ModelChangedListener rBoxChangedListener = new RBoxListener();
            this.rBoxProperties.register(rBoxChangedListener);
            ModelChangedListener aBoxChangedListener = new ABoxListener();
            this.aBoxFacts.register(aBoxChangedListener);
        }

        if (useReasoner) {
            if (isOntologyConsistent(AxiomFileType.ABox, this.aBoxFacts)) {
                log.fine("Ontology is consistent");
            } else {
                log.severe( "Ontology is not consistent!");
            }
        }
        dataset.end();
    }

    public Boolean isOntologyConsistent(AxiomFileType axiomFileType, Model targetModel) {
        if (axiomFileType == AxiomFileType.ABox) {
            Reasoner reasonerTBox = ReasonerRegistry.getOWLReasoner().bindSchema(this.tBoxSchema);

            InfModel infModelTBoxABox = ModelFactory.createInfModel(reasonerTBox, targetModel);

            ValidityReport validityReportTBoxABox = infModelTBoxABox.validate();
            if ( !validityReportTBoxABox.isValid() ) {
                log.fine("Ontology TBoxABox is not consistent");
                Iterator<ValidityReport.Report> iter = validityReportTBoxABox.getReports();
                while ( iter.hasNext() ) {
                    ValidityReport.Report report = iter.next();
                    log.info(report.toString());
                }
                return false;
            } else {
                log.fine( "Ontology TBoxABox is consistent");
            }

            Reasoner reasonerRBox = ReasonerRegistry.getOWLReasoner().bindSchema(this.rBoxProperties);
            InfModel infModelRBoxABox = ModelFactory.createInfModel(reasonerRBox, targetModel);

            ValidityReport validityReportRBoxABox = infModelRBoxABox.validate();
            if ( !validityReportRBoxABox.isValid() ) {
                log.fine("Ontology RBoxABox is not consistent");
                Iterator<ValidityReport.Report> iter = validityReportRBoxABox.getReports();
                while ( iter.hasNext() ) {
                    ValidityReport.Report report = iter.next();
                    log.info(report.toString());
                }
                return false;
            } else {
                log.fine("Ontology RBoxABox is consistent");
            }
            return true;
        }


        Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(targetModel);
        InfModel infModelTBoxRBox = ModelFactory.createInfModel(reasoner, this.aBoxFacts);


        ValidityReport validityReport1 = infModelTBoxRBox.validate();
        if ( !validityReport1.isValid() ) {
            log.warning("Ontology "+axiomFileType+" is not consistent");
            Iterator<ValidityReport.Report> iter = validityReport1.getReports();
            while ( iter.hasNext() ) {
                ValidityReport.Report report = iter.next();
                log.info(report.toString());
            }
            return false;
        } else {
            log.fine("Ontology "+axiomFileType+" is consistent");
            if (axiomFileType == AxiomFileType.TBox) {
                return true;
            }
            if (axiomFileType == AxiomFileType.RBox) {
                return true;
            }
            log.severe("Unexpected state");
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
            return executeSPARQLUpdateAction(SPARQLQueryFileLocation, ipfsHelpers, axiomFileFullPath, ethereumHelpers);
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
    private Boolean executeSPARQLUpdateAction(String locationOfSPARQL, IPFSHelpers ipfsHelpers, String axiomFileFullPath, EthereumHelpers ethereumHelpers) {
        AxiomFileType axiomFileType = null;
        Model toUpdate = null;
        if (axiomFileFullPath.toLowerCase().contains("tbox")) {
            axiomFileType = AxiomFileType.TBox;
            toUpdate = this.tBoxSchema;
        }
        if (axiomFileFullPath.toLowerCase().contains("abox")) {
            axiomFileType = AxiomFileType.ABox;
            toUpdate = this.aBoxFacts;
        }
        if (axiomFileFullPath.toLowerCase().contains("rbox")) {
            axiomFileType = AxiomFileType.RBox;
            toUpdate = this.rBoxProperties;
        }
        if (axiomFileType == null) {
            log.severe("No axiomFileType chosen!");
        }
        String namedModelName = axiomFileType.name().toLowerCase();

        dataset.begin(ReadWrite.WRITE);
        UpdateAction.readExecute(locationOfSPARQL, toUpdate);

        if (this.useReasoner) {
            if (isOntologyConsistent(axiomFileType, toUpdate)) {
                log.fine("Changes were executed");
                uploadChangesToBlockchains(axiomFileFullPath, axiomFileType, toUpdate, ipfsHelpers, ethereumHelpers);
                dataset.commit();
                return true;
            } else {
                log.severe("Changes were not made because ontology would be no longer consistent");
                dataset.abort();
                dataset.end();
                return false;
            }
        }
        dataset.commit();
        log.fine("Changes were commited");

        dataset.begin(ReadWrite.READ);
        //toUpdate = dataset.getNamedModel(namedModelName);
        uploadChangesToBlockchains(axiomFileFullPath, axiomFileType, toUpdate, ipfsHelpers, ethereumHelpers);
        dataset.end();
        return true;

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
        dataset.begin(ReadWrite.READ);
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE);
        dataset.end();
    }
}
