package lu.list.hermes.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.util.HibernateUtil;
import lu.list.hermes.controllers.*;

public class LaunchWithOllie {
	final static Logger logger = Logger.getLogger(LaunchWithOllie.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String corpuspath = "/Users/thourayabouzidi/Desktop/Guichet";
		CorpusController cc = new CorpusController();
		CorpusDao cdao = new CorpusDao();
		List<Corpus> listcorpus = cdao.getAllCorpus(); //control adding the corpus if it already exists then don't add it !
		int idc = 0 ;
		if (!(listcorpus.isEmpty())) //  the table corpus is not empty
		{
		for (Corpus cor : listcorpus) // check existing corpus in the database
		{ if (cor.getpath().equals(corpuspath))
		{
			idc = (int)cor.getIDc(); // if the corpus has been already added 
		} 
					
		}
		if (idc == 0)
		{
			try {
				idc= cc.addCorpus(corpuspath, "GuichetDomain");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		}
		else {
			try {
				idc= cc.addCorpus(corpuspath, "corpusdomain");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			
		}

		
   //Ollie 
		OllieExtractor ollie = new OllieExtractor();
		ollie.extractRelationsCorpus(idc);
		KodaExtractor kextractor = new KodaExtractor("http://smartdocs.list.lu/kodaweb/rest/koda-1.0/annotate?ontology=","YAGO-EN");
		kextractor.AnnoteCorpus(idc);
		NifFilesGenerator nifGenerator = new NifFilesGenerator();
		ConnectionAnnotRelation connectar = new ConnectionAnnotRelation();

		try {
			nifGenerator.nifRelationCorpus(idc, "YAGO/Ollie", "NifOllie");
			nifGenerator.generateNifCorpusAnnotator(idc, "YAGO/Ollie", "NifKoda", "Koda");
			connectar.writeRelationfile(idc, "YAGO/Ollie", "YAGO/Ollie", "corpusdomain");

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
