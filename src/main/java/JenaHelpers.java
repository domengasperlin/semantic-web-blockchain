import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

public class JenaHelpers {
    Model model;
//    Dataset dataset;
    public JenaHelpers(String fileName) {
        this.model = RDFDataMgr.loadModel(fileName);
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
