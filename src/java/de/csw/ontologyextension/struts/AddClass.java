package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

public class AddClass extends XWikiAction {
	
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
			out.write("FAIL");
			return false;
		}
		String sArray[] = new String[]{};
		List<String> classes =  Arrays.asList(sArray);

		classes = AddAxiom(query);
		
		Iterator<String> iterator = classes.iterator();
		while(iterator.hasNext()) {
			out.write(iterator.next());
		}	
		return true;
		}

	
		public List<String> AddAxiom(String query) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			IRI ontologyIRI = IRI.create("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
			IRI newClassIRI = IRI
	                .create(ontology.getOntologyID().getOntologyIRI() + "#" + query);
			OWLClass newClass = factory.getOWLClass(newClassIRI);
			OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(newClass);
			manager.addAxiom(ontology, declarationAxiom);
			
			OWLOntology newOntology = null;
			try {
				newOntology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> list = new ArrayList<String>();
		    for(OWLClass s : ontology.getClassesInSignature()) {
			    	list.add(s.toString());
			    	list.add("\n");
			    
		    }
		    return list;
		}
		
		
		public OWLOntology CreateOntology() throws OWLOntologyCreationException {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
		    // Now load the local copy
		    OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		    return ontology;
		}
	}


