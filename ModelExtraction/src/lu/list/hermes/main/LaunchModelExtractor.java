package lu.list.hermes.main;


import org.apache.log4j.Logger;
import lu.list.hermes.controllers.ModelExtractor;

public class LaunchModelExtractor {
	final static Logger logger = Logger.getLogger(LaunchModelExtractor.class);

	public static void main(String[] args) {
		
		logger.info("Build the domain model: relation - range - domain");
		ModelExtractor me = new ModelExtractor();
	    me.unifyRelations("KodaAndOpenie/JustKodaRel.txt");
	    logger.info("done !");
	    logger.info("the installation is done");

 
}


}
