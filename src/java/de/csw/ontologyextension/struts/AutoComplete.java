package de.csw.ontologyextension.struts;

import groovy.json.JsonOutput;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

import de.csw.linkgenerator.plugin.lucene.LucenePluginApi;
import de.csw.linkgenerator.plugin.lucene.SearchResult;
import de.csw.linkgenerator.plugin.lucene.SearchResults;
import de.csw.util.URLEncoder;
import de.csw.dbpedia.DBpediaLookupClient;


/**
 * @author hanna
 *
 */

public class AutoComplete extends XWikiAction {
	
    @Override
	public boolean action(XWikiContext context) throws XWikiException {
		
		PrintWriter out = null;
		try {
			out = context.getResponse().getWriter();
			context.getResponse().setContentType("application/json");
			
		} catch (IOException e) {
			return false;
		}

		String currentLang = context.getLocale().getLanguage();
		XWikiRequest request = context.getRequest();
		String query = request.get("text");

		
		String sArray[] = new String[]{};
		JSONObject classes =  null;

		if(query.isEmpty())
		{
			classes = null;
		}
		else 
		{
			classes = CompareToOntology(query, currentLang);
		}
		//classes.add(0, "[results : [");
		//classes.add("]]");
		/*Iterator<String> iterator = classes.iterator();
		
		while(iterator.hasNext()) {
				out.write(iterator.next());
		}*/
		
		
		System.out.println("Classes" + classes);
		out.print(classes);
		return true;
		
		
		
	}
    
    public List<String> getSynonyms(String query) {
    	HTTPXMLTest synonymsMethod = new HTTPXMLTest();
    	List<String> synonyms = synonymsMethod.main(query);
    	System.out.println("Synonyms:" + synonyms);
    	return synonyms;
    	
    	
    }
	
	public JSONObject CompareToOntology(String query, String currentLang) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology ontology = null;
		try {
			ontology = CreateOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    //Check if Characters (also one) is contained by class of ontology and return class if true
	    String toBeCheckedClass= "#"+ query;
	    List<String> synonymList = new ArrayList<String>();
	    List<String> ontologyList = new ArrayList<String>();
	    synonymList = getSynonyms(query);
	    
        JSONObject inner = null;
        JSONArray outer = new JSONArray();
        int i = 0;
    	for(OWLClass s : ontology.getClassesInSignature()) {
    		for(OWLAnnotation annotation : s.getAnnotations(ontology, factory.getRDFSLabel())) {
	    		if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                        System.out.println(s + " -> " + val.getLiteral());
                        ontologyList.add(val.getLiteral());
                        if(val.getLiteral().toLowerCase().indexOf(query.toLowerCase()) > -1) {
                        	if (val.hasLang(currentLang)) {        
                				try {
                					inner = new JSONObject();
                					inner.put("id", i);
                					inner.put("value", s.toString().split("#")[1].split(">")[0] + " (" + val.getLiteral() + ")");
                					inner.put("info", s.toString().split("#")[1].split(">")[0]);
                				} catch (JSONException e) {
                				// TODO Auto-generated catch block
                					e.printStackTrace();
                				}
                        	}
                        	else
                        	{
                        		try {
                        			inner = new JSONObject();
                        			inner.put("id", i);
                        			inner.put("value", s.toString().split("#")[1].split(">")[0]);
                        			inner.put("info", s.toString().split("#")[1].split(">")[0]);
                        		} catch (JSONException e) {
                        			// TODO Auto-generated catch block
                        			e.printStackTrace();
                        		}
                        	}
            				outer.put(inner);
            				i += 1;
            			
                        }
                }
    		}
    		System.out.println("ontologylist:" + ontologyList);
		}
    	Set set = new HashSet(synonymList);
		ontologyList.retainAll(set);
    		for(int j=0; j<ontologyList.size(); j++)
    		{
    			String value = ontologyList.get(j);
    				try {
        				inner = new JSONObject();
        				inner.put("id", j);
        				inner.put("value", value);
        				inner.put("info", value);
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
    			
    			outer.put(inner);
    		}
    		
    	
    	
    	JSONObject mainObj = new JSONObject();
		try {
			mainObj.put("results", outer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   //list.add(list2.toString());
	    System.out.println(mainObj);
	    return mainObj;
	    
	}
	
	public List<String> GetOntology() {
	    OWLOntology ontology = null;
		try {
			ontology = CreateOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    List<String> list = new ArrayList<String>();
	    
	    if(ontology == null) {
	    	list.add("Leere Ontology");
	    }
	    

	   for(OWLClass s : ontology.getClassesInSignature()) {
		    	list.add(s.toString().split("#")[1].split(">")[0]);
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
