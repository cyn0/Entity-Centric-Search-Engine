package Ranking;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class RankMatrix {

	private ValueToIndexMapping indexMapping = new ValueToIndexMapping();

	double[][] docRankMatix;

	private int numberOfPagesWithNoLinks = 0;

	public RankMatrix(int nPages) {
		docRankMatix = new double[nPages][nPages];
	}

	
	public void addLink(String pageUrl) {
		indexMapping.getIndex(pageUrl);
	}

	public void addLink(String fromPageUrl, String toPageUrl) {
		addLink(fromPageUrl, toPageUrl, 1);
	}

	public void addLink(String fromPageUrl, String toPageUrl, double weight) {
		int i = indexMapping.getIndex(fromPageUrl);
		int j = indexMapping.getIndex(toPageUrl);

		try {
			docRankMatix[i][j] = weight;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("fromPageUrl:" + fromPageUrl + ", toPageUrl: "
					+ toPageUrl);
		}
	}

	public void calculate() {

		for (int i = 0, n = docRankMatix.length; i < n; i++) {

			double rowSum = 0;

			for (int j = 0, k = docRankMatix.length; j < k; j++) 
				rowSum += docRankMatix[i][j];
			
			if (rowSum > 0) {

				for (int j = 0, k = docRankMatix.length; j < k; j++) {
					if (docRankMatix[i][j] > 0) {

						docRankMatix[i][j] = docRankMatix[i][j] / rowSum;
					}
				}

			} else 				
				numberOfPagesWithNoLinks++;
		}
	}


	public ValueToIndexMapping getIndexMapping() {
		return indexMapping;
	}

	public double[][] getMatrix() {
		return docRankMatix;
	}

	public int getNumberOfPagesWithNoLinks() {
		return this.numberOfPagesWithNoLinks;
	}

	public int getSize() {
		return docRankMatix.length;
	}

	public void print() {

		StringBuilder txt = new StringBuilder();
		BufferedWriter bw;
		FileWriter fw;
		try {
			
			fw = new FileWriter("C:\\FYP\\DocRank.csv");
			 bw = new BufferedWriter(fw);
		


	//	txt.append("\n").append("\n");

		for (int i = 0, n = docRankMatix.length; i < n; i++) {

			for (int j = 0, k = docRankMatix.length; j < k; j++) {

		//		txt.append(" ");
				txt.append(docRankMatix[i][j]);

				if (j < k - 1) {
					txt.append("\t");
				} else {
					txt.append("\n");
				}
			}
			bw.write(txt.toString());
			bw.flush();
			txt = new StringBuilder();
		}

		
		bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
