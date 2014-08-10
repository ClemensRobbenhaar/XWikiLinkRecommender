package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.de.GermanStemmer;
import org.apache.lucene.analysis.de.Stemmer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.csw.ontology.OntologyIndex;
import de.csw.ontology.util.OntologyUtils;
import de.csw.ontology.vocabular.Jura;
import de.csw.util.Config;

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
