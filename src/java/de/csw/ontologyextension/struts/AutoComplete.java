package de.csw.ontologyextension.struts;

import java.io.File;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiAction;
import com.xpn.xwiki.web.XWikiRequest;

/**
 * @author hanna
 *
 */
public class AutoComplete extends XWikiAction {
	
	@Override
	public boolean action(XWikiContext context) throws XWikiException {
		XWikiRequest request = context.getRequest();
		String query = request.get("text");
		if (query == null) {
			return false;
		}
		try {
			getOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void getOntology() throws OWLOntologyCreationException {
		// Get hold of an ontology manager
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		File file = new File("/home/hanna/Git/XWikiLinkRecommenderNew/resources/ontology/gewuerz.owl");
	    // Now load the local copy
	    OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
	    System.out.print("Loaded ontology: " + ontology);	
		//return true;
	}

}