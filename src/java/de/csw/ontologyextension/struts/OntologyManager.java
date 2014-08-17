package de.csw.ontologyextension.struts;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyManager {
	static final Logger log = Logger.getLogger(OntologyManager.class);

	static OWLOntology ontology;
	
	public static OWLOntology getOntology()
	{
		return ontology;
	}
	
	public static OWLOntology loadOntology(URL url) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI ontologyIRI = null;
		File file = new File(url.toString());
		try {
			ontologyIRI = IRI.create(url);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ontology = manager.loadOntologyFromOntologyDocument(ontologyIRI);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ontology:" + ontology);
		return ontology;
	}
	/*
	public static void saveOntology(URL url) 
	{
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		File file = new File(url.toString());
		try {
			manager.saveOntology(ontology, file);
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}*/

	

	


}
