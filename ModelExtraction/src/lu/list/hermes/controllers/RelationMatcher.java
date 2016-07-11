package lu.list.hermes.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
public class RelationMatcher {
	final static Logger logger = Logger.getLogger(RelationMatcher.class);

	
	/** search using the namespace of dbpedia for a match between our relations and dbpedia uris
	 * @param relation
	 * @return
	 */
	public  List<String> searchDBpediaRelation(String relation)
	
	{
		logger.info("search for dbpedia relations ...");
		List<String> dbpediaURIs = new ArrayList<String>();
		String service = "http://dbpedia.org/sparql";

		String query  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                        +"PREFIX dbo: <http://dbpedia.org/ontology/>"
				        +"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"+
                         "SELECT DISTINCT ?uri  "+
                        "WHERE { "+
                         "?uri rdfs:label ?namespace . "+
                        "?namespace <bif:contains> \""+relation+"\"@en ."
                        +"FILTER (lcase(str(?namespace)) = \""+relation+"\")"
                        + "FILTER( regex(str(?uri), \"^http://dbpedia.org/\"))}";


		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    dbpediaURIs.add(s.getResource("?uri").toString());

		    
		}
		
		  
		return dbpediaURIs;
	}
	

	/** search using yago predicates for a match between our relations and Yago uris
	 * @param relation
	 * @return
	 */
	public  List<String> searchYagoRelation(String relation)
	
	{
		logger.info("search for yago relations ...");
		String service = "http://linkeddata1.calcul.u-psud.fr/sparql";

		List<String> yagoURIs = new ArrayList<String>();
		String[] words = relation.split("\\s+|_");
		StringBuilder sb = new StringBuilder();
		sb.append(words[0]);
		for (int i = 1; i < words.length; i++) {
			logger.info(words[i]);
			if (words[i].length() > 1)
			{
			sb.append(words[i].substring(0, 1).toUpperCase() +words[i].substring(1));
			} 
			else if ( words[i].length() == 1)
			{
				sb.append(words[i].substring(0, 1).toUpperCase());

			}
			
		}
		String yagoRelation = sb.toString();
		System.out.println(yagoRelation);

		String query  = "PREFIX yago: <http://yago-knowledge.org/resource/>"+
                        "SELECT DISTINCT ?p2  WHERE { ?p1 ?p2 ?p3. FILTER (?p2 = yago:"+yagoRelation+")}";
		
		System.out.println(query);



		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    System.out.println(s.getResource("?p2").toString());
		    

		    
		}

		
		  
		return yagoURIs;
	}

	
	
	
	
	
/** Look for a match for the whole relations in the input file and save the match in the output path
 * @param pathname
 * @param outputpath
 */
public void searchYagoLinkForAll(String pathname, String outputpath)
	
	{
		ModelExtractor mextract = new ModelExtractor();
		List<String> spoLines = cleanRelations(pathname);
		 StringBuilder sb = new StringBuilder();
		for (String relation :spoLines)
		{


		      List<String> dbpediaMatchingRelations = searchYagoRelation(relation);
		      for (int i=0; i<dbpediaMatchingRelations.size();i++)
		      {
		      sb.append(relation+" the match is :" + dbpediaMatchingRelations.get(i) +"\n");
		      }
		      
		      
		//	}

				}
		
		logger.info("save the existing relations ..");
		PrintWriter out1;
		try {
			  out1 = new PrintWriter(outputpath+"/Yagorelations.txt");
			  out1.println(sb.toString());
			  out1.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
	}
	
	

	/** clean the relation : extract just the namespace in order to use it in the research process 
	 * @param pathname
	 * @return
	 */
	public  List<String>  cleanRelations (String pathname)
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
		 List<String> AllRelations =  new ArrayList<String>();

			for (String spo : lines) {
				Pattern patternrel = Pattern.compile("kr:.* ");
				Matcher matcher = patternrel.matcher(spo);
				if(matcher.find())
				{
				  String namespace = matcher.group(0).replaceAll("\\.|,|\\(|\\)", "").replaceAll("\\s+", "");
				  String relation = namespace.replaceAll("^kr:", "");
			      //String relation = namespace.substring(3, namespace.length());
			      AllRelations.add(relation);
			    
			}
			}
			List <String>list = new ArrayList<String>(new LinkedHashSet<String>(AllRelations));
			list.remove(0); // remove the <http://www.list.lu/kr#>

 

			Set<String> hs = new HashSet<>();
			hs.addAll(AllRelations);
			AllRelations.clear();
			AllRelations.addAll(hs);
			AllRelations.remove("<http://wwwlistlu/kr#>");

		
	   return AllRelations;
	}
	
	/**Look for a match for the whole relations in the input file and save the match in the output path
	 * @param pathname
	 * @param outputpath
	 */
	public void searchdbpediaLinkForAll(String pathname, String outputpath)
	
	{
		ModelExtractor mextract = new ModelExtractor();
		List<String> spoLines = cleanRelations(pathname);
		 StringBuilder sb = new StringBuilder();
		for (String relation :spoLines)
		{		      



		      List<String> dbpediaMatchingRelations = searchDBpediaRelation(relation);
		      for (int i=0; i<dbpediaMatchingRelations.size();i++)
		      {
		      sb.append(relation+" the match is :" + dbpediaMatchingRelations.get(i) +"\n");
		      }
		      
		      
		//	}

				}
		logger.info("save relations into the file ...");
		PrintWriter out1;
		try {
			  out1 = new PrintWriter(outputpath+"/dbpediarelations.txt");
			  out1.println(sb.toString());
			  out1.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
	}
	

}
