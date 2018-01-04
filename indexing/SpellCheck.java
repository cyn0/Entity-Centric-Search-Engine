package indexing;
import java.io.File;
import java.io.IOException;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import constant.constants;


public class SpellCheck {
public static void main(String[] args) throws IOException {

	String indexField = "contents";
	System.out.println("Now building SpellChecker index...");
	Directory dir = FSDirectory.open(new File(constants.suggestionsDirectory));
	SpellChecker spell = new SpellChecker(dir);
	long startTime = System.currentTimeMillis();


	Directory dir2 = FSDirectory.open(new File(constants.indexDirectory));
	IndexReader r = IndexReader.open(dir2);
	
//IndexWriterConfig config = new IndexWriterConfig(version,new WhitespaceAnalyzer(version));

//try {
//	spell.indexDictionary(new LuceneDictionary(r, indexField),config,false);
//}

	try{
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_30,new StandardAnalyzer(Version.LUCENE_30));
		spell.indexDictionary(new LuceneDictionary(r, indexField),config,true);
//	File[] files = new File("filesToIndex").listFiles();
//	for (File f: files) {
//	if (!f.isDirectory() &&!f.isHidden() &&f.exists() &&f.canRead() ) {
//		System.out.println("indexing "+ f.getName());
//		config = new IndexWriterConfig(version,new StandardAnalyzer(version));
//		spell.indexDictionary(new PlainTextDictionary(f),config,true);
//	}
//	}
	}
	finally {
		//r.close();
	}
	dir.close();
	//dir2.close();
	long endTime = System.currentTimeMillis();
	System.out.println(" took " + (endTime-startTime) + " milliseconds");
}
}