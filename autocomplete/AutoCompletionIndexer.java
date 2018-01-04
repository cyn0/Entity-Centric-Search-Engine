package autocomplete;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;

@SuppressWarnings("deprecation")
public class AutoCompletionIndexer {  
	  
	  public void createIndex(IndexReader sourceReader,   
	      Directory autoCompletionDirectory) throws CorruptIndexException,   
	      IOException {  
	  
	    Map<String,Integer> wordsMap = buildWordsMap(sourceReader);  
	    index(wordsMap, autoCompletionDirectory);  
	  }  
	  
	  /** 
	   *  go through every word, storing the original word (incl. n-grams) 
	   *  and the number of times it occurs 
	   */  
	  protected Map<String,Integer> buildWordsMap(IndexReader sourceReader)  
	      throws IOException {  
	  
	    LuceneDictionary dict = new LuceneDictionary(sourceReader, "contents");  
	    Map<String, Integer> wordsMap = new HashMap<String, Integer>();  
	  
	  BytesRefIterator iter =   dict.getWordsIterator();  
	  if(iter!=null)
		  System.out.println(iter.next().utf8ToString());
	    while (true) {
	    	if(iter == null) break;
	    	BytesRef curr = iter.next();
	    	if(curr == null) break;
	    	String word = curr.utf8ToString().replaceAll("[,.\"]", "");
	    	 System.out.println(word);
	    	 if (word.length() >= 3) {  
	    		 wordsMap.put(word, sourceReader.docFreq(new Term("cont", word)));  
	    		 System.out.println("done");
	    	 }
	    }
	  
	    return wordsMap;  
	  }  
	  
	  protected void index(Map<String, Integer> wordsMap,   
	      Directory autoCompletionDirectory) throws CorruptIndexException,   
	      IOException {  
	  
	    /* create a new index */  
	    IndexWriter writer = new IndexWriter(autoCompletionDirectory,   
	        new AutoCompletionAnalyzer(), true, MaxFieldLength.LIMITED);  
	  
	    writer.setMergeFactor(300);  
	    writer.setMaxBufferedDocs(150);  
	  
	    for (Entry<String,Integer> entry : wordsMap.entrySet()) {  
	      Document doc = new Document();  
	  
	      /* original term, grammed term, count */  
	      doc.add(new Field("originalWord", entry.getKey(),   
	          Store.YES, Index.NOT_ANALYZED_NO_NORMS));  
	      doc.add(new Field("grammedWords", entry.getKey(),   
	          Store.YES, Index.ANALYZED));  
	      doc.add(new Field("count", Integer.toString(entry.getValue()),   
	          Store.NO, Index.NOT_ANALYZED_NO_NORMS));  
	  
	      writer.addDocument(doc);  
	    }  
	  
	    writer.optimize();  
	    writer.close();  
	    } 
	  
	  public static void main(String[] a) throws CorruptIndexException, IOException{
		  Directory dir = FSDirectory.open(new File("C:\\FYP\\SearchENG\\indexDirectory"));
		  IndexReader indexReader = IndexReader.open(dir);
		  if(indexReader== null)
			  System.out.println("reader null");
		  
		  Directory AutoCompletedir = FSDirectory.open(new File("C:\\FYP\\SearchENG\\autoDirectory"));
		  AutoCompletionIndexer Indexer = new AutoCompletionIndexer();
		  Indexer.createIndex(indexReader,AutoCompletedir);
	  }
	}  
