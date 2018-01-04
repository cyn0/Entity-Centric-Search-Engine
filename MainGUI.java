import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import constant.constants;
import readers.*;
import au.com.bytecode.opencsv.CSVReader;
import autocomplete.AutoCompletion;

import AttributeTable.RDFHelper;
import Ranking.DocRank;
import Ranking.EntityRank;



@SuppressWarnings("deprecation")
public class MainGUI extends GUI{
	
	private static final long serialVersionUID = 3127807820813515301L;
	
	private JLabel Dm[];
	
	int count=0;
	QueryProcess qryProcess;
	
	String refinedquery= "";
	public MainGUI()
	{
		super();
		qryProcess = new QueryProcess();
		new Thread()
		{
		    public void run() {
		    	qryProcess.initClassifiers();
		    }
		}.start();
		
		
		QueryBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				setAutoCompletion();
			}
		});
		
		QueryBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				closeSuggestionWindow();
				try {
					getSuggestions();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//setCardResult();
				performSearch();		
			}
		});
		
		//adding listener to SEARCH button
		Search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					getSuggestions();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//performSearch();
				refinedquery = "";
				setCardResult();
				performSearchUsingDocRank();
			}
		});

	}
	
	protected void setAutoCompletion(){
		String text = QueryBox.getText();
		int occurenceOfLastSpace = text.lastIndexOf(" ")+1;
		if(occurenceOfLastSpace>1)
			text = text.substring(occurenceOfLastSpace);
	
		
		  List<String> suggestions = getAutoCompletionList(text);
		  
		  Iterator<String>iter = suggestions.iterator();
		  int ht = 0;
		  suggestionPanel.removeAll();
		  while(iter.hasNext()){
			  String s = iter.next();
			  System.out.println(s);
			  JLabel t = new JLabel(s);
			  t.setPreferredSize(new Dimension(600, 25));
			  suggestionPanel.add(t);
			  ht+=25;
		  }
		  suggestionPanel.setVisible(true);
		  
		  popUpAutoComplete.add(suggestionPanel);
		  popUpAutoComplete.setVisible(true);
		  popUpAutoComplete.setSize(500,ht);

	}
	
	protected List<String> getAutoCompletionList(String text){
		Directory AutoCompletedir;
		List<String> suggestions = null;
		try {
			AutoCompletedir = FSDirectory.open(new File(constants.autoCompleteDirectory));	
			AutoCompletion ac = new AutoCompletion(AutoCompletedir);
			suggestions = ac.suggestTermsFor(text, 10);
	  
		}catch (Exception e) {
			e.printStackTrace();
		}
		return suggestions;
	}
	
	
	
	protected void performSearch() {
		
		ResultList.removeAll();
		count =0;
		
		
		String Query=QueryBox.getText();
		//SHOULD PREPROCESS QUERY
		
		try {
			IndexSearcher is = getIndexSearchforDirectory(constants.indexDirectory);
			
			QueryParser parser = new QueryParser(Version.LUCENE_30,constants.fieldContents,
					new StandardAnalyzer(Version.LUCENE_30));
			
			Query query = parser.parse(Query);
			long start = System.currentTimeMillis();
			TopDocs hits = is.search(query, 10);
			long end = System.currentTimeMillis();
			System.err.println("Found " + hits.totalHits +" document(s) (in " + (end - start) +" milliseconds) that matched query '" +
					Query + "':");
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				
				Document doc = is.doc(scoreDoc.doc);
				System.out.println(doc.get("fullpath") + "____" + scoreDoc.doc + "_____________");
				 
				ResultList.add((R[count]=new JLabel("<html><body><font size=5 color=\"blue\"><u>"+doc.get("fullpath") + "</u></font>&nbsp;&nbsp;&nbsp;Score:"+scoreDoc.score+"</body></html>")));
				//StringBuffer text= getMatchedTextLine(path,Query);	//should pass processed query
				//ResultList.add(new JLabel(text.toString()));
				R[count++].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(java.awt.event.MouseEvent arg0) {
						ResultContents.setText("");
						
						//parsing to get Text
						 String s=arg0.getComponent().toString();
						 int startPos=s.indexOf("<u>") + 3;
						 int endPos=s.indexOf("</u>");
						 String dest=s.substring(startPos,endPos);
						 ResultTitle.setText(dest);
						 
						try
						{
							if(dest.endsWith(".doc"))
								ResultContents.append(MSwordReader.readData(dest));
							else
								ResultContents.append(TxtReader.readData(dest));
							
				 		}catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				});
					
			}
				
			is.close();
							
			validate();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void performSearchUsingDocRank() {
		try {
	
//			DocRank dr = new DocRank();
//		
//			dr.setAlpha(0.9);
//		
//			dr.setEpsilon(0.00000001);
//			
//			dr.build();
			
			String modifiedQuery = QueryBox.getText() + refinedquery;
			System.out.println("$$$$$$$$$$$$$$" + modifiedQuery);
			search(modifiedQuery, 7, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	
	
	
public void search(String query, int numberOfMatches, DocRank pR) {
		
	//System.out.print("QRY------"+query);
	//	int n = pR.getH().getSize();
		int n = 17;
		/**
		 * When the number of pages are few, the PageRank values
		 * need some boosting. As the number of pages increases m approaches the
		 * value 1 quickly because 1/n goes to zero.
		 */
		
		double m = 1 - (double) 1 / n;
		try {
			
			IndexSearcher is = getIndexSearchforDirectory(constants.indexDirectory);
			QueryParser parser = new QueryParser(Version.LUCENE_30,constants.fieldContents,
					new StandardAnalyzer(Version.LUCENE_30));
			Query query1 = parser.parse(query);
			long start = System.currentTimeMillis();
			TopDocs hits = is.search(query1, 10);
			long end = System.currentTimeMillis();
			System.err.println("Found " + hits.totalHits +" document(s) (in " + (end - start) +" milliseconds) that matched query '" +
					query + "':");
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				
				System.out.println("score before doc rank"+scoreDoc.score);
				Document doc = is.doc(scoreDoc.doc);
				
				System.out.println(doc.get("fullpath"));
				
				
				EntityList.initialise();
				ArrayList<String> entities = EntityList.getEntitiesList(query);
				float multiplier = EntityRank.calculateEntitiesScore(doc.get("fullpath"),entities);
				System.out.println("multiplier: " + multiplier);
				
				//Local rank
				//scoreDoc.score= (float) (scoreDoc.score * multiplier);
				
				
				//Global aggregation
				double rankValue = 1.0;
				CSVReader csvReader = new CSVReader(new FileReader(constants.csvFilename), '\t', '\'', scoreDoc.doc);
				String[] row = null;
				if((row = csvReader.readNext()) != null) {
				    System.out.println(scoreDoc.doc + "Rank value: " + row[0]);
				    rankValue = Double.parseDouble(row[0]);
				
				}else{
					System.out.println("_______Rank not available");
				}
				scoreDoc.score= (float) (scoreDoc.score * Math.pow(rankValue, m));
				
				System.out.println("score after doc rank"+scoreDoc.score);
				
				
				///////////// scoring based on entities
				//boolean flag = containsEntitiesInTheQuery(doc.get("fullpath"));
				
				System.out.println();
				
				
			}
			System.out.println("--------------------------------------------------------------------------------------------------");
			
			//Sorting the docs
			
			Arrays.sort(hits.scoreDocs, new Comparator<ScoreDoc>() {
				public int compare(ScoreDoc r1, ScoreDoc r2) {
					int result = 0;
					// sort based on score value
					if (r1.score< r2.score) {
						result = 1; // sorting in descending order
					} else if (r1.score > r2.score) {
						result = -1;
					} else {
						result = 0;
					}
					return result;
				}
			});
			
	        
			modifiedResultPanel.removeAll();
			modifiedResultPanel.repaint();
			JLabel title = new JLabel("<html><body><font size=5 color=\"red\"><u>"+"Modified Search results"+"</body></html>");
			title.setPreferredSize(new Dimension(500,20));
	     	modifiedResultPanel.add(title);
			
	     	
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println(doc.get("fullpath"));
				modifiedResultPanel.add((R[count]=new JLabel("<html><body><font size=5 color=\"blue\"><u>"+doc.get("fullpath") + "</u></font>&nbsp;&nbsp;&nbsp;Score:"+scoreDoc.score+"</body></html>")));
				if(EntityRank.EntityValues != null)
					modifiedResultPanel.add(new JLabel(EntityRank.EntityValues.toString()));
				EntityRank.EntityValues = null;
				System.out.println("score: "+scoreDoc.score);
			}
			validate();

		}catch (Exception e) {
			e.printStackTrace();
		}			
	}

	
	
	
	
//	public void search(String query, int numberOfMatches, DocRank pR) {
//		
//		int n = pR.getH().getSize();
////		int n = 3376;
//		/**
//		 * When the number of pages are few, the PageRank values
//		 * need some boosting. As the number of pages increases m approaches the
//		 * value 1 quickly because 1/n goes to zero.
//		 */
//		
//		double m = 1 - (double) 1 / n;
//		try {
//			
//			IndexSearcher is = getIndexSearchforDirectory(constants.indexDirectory);
//			QueryParser parser = new QueryParser(Version.LUCENE_30,constants.fieldContents,
//					new StandardAnalyzer(Version.LUCENE_30));
//			Query query1 = parser.parse(query);
//			long start = System.currentTimeMillis();
//			TopDocs hits = is.search(query1, 10);
//			long end = System.currentTimeMillis();
//			System.err.println("Found " + hits.totalHits +" document(s) (in " + (end - start) +" milliseconds) that matched query '" +
//					query + "':");
//			for(ScoreDoc scoreDoc : hits.scoreDocs) {
//				
//				System.out.println("score before doc rank"+scoreDoc.score);
//				Document doc = is.doc(scoreDoc.doc);
//				
//				System.out.println(doc.get("fullpath"));
//				
//				
//				EntityList.initialise();
//				ArrayList<String> entities = EntityList.getEntitiesList(query);
//				float multiplier = EntityRank.calculateEntitiesScore(doc.get("fullpath"),entities);
//				System.out.println("multiplier: " + multiplier);
//				
//				//Local rank
//				//scoreDoc.score= (float) (scoreDoc.score * multiplier);
//				
//				
//				//Global aggregation
//				scoreDoc.score= (float) (scoreDoc.score * Math.pow(pR.getPageRank(doc.get("fullpath")), m));
//				
//				System.out.println("score after doc rank"+scoreDoc.score);
//				
//				
//				///////////// scoring based on entities
//				//boolean flag = containsEntitiesInTheQuery(doc.get("fullpath"));
//				
//				System.out.println();
//				
//				
//			}
//			System.out.println("--------------------------------------------------------------------------------------------------");
//			
//			//Sorting the docs
//			
//			Arrays.sort(hits.scoreDocs, new Comparator<ScoreDoc>() {
//				public int compare(ScoreDoc r1, ScoreDoc r2) {
//					int result = 0;
//					// sort based on score value
//					if (r1.score< r2.score) {
//						result = 1; // sorting in descending order
//					} else if (r1.score > r2.score) {
//						result = -1;
//					} else {
//						result = 0;
//					}
//					return result;
//				}
//			});
//			
//	        
//			modifiedResultPanel.removeAll();
//			modifiedResultPanel.repaint();
//			JLabel title = new JLabel("<html><body><font size=5 color=\"red\"><u>"+"Modified Search results"+"</body></html>");
//			title.setPreferredSize(new Dimension(500,20));
//	     	modifiedResultPanel.add(title);
//			
//	     	
//			for(ScoreDoc scoreDoc : hits.scoreDocs) {
//				Document doc = is.doc(scoreDoc.doc);
//				System.out.println(doc.get("fullpath"));
//				modifiedResultPanel.add((R[count]=new JLabel("<html><body><font size=5 color=\"blue\"><u>"+doc.get("fullpath") + "</u></font>&nbsp;&nbsp;&nbsp;Score:"+scoreDoc.score+"</body></html>")));
//				if(EntityRank.EntityValues != null)
//					modifiedResultPanel.add(new JLabel(EntityRank.EntityValues.toString()));
//				EntityRank.EntityValues = null;
//				System.out.println("score: "+scoreDoc.score);
//			}
//			validate();
//
//		}catch (Exception e) {
//			e.printStackTrace();
//		}			
//	}
	
	public void setCardResult(){
		String query = QueryBox.getText();
		
		
		ArrayList<String> entityList = qryProcess.getEntities(query);
		ArrayList<String> relationList =  qryProcess.getRelations(query, entityList);
		String entity = "";
		if(entityList!=null && entityList.size()>0)
			entity = entityList.get(0);
		String relation = "";
		if(relationList !=null && relationList.size()>0)
			relation = relationList.get(0);
		System.out.println("E___" + entity +"R_____" +relation);
		//relation = relation.replace(" ", "%20");
		 
		StringBuffer results = RDFHelper.readRdf(entity, relation.replaceAll(" ","%20"));
		
		if(results == null || results.length()<1)
			entityResultPanel.setVisible(false);
		else{
			entityResultPanel.setVisible(true);
		
			mainLabelCard.setText("<html><body><font size=5><center>"+ results +"</center></body></html>");
			taglineCard.setText(entity + ", " + relation);
			
			refinedquery = " " + results + " " + relation;
		}
		
			
	}
	
	
	public void exampl() throws IOException, ParseException{
		System.out.println("_____________________________ExMPLE__________________");
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);  

		Directory dir = FSDirectory.open(new File(constants.entityIndexDirectory));

        // 2. query  
		Query q= new TermQuery(new Term("ORGANIZATION", "Google"));
        //Query q = new QueryParser(Version.LUCENE_CURRENT, constants.fieldLocation, analyzer).parse("Portland");  

        // 3. search  
        int hitsPerPage = 10;  
        IndexSearcher searcher = new IndexSearcher(dir, true);  
        IndexReader reader = IndexReader.open(dir, true);  
        searcher.setDefaultFieldSortScoring(true, false);  
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);  
        searcher.search(q, collector);  
        ScoreDoc[] hits = collector.topDocs().scoreDocs;  

        // 4. display term positions, and term indexes   
        System.out.println("Found " + hits.length + " hits.");  
        for(int i=0;i<hits.length;++i) {  

            int docId = hits[i].doc;  
            TermFreqVector tfvector = reader.getTermFreqVector(docId, constants.fieldOrganisation);  
            TermPositionVector tpvector = (TermPositionVector)tfvector;  
            // this part works only if there is one term in the query string,  
            // otherwise you will have to iterate this section over the query terms.  
            int termidx = tfvector.indexOf("Google");  
            System.out.println(termidx);
            int[] termposx = tpvector.getTermPositions(termidx);  
            TermVectorOffsetInfo[] tvoffsetinfo = tpvector.getOffsets(termidx);  

            for (int j=0;j<termposx.length;j++) {  
                System.out.println("termpos : "+termposx[j]);  
            }  
            for (int j=0;j<tvoffsetinfo.length;j++) {  
                int offsetStart = tvoffsetinfo[j].getStartOffset();  
                int offsetEnd = tvoffsetinfo[j].getEndOffset();  
                System.out.println("offsets : "+offsetStart+" "+offsetEnd);  
            }  

            // print some info about where the hit was found...  
            Document d = searcher.doc(docId);  
            System.out.println((i + 1) + ". " + d.get(constants.fieldFileName));  
        }  

        // searcher can only be closed when there  
        // is no need to access the documents any more.   
        searcher.close();
        
        System.out.println("_____________________________END__________________");
    }      
	
	public void getSuggestions() throws IOException{
		
		String Query=QueryBox.getText();
		for(int i=0;i<5;i++){
			if(Dm!=null)
				DidUmeanPanel.remove(Dm[i]);
		}
		
		Dm = new JLabel[10];
		for(int i=0;i<5;i++)
			Dm[i] = new JLabel();
		
		int c=0;
		String[] suggestions = SpellCheckEx.getSuggestions(Query);
		
		if(suggestions.length > 0)
			Didumean.setVisible(true);
		else
			Didumean.setVisible(false);
		
		for (String suggestion : suggestions){
			Dm[c].setText("<html><body><font size=3 color=\"blue\"><u>"+suggestion+" "+"</u></font></body></html>");		
			Dm[c].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					 String s=arg0.toString();
					 int startPos=s.indexOf("<u>") + 3;
					 int endPos=s.indexOf(" </u>");
					 String dest=s.substring(startPos,endPos);
					 QueryBox.setText(dest);
					
				}
			});
			DidUmeanPanel.add(Dm[c]);
			c++;
		}
			
	}
	
	public IndexSearcher getIndexSearchforDirectory(String DirectoryPath){
		IndexSearcher is = null;
		try {
			Directory dir = FSDirectory.open(new File(DirectoryPath));
			is = new IndexSearcher(dir); 
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}
	
	public static void main(String[] args) {
		
		MainGUI h=new MainGUI();
		h.setVisible(true);
		h.setExtendedState(MAXIMIZED_BOTH);
		h.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	
	public void closeSuggestionWindow(){
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	popUpAutoComplete.setVisible(false);
            }
        });
	
	}

}
