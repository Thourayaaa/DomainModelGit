package lu.list.hermes.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.utils.Charsets;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.log4j.Logger;

/**  find dbpedia relations that matchs the extracted relations
 * @author thourayabouzidi
 *
 */
public class RelationToDBpediaLinking {
	final static Logger logger = Logger.getLogger(RelationToDBpediaLinking.class);

	
	/** search using the namespace of dbpedia for a match between our relations and dbpedia uris
	 * @param relation
	 * @return
	 */
	public static List<String> searchDBpediaRelation(String relation)
	
	{
		logger.info("search for dbpedia relations ...");
		List<String> dbpediaURIs = new ArrayList<String>();
		String service = "http://dbpedia.org/sparql";

		String query  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                        +"PREFIX dbo: <http://dbpedia.org/ontology/>"
				        +"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"+
                         "SELECT ?uri ?namespace "+
                        "WHERE { "+
                         "?uri rdfs:label ?namespace . "+
                     "?namespace <bif:contains> \""+relation+"\"@en . }";
                     
		

		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    dbpediaURIs.add(s.getResource("?uri").toString());
		    
		}
		
		
		return dbpediaURIs;
	}
	public static  List<String>  cleanRelations (String pathname)
	{

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

		
	   return spoLines;
	}
	
	public static void searchdbpediaLinkForAll(String pathname)
	
	{
		ModelExtractor mextract = new ModelExtractor();
		List<String> spoLines = cleanRelations(pathname);
		 
		for (String line :spoLines)
		{
			Pattern patternrel = Pattern.compile("kr:.* ");
			Matcher matcher = patternrel.matcher(line);
			if(matcher.find())
			{
				String namespace = matcher.group(0).replaceAll("\\.|,|\\(|\\)", "");
		      String relation = namespace.substring(4, namespace.length());

		      List<String> dbpediaMatchingRelations = searchDBpediaRelation(relation);
		      
		      
			}

		}
		
		
	}
	

}
