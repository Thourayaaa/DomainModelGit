package lu.list.hermes.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.util.HibernateUtil;
import lu.list.hermes.controllers.*;

public class LaunchAll {
	final static Logger logger = Logger.getLogger(LaunchAll.class);

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
		}
		else {
			try {
				idc= cc.addCorpus(corpuspath, "corpusdomain");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			
		}

		if (idc == 0)
		{
			try {
				idc= cc.addCorpus(corpuspath, "corpusdomain");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
   //Ollie 
		OllieExtractor ollie = new OllieExtractor("engmalt.linear-1.7.mco");
		ollie.extractRelationsCorpus(idc);
		KodaExtractor kextractor = new KodaExtractor("http://smartdocs.list.lu/kodaweb/rest/koda-1.0/annotate?ontology=","DBPEDIA_EN_EN");
		kextractor.AnnoteCorpus(idc);
		NifFilesGenerator nifGenerator = new NifFilesGenerator();
		ConnectionAnnotRelation connectar = new ConnectionAnnotRelation();

		try {
			nifGenerator.nifRelationCorpus(idc, "OllieNif", "NifOllie");
			nifGenerator.generateNifCorpusAnnotator(idc, "KodaNif", "NifKoda", "Koda");
			connectar.writeRelationfile(idc, "OllieNif", "OllieNif", "corpusdomain");

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
