package de.csw.ontologyextension.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

import de.csw.util.Config;
import de.csw.xwiki.plugin.OntologyPlugin;

public class Addition extends XWikiAction {
	
	@Override
	public boolean action(XWikiContext context) throws XWikiException {
		
		PrintWriter out = null;
		try {
			out = context.getResponse().getWriter();
			context.getResponse().setContentType("text/plain");
			
		} catch (IOException e) {
			return false;
		}
		
		XWikiRequest request = context.getRequest();
		String query = request.get("text");
		if (query == null) {
			return false;
		}
		String sArray[] = new String[]{};
		List<String> classes =  Arrays.asList(sArray);
		String currentlang = context.getLocale().getLanguage();
		classes = AddAxiom(query, currentlang);
		
		Iterator<String> iterator = classes.iterator();
		while(iterator.hasNext()) {
			out.write(iterator.next());
		}	
		return true;
		}

	
		public List<String> AddAxiom(String query, String currentlang) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			/*File file = new File("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
			OWLOntology ontology = null;
			try {
				ontology = manager.loadOntologyFromOntologyDocument(file);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			/*ClassLoader cl = OntologyPlugin.class.getClassLoader();
			URL url = cl.getResource(Config.getAppProperty(Config.ONTOLOGY_FILE));
			IRI ontologyIRI = null;
			try {
				ontologyIRI = IRI.create(url);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			OWLOntology ontology = null;
			try {
				ontology = manager.loadOntologyFromOntologyDocument(ontologyIRI);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			OWLOntology ontology = OntologyManager.getOntology();
			IRI newClassIRI = IRI
	                .create(ontology.getOntologyID().getOntologyIRI() + "#" + query);
			OWLClass newClass = factory.getOWLClass(newClassIRI);
			List<String> list = new ArrayList<String>();

			for(OWLClass s : ontology.getClassesInSignature()) {
				for(OWLAnnotation annotation : s.getAnnotations(ontology, factory.getRDFSLabel())) {
		    		if (annotation.getValue() instanceof OWLLiteral) {
	                    OWLLiteral val = (OWLLiteral) annotation.getValue();
	                        if(val.getLiteral().toLowerCase().equals(query.toString().toLowerCase())) {
	                        	return list;
	                        }
		    		}
		    	}
		    }
			
			OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(newClass);
			//AddAxiom addAxiomDecl = new AddAxiom(ontology, declarationAxiom);
	        //manager.applyChange(addAxiomDecl);
	        manager.addAxiom(ontology, declarationAxiom);
			// add new language tag
			OWLAnnotation langTag = factory.getOWLAnnotation(factory.getRDFSLabel(),
					factory.getOWLLiteral(query, currentlang));
			OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(),
					langTag);
			AddAxiom addAxiomAnnot = new AddAxiom(ontology, ax);
			manager.addAxiom(ontology, ax);
	        //manager.applyChange(addAxiomAnnot);
	        /*try {
				manager.saveOntology(ontology);
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	*/        

			 for(OWLClass cls : ontology.getClassesInSignature()) {
				    	for(OWLAnnotation annotation : cls.getAnnotations(ontology, factory.getRDFSLabel())) {
				    		System.out.println(cls + ":" + annotation);
				    		
				    	}
				    }
			 
		    for(OWLClass s : ontology.getClassesInSignature()) {
		    	if(s.toString().indexOf(query) > -1) {
			    	list.add(s.toString());
			    	list.add("\n");
		    	}
		    }
		    
		    return list;
		}
		
	}


