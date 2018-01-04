package Ranking;

import java.util.HashMap;
public class TextDocumentTerms {

	HashMap<String, Integer> termFrequency;

	public TextDocumentTerms(String text) {

		String[] terms = text.split("\\s");

		termFrequency = new HashMap<String, Integer>(terms.length);

		for (String s : terms) {
			Integer f = termFrequency.get(s);

			if (f == null)
				termFrequency.put(s, Integer.valueOf(1));
			else 
				termFrequency.put(s, ++f);
		}
	}

	public String[] getTerms() {
		String[] terms = new String[termFrequency.size()];
		int i = 0;
		for (String s : termFrequency.keySet()) {
			terms[i] = s;
			i++;
		}
		return terms;
	}

	public HashMap<String, Integer> getTf() {
		return termFrequency;
	}
}
