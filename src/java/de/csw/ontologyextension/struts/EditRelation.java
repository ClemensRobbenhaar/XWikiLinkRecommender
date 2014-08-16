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
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

public class EditRelation extends XWikiAction {
	
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
		String cls = request.get("cls");
		String rel = request.get("rel");
		if (query == null) {
			return false;
		}
		String sArray[] = new String[]{};
		List<String> axioms =  Arrays.asList(sArray);
		
		axioms = SaveRelation(query, cls, rel);
		Iterator<String> iterator = axioms.iterator();
		
		while(iterator.hasNext()) {
			out.write(iterator.next());
		}	
		return true;
		}

		public List<String> SaveRelation(String query, String cls, String rel) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	        OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntology ontology = OntologyManager.getOntology();
			List<String> list = new ArrayList<String>();
			OWLClass newCls = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI() + "#" + query));
	        OWLClass superCls = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI() + "#" + cls));
	        
	        // Now create the axiom - Class A is Subclass of Class B
	        if(rel.equalsIgnoreCase("SubClassOf")){
	        	OWLAxiom subAxiom = factory.getOWLSubClassOfAxiom(newCls, superCls);
	        	manager.addAxiom(ontology, subAxiom);
	        	//AddAxiom addSubAxiom = new AddAxiom(ontology, subAxiom);
	        	//manager.applyChange(addSubAxiom);
	        
	        Set<OWLClassExpression> superClasses = newCls.getSuperClasses(ontology);
	        for (OWLClassExpression desc : superClasses) {
	            list.add("#S#,"  + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        Set<OWLClassExpression> subClasses = newCls.getSubClasses(ontology);
	        for (OWLClassExpression desc : subClasses) {
	            System.out.println(desc);
	            list.add("#Sub#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        }
	        
	        else if(rel.equalsIgnoreCase("EquivalentClasses")) {
	        //Add Equivalent Class
	        OWLAxiom equiAxiom = factory.getOWLEquivalentClassesAxiom(newCls, superCls);
	        manager.addAxiom(ontology, equiAxiom);
        	//AddAxiom addEquiAxiom = new AddAxiom(ontology, equiAxiom);
        	//manager.applyChange(addEquiAxiom);

        	//if equivalent class has superclass, edit superclass of new class
        	Set<OWLClassExpression> superOfEqui = superCls.getSuperClasses(ontology);
	        for (OWLClassExpression desc : superOfEqui) {
	        	OWLAxiom sub = factory.getOWLSubClassOfAxiom(newCls, desc);
	        	manager.addAxiom(ontology, sub);
	        	//AddAxiom addSub = new AddAxiom(ontology, sub);
	        	//manager.applyChange(addSub);
	        }
	        for (OWLClassExpression desc : superCls.getEquivalentClasses(ontology)) {
	            OWLAxiom equi = factory.getOWLEquivalentClassesAxiom(newCls, desc);
	            manager.addAxiom(ontology, equi);
	        	//AddAxiom addEqui = new AddAxiom(ontology, equi);
	        	//manager.applyChange(addEqui);
	        }
	        
	        Set<OWLClassExpression> equiClasses = newCls.getEquivalentClasses(ontology);
	        for (OWLClassExpression desc : equiClasses) {
	        	
	        	for(OWLClassExpression expr : desc.getClassesInSignature())
	        	{	        		
	        		list.add("#E#," + expr.toString().split("#")[1].split(">")[0] + "," + expr.toString().replaceAll("[<>]", ""));
	        		list.add("\n");	        			        	
	        	}
	        }
	        
	        
	        Set<OWLClassExpression> superOfNew = newCls.getSuperClasses(ontology);
	        for (OWLClassExpression desc : superOfNew) {
	            list.add("#S#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        
	        Set<OWLClassExpression> subClasses = newCls.getSubClasses(ontology);
	        for (OWLClassExpression desc : subClasses) {
	            list.add("#Sub#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        
	        }
	        
	        // SuperClass
	        else if(rel.equalsIgnoreCase("SuperClass")) {
	        	OWLAxiom superAxiom = factory.getOWLSubClassOfAxiom(superCls, newCls);
	        	manager.addAxiom(ontology, superAxiom);
	        	//AddAxiom addSubAxiom = new AddAxiom(ontology, superAxiom);
	        	//manager.applyChange(addSubAxiom);
	        	
	        	Set<OWLClassExpression> subClasses = newCls.getSubClasses(ontology);
		        for (OWLClassExpression desc : subClasses) {
		            list.add("#Sub#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
		            list.add("\n");
		        }
	        	
	        }
	        return list;
			
		}
	
	}


