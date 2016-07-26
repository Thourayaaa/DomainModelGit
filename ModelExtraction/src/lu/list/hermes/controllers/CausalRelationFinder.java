package lu.list.hermes.controllers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;


/** detect causative relations and extract these informations from it, if it is found 
 * <NP1; verb; NP2; Target>, using a file.txt that contains causative verbs 
 * 
 * @author thourayabouzidi
 *
 */
public class CausalRelationFinder {
	
	final static Logger logger = Logger.getLogger(CausalRelationFinder.class);

	// split sentences
	// put the .txt content in a table
	//detect verb 
	// verb detected ? negative or positive
	// extract the whole verb
	// final result <NP1, verb, NP2, target>
	/** Split the input text into sentences 
	 * @param text
	 * @return
	 */
	public List<String> splitText (String text)
	{
		
	logger.info("split the text into sentences ...");
	Reader reader = new StringReader(text);
	DocumentPreprocessor dp = new DocumentPreprocessor(reader);
	List<String> sentenceList = new ArrayList<String>();

	for (List<HasWord> sentence : dp) {
		
	   String sentenceString = Sentence.listToString(sentence).toLowerCase();
		
	   sentenceList.add(sentenceString.toString());
	} 
	return sentenceList;
}
	/** read the input file and put the content in a String
	 * @param  pathname
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String pathname) throws IOException {


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
	/**  in order to simply the use of verbs and the comparaison, we put the content in a List of Strings
	 * @param pathname
	 * @return
	 */
	public static List<List<String>> loadCausativeVerbs (String pathname)
	{
		logger.info("load causative verbs from the input file ...");
		List <List<String>> causalVerbs = new ArrayList<List<String>>();
		
		
		//String[] parts = string.split("-");
		String fileContent = null;
		try {
			fileContent = readFile (pathname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] allVerbforms = fileContent.split("\n");
		for (int i = 0; i<allVerbforms.length ; i++)
		{
			String[] oneverb = allVerbforms[i].split("-");
			List<String> oneVerbList = new ArrayList<String>();
			for (int j =0;j< oneverb.length;j++)
			{
				oneVerbList.add(oneverb[j]);
			}
			causalVerbs.add(oneVerbList);
		}

		return causalVerbs;
		
	}
	
	/**  go through the text to look for a causative verb that exists in the List
	 * 
	 * @param sentence
	 * @param AllverbsList
	 * @return
	 */
	public static List<String> findVerb (String sentence, List<List<String>> AllverbsList )

	{
		List<String> VerbAndIndex= new ArrayList<String>();
		logger.info("look for a causative verb ...");
		
		// look for the verb in the sentence : parcourir  tout le tableau to find a match ! 
		//we suppose that a sentence contains one causal verb
		for (int i =0; i< AllverbsList.size();i++)
		{
			List<String> verbList = AllverbsList.get(i);
			for (int j=0; j< verbList.size();j++)
			{
				
				String verb = verbList.get(j);
				if (sentence.contains(verb))
				{
					VerbAndIndex.add(verb);
					VerbAndIndex.add(Integer.toString(sentence.indexOf(verb)));
				}
				
				
			}
			
		}
		
		return VerbAndIndex;
		
		
	}
	public static List<String> negative = new ArrayList<String>(
			Arrays.asList("don't", "doesn't", "shouldn't", "couldn't", "didn't","may not","isn't","aren't","wasn't","weren't",
					"hasn't","haven't","hadn't", "do not","should not", "could not", "is not", "are not","was not", "were not",
					"has not", "have not", "had not", "wouldn't", "would not"));
	
	public static List<String> positive = new ArrayList<String>(
			Arrays.asList("should", "could", "may", "might","could", "should","was","were","are","is"
					,"was","were","have","has","had","does","would"));
	
	/**split the sentence into words and put it in a table of Strings
	 * @param sentence
	 * @return
	 */
	public static String[] fromSentencetoWords(String sentence)
	{
		String[] words = sentence.split("\\s+");
		for (int i = 0; i < words.length; i++) {
		    // You may want to check for a non-word character before blindly
		    // performing a replacement
		    // It may also be necessary to adjust the character class
		    words[i] = words[i];
		}
      return words;
	}
	/** Find causal relations in the sentence : after finding the verb, test using negative and positive Lists if the meaning
	 * is affirmative or negative, the output is <NP1 ; verb ; NP2 ; target> 
	 * target is YES or NO : if the sentence is affirmative target = YES , if it is negative target = NO
	 * @param verbAndindex
	 * @param negative
	 * @param positive
	 * @param sentence
	 * @return
	 */
	public static List<String> findCausalRelations (List<String> verbAndindex, List<String> negative,List<String> positive, String sentence)
	{
       
		logger.info("find causal relations ....");
		
		
		List<String> causalTriplet = new ArrayList<String>();
	    String[] words = fromSentencetoWords(sentence);
	    String beforeVerb ="";
	    for (int i=0; i<words.length;i++)
	    {
    	    

	    	if (words[i].replaceAll("^\\s+|\\s+$", "").equals(verbAndindex.get(0).replaceAll("^\\s+|\\s+$", "")))
	    	{

	    		beforeVerb = words[i-1] ;
	    		
	    	}
	    	
	    }
	    int beginrelaIndex = sentence.indexOf(beforeVerb);

	    if (negative.contains(beforeVerb))
	    {
	    	causalTriplet.add(sentence.substring(0,beginrelaIndex -1));
	    	causalTriplet.add(sentence.substring(beginrelaIndex,(Integer.parseInt(verbAndindex.get(1)) +verbAndindex.get(0).length())));
	    	causalTriplet.add(sentence.
	    			substring(Integer.parseInt(verbAndindex.get(1))+verbAndindex.get(0).length(),
	    			sentence.length()));
	    	causalTriplet.add("NO");
	    	
	    }
	    else if (positive.contains(beforeVerb))
	    {
	    	causalTriplet.add(sentence.substring(0,beginrelaIndex-1));
	    	causalTriplet.add(sentence.substring(beginrelaIndex,(Integer.parseInt(verbAndindex.get(1)) +verbAndindex.get(0).length())));
	    	causalTriplet.add(sentence.substring(Integer.parseInt(verbAndindex.get(1))+verbAndindex.get(0).length(),
	    			sentence.length()));
	    	causalTriplet.add("YES");
	    }
	    else 
	    {
	    	causalTriplet.add(sentence.substring(0,(Integer.parseInt(verbAndindex.get(1)))));
	    	causalTriplet.add(sentence.substring((Integer.parseInt(verbAndindex.get(1))),(Integer.parseInt(verbAndindex.get(1)) +verbAndindex.get(0).length())));
	    	causalTriplet.add(sentence.substring(Integer.parseInt(verbAndindex.get(1))+verbAndindex.get(0).length(),
	    			sentence.length()));
	    	causalTriplet.add("YES");
	    }
		return causalTriplet;
	}
	
	public static void main(String[] args) {
		String sentence = "global warming cause high temperatures";
		List<List<String>> allCausalVerbs = loadCausativeVerbs("/Users/thourayabouzidi/Desktop/causalverbs/baseform.txt");
		List<String> verbAndIndex = findVerb (sentence, allCausalVerbs );
		List <String> causalTriplets = findCausalRelations (verbAndIndex , negative,
			positive, sentence);
		System.out.print(causalTriplets.get(0)+"\n"+ causalTriplets.get(1)+"\n"+causalTriplets.get(2)+"\n"+causalTriplets.get(3));

		
		
	}
	
	
	
	
	
	
	
	
	
	
}
