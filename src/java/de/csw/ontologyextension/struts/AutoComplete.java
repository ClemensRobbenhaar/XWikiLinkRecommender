package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.sse.builders.BuilderExpr.Build;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

import de.csw.ontology.OntologyIndex;
import de.csw.util.Config;


/**
 * @author hanna
 *
 */

public class AutoComplete extends XWikiAction implements Synonyms {
	final static String APP_PROP_FILE = "build.properties";
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

		
		JSONObject classes =  null;

		if(query.isEmpty())
		{
			classes = null;
		}
		else 
		{
			classes = CompareToOntology(query, currentLang);
		}
		out.print(classes);
		return true;
		
		
		
	}
    
    public List<String> getSynonyms(String query, String currentLang) {
    	List<String> synonyms = null;
		try {
			synonyms = start(query, currentLang);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	    
	    List<String> synonymList = new ArrayList<String>();
	    List<String> ontologyList = new ArrayList<String>();
	    synonymList = getSynonyms(query, currentLang);
	    
        JSONObject inner = null;
        JSONArray outer = new JSONArray();
        int i = 0;
    	for(OWLClass s : ontology.getClassesInSignature()) {
    		for(OWLAnnotation annotation : s.getAnnotations(ontology, factory.getRDFSLabel())) {
	    		if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                        ontologyList.add(val.getLiteral().toLowerCase());
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
    		//System.out.println("ontologylist:" + ontologyList);
		}
		System.out.println("Synonymes:" + synonymList);
		System.out.println("OntologieElements:" + ontologyList);
    	Set<String> set = new HashSet<String>(synonymList);
		ontologyList.retainAll(set);
    		for(int j=0; j<ontologyList.size(); j++)
    		{
    			String value = ontologyList.get(j);
    				try {
        				inner = new JSONObject();
        				inner.put("id", j);
        				inner.put("value", WordUtils.capitalize(value) + " (Synonym of " + query + ")");
        				inner.put("info", value);
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
    			
    			outer.put(inner);
    		}
    		
    	
    	System.out.println("FinalList:" + outer);
    	
    	
    	JSONObject mainObj = new JSONObject();
		try {
			mainObj.put("results", outer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   //list.add(list2.toString());
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
		ClassLoader cl = this.getClass().getClassLoader();
		Config.loadConfigFile(cl.getResourceAsStream(APP_PROP_FILE), false);
		Config.loadConfigFile(cl.getResourceAsStream(APP_PROP_FILE + ".user"), true);
		String resource = Config.getAppProperty(Config.DIR_RESOURCES);
		System.out.println("RESULT:" + resource);
		File file = new File("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
		System.out.println(file);
	    // Now load the local copy
	    OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
	    return ontology;
	}

	@Override
	public List<String> start(String query, String currentLang) throws Exception 
	{
		List<String> list =  new ArrayList<String>();
		
        URL url = new URL("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString=" + query);
        URLConnection connection = url.openConnection();

        Document doc = parseXML(connection.getInputStream());
        NodeList descNodes = doc.getElementsByTagName("Label");

        for(int i=0; i<descNodes.getLength();i++)
        {
        	if(descNodes.item(i).getParentNode().toString().contains("Result"))
        	{
        		list.add(descNodes.item(i).getTextContent().toLowerCase());
        	}
        	
        }
         
        // get Translations:
        String squery = "'" + query + "'";
        String queryTranslations=
	    		"SELECT ?b"+
	    		" WHERE { " +
	    		"?a <http://www.w3.org/2000/01/rdf-schema#label>" + squery + "@" + currentLang + "." +
	    		" ?a <http://wiktionary.dbpedia.org/terms/hasTranslation> ?b . "  +
	    		"}";
        // now creating query object
		Query queryRequest = QueryFactory.create(queryTranslations);
		// initializing queryExecution factory with remote service.
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://wiktionary.dbpedia.org/sparql", queryRequest);

		//List<String> listSynonyms = new ArrayList<String>();
		try {
		    ResultSet results = qexec.execSelect();
		    while (results.hasNext()) {
				QuerySolution qs = results.next();
				list.add(qs.toString().split("/")[4].split("-")[0].toLowerCase());
			}
		}
		finally {
 		   qexec.close();
 		}
			
        return list;
	}

	@Override
	public Document parseXML(InputStream stream) throws Exception{
		DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;
        try
        {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        }
        catch(Exception ex)
        {
            throw ex;
        }       

        return doc;
	}

}
