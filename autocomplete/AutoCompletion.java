package autocomplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;



public class AutoCompletion {  
	
	  private IndexSearcher autoCompleteSearcher;  
	  
	  @SuppressWarnings("deprecation")
	public AutoCompletion(Directory autoCompleteDirectory) throws IOException {  
	    this.autoCompleteSearcher = new IndexSearcher(autoCompleteDirectory);  
	  }  
	  
	  public List<String> suggestTermsFor(String term, int maxResults)   
	    throws IOException {  
	  
	    Query query = new TermQuery(new Term("grammedWords", term));  
	    Sort sort = new Sort(new SortField("count", SortField.INT, true));  
	  
	    TopDocs docs = autoCompleteSearcher.search(query, null, maxResults, sort);  
	    List<String> suggestions = new ArrayList<String>();  
	  
	    for (ScoreDoc doc : docs.scoreDocs) {  
	      String suggestion = autoCompleteSearcher.getIndexReader()  
	        .document(doc.doc).get("originalWord");  
	  
	      suggestions.add(suggestion);  
	    }  
	  
	    return suggestions;  
	  }  
	  
//	  public static void main(String[] a) throws IOException{
//		  Directory AutoCompletedir = FSDirectory.open(new File("C:\\FYP\\SearchENG\\autoDirectory")); 
//		  AutoCompletion ac = new AutoCompletion(AutoCompletedir);
//		  
//		  List<String> suggestions = ac.suggestTermsFor("goog", 10);
//		  
//		  Iterator<String>iter = suggestions.iterator();
//		  
//		  while(iter.hasNext()){
//			  System.out.println(iter.next());
//		  }
//	  }
	}