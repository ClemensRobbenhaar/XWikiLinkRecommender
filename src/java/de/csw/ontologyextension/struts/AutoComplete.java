package de.csw.ontologyextension.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

import de.csw.linkgenerator.plugin.lucene.LucenePluginApi;
import de.csw.linkgenerator.plugin.lucene.SearchResult;
import de.csw.linkgenerator.plugin.lucene.SearchResults;
import de.csw.util.URLEncoder;


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
			context.getResponse().setContentType("text/plain");
			
		} catch (IOException e) {
			return false;
		}

		XWikiRequest request = context.getRequest();
		String query = request.get("text");

		
		String sArray[] = new String[]{};
		List<String> classes =  Arrays.asList(sArray);

		if(query.isEmpty())
		{
			classes = GetOntology();
		}
		else 
		{
			classes = CompareToOntology(query);
		}
		

		Iterator<String> iterator = classes.iterator();
		while(iterator.hasNext()) {
			    int i = 1;
				out.write((i + "," + iterator.next()));
				i++;
		}
		return true;
		
		
		
	}
	
	public List<String> CompareToOntology(String query) {
	    OWLOntology ontology = null;
		try {
			ontology = CreateOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    //Check if Characters (also one) is contained by class of ontology and return class if true
	    String toBeCheckedClass= "#"+ query;
	    List<String> list = new ArrayList<String>();
	    
	    if(ontology == null) {
	    	list.add("Leere Ontology-Query not");
	    }
	    

    	for(OWLClass s : ontology.getClassesInSignature()) {
    		if(s.toString().contains(toBeCheckedClass)) {
    			System.out.println("TRUE: " + toBeCheckedClass + "," + s);
    			list.add(s.toString().split("#")[1].split(">")[0]);
				list.add("\n");
		
	    	}
		}
	    
	    
	    return list;
	    
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
