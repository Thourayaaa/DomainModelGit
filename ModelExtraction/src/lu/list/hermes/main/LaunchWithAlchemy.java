package lu.list.hermes.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.util.HibernateUtil;
import lu.list.hermes.controllers.*;

public class LaunchWithAlchemy {
	final static Logger logger = Logger.getLogger(LaunchWithAlchemy.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String corpuspath = "/Users/thourayabouzidi/Desktop/guichetL";
		String CorpusName = "GuichetLuxembourg";
		CorpusController cc = new CorpusController();
		CorpusDao cdao = new CorpusDao();
		List<Corpus> listcorpus = cdao.getAllCorpus(); //control adding the corpus if it already exists then don't add it !
		int idc = 0 ;
		if (!(listcorpus.isEmpty())) //  the table corpus is not empty
		{
		for (Corpus cor : listcorpus) // check existing corpus in the database
		{ if (cor.getpath().equals(corpuspath) && cor.getCorpusName().equals(CorpusName))
		{
			idc = (int)cor.getIDc(); // if the corpus has been already added 
		} 
					
		}
		if (idc == 0) //no match between corpus we are going to add and the corpus in the database then we can add it !
		{
			try {
				idc= cc.addCorpus(corpuspath, CorpusName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		}
		else { // the corpus table is empty
			try {
				logger.info("add the corpus ..");
				idc= cc.addCorpus(corpuspath, CorpusName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			
		}

		
   //Alchemy
		AlchemyExtractor alchem = new AlchemyExtractor();
		logger.info(" Main : extract relations ..");
		KodaExtractor kextractor = new KodaExtractor("http://smartdocs.list.lu/kodaweb/rest/koda-1.0/annotate?ontology=","DBPEDIA_EN_EN");
		alchem.extractRelationsCorpus(idc);
		logger.info(" Main : extract annotations ..");

		kextractor.AnnoteCorpus(idc);
		
		logger.info("Main : generate NIF files ");
		NifFilesGenerator nifGenerator = new NifFilesGenerator();
		ConnectionAnnotRelation connectar = new ConnectionAnnotRelation();

		try {
			logger.info("Main : Generate  files containg relations between ollie and koda outputs...");

			nifGenerator.nifRelationCorpus(idc, "TestKoda", "NifOllie");
			nifGenerator.generateNifCorpusAnnotator(idc, "TestKoda", "NifKoda", "Koda");
			connectar.writeRelationfile(idc, "TestKoda", "TestKoda", "corpusdomain");

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
