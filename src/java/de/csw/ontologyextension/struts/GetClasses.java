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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class GetClasses extends XWikiAction {
	
	@Override
	public boolean action(XWikiContext context) throws XWikiException {
		
		PrintWriter out = null;
		try {
			out = context.getResponse().getWriter();
			context.getResponse().setContentType("application/json");
			
		} catch (IOException e) {
			return false;
		}

		JSONArray classes = getClasses();
		out.print(classes);	
		
		return true;
		}

	
		public JSONArray getClasses() {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLOntology ontology = OntologyManager.getOntology();
			JSONObject inner = null;
            JSONArray outer = new JSONArray();
			for(OWLAxiom bx : ontology.getLogicalAxioms()) {
				inner = new JSONObject();
				System.out.println("AxiomWithout Anno: " + bx.getAxiomWithoutAnnotations());
				String[] rel = bx.getAxiomWithoutAnnotations().toString().split("\\(");
				if(bx.getAxiomWithoutAnnotations().toString().indexOf("ObjectIntersectionOf") > -1) 
				{
					try {
						inner.put("Relation", rel[0]);
						inner.put("Class" , rel[1].split("#")[1].split(" ")[0].replace(">", ""));
						inner.put("ClassEqui", rel[2].split(" ")[0].split("#")[1].replace(">", ""));
						inner.put("ClassEqui", rel[2].split(" ")[1].split("#")[1].replaceAll(">\\)", ""));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					outer.put(inner);
				}

				else {
					String[] axiom = rel[1].split(" ");
				try {
					inner.put("Relation", rel[0]);
					inner.put("Class", axiom[0].split("#")[1].replace(">", ""));
					inner.put("ClassTop", axiom[1].split("#")[1].replaceAll(">\\)", ""));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				outer.put(inner);
				System.out.println(outer);
				}
			}

		    return outer;
		}
		
	}


