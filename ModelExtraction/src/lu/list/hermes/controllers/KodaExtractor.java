package lu.list.hermes.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;


import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		
			
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
			
		} 
		
		// The ouput is a Stringbuilder we should write something to take only the words we  need
		ArrayList<String> arrayList = new ArrayList(Arrays.asList((sb.toString().split("\",\""))));
		ArrayList<String> aList= arrayList;
	    
        String s1= ((String) aList.get(0)).replace("{\"",""); aList.set(0,s1);
	    String s2= ((String) aList.get(aList.size()-1)).replace("}",""); aList.set(aList.size()-1,s2);

	      
	      for(int i=0;i<aList.size();i++)
	      {   
		      ArrayList<String> aList1= new ArrayList(Arrays.asList((((String) aList.get(i)).split("\":\""))));
	    	  
		      for(int j=0 ;j<aList1.size()-1;j++)
		      {  
		       logger.info("insert the annotations and its Dbpedia into the database");
		    	
		    	Annotation annot = new Annotation();
		    	AnnotationDao adao = new AnnotationDao();		    	  
		    	  
		    	annot.setAnnotation(aList1.get(j).toString());
		    	annot.setdb(aList1.get(j+1).toString());
		    	annot.setDocument(doc);
		    	annot.setindexa(doc.getDocText().indexOf(aList1.get(j).toString()));
		    	annot.seturia("http://www.list.lu/"+aList1.get(j).toString().replaceAll("\\s", "_"));
		    	annot.setlonga(aList1.get(j).toString().length());
		    	adao.addAnnotation(annot);
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
			 AnnoteDocument(doo);
		
		

		 }
		
		
	}

	@Override
	public void AnnoteDocument(Document doc) {
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

		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		
			
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
		} 
		
		// The ouput is a Stringbuilder we should write something to take only the words we  need
		ArrayList<String> arrayList = new ArrayList(Arrays.asList((sb.toString().split("\",\""))));
		ArrayList<String> aList= arrayList;
	    
        String s1= ((String) aList.get(0)).replace("{\"",""); aList.set(0,s1);
	    String s2= ((String) aList.get(aList.size()-1)).replace("}",""); aList.set(aList.size()-1,s2);

	      
	      for(int i=0;i<aList.size();i++)
	      {   
		      ArrayList<String> aList1= new ArrayList(Arrays.asList((((String) aList.get(i)).split("\":\""))));
	    	  
		      for(int j=0 ;j<aList1.size()-1;j++)
		      {   
		    	logger.info("insert the annotation and its dbpedia into the database");
		    	Annotation annot = new Annotation();
		    	AnnotationDao adao = new AnnotationDao();		    	  
		    	  
		    	annot.setAnnotation(aList1.get(j).toString());
		    	annot.setdb(aList1.get(j+1).toString());
		    	annot.setDocument(doc);
		    	annot.setindexa(doc.getDocText().indexOf(aList1.get(j).toString()));
		    	annot.seturia("http://www.list.lu/"+aList1.get(j).toString().replaceAll("\\s", "_"));
		    	adao.addAnnotation(annot);
		    	AnnotANDdb.add(annot);			        	    
		      }


	      }}
		
	}}
