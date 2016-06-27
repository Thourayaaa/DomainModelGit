package lu.list.hermes.main;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import lu.list.hermes.controllers.KodaExtractor;
import lu.list.hermes.controllers.ModelExtractor;

public class LaunchModelExtractor {
	final static Logger logger = Logger.getLogger(LaunchModelExtractor.class);

	public static void main(String[] args) {
		
		System.out.print("hey");
		logger.info("Build the domain model : relation - range - domain");
		ModelExtractor me = new ModelExtractor();
	    me.unifyRelations("KodaAndOllie/JustKodaRel.txt");
	    logger.info("done !");
		System.out.print("hey");

 
}


}
