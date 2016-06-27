package lu.list.hermes.main;

import org.apache.log4j.Logger;

import lu.list.hermes.controllers.CorpusController;
import lu.list.hermes.controllers.RelationToDBpediaLinking;

public class dbpediaRelationMatcher {
	final static Logger logger = Logger.getLogger(dbpediaRelationMatcher.class);

	public static void main(String[] args) {
		logger.info("find match between relations in the input file and dbpedia uris ...");
		
		 RelationToDBpediaLinking Reldbpedia = new RelationToDBpediaLinking();
		 Reldbpedia.searchdbpediaLinkForAll("KodaAndOllie/JustKodaRel.txt");
		
		
		
		
	}

}