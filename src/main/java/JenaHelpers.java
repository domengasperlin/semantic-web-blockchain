import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
import java.util.Iterator;
import java.util.Scanner;

public class JenaHelpers {
    private Model model;
    private Model tBoxSchema;
    private Model aBoxFacts;
    private static String datasetLocation = "target/dataset";

    private static final Logger log = LoggerFactory.getLogger(JenaHelpers.class);
    public JenaHelpers(String tBoxFileName, String aBoxFileName) {
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
        this.tBoxSchema = dataset.getNamedModel("tbox");
        this.aBoxFacts = dataset.getNamedModel("abox");
        this.model = dataset.getNamedModel("urn:x-arq:UnionGraph");
        dataset.commit();
        dataset.end();
    }

    public Boolean checkConsistency() {
        // TODO: reasoner should be used when querying OWL ontology
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner().bindSchema(this.tBoxSchema);
        InfModel infModel = ModelFactory.createInfModel(reasoner, this.aBoxFacts);

        ValidityReport validityReport = infModel.validate();

        // My idea is perform operation with commit and then check consisentency if not consistent rollback the operation
        if ( !validityReport.isValid() ) {
            log.warn("Ontology is not consistent");
            Iterator<ValidityReport.Report> iter = validityReport.getReports();
            while ( iter.hasNext() ) {
                ValidityReport.Report report = iter.next();
                log.info(report.toString());
            }
            return true;
        } else {
            log.info("Ontology is consistent");
            return false;
        }
    }

    public void executeSPARQL(String SPARQLQueryFileLocation) {
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
            executeSPARQLSelectQuery(SPARQLQueryFileLocation);
        } else {
            executeSPARQLUpdateAction(SPARQLQueryFileLocation);
        }

    }

    private void executeSPARQLSelectQuery(String SPARQLSelectLocation) {
        Query query = QueryFactory.read(SPARQLSelectLocation);
        QueryExecution queryExec = QueryExecutionFactory.create(query, this.model);
        try {
            ResultSet results = queryExec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                log.info(soln.toString());
            }
        } finally {
            queryExec.close();
        }
    }

    // TODO: decide how you are going to split operations on abox, tbox. Can't do updates on UnionGraph
    private void executeSPARQLUpdateAction(String LocationOfSPARQL) {
        UpdateRequest request = UpdateFactory.create() ;
        UpdateAction.readExecute(LocationOfSPARQL, this.tBoxSchema) ;
        UpdateAction.execute(request, this.tBoxSchema);
    }

    public void printDatasetToStandardOutput() {
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE) ;
    }
}
