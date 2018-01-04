import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import constant.constants;

public class SpellCheckEx {


public static String[] getSuggestions(String wordToRespell) throws IOException{
	Directory dir = FSDirectory.open(new File(constants.suggestionsDirectory));
	//Directory dir = FSDirectory.getDirectory(spellCheckDir);
	if (!IndexReader.indexExists(dir)) {
		System.out.println("\nERROR: No spellchecker index at path \"" +constants.suggestionsDirectory +
				"\"; please run CreateSpellCheckerIndex first\n");
		System.exit(1);
	}
	
	SpellChecker spell = new SpellChecker(dir);
	spell.setStringDistance(new LevensteinDistance());
	String[] suggestions = spell.suggestSimilar(wordToRespell, 5);
	System.out.println(suggestions.length +" suggestions for '" +wordToRespell + "':");

//	for (String suggestion : suggestions)
//		System.out.println(" " + suggestion);
	return suggestions;
}
}