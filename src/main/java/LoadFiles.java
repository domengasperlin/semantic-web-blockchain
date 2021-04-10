import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.util.stream.Stream;

public class LoadFiles {

    public static void main(String[] args) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            // pizza ontology - Nick Drummond Creative Commons Attribution 3.0
            ontology = manager.loadOntology(IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl"));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        try {
            assert ontology != null;
            ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }

    }
}
