package lu.list.hermes.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import lu.list.hermes.dao.DomainDao;
import lu.list.hermes.dao.ModelRelationDao;
import lu.list.hermes.dao.RangeDao;
import lu.list.hermes.models.Domain;
import lu.list.hermes.models.ModelRelation;
import lu.list.hermes.models.RelationRange;
import lu.list.hermes.util.HibernateUtil;

import org.apache.commons.compress.utils.Charsets;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.Morphology;
import edu.stanford.nlp.trees.WordStemmer;

/** this class organize ollie relations: identifier, range and domain.
 * @author thourayabouzidi
 *
 */
public class ModelExtractor {
	
	final static Logger logger = Logger.getLogger(ModelExtractor.class);

	/** get the base form of the verbs in the relation part
	 * @param relation
	 * @return
	 */
	public  String getBareinfinitive(String relation)
	{
		System.setProperty("wordnet.database.dir", "dict/");
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		Morphology id = Morphology.getInstance();
		StringBuilder sb = new StringBuilder();
		for (String word : relation.split("\n|\\s"))
		{
		    String[] arr = id.getBaseFormCandidates(word, SynsetType.VERB);
		    
		    
		   logger.info(word);
		    if (arr.length != 0)
		    		{
			String verb = arr[0].toString();
		   
		    sb.append(verb+" ");
		   
		    		}
		    else
		    {
		    	sb.append(word+" ");
		    }
		    
			}

		
		return sb.toString();
	}
	
	/** delete duplicated lines
	 * @param pathname
	 * @return
	 */
	public  List<String>  cleanRelations (String pathname)
	{
		   logger.info(" start Clean relations ..");

		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		 List<String> spoLines =  new ArrayList<String>();
		 HashSet<String> set1 =  new HashSet<String>();
 
		for (String spo : lines) {
			if (!(set1.add(spo))) {
				( spoLines).add(spo);
			}
		}

		
		   logger.info("  clean relations done ..");

	   return spoLines;
	}
	/**check if the relation has been added or not and returns the relation that matches the input base form .
	 * @param baseform
	 * @return
	 */
	public  int checkRelationExist(String baseform)

	{
		logger.info("check if relation has been already found ..");
		ModelRelationDao mdao = new ModelRelationDao();
		List<ModelRelation> listmr = mdao.getAllModelRelation();
		Boolean exist = false;
		int idmr = 0;
		for (ModelRelation mr : listmr)
		{
			if (mr.getBaseForm().equals(baseform))
			{
				exist = true;
				idmr = (int)mr.getiDr();
				
			}
		}
		return idmr;
	}
	
	/** check if the input dbpedia uri is already added to the table domain in the database
	 * @param subjuri
	 * @return
	 */
	public  boolean checkDomainExist (String subjuri)
	{
		logger.info("check if this domain has been already added to the relation domain ..");
		DomainDao  ddao = new DomainDao();
		List<Domain> listDomain = ddao.getAllDomain();
		    Boolean exist = false;

		for (Domain domain: listDomain)
			if (domain.getdomainURI().equals(subjuri))
			{
				exist = true;
				break;
				
			}
		
		
		return exist;
		
	}
	
	
	/** check if the input dbpedia uri is already added to the table range in the datatabse
	 * @param subjuri
	 * @return
	 */
	public  boolean checkRangeExist (String objuri)
	{
		
		logger.info("check if this range has been already added to the relation range ");
		RangeDao  ddao = new RangeDao();
		List<RelationRange> listrange = ddao.getAllRange();
		    Boolean exist = false;

		for (RelationRange range: listrange)
			if (range.getrangeURI().equals(objuri))
			{
				exist = true;
				break;
				
			}
		
		
		return exist;
		
	}
	/**get the rdf type of the input dbpedia uri
	 * @param uri
	 * @return
	 */
	public  List<String> getRDFtype(String uri)
	{
		
		logger.info("get RDF type of : "+ uri);
		List<String> rdfType = new ArrayList<String>();
		String service = "http://dbpedia.org/sparql";

		String query  = "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			   // +"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
//				+"PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
//				+"PREFIX dc: <http://purl.org/dc/elements/1.1/>"
//				+"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+"PREFIX : <http://dbpedia.org/resource/>"
				+"PREFIX ru: <http://ru.dbpedia.org/resource/>"
				+"PREFIX dbpedia2: <http://dbpedia.org/property/>"
				+"PREFIX dbpedia: <http://dbpedia.org/>"
				+"PREFIX dbo: <http://dbpedia.org/ontology/>"
                + "SELECT ?type "
                + "WHERE { "+uri+" rdf:type ?type. }";
		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    rdfType.add(s.getResource("?type").toString());
		}
	
