package Ranking;



public class DocRank extends Rank 
{
	DocRankMatrixBuilder docRankBuilder;

	public DocRank() {
		docRankBuilder = new DocRankMatrixBuilder();
		docRankBuilder.run();
	}

	@Override
	public RankMatrix getH() {
		return docRankBuilder.getH();
	}
	
	@Override
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
	
}
