package lu.list.hermes.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class RelationToDBpediaLinking {
	
	public static List<String> searchDBpediaRelation(String relation)
	
	{
		List<String> dbpediaURIs = new ArrayList<String>();
		String service = "http://dbpedia.org/sparql";

		String query  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                        +"PREFIX dbo: <http://dbpedia.org/ontology/>"+
                         "SELECT ?uri ?namespace"+
                        "WHERE { "+
                         "?uri rdfs:label ?namespace . "+
                     "?namespace <bif:contains> \""+relation+"\"@en .}";
                     

		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    dbpediaURIs.add(s.getResource("?uri").toString());
		    System.out.print(s.getResource("?uri").toString()+"\n");
		}
		
		
		return dbpediaURIs;
	}
	
	public void SearchdbpediaLinkForAll()
	{
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		List<String> dbrelations = searchDBpediaRelation("Paris");
		
		
		
		
	}

}
