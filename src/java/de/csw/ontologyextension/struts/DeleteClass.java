package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

public class DeleteClass extends XWikiAction {
	
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
			System.out.println("NULL NULL NULL");
			return false;
		}
		String sArray[] = new String[]{};
		List<String> classes =  Arrays.asList(sArray);

		classes = DeleteAxiom(query);
		
		Iterator<String> iterator = classes.iterator();
		while(iterator.hasNext()) {
			out.write(iterator.next());
		}	
		
		return true;
		}

	
		public List<String> DeleteAxiom(String query) {
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
			OWLOntology ontology = OntologyManager.getOntology();
			OWLClass toDelete = manager.getOWLDataFactory().getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI() + "#" + query));
			Set<OWLAxiom> axiomsToRemove = new HashSet<OWLAxiom>();
	        for (OWLAxiom ax : ontology.getAxioms()) {
	            if (ax.getSignature().toString().indexOf(query.toString()) > -1) {
	            	System.out.println("ax" + ax);
	                axiomsToRemove.add(ax);
	            }
	        }
	        
	        for (OWLClass cls : ontology.getClassesInSignature()) {
	        	if(cls.toString().indexOf(query) > -1) {
	        	for(OWLAnnotation annotation : cls.getAnnotations(ontology, factory.getRDFSLabel())) {
	        		System.out.println("Annotation: " + annotation);
	                RemoveOntologyAnnotation remAnno = new RemoveOntologyAnnotation(ontology, annotation);
	                System.out.println("RemAnno: " + remAnno);
	                manager.applyChange(remAnno);  
	                /*try {
	        			manager.saveOntology(ontology);
	        			System.out.println("Saved");
	        		} catch (OWLOntologyStorageException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		} */
	        	}
		    	}
	        }

	        manager.removeAxioms(ontology, axiomsToRemove);
	       /* try {
				manager.saveOntology(ontology);
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    
	        */
	        List<String> list = new ArrayList<String>();
	        
		    for(OWLClass s : ontology.getClassesInSignature()) {
			    	list.add(s.toString());
			    	list.add("\n");
			    
		    }

		    return list;
		}
		
	}


