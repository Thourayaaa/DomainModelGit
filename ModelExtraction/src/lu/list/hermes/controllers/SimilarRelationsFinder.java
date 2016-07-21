package lu.list.hermes.controllers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lu.list.hermes.dao.DomainDao;
import lu.list.hermes.dao.ModelRelationDao;
import lu.list.hermes.dao.RangeDao;
import lu.list.hermes.models.Domain;
import lu.list.hermes.models.ModelRelation;
import lu.list.hermes.models.RelationRange;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.Morphology;


public class SimilarRelationsFinder {

	/** find verb using WordNet dict directory
	 * @param relation
	 * @return
	 */
	public String findVerb (String relation)
	{

		String verb = null;
		System.setProperty("wordnet.database.dir", "dict/");
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		Morphology id = Morphology.getInstance();
		StringBuilder sb = new StringBuilder();
		for (String word : relation.split("\n|\\s"))
		{
		    String[] arr = id.getBaseFormCandidates(word, SynsetType.VERB);
		    if (arr.length != 0)
			{
	           verb = arr[0].toString();
			}
		}
		
		return verb;
	}
	
	public List<String> findSynonyms (String verb)
	{
		List<String> synonyms = new ArrayList<String>();
		System.setProperty("wordnet.database.dir", "dict/");
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		  Synset[] synsets = database.getSynsets(verb,SynsetType.VERB);
		//  Display the word forms and definitions for synsets retrieved
		if (synsets.length > 0)
		{
			for (int i = 0; i < synsets.length; i++)
			{
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)
				{
					synonyms.add(wordForms[j]);
					
				}
			}
		}
		else
		{
			System.err.println("No synsets exist that contain " +
					"the word form '" + verb + "'");
		}
	
		return synonyms;
		
	}
	
	public List<String> generateSuperClassesDomain (int relationid)
	{
		List<String> superclassesDomain = new ArrayList<String>();
		
		DomainDao rdao = new DomainDao();
		List<Domain> rlist =  rdao.getAllDomainsByRelid(relationid);
		
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
		superclassesDomain.add(Integer.toString(relationid));
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    superclassesDomain.add(s.getResource("?class").toString());
		}
		
				 }
		return superclassesDomain;
		
	}
	
	
	public List<String> generateSuperClassesRange (int idrel)
	{
		List<String> subClassesrange = new ArrayList<String>();
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
		subClassesrange.add(Integer.toString(idrel));
		while (rs.hasNext()){
		    QuerySolution s= rs.nextSolution();
		    subClassesrange.add(s.getResource("?class").toString());
		}
		
				 }
		
		return subClassesrange;

		
	}
	
	
	/** calculate the similarity using common domains or ranges of two relations : in order to test if we should
	 * consider them as one relation or not.
	 * @param Domainrela1
	 * @param Domainrela2
	 * @return
	 */
	public int calculateSimilarity (List<String> Domainrela1, List<String> Domainrela2)
	{
		int commonRelNumber =0;
		int similarityPourcentage =0;
		for (int i= 1;i<Domainrela1.size();i++)
		{
			if (Domainrela2.contains(Domainrela1.get(i)))
			{
				commonRelNumber++;
			}
		}
		similarityPourcentage = commonRelNumber / ((Domainrela1.size()+Domainrela2.size())/2);
		return similarityPourcentage;
	}
	
	public List<String> testRelationadded (String relation, List<List<String>> wholeModel)

	{
		
		// look for the verb in the sentence : parcourir  tout le tableau to find a match ! 
		//we suppose that a sentence contains one causal verb
		Boolean added = false;
		List<String> existingList = new ArrayList<String>();
		for (int i =0; i< wholeModel.size();i++)
		{
			List<String> verbList = wholeModel.get(i);
			for (int j=0; j< verbList.size();j++)
			{
				
				String verb = verbList.get(j);
				if (verb.equals(relation))
				{
					//List<SomeBean> wsListCopy=new ArrayList<SomeBean>(wsList);  
					for (String rel:verbList )
					{
						existingList.add(rel);
					}

				}			
			}
			
		}
		
		return existingList;		
	}
	
	/** for the input relation the 
	 * @param idrel
	 * @param wholeModel
	 * @return
	 */
	public List<List<String>> buildModel (int idrel, List<List<String>> wholeModel)
	{
		
		List<String> similarRelations = new ArrayList<String>();
		
		ModelRelationDao mrdao = new ModelRelationDao();
		ModelRelation mr = mrdao.getModelRelationById(idrel);
		String theRelation = mr.getRelationName();
		String verb = findVerb(theRelation);
		List <String> verbSynonyms = findSynonyms (verb);
		List<String> RangeSuperClasses = generateSuperClassesRange(idrel);
		List<String> DomainSuperClasses = generateSuperClassesDomain(idrel);
		List<ModelRelation> allRelations = mrdao.getAllModelRelation();
        allRelations.remove(mr);
        for (int i=0; i<  allRelations.size();i++)
        {
        	ModelRelation relm = allRelations.get(i);
        	int idrelation = (int) relm.getiDr();
        	String relationText = relm.getRelationName();
        	String verbrel = findVerb(relationText);
        	
        	List<String> testExistingList = testRelationadded(verbrel, wholeModel);
        	if ((verbSynonyms.contains(verbrel)))
        	{
        		List<String> RangeSuperClass = generateSuperClassesRange(idrelation);
        		List<String> DomainSuperClass = generateSuperClassesDomain(idrelation);
        		int similarityDomain =  calculateSimilarity(DomainSuperClasses,DomainSuperClass) ;    
        		int similarityRange =  calculateSimilarity(RangeSuperClasses,RangeSuperClass) ; 
        		int similarity = (similarityDomain +similarityRange)/2;
        		if (similarity > 0.7)
        		{
        			if ((testExistingList.isEmpty()))
        			{	//we can make a model !
        			//test if the relation loula fel input mahich mawjouda 
        			similarRelations.add(relationText);
        			similarRelations.add(theRelation);
        			
        			}
            		wholeModel.add(similarRelations);
            		 if ( (!testExistingList.isEmpty()))
            		{
            			 wholeModel.remove(testExistingList);
            			 testExistingList.add(theRelation);
            			 wholeModel.remove(testExistingList);       			 
            		}
        		}
        		
        	}
        	// it is new ! no match so we have a new relation to add to the final model
        	else 
        	{
    			similarRelations.add(theRelation);
   			     wholeModel.add(testExistingList);


        		
        	}
        }	
		
		return wholeModel;		
		
	}
	
	/** this function builds the final model with different relations and their labels
	 * @return
	 */
	List<List<String>> buildFinalModel()
	{
		List<List<String>> relationsModel = new ArrayList<List<String>>();
		ModelRelationDao mrdao = new ModelRelationDao();
		List<ModelRelation> allRelations = mrdao.getAllModelRelation();
		for (int i=0; i<allRelations.size();i++)
		{
			ModelRelation mrel = allRelations.get(i);
			int idrel = (int) mrel.getiDr();
			String relText = mrel.getRelationName();
			//test not added !
			List<String>  alreadyadded = testRelationadded(relText , relationsModel);
			if (alreadyadded.isEmpty())
			{
				relationsModel = buildModel(idrel, relationsModel);
			}
			
		}
		return relationsModel;
	}
		
}
