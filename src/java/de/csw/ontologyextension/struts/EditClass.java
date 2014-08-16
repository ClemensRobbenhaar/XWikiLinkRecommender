package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
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
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

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
			return false;
		}


		//JSONObject axioms = GetAxiom(query);
		//JSONObject axiomTypes = GetAxiomTypes();
		
		OWLClass existing = exists(query);

		System.out.println("Existing:" + existing);
		if(existing != null)
		{
			JSONArray existingElements = getRelations(existing, query);
			System.out.println("GetRelations:" + existingElements);
			out.print(existingElements);
		}
		else
		{
			JSONArray relations = getAxiom(query);
			System.out.println("GetAxioms:" + relations);
			out.print(relations);
		}

		return true;
		}

	
		public JSONArray getRelations(OWLClass existing, String query) {
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
			JSONObject inner = null;
            JSONArray outer = new JSONArray();
			List<String> typeList = new ArrayList<String>();
	        Set<OWLClassExpression> superClasses = existing.getSuperClasses(ontology);
	        Set<OWLClassExpression> subClasses = existing.getSubClasses(ontology);
	        Set<OWLClassExpression> equiClasses = existing.getEquivalentClasses(ontology);

	        if(superClasses == null && subClasses == null && equiClasses == null)
	        {
	        	outer = getAxiom(query);
	        }
	        else
	        {
		        for (OWLClassExpression desc : superClasses) {
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
		        for (OWLClassExpression desc : subClasses) {
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
		        
		        for(OWLClassExpression desc : equiClasses) {
		        	try {
						inner = new JSONObject();
						inner.put("SelectedRelation", "EquivalentClass");
						inner.put("SelectedClass", desc.toString().split("#")[1].split(">")[0]);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            outer.put(inner);
		        }
		        
		        for(OWLAxiom bx : ontology.getLogicalAxioms()) {
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
		        //Superclass:
		        inner = new JSONObject();
		        try {
					inner.put("Type", "SuperClass");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        outer.put(inner);
		        
		        /*
		        IRI newClassIRI = IRI
		                .create(ontology.getOntologyID().getOntologyIRI() + "#" + query);
				OWLClass newClass = factory.getOWLClass(newClassIRI);
				OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(newClass);
				AddAxiom declAxiom = new AddAxiom(ontology, declarationAxiom);
				manager.applyChange(declAxiom);
				try {
					manager.saveOntology(ontology);
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/	
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
				System.out.println("Outer - getRelations:" + outer);
	        }
			return outer;
		}


		public OWLClass exists(String query)
		{
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			/*OWLOntology ontology = null;
			try {
				ontology = CreateOntology();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			OWLOntology ontology = OntologyManager.getOntology();
			for(OWLClass s : ontology.getClassesInSignature()) {
				for(OWLAnnotation annotation : s.getAnnotations(ontology, factory.getRDFSLabel())) {
		    		if (annotation.getValue() instanceof OWLLiteral) {
	                    OWLLiteral val = (OWLLiteral) annotation.getValue();
	                        if(val.getLiteral().toLowerCase().equals(query.toString().toLowerCase())) {
	                        	return s;
	                        }
		    		}
		    	}
		    }
			return null;
		}
	
		/*
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
		}*/
		
		public JSONArray getAxiom(String query) {
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
			JSONObject inner = null;
			JSONArray outer = new JSONArray();
			List<String> typeList = new ArrayList<String>();
			IRI newClassIRI = IRI
	                .create(ontology.getOntologyID().getOntologyIRI() + "#" + query);
			OWLClass newClass = factory.getOWLClass(newClassIRI);
			OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(newClass);
			/*AddAxiom declAxiom = new AddAxiom(ontology, declarationAxiom);
			manager.applyChange(declAxiom);
			try {
				manager.saveOntology(ontology);
			} catch (OWLOntologyStorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	*/
			for(OWLAxiom bx : ontology.getLogicalAxioms()) {
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
			System.out.println("GetAxioms - outer:" + outer);
		    return outer;
		}
		
	}


