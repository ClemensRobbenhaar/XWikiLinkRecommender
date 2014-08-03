package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

public class EditClass extends XWikiAction {
	
	@Override
	public boolean action(XWikiContext context) throws XWikiException {
		
		PrintWriter out = null;
		try {
			out = context.getResponse().getWriter();
			context.getResponse().setContentType("application/json");
			
		} catch (IOException e) {
			return false;
		}
		
		XWikiRequest request = context.getRequest();
		String query = request.get("text");
		if (query == null) {
			out.write("FAIL");
			return false;
		}


		//JSONObject axioms = GetAxiom(query);
		//JSONObject axiomTypes = GetAxiomTypes();
		
		OWLClass existing = exists(query);

		if(existing != null)
		{
			System.out.println("TRUE");
			JSONArray existingElements = getRelations(existing, query);
			System.out.println("ExistingElement" + existingElements);
			out.print(existingElements);
		}
		else
		{
			System.out.println("FALSE");
			JSONArray relations = GetAxiom(query);
			System.out.println("Not existing: " + relations);
			out.print(relations);
		}
		
		//out.print(axioms);
		//out.print(axiomTypes);
		return true;
		}

	
		public JSONArray getRelations(OWLClass existing, String query) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject inner = null;
            JSONArray outer = new JSONArray();
	        Set<OWLClassExpression> superClasses = existing.getSuperClasses(ontology);
            System.out.println("SUperClasses: " + superClasses);

	        System.out.println("Asserted superclasses of " + existing + ":");
	        for (OWLClassExpression desc : superClasses) {
	            System.out.println(desc);
	            try {
    				inner = new JSONObject();
    				inner.put("SelectedRelation", "SuperClass");
    				inner.put("SelectedClass", desc.toString().split("#")[1].split(">")[0]);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
	            outer.put(inner);
	        }
	        Set<OWLClassExpression> subClasses = existing.getSubClasses(ontology);
	        System.out.println("Asserted subClasses of " + existing + ":");
	        for (OWLClassExpression desc : subClasses) {
	            System.out.println(desc);
	            try {
    				inner = new JSONObject();
    				inner.put("SelectedRelation", "SubClassOf");
    				inner.put("SelectedClass", desc.toString().split("#")[1].split(">")[0]);
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
	            outer.put(inner);
	        }
	        
	        for(OWLAxiom bx : ontology.getLogicalAxioms()) {
				if(inner.has(bx.getAxiomType().toString()) == false) {
					inner = new JSONObject();
    				try {
						inner.put("Type", bx.getAxiomType().toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				outer.put(inner);
			}
	        //Superclass:
	        inner = new JSONObject();
	        try {
				inner.put("Type", "SuperClass");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        outer.put(inner);
	        
	        
	        IRI newClassIRI = IRI
	                .create(ontology.getOntologyID().getOntologyIRI() + "#" + query);
			OWLClass newClass = factory.getOWLClass(newClassIRI);
			OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(newClass);
			manager.addAxiom(ontology, declarationAxiom);
			
		    for(OWLClass s : ontology.getClassesInSignature()) {
		    	inner = new JSONObject();
				try {
					inner.put("Class", s.toString().split("#")[1].split(">")[0]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outer.put(inner);
		    }
		    
			return outer;
		}


		public OWLClass exists(String query)
		{
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*IRI ontologyIRI = IRI.create("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
			IRI newClassIRI = IRI
	                .create(ontology.getOntologyID().getOntologyIRI() + "#" + query);
			OWLClass newClass = factory.getOWLClass(newClassIRI);*/
			for(OWLClass s : ontology.getClassesInSignature()) {
				for(OWLAnnotation annotation : s.getAnnotations(ontology, factory.getRDFSLabel())) {
		    		if (annotation.getValue() instanceof OWLLiteral) {
	                    OWLLiteral val = (OWLLiteral) annotation.getValue();
	                        if(val.getLiteral().equals(query.toString())) {
	                        	return s;
	                        }
		    		}
		    	}
		    }
			return null;
		}
	
		public JSONObject GetAxiomTypes() {
			OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject inner = null;
			List<String> list = new ArrayList<String>();
			for(OWLAxiom bx : ontology.getLogicalAxioms()) {
				if(inner.equals(bx.getAxiomType().toString()) == false) {
					inner = new JSONObject();
    				try {
						inner.put("Type", bx.getAxiomType().toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			list.add("Superclass");
			return inner;
		}
		public JSONArray GetAxiom(String query) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject inner = null;
			JSONArray outer = new JSONArray();
			List<String> typeList = new ArrayList<String>();
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

			for(OWLAxiom bx : ontology.getLogicalAxioms()) {
				System.out.println("TypeList:" + typeList);
				if(typeList.contains(bx.getAxiomType().toString()) == false) {
					inner = new JSONObject();
    				try {
						inner.put("Type", bx.getAxiomType().toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				outer.put(inner);
				}
				typeList.add(bx.getAxiomType().toString());
			}
			System.out.println("out1:" + outer);
			if(typeList.contains("SuperClass") == false)
			{
				inner = new JSONObject();
				try {
					inner.put("Type", "SuperClass");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			outer.put(inner);
			System.out.println("inner" + inner.has("Type"));
			System.out.println("out2:" + outer);
		   
		    for(OWLClass s : ontology.getClassesInSignature()) {
		    	inner = new JSONObject();
				try {
					inner.put("Class", s.toString().split("#")[1].split(">")[0]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    	
				outer.put(inner); 
		    }
		    System.out.println("out3:" + outer);
		    return outer;
		}
		
		
		public OWLOntology CreateOntology() throws OWLOntologyCreationException {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
		    // Now load the local copy
		    OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		    return ontology;
		}
	}


