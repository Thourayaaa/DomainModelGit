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
	
	

	
	public ArrayList<Annotation> generateAnnotationFromDocument(Document doc, String language);
	public void AnnoteCorpus (int idcorpus);
	public void AnnoteDocument (Document doc);


}