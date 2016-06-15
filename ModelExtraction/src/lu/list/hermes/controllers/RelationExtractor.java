package lu.list.hermes.controllers;
import java.util.ArrayList;
import java.util.List;
import lu.list.hermes.models.*;



/**The interface for the SPO extraction task: All classes responsible for
 * extracting (subject-relation-object) must implement from this interface 
 * @author thourayabouzidi
 *
 */
public interface RelationExtractor {
   
	
      public  ArrayList<Relation>  generateSPOFromDocument (Document docc, String language);
     public void extractRelationsCorpus (int idcorpus);
     public void extractRelationsDocument (Document document);

     
	
	
	
}
