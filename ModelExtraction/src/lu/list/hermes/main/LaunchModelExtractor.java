package lu.list.hermes.main;

import java.util.List;

import org.apache.log4j.Logger;

import lu.list.hermes.controllers.KodaExtractor;
import lu.list.hermes.controllers.ModelExtractor;

public class LaunchModelExtractor {
	final static Logger logger = Logger.getLogger(LaunchModelExtractor.class);

	public static void main(String[] args) {
		
		ModelExtractor me = new ModelExtractor();
	    me.unifyRelations("KodaAndOllie/JustKodaRel.txt");
	    logger.info("done !");
 
}


}
