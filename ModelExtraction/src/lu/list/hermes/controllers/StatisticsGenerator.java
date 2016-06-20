package lu.list.hermes.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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


	/** read a file from the input path and returns its content in a String
	 * @param pathname
	 * @return
	 * @throws IOException
	 */
	public String readFile(String pathname) throws IOException {


        File file = new File(pathname);
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new FileReader(file));

        try {
            String line = null;

            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            input.close();
        }

        return contents.toString();
	}

	
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
		          
		  logger.info("total relations number is"+ spoNumber);
		return spoNumber;
	}
	
	
	/** Calculate total relations number (without duplication) in the output file
	 * @param Pathname
	 * @return
	 */
	public int calculateRelations (String pathname)
	{
		int relationsNumber=0;
		List<String> lines = null;
		try {
	      lines = Files.readAllLines(Paths.get(pathname), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> calculatedRelations = new ArrayList<String>();
		for (String line :lines)
		{
			
			Pattern pattern = Pattern.compile("kr:.* ");
			Matcher matcher = pattern.matcher(line);
			Boolean b = false;// relation doesn't exist
			String relation = null;


			if (matcher.find())
			{

				 relation = matcher.group(0);

				 if (calculatedRelations.isEmpty())
				 {
					 calculatedRelations.add(relation);
	            	 relationsNumber ++;
				 }
				for (String rel : calculatedRelations)
				{
					
					if ( !(relation.equals(rel)))
					{
						b = false;
					}
					else {
						b = true;

					}
						
				}
				
			}
             if (b == false)
             {
            	 relationsNumber ++;
            	 calculatedRelations.add(relation);
            	 
             }

			
		}		
		
   	 logger.info("number of total used relations without duplication is"+relationsNumber);

		
		return relationsNumber;
		
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
		logger.info("number of duplicated spo is:"+ duplicatedSpoNum);
		
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
		logger.info("The number of subjects used only once in the output file is: "+ uniqueObjectsNum);
		return uniqueObjectsNum;
		
		
	}

	
	

}
