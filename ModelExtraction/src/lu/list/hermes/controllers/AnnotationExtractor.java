package lu.list.hermes.controllers;

import java.util.ArrayList;
import java.util.List;

import lu.list.hermes.models.*;

/** The interface for the Annotation task: All classes responsible for
 * annotation must implement from this interface
 * @author thourayabouzidi
 *
 */
public interface AnnotationExtractor {
	
	

	
	/**Annotate the input document text using an annotation tool such as KODA and returns list of relations
	 * @param doc
	 * @param language
	 * @return
	 */
	public ArrayList<Annotation> generateAnnotationFromDocument(Document doc, String language);
	
	/**Annotate the whole corpus found by the input id 
	 * @param idcorpus
	 */
	public void AnnoteCorpus (int idcorpus);
	/**Annotate the input document text using an annotation tool such as KODA
	 * @param doc
	 */
	public void AnnoteDocument (Document doc);


}