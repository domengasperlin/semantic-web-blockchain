import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.File;
import java.util.Set;

public class LoadFiles {

    public static void main(String[] args) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File("cud.owl"));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        // ABox - facts
        Set<OWLAxiom> aBoxAxioms = ontology.getABoxAxioms(Imports.INCLUDED);
        // TBox - classes and properties
        Set<OWLAxiom> tBoxAxioms = ontology.getTBoxAxioms(Imports.INCLUDED);
        // RBox - reflexivity, symmetry and transitivity of roles
        Set<OWLAxiom> rBoxAxioms = ontology.getRBoxAxioms(Imports.INCLUDED);


        try {
            ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }

    }
}
