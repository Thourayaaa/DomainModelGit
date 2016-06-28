package lu.list.hermes.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.utils.Charsets;
import org.apache.log4j.Logger;

import scala.collection.Set;

/**  This class implements different functions that returns statistics about different output files using
 * different relation extractor tools 
 * @author thourayabouzidi
 *
 */
public class StatisticsGenerator {
	
	final static Logger logger = Logger.getLogger(StatisticsGenerator.class);




	
	/** Calculate number of total relations in the output file
	 * @param pathname
	 * @return
	 */
	public int calculateSPO (String pathname)
	{
		int spoNumber =0;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(pathname);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LineNumberReader l = new LineNumberReader(       
		       new BufferedReader(new InputStreamReader(fis)));
		              String str;
					try {
						while ((str=l.readLine())!=null)
						 {
						    spoNumber = l.getLineNumber();
						 }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		          
		  logger.info("triplets number is"+ spoNumber);
		return spoNumber;
	}
	
	
	/** Calculate total relations number (without duplication) in the output file
	 * @param Pathname
	 * @return
	 */
	public int calculateRelations (String pathname)
	{
		int relationsNumber=0;
		List<String> AllRelations = new ArrayList<String>();
		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String line :lines)
		{
			
			Pattern pattern = Pattern.compile("kr:.* ");
			Matcher matcher = pattern.matcher(line);


			if (matcher.find())
			{
                AllRelations.add(matcher.group(0));
				 
					}
						
				}
				
		HashSet<String> set = new HashSet<>();
		List<String> result = new ArrayList<>();


		// Loop over argument list.
		for (String item : AllRelations) {

		    // If String is not in set, add it to the list and the set.
		    if (!set.contains(item)) {
			result.add(item);
			set.add(item);
		    }
		}
		AllRelations.remove("kr:<http://wwwlistlu/kr#>");
		AllRelations.remove("<http://wwwlistlu/kr#>");
             

			
		
		
   	 logger.info("number of total used relations without duplication is"+result.size());

		
		return result.size();
		
	}
	
	
	/** calculate the number of duplicated lines (subject, relation, object)
	 * @param pathname
	 * @return
	 */
	public int calculateSpoDuplication(String pathname)
	
	{
		int duplicatedSpoNum =0;
		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		 HashSet<String> duplicatedSpo =  new HashSet<String>();
		 HashSet<String> set1 =  new HashSet<String>();
 
		for (String spo : lines) {
			if (!(set1.add(spo))) {
				( duplicatedSpo).add(spo);
			}
		}
		
		duplicatedSpoNum = duplicatedSpo.size();
		logger.info("number of duplicated triplets is:"+ duplicatedSpoNum);
		
		return duplicatedSpoNum  ;
	}
	
	
	/**calculate the number of relations used only once
	 * @param pathname
	 * @return
	 */
	public int calculateUniqueRelations(String pathname)
	{
		int uniqueRelationsNum =0;
		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> allRelations = new ArrayList<String>();
		for (String line :lines)
		{
			
			Pattern pattern = Pattern.compile("kr:.* ");
			Matcher matcher = pattern.matcher(line);
			if(matcher.find())
			{
				allRelations.add(matcher.group(0));
			}
			

		}
		HashSet<String> uniqueRelations = new HashSet<String>(allRelations);
		uniqueRelationsNum = uniqueRelations.size();
		logger.info("The number of relations used once in the output file is: "+ uniqueRelationsNum);

		
		return uniqueRelationsNum -1;
		
	}
	
	
	/** calculate the number of dbpedia uri used once as a subject
	 * @param pathname
	 * @return
	 */
	public int calculateUniqueSubject(String pathname)
	{
		int uniqueSubjectsNum =0;
		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> allSubjects = new ArrayList<String>();
		for (String line :lines)
		{
			
			Pattern pattern = Pattern.compile("<.*>\t");
			Matcher matcher = pattern.matcher(line);
			if(matcher.find())
			{
				allSubjects.add(matcher.group(0));
			}
			

		}
		HashSet<String> uniqueRelations = new HashSet<String>(allSubjects);
		uniqueSubjectsNum = uniqueRelations.size();
		logger.info("The number of subjects used only once in the output file is: "+ uniqueSubjectsNum);
		return uniqueSubjectsNum;	
		
	}
	
	/** calculate the number of dbpedia uri used once as an object
	 * @param pathname
	 * @return
	 */
	public int calculateUniqueObject(String pathname)
	{
		int uniqueObjectsNum =0;
		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> allObjects = new ArrayList<String>();
		for (String line :lines)
		{
			
			Pattern pattern = Pattern.compile(" <.*>.");
			Matcher matcher = pattern.matcher(line);
			if(matcher.find())
			{
				allObjects.add(matcher.group(0));

			}
			

		}
		HashSet<String> uniqueObjects = new HashSet<String>(allObjects);
		uniqueObjectsNum = uniqueObjects.size();
		logger.info("The number of objects used only once in the output file is: "+ uniqueObjectsNum);
		return uniqueObjectsNum;
		
		
	}

	
	

}
