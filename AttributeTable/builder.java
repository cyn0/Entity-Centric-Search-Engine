package AttributeTable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hwpf.HWPFDocument;
import org.tartarus.snowball.ext.EnglishStemmer;


import readers.MSWordDocumentParser;

import constant.constants;
import constant.tuple;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.WuPalmer;

public class builder {
	private static ILexicalDatabase db = new NictWordNet();
	
	
	
	private String relation;
	private String entity1;
	private String entity2;
	
	private tuple[] tuples = null; 
	
	public builder(String entity1, String relation,String entity2){
		this.relation = relation;
		this.entity1 = entity1;
		this.entity2 = entity2;
	}
	
	public void getAllEntities(){
		try {
			
			IndexReader reader = IndexReader.open(FSDirectory.open(new File(constants.entityIndexDirectorySmall)));
		//	RDFHelper.initFromBuilder();
			for (int i=0; i<reader.maxDoc(); i++) {
				//if(i++ > 100) break;
			    if (reader.isDeleted(i))
			        continue;

			    Document doc = reader.document(i);
			    
			    String entity1values[] = doc.getValues(entity1);
			    String entity2values[] = doc.getValues(entity2);
			    
			    if(entity1values == null || entity2values == null){
			    	System.out.println("file doesnt contain");
			    	continue;
			    }
			    	
			    
			    String fileName = doc.get(constants.fieldFileName);
			    if(fileName.contains(".txt")) continue;
			    System.out.println(fileName);
	            System.out.println();
	            

	            Set<String> oSet = new HashSet<String>(Arrays.asList(entity1values));
	            Set<String> pSet = new HashSet<String>(Arrays.asList(entity2values));
	            
	            Iterator<String> firstIterator = oSet.iterator();
	            Iterator<String> secondIterator = pSet.iterator();
	            
	            File file = new File(fileName);
	    		
	            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
	            HWPFDocument document = new HWPFDocument(fis);
	            MSWordDocumentParser docp = new MSWordDocumentParser();
	            int countTuples = 0,it=1;
			    
	            while(firstIterator.hasNext()) 
			    {
			    	String firstEntity = firstIterator.next();
			    	secondIterator = pSet.iterator();
			    	
			    	while(secondIterator.hasNext()){
			    		String secondEntity = secondIterator.next();
			    		if(firstEntity.equals(secondEntity))
			    			continue;
			            String paragraphs[] = docp.readParagraphs(document);
			            
			            for(String currentParagraph:paragraphs){
			            	
			            	if(currentParagraph.contains(firstEntity) && currentParagraph.contains(secondEntity))
			            	{
			            		
			            		String substring = getSubStringFromParagraph(firstEntity , secondEntity, currentParagraph);
			            		//System.out.println(substring);
			            		//System.out.println();
			            		
			            		for(String word : substring.split(" ")){
			            			//System.out.println(word);
//			            			stemmer.setCurrent(word);
//			           		      	stemmer.stem();
//			           		      	word = stemmer.getCurrent();
			           		      	
			           		      	double similarity = calculateSimilarity(word);
			           		      	
			           		      	if(similarity >= 0.5){
			           		      		System.out.println(substring);
			           		      		System.out.println("___Similar word!___ : "+ word);
			           		      		System.out.println("____"+firstEntity + "___" + relation +"____" + secondEntity);
			           		      		//RDFHelper.addProperytoRDFFromBuilder(firstEntity , secondEntity, relation,""+it);
			           		      		it=0;
			           		      	}
			            		}
			            	}
			            }
			    		
			    	}
			    }
	            
			}
			
		//	RDFHelper.writeToFileFrom();
			//doCorrectRDFFile();
			
			
		}catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	
	
		
	public static void doCorrectRDFFile() {
		// TODO Auto-generated method stub
		try{
		File inputFile = new File(constants.RDFFile);
		File tempFile = new File("myTempFile.rdf");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String lineToRemove = "</rdf:RDF>";
		String currentLine;
		boolean done=false;
		
		while((currentLine = reader.readLine()) != null) {
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.startsWith(lineToRemove)&&done==false) 
		    	{ 
		    	done=true;
		    	System.out.println(reader.readLine());
		    	System.out.println(reader.readLine());
		    	System.out.println(reader.readLine());
		    	continue;
		    	}
		    writer.write(currentLine);
		    
		}

		boolean successful = tempFile.renameTo(inputFile);

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
//		String returnValue = "";
//		int count=1;
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(constants.RDFFile));
//			
//			String line = "";
//		    while ((line = br.readLine()) != null) {
//		      if(line.trim().startsWith("</rdf:RDF>"))
//		      {
//		    	  
//		      }
//		     
//		      
//		    }
//		  
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	}

	public double calculateSimilarity(String word){
		if(word.contains(relation))
			return 1.0;
		Lin lin = new Lin(db);
		
		double s = lin.calcRelatednessOfWords(word, relation);
		if(s < 0.4) return 0.0;
		
		WuPalmer wup = new WuPalmer(db);
		double s1 = wup.calcRelatednessOfWords(word, relation);
		if(s1 < 0.4) return 0.0; 
		else if(s > s1)
			return s;
		else
			return s1;
	}
	
	public String getSubStringFromParagraph(String firstEntity ,String secondEntity,String currentParagraph){
		
		int firstIndex = currentParagraph.indexOf(firstEntity);
		int lastindex = currentParagraph.lastIndexOf(firstEntity);
		
		int firstIndex1 = currentParagraph.indexOf(secondEntity);
		int lastIndex1 = currentParagraph.lastIndexOf(secondEntity);
		
		int beginning = firstIndex > firstIndex1 ? firstIndex1 : firstIndex1;
		
		int end = lastindex > lastIndex1 ? lastindex : lastIndex1;

		return currentParagraph.substring(beginning, end);
	}
	
	public static void main(String[] a){
		builder o = new builder(constants.fieldPerson, "founder" , constants.fieldOrganisation);
		o.getAllEntities();
	}
}
