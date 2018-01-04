package autocomplete;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
@SuppressWarnings("deprecation")
public class AutoCompletionAnalyzer extends Analyzer {  
	  
	  private static final int MAX_TOKEN_LENGTH =  StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;  
	  
	  
	private static final boolean ENABLE_POSITION_INCREMENTS =  
	   StopFilter.getEnablePositionIncrementsVersionDefault(Version.LUCENE_30);  
	  //private static final Set<!--?--> STOP_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;  
	  
	  @Override  
	  public TokenStream tokenStream(String fieldName, Reader reader) {  
	  
	    StandardTokenizer tokenStream =   
	      new StandardTokenizer(Version.LUCENE_CURRENT, reader);  
	    tokenStream.setMaxTokenLength(MAX_TOKEN_LENGTH);  
	  
	    TokenStream result = new StandardFilter(tokenStream);  
	    result = new LowerCaseFilter(result);  
	    result = new StopFilter(ENABLE_POSITION_INCREMENTS, result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);  
	    result = new EdgeNGramTokenFilter(result, Side.FRONT, 1, 20);  
	  
	    return result;  
	  }  
	}  
