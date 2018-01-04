package Ranking;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import constant.constants;
public class EntityRank {
	public static StringBuffer EntityValues = null;
	
	@SuppressWarnings("deprecation")
	public static float calculateEntitiesScore(String fileName,ArrayList<String> entities){
		String[] values = null;
		float scoreMultiplier = 1.0f;
	
		try{
			Directory dir = FSDirectory.open(new File(constants.entityIndexDirectory));
						
			IndexSearcher is = new IndexSearcher(dir);
			IndexReader reader = IndexReader.open(dir, true);
			
			Query query = new TermQuery(new Term(constants.fieldFileName, fileName));
			
			long start = System.currentTimeMillis();
			TopDocs hits = is.search(query, 10);
			long end = System.currentTimeMillis();
			
			System.err.println("Found " + hits.totalHits +" document(s) (in " + (end - start) +" milliseconds) that matched query '" +
					fileName + "':");
			
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				
				Document doc = is.doc(scoreDoc.doc);
				Iterator<String> it = entities.iterator();
				while(it.hasNext()){
					String entity = it.next();
					values =doc.getValues(entity);
					EntityValues = new StringBuffer();
					for(String v:values){
						System.out.println(v);
						EntityValues.append(v + " ");
					}
				}
				
//				int docId = hits[i].doc;  
//	            TermFreqVector tfvector = reader.getTermFreqVector(docId, constants.fieldOrganisation);  
//	            TermPositionVector tpvector = (TermPositionVector)tfvector;  
//	            // this part works only if there is one term in the query string,  
//	            // otherwise you will have to iterate this section over the query terms.  
//	            int termidx = tfvector.indexOf("Google");  
//	            System.out.println(termidx);
//	            int[] termposx = tpvector.getTermPositions(termidx);  
//	            TermVectorOffsetInfo[] tvoffsetinfo = tpvector.getOffsets(termidx);  
//
//	            for (int j=0;j<termposx.length;j++) {  
//	                System.out.println("termpos : "+termposx[j]);  
//	            }  
//	            for (int j=0;j<tvoffsetinfo.length;j++) {  
//	                int offsetStart = tvoffsetinfo[j].getStartOffset();  
//	                int offsetEnd = tvoffsetinfo[j].getEndOffset();  
//	                System.out.println("offsets : "+offsetStart+" "+offsetEnd);  
//	            }  
//
								
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(values == null)
			return 1.0f;
		if(values.length > 0)
			return 2.0f;
		else
			return 1.0f;
		
		
	}
	

}
