import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.NTriplesDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.File;
import java.util.Set;

public class OntologyHelpers {
    public OWLOntology ontology;
//    private static QueryEngine engine;

    public OntologyHelpers(String fileName) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File(fileName));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
//        StructuralReasonerFactory factory = new StructuralReasonerFactory();
//        OWLReasoner reasoner = factory.createReasoner(ontology);
//        engine = QueryEngine.create(manager, reasoner, true);
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void saveTBoxAxiomsToFile() {
        Set<OWLAxiom> tBoxAxioms = ontology.getTBoxAxioms(Imports.INCLUDED);
        OWLOntologyManager tBoxManager = OWLManager.createOWLOntologyManager();
        OWLOntology tBoxOntology = null;
        try {
            tBoxOntology = tBoxManager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        tBoxManager.addAxioms(tBoxOntology, tBoxAxioms);
        saveOntologyToFile("rdf-sparql/output/tbox-axioms.nt", tBoxOntology, new NTriplesDocumentFormat());
    }

    public void saveABoxAxiomsToFile() {
        Set<OWLAxiom> aBoxAxioms = ontology.getABoxAxioms(Imports.INCLUDED);
        OWLOntologyManager tBoxManager = OWLManager.createOWLOntologyManager();
        OWLOntology aBoxOntology = null;
        try {
            aBoxOntology = tBoxManager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        tBoxManager.addAxioms(aBoxOntology, aBoxAxioms);
        saveOntologyToFile("rdf-sparql/output/abox-axioms.ttl", aBoxOntology, new TurtleDocumentFormat());
    }

    public void saveRBoxAxiomsToFile() {
        // Ethereum - TBox - classes and properties
        Set<OWLAxiom> rBoxAxioms = ontology.getRBoxAxioms(Imports.INCLUDED);
        OWLOntologyManager rBoxManager = OWLManager.createOWLOntologyManager();
        OWLOntology rBoxOntology = null;
        try {
            rBoxOntology = rBoxManager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        rBoxManager.addAxioms(rBoxOntology, rBoxAxioms);
        saveOntologyToFile("rdf-sparql/output/rbox-axioms.ttl", rBoxOntology, new TurtleDocumentFormat());
    }

    public void saveOntologyToFile(String outputFile, OWLOntology owlOntology, OWLDocumentFormat owlDocumentFormat) {
        try {
            File outputOntology = new File(outputFile);
            owlOntology.saveOntology(owlDocumentFormat, IRI.create(outputOntology.toURI()));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    public void outputOntologyToSystemOut() {
        try {
            ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }
}
