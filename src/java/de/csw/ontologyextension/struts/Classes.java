package de.csw.ontologyextension.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;

public class Classes extends XWikiAction {
	
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
			OWLOntology ontology = OntologyManager.getOntology();
			JSONObject inner = null;
            JSONArray outer = new JSONArray();
            ArrayList<String> all = new ArrayList<String>();
			for(OWLAxiom bx : ontology.getLogicalAxioms()) {
				inner = new JSONObject();
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
			      	all.add(axiom[0]);
			      	all.add(axiom[1]);
				try {
					inner.put("Relation", rel[0]);
					inner.put("Class", axiom[0].split("#")[1].replace(">", ""));
					inner.put("ClassTop", axiom[1].split("#")[1].replaceAll(">\\)", ""));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		      	
				outer.put(inner);
				}
			}
			
	        ArrayList<String> cls = new ArrayList<String>();
			for(OWLClass cx : ontology.getClassesInSignature()) {
	    		cls.add(cx.toString());
	    		
		    }
	    	Set<String> set = new HashSet<String>(all);
	    	cls.removeAll(set);

	    	for(String str : cls)
	    	{				
	    		inner = new JSONObject();
		    	System.out.println(str);
	    		try {
		    		inner.put("NoRelation", str.split("#")[1].replace(">", ""));
			    	System.out.println(inner);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		outer.put(inner);
	    	}
	    	System.out.println(cls);
	    	System.out.println(outer);

		    return outer;
		}
		
	}


