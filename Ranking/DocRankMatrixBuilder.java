package Ranking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import constant.constants;


public class DocRankMatrixBuilder  
{

	private RankMatrix matrixH;

	private RankMatrix buildDocMatrix(IndexReader idxR) throws IOException {

		List<Integer> allDocs = getAllDocs(idxR);
		
		RankMatrix docMatrix = new RankMatrix(allDocs.size());
		
		for (int i = 0, n = allDocs.size(); i < n; i++) {
			
			for (int j = 0, k = allDocs.size(); j < k; j++) {

				double similarity = 0.0d;

				Document docX = idxR.document(i);
				
				String xPath = docX.get(constants.fieldFullPath);
				System.out.println(xPath);
				if (i == j) 
					docMatrix.addLink(xPath, xPath, similarity);
				
				else {
					TextDocumentTerms xDocumentTerms = new TextDocumentTerms(docX.get("cont"));
					Document docY = idxR.document(j);
					TextDocumentTerms yDocumentTerms = new TextDocumentTerms(docY.get("cont"));
					similarity = getImportance(xDocumentTerms, yDocumentTerms);
					String yPath = docY.get(constants.fieldFullPath);
					docMatrix.addLink(xPath, yPath, similarity);
				}
			}
		}

		docMatrix.calculate();
		docMatrix.print();
		return docMatrix;
	}


	public RankMatrix getH() {
		return matrixH;
	}

	
	 // importance of document Y in the context of document X
	 
	private double getImportance(TextDocumentTerms xTerms,TextDocumentTerms yTerms) {

	
		Set<String> commonWords = xTerms.getTf().keySet();
		commonWords.retainAll(yTerms.getTf().keySet());

		double commonWordsSum = 0.0;
		double xVal,yVal;
		for (String term : commonWords) {

			xVal = xTerms.getTf().get(term).doubleValue();
			yVal = yTerms.getTf().get(term).doubleValue();
			commonWordsSum += Math.round(Math.tanh(yVal / xVal));
		}

		return commonWordsSum;
	}


	private List<Integer> getAllDocs(IndexReader idxR) throws IOException {
		List<Integer> docs = new ArrayList<Integer>();
		for (int i = 0, n = idxR.maxDoc(); i < n; i++) {
			if (idxR.hasDeletions() == false) {
				//Document doc = idxR.document(i);
				//if (eligibleForDocRank(doc.get("doctype"))) {
					docs.add(i);
				//}
			}
		}
		return docs;

	}


	public void run() {
		try {
			IndexReader idxR = IndexReader.open(FSDirectory.open(new File(constants.indexDirectory)));
//			IndexReader idxR = IndexReader.open(new FSDirectory(
//					new File(indexDir)));

			matrixH = buildDocMatrix(idxR);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
