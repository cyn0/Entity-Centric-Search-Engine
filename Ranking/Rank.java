package Ranking;

import java.io.BufferedWriter;
import java.io.FileWriter;


public abstract class Rank {

	
	private double alpha;
	private double epsilon;
	double[] pR;
	RankMatrix matrixH = null;
	int n;
	double inv_n;

	
	public void build() throws Exception {

		init();
		
		findVector(alpha, epsilon);
	}

	
	private void init() {
		matrixH = getH();
		n = matrixH.getSize();
		if (n > 0)
			inv_n = (double) 1 / n;
		else
			inv_n = (double) 1 / 0.000001d;
	}

	public void findVector(double alpha, double epsilon) {
		
		double[][] docR = matrixH.getMatrix();
		double error = 1;
		
		pR = new double[n];
		
		double[] tempPR = new double[n];

		for (int i = 0; i < n; i++) {
			pR[i] = inv_n;
		}

		double tNodes = (1 - alpha) * inv_n;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				
				docR[i][j] = alpha * docR[i][j] + tNodes;
			}
		}

		int k = 0;

		while (error >= epsilon) {
	
			for (int i = 0; i < n; i++) {
				tempPR[i] = pR[i];
			}

			double temp = 0;
			
			for (int i = 0; i < n; i++) {
				temp = 0;
				for (int j = 0; j < n; j++) {
					temp += tempPR[j] * docR[j][i];
				}
				pR[i] = temp;
			}

			error = normalise(pR, tempPR);
			k++;
		}

		
		//List<RelevanceScore> allRankings = new ArrayList<RelevanceScore>();
		FileWriter fw;
		BufferedWriter bw;
		//StringBuilder txt = new StringBuilder();
		try{
			 fw = new FileWriter("C:\\FYP\\Rank.csv");
			 bw = new BufferedWriter(fw);
			
			for (int i = 0; i < n; i++) {
				bw.write(pR[i]+"");
				bw.write("\n");
			}
			bw.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getAlpha() {
		return alpha;
	}

	
	public double getEpsilon() {
		return epsilon;
	}

	public abstract RankMatrix getH();

	public double getPageRank(String url) {

		int i = getH().getIndexMapping().getIndex(url);
		double val;
		if (i < pR.length && !(i < 0)) {
			val = pR[i];
		} else {
			val = 0;
		}
		return val;
	}

	private double normalise(double[] a, double[] b) {
		double norm = 0;
		int n = a.length;

		for (int i = 0; i < n; i++)
			norm += Math.abs(a[i] - b[i]);
		
		return norm;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
}