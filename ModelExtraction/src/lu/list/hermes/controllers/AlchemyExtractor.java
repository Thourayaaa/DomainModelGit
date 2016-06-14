package lu.list.hermes.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_RelationParams;

import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.dao.EntityRelDao;
import lu.list.hermes.dao.RelationDao;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.models.Document;
import lu.list.hermes.models.EntityRel;
import lu.list.hermes.models.Relation;
import lu.list.hermes.util.HibernateUtil;

public class AlchemyExtractor implements RelationExtractor {
	final static Logger logger = Logger.getLogger(AlchemyExtractor.class);
	 private static String getStringFromDocument(org.w3c.dom.Document doc) {
	        try {
	            DOMSource domSource = new DOMSource(doc);
	            StringWriter writer = new StringWriter();
	            StreamResult result = new StreamResult(writer);

	            TransformerFactory tf = TransformerFactory.newInstance();
	            Transformer transformer = tf.newTransformer();
	            transformer.transform(domSource, result);

	            return writer.toString();
	        } catch (TransformerException ex) {
	            ex.printStackTrace();
	            return null;
	        }

	 }

	@Override
	public ArrayList<Relation> generateSPOFromDocument(Document docc,
			String language) {
		// TODO Auto-generated method stub
		  // Create an AlchemyAPI object.
		ArrayList<Relation> relations = new ArrayList<Relation>();

        try {
			AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");
			AlchemyAPI_RelationParams relationParams = new AlchemyAPI_RelationParams();
			String text = docc.getDocText();
		    org.w3c.dom.Document doc1 =  alchemyObj.TextGetRelations(text, relationParams);
		    InputSource source = new InputSource(new StringReader(getStringFromDocument(doc1)));

			 
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    org.w3c.dom.Document doc = docBuilder.parse(source);
		    NodeList listOfRelations = doc.getElementsByTagName("relation");

	        for(int i=0; i<listOfRelations.getLength() ; i++) {

	            Node firstRelation = listOfRelations.item(i); //the relation
	            
	                  NodeList listofnodes = firstRelation.getChildNodes();
	             	  RelationDao rdao = new RelationDao();
	             	  EntityRelDao edao = new EntityRelDao();
	             	
	                  for(int j=0; j< listofnodes.getLength() ; j++) {
		                  Relation relation = new Relation();
                          EntityRel entobj = new EntityRel();
                          EntityRel entsubj = new EntityRel();
 	                      Node firstnodee = listofnodes.item(j);

                          if (firstnodee.getNodeName() == "object" && !(firstnodee.getTextContent().isEmpty()) &&
                        		  firstnodee.getNodeName() == "subject" && !(firstnodee.getTextContent().isEmpty())
                        		  && firstnodee.getNodeName() == "action" && !(firstnodee.getTextContent().isEmpty())){
	                	  if (firstnodee.getNodeName() == "object")
	                	  {
	                		  entobj.setEntitytext(firstnodee.getTextContent()); 
	                		  entobj.setindexe(docc.getDocText().indexOf(firstnodee.getTextContent()));
	                		  entobj.setlabel("Object");
	                		  entobj.setlongent(firstnodee.getTextContent().length());
	                		  entobj.seturient("http://www.list.lu/"+firstnodee.getTextContent().replaceAll("\\s", "_"));
	                	  }
	                	  if (firstnodee.getNodeName() == "subject" )
	                	  {
	                		  entsubj.setEntitytext(firstnodee.getTextContent().replaceAll("\\s{2,}", "")); 
	                		  entsubj.setindexe(docc.getDocText().indexOf(firstnodee.getTextContent().replaceAll("\\s{2,}", "")));
	                		  entsubj.setlabel("Subject");
	                		  entsubj.setlongent(firstnodee.getTextContent().replaceAll("\\s{2,}", "").length());
	                		  entsubj.seturient("http://www.list.lu/"+firstnodee.getTextContent().replaceAll("\\s{2,}", "").replaceAll("\\s", "_"));

	                		  
	                		  
	                	  }
	                	  if (firstnodee.getNodeName() == "action" )
	                	  {
	                		NodeList childAction =  firstnodee.getChildNodes();  
	                		
	                        for(int k=0; k<childAction.getLength() ; k++) {
	                        	if (childAction.item(k).getNodeName() == "text")
	                      	  {
	                        		relation.setDocument(docc);
	                        		relation.setrelation(childAction.item(k).getTextContent());
	                    	                         }
	                         	if (childAction.item(k).getNodeName() == "lemmatized")
	                        	  {
	                         		relation.setrelation(childAction.item(k).getNodeName());
	                      	                         }
	                        }
	                		
	                		  rdao.addRelation(relation);
	                		  entobj.setRelation(relation);
	                		  entsubj.setRelation(relation);
	                		  edao.addEntityRel(entsubj);
	                		  edao.addEntityRel(entobj);
	                		  relations.add(relation);
	                	              	  }
	                  } }
	            }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       


		return relations;
	}

	@Override
	public void extractRelationsCorpus(int idcorpus) {
		// TODO Auto-generated method stub
		SessionFactory sf = HibernateUtil.getSessionFactory();
		 Session session = sf.openSession();
		 session.beginTransaction();
		 CorpusDao cdao = new CorpusDao();
		 Corpus c = cdao.getCorpusById(idcorpus);
		 
		 Set<lu.list.hermes.models.Document> dc = c.getDocuments();
		 
		 
		 for (lu.list.hermes.models.Document doo :dc)
		 {
			 extractRelationsDocument(doo); //save SPO into database	 
		 }
		
	}

	@Override
	public void extractRelationsDocument(Document document) {
		// TODO Auto-generated method stub

        try {
			AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");
			AlchemyAPI_RelationParams relationParams = new AlchemyAPI_RelationParams();
			String text = document.getDocText();
		    org.w3c.dom.Document doc1 =  alchemyObj.TextGetRelations(text, relationParams);
		    InputSource source = new InputSource(new StringReader(getStringFromDocument(doc1)));

			 
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    org.w3c.dom.Document doc = docBuilder.parse(source);
		    NodeList listOfRelations = doc.getElementsByTagName("relation");

	        for(int i=0; i<listOfRelations.getLength() ; i++) {

	            Node firstRelation = listOfRelations.item(i); //the relation
	            
	                  NodeList listofnodes = firstRelation.getChildNodes();
	             	  RelationDao rdao = new RelationDao();
	             	  EntityRelDao edao = new EntityRelDao();
	             	 Relation relation = new Relation();
                     EntityRel entobj = new EntityRel();
                     EntityRel entsubj = new EntityRel();

	             	
	                  for(int j=0; j< listofnodes.getLength() ; j++) {
		                  
                          
	                     Node firstnodee = listofnodes.item(j);
	                    
	                	  if (firstnodee.getNodeName() == "object")
	                	  {
	                		  entobj.setEntitytext(firstnodee.getTextContent().replaceAll("\\s{2,}", "")); 
	                		  entobj.setindexe(document.getDocText().indexOf(firstnodee.getTextContent().replaceAll("\\s{2,}", "")));
	                		  entobj.setlabel("Object");
	                		  entobj.setlongent(firstnodee.getTextContent().replaceAll("\\s{2,}", "").length());
	                		  entobj.seturient("http://www.list.lu/"+firstnodee.getTextContent().replaceAll("\\s{2,}", "").replaceAll("\\s", "_"));
	                	  }
	                	  if (firstnodee.getNodeName() == "subject" )
	                	  {
	                		  entsubj.setEntitytext(firstnodee.getTextContent().replaceAll("\\s{2,}", "")); 
	                		  entsubj.setindexe(document.getDocText().indexOf(firstnodee.getTextContent().replaceAll("\\s{2,}", "")));
	                		  entsubj.setlabel("Subject");
	                		  entsubj.setlongent(firstnodee.getTextContent().replaceAll("\\s{2,}", "").length());
	                		  entsubj.seturient("http://www.list.lu/"+firstnodee.getTextContent().replaceAll("\\s{2,}", "").replaceAll("\\s", "_"));

	                		  
	                		  
	                	  }
	                	  if (firstnodee.getNodeName() == "action" )
	                	  {
	                		NodeList childAction =  firstnodee.getChildNodes();  
	                		
	                        for(int k=0; k<childAction.getLength() ; k++) {
	                        	if (childAction.item(k).getNodeName() == "text" )
	                      	  {
		                        		  relation.setrelationNL(childAction.item(k).getTextContent());


	                    	                         }
	                         	if (childAction.item(k).getNodeName() == "lemmatized")
	                        	  {
	                      		  //System.out.print(childAction.item(k).getTextContent()+"**************"+"\n");
	                         		relation.setrelation(childAction.item(k).getTextContent());
	                      	                         }
	                        }
	                		
	                		          	  }
	                	 
	                  } 
	              
            		  relation.setDocument(document);
            		  rdao.addRelation(relation); 
            		  entobj.setRelation(relation);
            		  entsubj.setRelation(relation);
            		  edao.addEntityRel(entsubj);
            		  edao.addEntityRel(entobj);
	        }
	            

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
