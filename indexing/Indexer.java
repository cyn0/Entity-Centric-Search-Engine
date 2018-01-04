package indexing;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import constant.constants;
import readers.*;


public class Indexer {
	
@SuppressWarnings("deprecation")
public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(constants.indexDirectory));
		
		File stopWordsFile = new File("C:\\FYP\\stopwords.txt");
		
		//IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_30,new StandardAnalyzer(Version.LUCENE_30,new FileReader(stopWordsFile)));
		//writer = new IndexWriter(dir, iwc);
		writer = new IndexWriter(dir,new StandardAnalyzer(Version.LUCENE_30,stopWordsFile),true,IndexWriter.MaxFieldLength.UNLIMITED);
		//writer = new IndexWriter(dir,new AutoCompletionAnalyzer(),true,IndexWriter.MaxFieldLength.UNLIMITED);
	}

public static void main(String[] args) throws Exception {
	
	
	long start = System.currentTimeMillis();
	Indexer indexer = new Indexer(constants.indexDirectory);
	int numIndexed;
	try {
		numIndexed = indexer.index(constants.fileDirectorySmall, new TextFilesFilter());
	}finally {
		indexer.close();
	}
	long end = System.currentTimeMillis();
	System.out.println("Indexing " + numIndexed + " files took "+ (end - start) + " milliseconds");
}

private IndexWriter writer;

public void close() throws IOException {
	writer.close();
}

public int index(String dataDir, FileFilter filter)throws Exception {
	File[] files = new File(dataDir).listFiles();
		for (File f: files) {
			if (!f.isDirectory() &&!f.isHidden() &&f.exists() &&f.canRead() ) {
				indexFile(f);
			}
		}
return writer.numDocs();
}


private static class TextFilesFilter implements FileFilter {
	public boolean accept(File path) {
		return path.getName().toLowerCase().endsWith(".txt");
}
}
protected Document getDocument(File f) throws Exception {
	Document doc = new Document();
	
	//PorterStemmer stemmer=new PorterStemmer();
    
	
	String fileData = "";
	if(f.getAbsolutePath().endsWith(".doc"))
		fileData= MSwordReader.readData(f.getAbsolutePath());
	else
		fileData= TxtReader.readData(f.getAbsolutePath());
	
	System.out.println(fileData);
	doc.add(new Field(constants.fieldCont, fileData.toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	
	
	if(f.getAbsolutePath().endsWith(".doc")){
		String[] parts = fileData.split("[ \n\r]+");
		for(String p : parts){
//			String stemmedWord = stemmer.stem(p);
//			doc.add(new Field("contents",stemmedWord.toLowerCase(),Field.Store.YES, Field.Index.NOT_ANALYZED));
			
			doc.add(new Field(constants.fieldContents,p.toLowerCase(),Field.Store.YES, Field.Index.NOT_ANALYZED,TermVector.WITH_POSITIONS_OFFSETS));
		}
			
	}
	else
		doc.add(new Field(constants.fieldContents,new FileReader(f)));
	
	doc.add(new Field(constants.fieldFileName, f.getName(),Field.Store.YES, Field.Index.NOT_ANALYZED));
	
	doc.add(new Field(constants.fieldFullPath, f.getCanonicalPath(),Field.Store.YES, Field.Index.NOT_ANALYZED));
	
	return doc;
}
private void indexFile(File f) throws Exception {
	System.out.println("Indexing " + f.getCanonicalPath());
	Document doc = getDocument(f);
	writer.addDocument(doc);
}
}