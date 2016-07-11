package lu.list.hermes.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.openie.Part;
import edu.knowitall.srlie.SrlExtraction.Argument;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.dao.EntityRelDao;
import lu.list.hermes.dao.RelationDao;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.models.Document;
import lu.list.hermes.models.EntityRel;
import lu.list.hermes.models.Relation;
import lu.list.hermes.util.HibernateUtil;
import scala.collection.JavaConversions;
import scala.collection.Seq;

/** this class implements RelationExtractor class: generates (the subject, relation, object) triplets for a document or a corpus 
 *  and save it into the database using OpenIE tool
 * @author thourayabouzidi
 *
 */
public class OpenieExtractor implements RelationExtractor
{
	final static Logger logger = Logger.getLogger(OpenieExtractor.class);

		
	@Override
	public ArrayList<Relation> generateSPOFromDocument(Document doc,
			String language) {
		ArrayList<Relation> relations = new ArrayList<Relation>();
		OpenIE openIE = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer(ClearTokenizer.defaultModelUrl()))),
				new ClearSrl(), false);
		   Seq<Instance> extractions = openIE.extract(doc.getDocText());
		   List<Instance> list_extractions = JavaConversions.seqAsJavaList(extractions);
	       for(Instance instance : list_extractions) {
	       logger.info("Find subject, object and relation in the ollie outputs");

	        	String subj = instance.extr().arg1().text();
	        	String rel = instance.extr().rel().text().replaceAll("^\\s+|\\s+$", "");
	        	
	        	 Relation relation = new Relation();
	        	 RelationDao rdao = new RelationDao();
	        	 EntityRelDao edao = new EntityRelDao();
	        	 //here i am supposed to save set of relations
	        	 relation.setrelationNL(rel);
	        	 relation.setDocument(doc);
	         	 rdao.addRelation(relation);

	         	 // Write the object in a String
	         	 StringBuilder sb = new StringBuilder();
	         	List<Part> list_arg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());

	            for(Part argument : list_arg2s) {

	        	 sb.append(argument.text());
	            }
	            String obj =sb.toString();
	        	 logger.info("insert the subject,object and relation into the database");


	        	 EntityRel entityobj = new EntityRel();    //save object as an entity
	        	 entityobj.setEntitytext(obj); entityobj.setindexe(doc.getDocText().indexOf(obj));entityobj.setlongent(obj.length());
	        	 entityobj.seturient("http://www.list.lu/"+obj.replaceAll("\\s", "_")); entityobj.setlabel("Object");
	             entityobj.setRelation(relation);
	        	 EntityRel entitysubj = new EntityRel(); //save subject as an entity
	        	 entitysubj.setEntitytext(subj); entitysubj.setindexe(doc.getDocText().indexOf(subj));entitysubj.setlongent(subj.length());
	        	 entitysubj.seturient("http://www.list.lu/"+subj.replaceAll("\\s", "_")); entitysubj.setlabel("Subject");
	             entitysubj.setRelation(relation);

	            edao.addEntityRel(entitysubj);
	        	edao.addEntityRel(entityobj);
	        	 relations.add(relation);
	        	
	        	
	        }
		   
				return relations;
		// TODO Auto-generated method stub
			}

	@Override
	public void extractRelationsCorpus(int idcorpus) {
		// TODO Auto-generated method stub
		SessionFactory sf = HibernateUtil.getSessionFactory();
		 Session session = sf.openSession();
		 session.beginTransaction();
		 CorpusDao cdao = new CorpusDao();
		 Corpus c = cdao.getCorpusById(idcorpus);
		 logger.info("Extract relations from the whole corpus");

		 Set<lu.list.hermes.models.Document> dc = c.getDocuments();
		 
		 
		 for (lu.list.hermes.models.Document doo :dc)
		 { 
			 logger.info("extract relations from the document"+doo.getIdDoc());

			 extractRelationsDocument(doo); //save SPO into database	 
		 }
		
	}

	@Override
	public void extractRelationsDocument(Document doc) {
		// TODO Auto-generated method stub
		OpenIE openIE = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer(ClearTokenizer.defaultModelUrl()))),
				new ClearSrl(), false);
		   Seq<Instance> extractions = openIE.extract(doc.getDocText());
		   List<Instance> list_extractions = JavaConversions.seqAsJavaList(extractions);
	        for(Instance instance : list_extractions) {
	        	 logger.info("find subject, object and relation in the ollie outputs");

	        	String subj = instance.extr().arg1().text();
	        	String rel = instance.extr().rel().text();
	        	
	        	 Relation relation = new Relation();
	        	 RelationDao rdao = new RelationDao();
	        	 EntityRelDao edao = new EntityRelDao();
	        	 //here i am supposed to save set of relations
	        	 relation.setrelationNL(rel);
	        	 relation.setDocument(doc);
	         	 rdao.addRelation(relation);
	         	 // Write the object in a String
	         	 StringBuilder sb = new StringBuilder();
	         	List<Part> list_arg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());

	            for(Part argument : list_arg2s) {

	        	 sb.append(argument.text());
	            }
	            String obj =sb.toString();

	        	 logger.info("insert subject object relation into the database");

	        	 EntityRel entityobj = new EntityRel();    //save object as an entity
	        	 entityobj.setEntitytext(obj); entityobj.setindexe(doc.getDocText().indexOf(obj));entityobj.setlongent(obj.length());
	        	 entityobj.seturient("http://www.list.lu/"+obj.replaceAll("\\s", "_")); entityobj.setlabel("Object");
	             entityobj.setRelation(relation);
	        	 EntityRel entitysubj = new EntityRel(); //save subject as an entity
	        	 entitysubj.setEntitytext(subj); entitysubj.setindexe(doc.getDocText().indexOf(subj));entitysubj.setlongent(subj.length());
	        	 entitysubj.seturient("http://www.list.lu/"+subj.replaceAll("\\s", "_")); entitysubj.setlabel("Subject");
	             entitysubj.setRelation(relation);

	            edao.addEntityRel(entitysubj);
	        	edao.addEntityRel(entityobj);
	        	
	        	
	        }
		
	}

		
	
	
	
	
	
}
