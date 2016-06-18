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
   
	
     /** generate (subject, relation,object) triplets from the input document and returns the list of relations
     * @param docc
     * @param language
     * @return
     */
    public  ArrayList<Relation>  generateSPOFromDocument (Document docc, String language);
     /** Apply the extractRelationDocument to all the corpus document 
     * @param idcorpus
     */
    public void extractRelationsCorpus (int idcorpus);
     /** Extract (subject, relation,object) triplets from the input document and save it into the database
     * @param document
     */
    public void extractRelationsDocument (Document document);

     
	
	
	
}
