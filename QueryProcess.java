import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import AttributeTable.RDFHelper;
import PlingStem.PlingStemmer;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;


public class QueryProcess {
	public static String[] serializedClassifiers ={ "classifiers/english.all.3class.distsim.crf.ser.gz" };
												//	,"classifiers/ner-model-email.ser.gz"};
	public static ArrayList<AbstractSequenceClassifier<CoreLabel>> classifiers;
	private static ILexicalDatabase db = new NictWordNet();
	
	public void initClassifiers(){
		classifiers = new ArrayList<AbstractSequenceClassifier<CoreLabel>>();
		classifiers.add(CRFClassifier.getClassifierNoExceptions("classifiers/english.muc.7class.distsim.crf.ser.gz"));
		//classifiers.add(CRFClassifier.getClassifierNoExceptions("classifiers/ner-model-email.ser.gz"));
		
	}
	public ArrayList<String> getEntities(String query){
		ArrayList<String> entityList = new ArrayList<String>();
		for(int i=0; i<classifiers.size(); i++){
			//AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifiers[i]);
			List<List<CoreLabel>> out = classifiers.get(0).classify(query);
			for (List<CoreLabel> sentence : out) {
				for (CoreLabel word : sentence) {
					//System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
			
					if(word.get(CoreAnnotations.AnswerAnnotation.class).equals("O"))
		            	continue;
		            entityList.add(word.word());
					
				}
			}
		}
		System.out.println("___Entity List___" );
		Iterator<String> iter = entityList.iterator();
		while(iter.hasNext())
			System.out.println("E__" + iter.next());
		return entityList;
	}
	
	
	public ArrayList<String> getRelations(String query, ArrayList<String> entityList){
		
		ArrayList<String> words = processQuery(query);
		
		ArrayList<String> predicatesList = null;
		ArrayList<String> relationsList = new ArrayList<String>();
		
		Iterator<String> iter = entityList.iterator();
		while(iter.hasNext()){
			predicatesList = RDFHelper.getPropertyOfResourse(iter.next());
			Iterator<String> relIter = predicatesList.iterator();
			while(relIter.hasNext()){
				String predicate = relIter.next();
				predicate = predicate.replaceAll("%20", " ");
				if(query.contains(predicate)){
					System.out.println("R___ added");
					relationsList.add(predicate);
				}
				else{
					for(String word: words){
						double sim = calculateSimilarity(word,predicate);
					
						//sim = Math.round( sim * 100.0 ) / 100.0;
						System.out.println(word + "_____" +predicate + " "+sim);
					
						if(sim >= 0.5){
							//System.out.println("R__" + predicate);
							relationsList.add(predicate);
						}
					}
				}
			}
		}
		System.out.println("___Relation List___" );
		Iterator<String> iter1 = relationsList.iterator();
		while(iter1.hasNext())
			System.out.println("R__" + iter1.next());
		return relationsList;
	}
	
	public double calculateSimilarity(String word1, String word2){
		if(word1.contains(word2))
			return 1.0;
		
		Lin lin = new Lin(db);
		double s = lin.calcRelatednessOfWords(word1, word2);
		
		if(s < 0.3) return 0;
		
		WuPalmer wup = new WuPalmer(db);
		double s1 = wup.calcRelatednessOfWords(word1, word2);
		
		if(s1 < 0.3) 
			return 0;
		else if(s > s1)
			return s;
		else
			return s1;
	}
	
	public ArrayList<String> processQuery(String query){
		
		String[] words = query.split(" ");
		ArrayList<String> stopWordsList = getStopWordsList();
		
		ArrayList<String> processedQuery = new ArrayList<String>();
		for(String word: words){
			if(stopWordsList.contains(word))
				continue;
			word = stemWord(word);
			processedQuery.add(word);
		}
		
		return processedQuery;
	}
	
	public ArrayList<String> getStopWordsList(){

		ArrayList<String> stopWordsList = new ArrayList<String>();
		stopWordsList.add("who");
		stopWordsList.add("what");
		stopWordsList.add("when");
		stopWordsList.add("where");
		stopWordsList.add("how");
		stopWordsList.add("is");
		stopWordsList.add("the");
		stopWordsList.add("a");
		stopWordsList.add("that");
		stopWordsList.add("was");
		stopWordsList.add("of");
		stopWordsList.add("are");
		stopWordsList.add("and");
		stopWordsList.add("at");
		stopWordsList.add("do");
		stopWordsList.add("get");
		stopWordsList.add("has");
		
		return stopWordsList;
	}
	
	public static String stemWord(String word){
		 PlingStemmer stemmer = new PlingStemmer();
		 return stemmer.stem(word);
	}
}
