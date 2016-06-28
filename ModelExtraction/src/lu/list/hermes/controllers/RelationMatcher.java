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
                         "SELECT ?uri ?namespace "+
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
				  String relation = namespace.replaceAll("^kr:_|^kr:", "");
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
			AllRelations.remove("kr:<http://wwwlistlu/kr#>");
			AllRelations.remove("<http://wwwlistlu/kr#>");

		
	   return AllRelations;
	}
	
	public void searchdbpediaLinkForAll(String pathname, String outputpath)
	
	{
		ModelExtractor mextract = new ModelExtractor();
		List<String> spoLines = cleanRelations(pathname);
		 StringBuilder sb = new StringBuilder();
		for (String relation :spoLines)
		{

			System.out.println("********************"+relation);

		      List<String> dbpediaMatchingRelations = searchDBpediaRelation(relation);
		      for (int i=0; i<dbpediaMatchingRelations.size();i++)
		      {
		      sb.append(relation+" the match is :" + dbpediaMatchingRelations.get(i) +"\n");
		      }
		      
		      
		//	}

				}
		
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
