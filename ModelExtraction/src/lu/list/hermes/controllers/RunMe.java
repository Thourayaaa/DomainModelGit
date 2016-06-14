package lu.list.hermes.controllers;

/**
 * 
 */

import java.util.List;

import scala.collection.JavaConversions;
import scala.collection.Seq;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.openie.Part;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.Argument;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;

/**
 * @author harinder
 *
 */
public class RunMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("---Started---");
		
		OpenIE openIE = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer(ClearTokenizer.defaultModelUrl()))),
				new ClearSrl(), false);

        Seq<Instance> extractions = openIE.extract("U.S. president Barack Obama gave his inaugural address on January 20, 2013.");
        
        List<Instance> list_extractions = JavaConversions.seqAsJavaList(extractions);
        for(Instance instance : list_extractions) {
        	StringBuilder sb = new StringBuilder();
        	
            sb.append("the subject:"+instance.extr().arg1().text())
            .append('\t')
            .append("the relation: " +instance.extr().rel().text())
            .append('\t');
           
            
            List<Part> list_arg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());
       	     sb.append("the object:");

            for(Part argument : list_arg2s) {

        	 sb.append(argument.text());
            }
            
            System.out.println(sb.toString());
        }
	}

}
