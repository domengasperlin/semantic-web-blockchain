import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
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
    Model model;
    // TODO: handle this better, e.g. check if those folders already exist
    private static boolean doInitialLoad = true;
    private static String tBoxLocation = "target/tbox";
    private static String aBoxLocation = "target/abox";
    Model tBoxSchema;
    Model aBoxFacts;
    private static final Logger log = LoggerFactory.getLogger(JenaHelpers.class);
    public JenaHelpers(String tBoxFileName, String aBoxFileName) {
        FileManager fm = FileManager.get();

        if (doInitialLoad) {
            Dataset tBoxDataset = TDBFactory.createDataset(tBoxLocation);
            tBoxDataset.begin(ReadWrite.WRITE);
            this.tBoxSchema = fm.readModel(tBoxDataset.getDefaultModel(), tBoxFileName);
            tBoxDataset.commit();
            tBoxDataset.end();
        } else {
            Dataset dataset = TDBFactory.createDataset(tBoxLocation);
            this.tBoxSchema = dataset.getDefaultModel();
        }

        if (doInitialLoad) {
            Dataset aBoxDataset = TDBFactory.createDataset(aBoxLocation);
            aBoxDataset.begin(ReadWrite.WRITE);
            this.aBoxFacts = fm.readModel(aBoxDataset.getDefaultModel(), aBoxFileName);
            aBoxDataset.commit();
            aBoxDataset.end();
        } else {
            Dataset dataset = TDBFactory.createDataset(aBoxLocation);
            this.aBoxFacts = dataset.getDefaultModel();
        }

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
        } else {
            log.info("Ontology is consistent");
        }
        this.model = this.tBoxSchema.add(this.aBoxFacts);
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
        QueryExecution queryExec = QueryExecutionFactory.create(query, model);
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

    private void executeSPARQLUpdateAction(String LocationOfSPARQL) {
        UpdateRequest request = UpdateFactory.create() ;
        UpdateAction.readExecute(LocationOfSPARQL, model) ;
        UpdateAction.execute(request, model);
    }

    public void printDatasetToStandardOutput() {
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE) ;
    }
}
