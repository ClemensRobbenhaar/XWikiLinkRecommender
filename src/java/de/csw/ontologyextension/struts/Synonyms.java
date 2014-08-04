package de.csw.ontologyextension.struts;

import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;

public interface Synonyms
{
	public List<String> start(String query, String lang) throws Exception;
	public Document parseXML(InputStream stream) throws Exception;

}
