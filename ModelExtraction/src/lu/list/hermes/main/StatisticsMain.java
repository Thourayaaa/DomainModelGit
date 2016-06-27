package lu.list.hermes.main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import lu.list.hermes.controllers.StatisticsGenerator;

import org.apache.log4j.Logger;

public class StatisticsMain {
	
	final static Logger logger = Logger.getLogger(StatisticsMain.class);

	public static void main(String[] args) {
		StatisticsGenerator sg = new StatisticsGenerator();
		StringBuilder sb = new StringBuilder();
		/*ollie values */
		logger.info(" Main :This is what Ollie gives: ");
		int totalspo = sg.calculateSPO("KodaAndOllie/JustKodaRel.txt");
		int allrelations = sg.calculateRelations("KodaAndOllie/JustKodaRel.txt");
		int duplicatedSpo = sg.calculateSpoDuplication("KodaAndOllie/JustKodaRel.txt");
		 int uniqueRel = sg.calculateUniqueRelations("KodaAndOllie/JustKodaRel.txt");
		int uniqueObj = sg.calculateUniqueObject("KodaAndOllie/JustKodaRel.txt");
		int uniqueSubj = sg.calculateUniqueSubject("KodaAndOllie/JustKodaRel.txt");

		
		sb.append("Ollie :** totalSpo is "+totalspo+" **all used relations : "+allrelations+ " **duplicated spo :"+ duplicatedSpo
				+"**relations only used once: "+uniqueRel+"**Subjects only used once : "+uniqueSubj+"**Objects only used once : "+uniqueObj+"\n");
		/*Alchemy values */
		logger.info(" Main : This is what Alchmey gives: ");
		int totalspo1 = sg.calculateSPO("KodaAndAlchemy/JustKodaRel.txt");
		int allrelations1 = sg.calculateRelations("KodaAndAlchemy/JustKodaRel.txt");
		int duplicatedSpo1 = sg.calculateSpoDuplication("KodaAndAlchemy/JustKodaRel.txt");
		 int uniqueRel1 = sg.calculateUniqueRelations("KodaAndAlchemy/JustKodaRel.txt");
		int uniqueObj1 = sg.calculateUniqueObject("KodaAndAlchemy/JustKodaRel.txt");
		int uniqueSubj1 = sg.calculateUniqueSubject("KodaAndAlchemy/JustKodaRel.txt");

		sb.append("Alchmey :** totalSpo is "+totalspo1+" **all used relations : "+allrelations1+
				" **duplicated spo :"+ duplicatedSpo1
				+"**relations only used once: "+uniqueRel1+"**Subjects only used once : "+uniqueSubj1+
				"**Objects only used once : "+uniqueObj1+"\n");
		

		
		/*OpenIE values */
		logger.info(" Main :This is what OpenIE gives: ");
		int totalspo2 = sg.calculateSPO("KodaAndOpenie/JustKodaRel.txt");
		int allrelations2 = sg.calculateRelations("KodaAndOpenie/JustKodaRel.txt");
		int duplicatedSpo2 = sg.calculateSpoDuplication("KodaAndOpenie/JustKodaRel.txt");
		 int uniqueRel2 = sg.calculateUniqueRelations("KodaAndOpenie/JustKodaRel.txt");
		int uniqueObj2 = sg.calculateUniqueObject("KodaAndOpenie/JustKodaRel.txt");
		int uniqueSubj2 = sg.calculateUniqueSubject("KodaAndOpenie/JustKodaRel.txt");
		sb.append("OpenIE :** totalSpo is "+totalspo2+" **all used relations : "+allrelations2+
				" **duplicated spo :"+ duplicatedSpo2
				+"**relations only used once: "+uniqueRel2+"**Subjects only used once : "+uniqueSubj2+
				"**Objects only used once : "+uniqueObj2+"\n");
	
		PrintWriter out = null;
		try {
			out = new PrintWriter("Statistics.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        out.println(sb.toString());
        out.close();

		
		
	}
	}



