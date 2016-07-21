package lu.list.hermes.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import lu.list.hermes.dao.DomainDao;
import lu.list.hermes.dao.ModelRelationDao;
import lu.list.hermes.dao.RangeDao;
import lu.list.hermes.models.Domain;
import lu.list.hermes.models.ModelRelation;
import lu.list.hermes.models.RelationRange;

/** class to build the final model using extracted relations : relation, domain, model
 * @author thourayabouzidi
 *
 */
public class ModelBuilder {
	
	 
	/**  Extract the domain superclasses of the input relation
	 * @param idrel
	 * @return
	 */
	public List<String> generatesubclassesDomain (int idrel)
	{
		List<String> subClasses = new ArrayList<String>();
		DomainDao rdao = new DomainDao();
		List<Domain> rlist =  rdao.getAllDomainsByRelid(idrel);
		
		String service = "http://dbpedia.org/sparql";

		Iterator<Domain> iter = rlist.iterator();
		 while (iter.hasNext())
				 {
			 Domain rr = iter.next();
			 String uri = rr.getdomainURI();
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
                + "SELECT ?class "
                + "WHERE { "+uri+" rdfs:subclassOf  ?class. "
                + "FILTER( regex(str(?type), \"^http://dbpedia.org/\"))}";

		System.out.println(query);
		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    subClasses.add(s.getResource("?class").toString());
		}
		
				 }
		
		return subClasses;
		
	}
	
	
	
	
	
	
	
	
	
	/** generate the range superclasses of the input relation
	 * @param idrel
	 * @return
	 */
	public List<String> generatesubclassesRange (int idrel)
	{
		List<String> subClasses = new ArrayList<String>();
		RangeDao rdao = new RangeDao();
		List<RelationRange> rlist =  rdao.getAllRangesByRelid(idrel);
		
		String service = "http://dbpedia.org/sparql";

		Iterator<RelationRange> iter = rlist.iterator();
		 while (iter.hasNext())
				 {
			 RelationRange rr = iter.next();
			 String uri = rr.getrangeURI();
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
                + "SELECT ?class "
                + "WHERE { "+uri+" rdfs:subclassOf  ?class. "
                + "FILTER( regex(str(?type), \"^http://dbpedia.org/\"))}";

		System.out.println(query);
		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    subClasses.add(s.getResource("?class").toString());
		}
		
				 }
		
		return subClasses;

		
	}
	
    /** based on the approach that if two relations has the same
     * (or almost) the same
     * 
     */
    public void findSimilarRelations ()
    {
    	ModelRelationDao mrdao = new ModelRelationDao();
    	List<ModelRelation> listrelation = mrdao.getAllModelRelation();
		
		Iterator<ModelRelation> iter = listrelation.iterator();
		 while (iter.hasNext())
				 {
			 ModelRelation mr = iter.next();
			 int id = (int) mr.getiDr();
			 List<String> rlist = generatesubclassesRange(id);
			 List<String> dlist  = generatesubclassesDomain (id);

			 
				 }
    }
			
		}


