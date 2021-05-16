import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.NTriplesDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OntologyHelpers {
    private Set<OWLOntology> ontologies;
    private static final Logger log = LoggerFactory.getLogger(OntologyHelpers.class);
    public OntologyHelpers(ArrayList<String> filesToLoad) {
        if (filesToLoad.size() < 1) {
            log.error("Must have at least 1 input ontology");
            return;
        }
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        for (String fileName : filesToLoad) {
            try {
                manager.loadOntologyFromOntologyDocument(new File(fileName));
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }
        this.ontologies = manager.getOntologies();

//        StructuralReasonerFactory factory = new StructuralReasonerFactory();
//        OWLReasoner reasoner = factory.createReasoner(ontology);
//        engine = QueryEngine.create(manager, reasoner, true);
    }

    public OWLDocumentFormat chooseCorrectFormat(String fileName) {
        if (fileName.contains("ttl")) {
            return new TurtleDocumentFormat();
        }
        if (fileName.contains("nt")) {
            return new NTriplesDocumentFormat();
        }
        log.error("chooseCorrectFormat could not guess format from file extension");
        return null;
    }

    public void saveTBoxAxiomsToFile(String fileName) {
        Set<OWLAxiom> tBoxAxioms = new HashSet<>();
        for(OWLOntology ontology : ontologies) {
            tBoxAxioms.addAll(ontology.getTBoxAxioms(Imports.INCLUDED));
        }

        OWLOntologyManager tBoxManager = OWLManager.createOWLOntologyManager();
        OWLOntology tBoxOntology = null;
        try {
            tBoxOntology = tBoxManager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        tBoxManager.addAxioms(tBoxOntology, tBoxAxioms);
        saveOntologyToFile(fileName, tBoxOntology, chooseCorrectFormat(fileName));
    }

    public void saveABoxAxiomsToFile(String fileName) {
        Set<OWLAxiom> aBoxAxioms = new HashSet<>();
        for(OWLOntology ontology : ontologies) {
            aBoxAxioms.addAll(ontology.getABoxAxioms(Imports.INCLUDED));
        }
        OWLOntologyManager aBoxManager = OWLManager.createOWLOntologyManager();
        OWLOntology aBoxOntology = null;
        try {
            aBoxOntology = aBoxManager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        aBoxManager.addAxioms(aBoxOntology, aBoxAxioms);
        saveOntologyToFile(fileName, aBoxOntology, chooseCorrectFormat(fileName));
    }

    public void saveOntologyToFile(String outputFile, OWLOntology owlOntology, OWLDocumentFormat owlDocumentFormat) {
        try {
            File outputOntology = new File(outputFile);
            owlOntology.saveOntology(owlDocumentFormat, IRI.create(outputOntology.toURI()));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }
}
