package indexing;
import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import readers.*;

import constant.constants;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;



public class EntityIndexer {
	
	static String serializedClassifier;
	

public static void main(String[] args) throws Exception {
	
	
	serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
	//serializedClassifier = "classifiers/ner-model-email.ser.gz";
	AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
	
	Directory dir = FSDirectory.open(new File(constants.entityIndexDirectorySmall));
	IndexWriter indexWriter = new IndexWriter(dir,new StandardAnalyzer(Version.LUCENE_30),true,IndexWriter.MaxFieldLength.UNLIMITED);;
	
	
	File[] files = new File(constants.fileDirectorySmallEntities).listFiles();
	for (File f: files) {		
		
		if (!f.isDirectory() &&!f.isHidden() &&f.exists() &&f.canRead() ) {

			Document document = new Document();
			//String fileContents = IOUtils.slurpFile(f.getCanonicalPath());
			System.out.println("Indexing: " + f.getCanonicalPath());
			
			String fileContents = "";
			if(f.getCanonicalPath().endsWith(".doc"))
				fileContents = MSwordReader.readData(f.getCanonicalPath());
			else
				fileContents = TxtReader.readData(f.getCanonicalPath());
			
			List<List<CoreLabel>> out = classifier.classify(fileContents);
			for (List<CoreLabel> sentence : out) {
				for (CoreLabel word : sentence) {
					System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
			
					if(word.get(CoreAnnotations.AnswerAnnotation.class).equals("O"))
		            	continue;
		            
					document.add(new Field(word.get(CoreAnnotations.AnswerAnnotation.class), word.word(), Field.Store.YES, Field.Index.NOT_ANALYZED,TermVector.WITH_POSITIONS_OFFSETS));
		            
				}
				System.out.println();
				
			}
			document.add(new Field("filename", f.getCanonicalPath(),Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			indexWriter.addDocument(document);
			
			out = classifier.classifyFile(f.getCanonicalPath());
		}
	}
	indexWriter.optimize();
	indexWriter.close();
	dir.close();

}

}
