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

import java.util.Iterator;

public class JenaHelpers {
    Model model;
    private static boolean doInitialLoad = true;
    private static String tBoxLocation = "target/tbox";
    private static String aBoxLocation = "target/abox";
    Model tBoxSchema;
    Model aBoxFacts;
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
            System.out.println("Ontology is not consistent");
            Iterator<ValidityReport.Report> iter = validityReport.getReports();
            while ( iter.hasNext() ) {
                ValidityReport.Report report = iter.next();
                System.out.println(report);
            }
        } else {
            System.out.println("Ontology is consistent");
        }
        this.model = this.tBoxSchema.add(this.aBoxFacts);
    }

    public void executeSPARQLQuery(String SPARQLSelectLocation) {
        Query query = QueryFactory.read(SPARQLSelectLocation);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                System.out.println(soln);
            }
        } finally {
            qexec.close();
        }
    }

    public void executeSPARQLInsert(String SPARQLInsertLocation) {
        UpdateRequest request = UpdateFactory.create() ;
        UpdateAction.readExecute(SPARQLInsertLocation, model) ;
        UpdateAction.execute(request, model) ;
    }

    public void executeSPARQLUpdate(String SPARQLUpdateLocation) {
        UpdateRequest request = UpdateFactory.create() ;
        UpdateAction.readExecute(SPARQLUpdateLocation, model) ;
        UpdateAction.execute(request, model) ;
    }

    public void executeSPARQLDelete(String SPARQLDeleteLocation) {
        UpdateRequest request = UpdateFactory.create() ;
        UpdateAction.readExecute(SPARQLDeleteLocation, model) ;
        UpdateAction.execute(request, model) ;
    }

    public void printDatasetToStandardOutput() {
        RDFDataMgr.write(System.out, this.model, RDFFormat.TURTLE) ;
    }
}
