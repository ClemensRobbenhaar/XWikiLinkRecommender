package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
			out.write("FAIL");
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
			OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> list = new ArrayList<String>();
			System.out.println("Create Subclass:");
		    OWLClass newCls = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI() + "#" + query));
	        OWLClass superCls = factory.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI() + "#" + cls));
	        // Now create the axiom - Class A is Subclass of Class B
	        if(rel.equalsIgnoreCase("SubClassOf")){
	        	OWLAxiom subAxiom = factory.getOWLSubClassOfAxiom(newCls, superCls);
	        	AddAxiom addSubAxiom = new AddAxiom(ontology, subAxiom);
	        	manager.applyChange(addSubAxiom);
	        //}
	        for (OWLClass cl : ontology.getClassesInSignature()) {
	            System.out.println("Referenced class: " + cl);
	        }
	        Set<OWLClassExpression> superClasses = newCls.getSuperClasses(ontology);
	        System.out.println("Asserted superclasses of " + newCls + ":");
	        for (OWLClassExpression desc : superClasses) {
	            System.out.println(desc);
	            list.add("#S#,"  + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        Set<OWLClassExpression> subClasses = newCls.getSubClasses(ontology);
	        System.out.println("Asserted superclasses of " + newCls + ":");
	        for (OWLClassExpression desc : subClasses) {
	            System.out.println(desc);
	            list.add("#Sub#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        }
	        
	        else if(rel.equalsIgnoreCase("EquivalentClasses")) {
	        //Add Equivalent Class
	        OWLAxiom equiAxiom = factory.getOWLEquivalentClassesAxiom(newCls, superCls);
        	AddAxiom addEquiAxiom = new AddAxiom(ontology, equiAxiom);
        	manager.applyChange(addEquiAxiom);

        	//if equivalent class has superclass, edit superclass of new class
        	Set<OWLClassExpression> superOfEqui = superCls.getSuperClasses(ontology);
	        System.out.println("Asserted superclasses of " + newCls + ":");
	        for (OWLClassExpression desc : superOfEqui) {
	        	OWLAxiom sub = factory.getOWLSubClassOfAxiom(newCls, desc);
	        	AddAxiom addSub = new AddAxiom(ontology, sub);
	        	manager.applyChange(addSub);
	            System.out.println(desc);
	            //list.add("Superclass of " + superCls + desc.toString());
	            //list.add("\n");
	        }
	        for (OWLClassExpression desc : superCls.getEquivalentClasses(ontology)) {
	            System.out.println(desc);
	            OWLAxiom equi = factory.getOWLEquivalentClassesAxiom(newCls, desc);
	        	AddAxiom addEqui = new AddAxiom(ontology, equi);
	        	manager.applyChange(addEqui);
	        }
	        
	        Set<OWLClassExpression> equiClasses = newCls.getEquivalentClasses(ontology);
	        System.out.println("Asserted superclasses of " + newCls + ":");
	        for (OWLClassExpression desc : equiClasses) {
	        	
	        	for(OWLClassExpression expr : desc.getClassesInSignature())
	        	{
	        		System.out.println(expr);
	        		
	        		list.add("#E#," + expr.toString().split("#")[1].split(">")[0] + "," + expr.toString().replaceAll("[<>]", ""));
	        		list.add("\n");
	        		
	        		
	        	}
	            System.out.println(desc);
	        }
	        
	        
	        Set<OWLClassExpression> superOfNew = newCls.getSuperClasses(ontology);
	        System.out.println("Asserted superclasses of " + newCls + ":");
	        for (OWLClassExpression desc : superOfNew) {
	            System.out.println(desc);
	            list.add("#S#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        
	        Set<OWLClassExpression> subClasses = newCls.getSubClasses(ontology);
	        System.out.println("Asserted superclasses of " + newCls + ":");
	        for (OWLClassExpression desc : subClasses) {
	            System.out.println(desc);
	            list.add("#Sub#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
	            list.add("\n");
	        }
	        
	        }
	        
	        // SuperClass
	        else if(rel.equalsIgnoreCase("SuperClass")) {
	        	OWLAxiom superAxiom = factory.getOWLSubClassOfAxiom(superCls, newCls);
	        	AddAxiom addSubAxiom = new AddAxiom(ontology, superAxiom);
	        	manager.applyChange(addSubAxiom);
	        	
	        	Set<OWLClassExpression> subClasses = newCls.getSubClasses(ontology);
		        System.out.println("Asserted subclasses of " + newCls + ":");
		        for (OWLClassExpression desc : subClasses) {
		            System.out.println(desc);
		            list.add("#Sub#," + desc.toString().split("#")[1].split(">")[0] + "," + desc.toString().replaceAll("[<>]", ""));
		            list.add("\n");
		        }
	        	
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