		return rdfType;
	}
	
	/** Unify relations found in the input file : delete duplications, unify based on the base form, find range and domain
	 * @param pathname
	 */
	public void unifyRelations(String pathname) 
	
	{ 
        logger.info("start unifying relations in this file"+ pathname);
		List<String> spoLines = cleanRelations(pathname);
	 
	for (String line :spoLines)
	{
		Pattern patternrel = Pattern.compile("kr:.* ");
		Pattern patternsubj = Pattern.compile("<.*>\t");
		Pattern patternobj = Pattern.compile(" <.*>.");

		Matcher matcher = patternrel.matcher(line);
		Matcher matcher1 = patternsubj.matcher(line);
		Matcher matcher2 = patternobj.matcher(line);
		

		if(matcher.find() && (matcher1.find()) && (matcher2.find()))
		{
	         
			 String relation = matcher.group(0).replace("_", "\\S").substring(4, matcher.group(0).replace("_", "\\S").length());
	         String subject = matcher1.group(0);
	         String object = matcher2.group(0).substring(0, matcher2.group(0).length() -1);
            
	         List <String> domainlist = getRDFtype(subject); //relation domain
	         List<String> rangelist = getRDFtype(object); //relation range
	         
	         //bare infinitive of the verb in the relation 
	         String infinitive = getBareinfinitive(relation);
	          /*before adding the relation make sure it doesn't exist in the database based on the base form field
	         if it doesn't then just add the domain and range and make sure you don't add the same domain and range twice*/
	         ModelRelation  mr = new ModelRelation();
	         ModelRelationDao mddao= new ModelRelationDao();
	         
	         int idmr = checkRelationExist(infinitive);
	         
	         if (idmr == 0)
	        	 
	         {
	        	 // add relation
	        	 mr.setRelationName(relation);
	        	 mr.setIdentifier(relation.replaceAll("_", ""));
	        	 mr.setBaseform(infinitive);
	        	 mddao.addModelRelation(mr);
	        	 
	        	 idmr = (int)mr.getiDr();
	        	 
	         }
	         ModelRelation Modelr = mddao.getModelRelationById(idmr);
	        
	      // add domain entities 
	         logger.info("add the domain ..");
        	 for (String dom: domainlist )
        	 {
        			DomainDao ddao = new DomainDao();

        		Boolean exist = checkDomainExist(dom);
        		if (!exist)
        		{
        			Domain d = new Domain();
        			d.setdomainURI(dom);
        			d.setmodelrelation(Modelr);
        			ddao.addDomain(d);
        		}
        	 }
        		 
        	 //add range entities
	         logger.info("add the range ..");

        	 for (String range: rangelist )
        	 {
        			RangeDao randao = new RangeDao();

        		Boolean exist = checkRangeExist(range);
        		if (!exist)
        		{
        			RelationRange r = new RelationRange();
        			r.setrangeURI(range);
        			r.setmodelrelation(Modelr);
        			randao.addRange(r);
        		}
        	 }
	  
		}
	}
	
	

		
		
	}
}
