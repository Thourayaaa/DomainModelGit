package lu.list.hermes.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lu.list.hermes.dao.AnnotationDao;
import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.dao.DocumentDao;
import lu.list.hermes.models.*;
import lu.list.hermes.util.HibernateUtil;

/** This class implements functions responsable for finding the annotation and its dbpedia 
 * and saving it in the database
 * @author thourayabouzidi
 *
 */

public class KodaExtractor implements AnnotationExtractor {
	final static Logger logger = Logger.getLogger(KodaExtractor.class);

	String UR;
	String ontology;

	public KodaExtractor (String urKoda, String ontologyk)
	
	{
		this.UR = urKoda;
		this.ontology = ontologyk;
		
	}

	@Override
	public ArrayList<Annotation> generateAnnotationFromDocument(Document doc,
			String language) {
		// TODO Auto-generated method stub
		ArrayList<Annotation> AnnotANDdb = new ArrayList<Annotation>(); //structure to use to save annotation and db

		{			
		String myURL = null;
		try {
			myURL = this.UR+URLEncoder.encode(this.ontology, "UTF-8")+"&text="+URLEncoder.encode(doc.getDocText(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		String output = null;
		try {
			output = doHttpUrlConnectionAction(myURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// The ouput is a Stringbuilder we should write something to take only the words we  need
		InputSource source = new InputSource(new StringReader(output));

		 
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    org.w3c.dom.Document document = null;
		try {
			document = docBuilder.parse(source);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    NodeList listOfRelations = document.getElementsByTagName("annotation");

        for(int i=0; i<listOfRelations.getLength() ; i++) {

            Node firstRelation = listOfRelations.item(i); //the relation
            
                  NodeList listofnodes = firstRelation.getChildNodes();
                  for(int j=0; j< listofnodes.getLength() ; j++) {
                	  String term = null;
                	  String dbpedia = null;
	                      Node firstnodee = listofnodes.item(j);

	                      if (firstnodee.getNodeName() == "term")
	                	  {
	                			 term = firstnodee.getTextContent();

	                	  }
	                      if (firstnodee.getNodeName() == "disambiguation")
	                	  {
	                			 dbpedia = firstnodee.getTextContent();

	                	  }
		    	logger.info("insert the annotation and its dbpedia into the database");
		    	Annotation annot = new Annotation();
		    	AnnotationDao adao = new AnnotationDao();		    	  
		    	  
		    	annot.setAnnotation(term);
		    	annot.setdb(dbpedia);
		    	annot.setDocument(doc);
		    	annot.setindexa(doc.getDocText().indexOf(term));
		    	annot.seturia("http://www.list.lu/"+term.replaceAll("\\s", "_"));
		    	adao.addAnnotation(annot);
		    	annot.setlonga(term.length());

		    	AnnotANDdb.add(annot);			        	    
		      }


	      }}
		return AnnotANDdb;
	
	}

	@Override
	public void AnnoteCorpus(int idcorpus) {
		// TODO Auto-generated method stub
		
		 logger.info("Annotate the whole corpus");
		 SessionFactory sf = HibernateUtil.getSessionFactory();
		 Session session = sf.openSession();
		 session.beginTransaction();
		 CorpusDao cdao = new CorpusDao();
		 Corpus c = cdao.getCorpusById(idcorpus);
		 
		 Set <Document> dc = c.getDocuments();
		 
		 
		 for (Document doo :dc)
		 {
			 logger.info("Annotate the document"+doo.getIdDoc());
			 try {
				AnnoteDocument(doo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		

		 }
		
		
	}

	@Override
	public void AnnoteDocument(Document doc) {
		// TODO Auto-generated method stub

		{			
		String myURL = null;
		try {
			myURL = this.UR+URLEncoder.encode(this.ontology, "UTF-8")+"&text="+URLEncoder.encode(doc.getDocText(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		String output = null;
		try {
			output = doHttpUrlConnectionAction(myURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// The ouput is a Stringbuilder we should write something to take only the words we  need
		InputSource source = new InputSource(new StringReader(output));

		 
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    org.w3c.dom.Document document = null;
		try {
			document = docBuilder.parse(source);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    NodeList listOfRelations = document.getElementsByTagName("annotation");

        for(int i=0; i<listOfRelations.getLength() ; i++) {

            Node firstRelation = listOfRelations.item(i); //the relation
            
                  NodeList listofnodes = firstRelation.getChildNodes();
                  String term = null;
            	  String dbpedia = null;
                  
                  for(int j=0; j< listofnodes.getLength() ; j++) {
                	      Node firstnodee = listofnodes.item(j);

	                      if (firstnodee.getNodeName() == "term")
	                	  {
	                			 term = firstnodee.getTextContent(); 
	                			 logger.info("************"+term);
	                			 

	                	  }
	                      if (firstnodee.getNodeName() == "disambiguation")
	                	  {
	                			 dbpedia = firstnodee.getTextContent();

	                	  }
                  }
		    	logger.info("insert the annotation and its dbpedia into the database");
		    	Annotation annot = new Annotation();
		    	AnnotationDao adao = new AnnotationDao();		    	  
		    	  
		    	annot.setAnnotation(term);
		    	annot.setdb(dbpedia);
		    	annot.setDocument(doc);
		    	annot.setindexa(doc.getDocText().indexOf(term));
		    	annot.seturia("http://www.list.lu/"+term.replaceAll("\\s", "_"));
		    	annot.setlonga(term.length());
		    	adao.addAnnotation(annot);
		      


	      }}
		
	}
	

	public String doHttpUrlConnectionAction(String desiredUrl)
			  throws Exception
			  {
			    URL url = null;
			    BufferedReader reader = null;
			    StringBuilder stringBuilder;

			    try
			    {
			      // create the HttpURLConnection
			      url = new URL(desiredUrl);
			      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			      
			      // just want to do an HTTP GET here
			      connection.setRequestMethod("GET");
			      
			      // uncomment this if you want to write output to this url
			      //connection.setDoOutput(true);
			      
			      // give it 90 seconds to respond
			      connection.setReadTimeout(90*1000);

			      connection.connect();

			      // read the output from the server
			      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			      stringBuilder = new StringBuilder();

			      String line = null;
			      while ((line = reader.readLine()) != null)
			      {
			        stringBuilder.append(line + "\n");
			      }
			      return stringBuilder.toString();
			    }
			    catch (Exception e)
			    {
			      e.printStackTrace();
			      throw e;
			    }
			    finally
			    {
			      // close the reader; this can throw an exception too, so
			      // wrap it in another try/catch block.
			      if (reader != null)
			      {
			        try
			        {
			          reader.close();
			        }
			        catch (IOException ioe)
			        {
			          ioe.printStackTrace();
			        }
			      }



			    }}

}
