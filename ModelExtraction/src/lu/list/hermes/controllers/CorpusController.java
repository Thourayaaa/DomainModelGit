package lu.list.hermes.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import lu.list.hermes.models.Corpus;
import lu.list.hermes.models.Document;
import lu.list.hermes.util.HibernateUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** This class implements the functions responsable for adding the corpus
 * and its documents to the database
 * @author thourayabouzidi
 *
 */
public class CorpusController {
	final static Logger logger = Logger.getLogger(CorpusController.class);


		/** Read the file from in the input path and returns a string 
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
		public static String getFileExtension(File file) {
		    String name = file.getName();
		    try {
		        return name.substring(name.lastIndexOf(".") + 1);
		    } catch (Exception e) {
		        return "";
		    }
		
		}
		
		 /** Add the corpus and its documents to the database based on the path and the corpus name
		 * @param path
		 * @param Corpusname
		 * @return
		 * @throws IOException
		 */
		public int addCorpus (String path, String Corpusname) throws IOException
		 {
			 //add the Corpus First
			 SessionFactory sf = HibernateUtil.getSessionFactory();
			 Session session = sf.openSession();
			 session.beginTransaction();
			 
	         // add the corpus
	         Corpus c = new Corpus();
	         c.setCorpusName(Corpusname);
	         c.setpath(path);
	         session.save(c);
	         int idcc = (int) c.getIDc();
	         // save files inside the corpus
	        File file = new File(path);
	         File[] files = file.listFiles();
	         if (files != null ) {
	            for (int i = 0; i < files.length; i++) {
	            	
	        String ext = getFileExtension(files[i]); 
	         if ((ext.equals("txt")))
	       {		 
	         	 
	        	 String uridoc = new File(files[i].getAbsolutePath()).toURI().toURL().toString();
	             String sentence = readFile(files[i].getAbsolutePath());
	             Document d = new Document();
	             d.seturid(uridoc);
	             d.setDocText(sentence);
	             d.setCorpus(c);

	             session.save(d);
	         }
		 }

		logger.info("Corpus successfully added !");
	         }
	         session.getTransaction().commit();
	         session.close();
			return idcc;
	     	
	       
		 }


}
