package de.csw.ontologyextension.struts;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class HTTPXMLTest
{

	public List<String> main(String query) 
    {
		List<String> list = null;
        try {
            list = new HTTPXMLTest().start(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return list;
    }

    private List<String> start(String query) throws Exception
    {
		List<String> list =  new ArrayList<String>();
		
        URL url = new URL("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString=" + query);
        System.out.println(url);
        URLConnection connection = url.openConnection();

        Document doc = parseXML(connection.getInputStream());
        NodeList descNodes = doc.getElementsByTagName("Label");

        System.out.println("Hallo");
        System.out.println(descNodes.getLength());
        for(int i=0; i<descNodes.getLength();i++)
        {
            System.out.println(descNodes.item(i).getTextContent());
            list.add(descNodes.item(i).getTextContent());
        }
        
        /*
        URL url3 = new URL("http://lookup.dbpedia.org/api/search.asmx/PrefixSearch?QueryString=" + query);
        System.out.println(url3);
        URLConnection connection3 = url3.openConnection();

        Document doc3 = parseXML(connection3.getInputStream());
        NodeList descNodes3 = doc3.getElementsByTagName("Label");

        System.out.println("Hallo");
        System.out.println(descNodes3.getLength());
        for(int i=0; i<descNodes3.getLength();i++)
        {
            System.out.println(descNodes3.item(i).getTextContent());
            list.add(descNodes3.item(i).getTextContent());
        }
        */
        return list;
    }

    private Document parseXML(InputStream stream)
    throws Exception
    {
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
