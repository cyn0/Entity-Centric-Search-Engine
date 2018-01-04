import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;

import org.apache.jena.iri.impl.Main;



public class GUI extends JFrame{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -4644016414916386275L;
	
	JPanel MainPanel = new JPanel();
	JPanel modifiedResults = new JPanel();
	JPanel modifiedResultPanel = new JPanel();
	JPanel ResultList = new JPanel();
	JPanel DidUmeanPanel = new JPanel();
	JPanel suggestionPanel = new JPanel();
	JPanel entityResultPanel = new JPanel();
	
	JTextArea ResultContents=new JTextArea();
	
	JScrollPane scroll = new JScrollPane(ResultContents);	
	
	JTextField QueryBox=new JTextField(50);
	
	JButton Search=new JButton("Search");
	
	JLabel mainLabelCard = new JLabel();
	JLabel taglineCard = new JLabel();
 	
	JLabel Didumean = new JLabel("<html><body><font size=3 color=\"red\">Did you mean?  </body></html>");
	
	JLabel Title = new JLabel("<html><body><u><font size=10 color=\"red\">SEARCH ENGINE</u></body></html>");
	JLabel ResultTitle = new JLabel();
	
	JLabel[] R = new JLabel[20];
	
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, MainPanel, modifiedResults);
	
	JWindow popUpAutoComplete = new JWindow(this);
	
	public GUI(){
		
		Didumean.setVisible(false);
	    DidUmeanPanel.setPreferredSize(new Dimension(500, 50));
	    
	    modifiedResults.setLayout(new BorderLayout());
	    modifiedResults.add(entityResultPanel,BorderLayout.NORTH);
	    modifiedResults.add(modifiedResultPanel,BorderLayout.CENTER);
	    
//	    modifiedResultPanel.setLayout(new FlowLayout());
	    mainLabelCard.setPreferredSize(new Dimension(700,50));
	    mainLabelCard.setFont(mainLabelCard.getFont().deriveFont(60));
		entityResultPanel.add(mainLabelCard);
		entityResultPanel.add(taglineCard);
		entityResultPanel.setPreferredSize(new Dimension(500, 100));
		entityResultPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		entityResultPanel.setVisible(false);
		
	    ResultList.setLayout(new BoxLayout(ResultList, BoxLayout.PAGE_AXIS));
	      
	    DidUmeanPanel.setLayout(new BoxLayout(DidUmeanPanel,BoxLayout.LINE_AXIS));
	    DidUmeanPanel.add(Didumean);
	
	    suggestionPanel.setLayout(new GridLayout(0, 1));
		suggestionPanel.setBackground(Color.WHITE.brighter());
		
		
	    
	    MainPanel.setLayout(new FlowLayout());
	   
	    MainPanel.add(Title);
	    MainPanel.add(QueryBox);
	    MainPanel.add(Search);
	    MainPanel.add(DidUmeanPanel);
	  //  MainPanel.add(entityResultPanel);
	    MainPanel.add(ResultList);
	    
	    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	       
	    this.add(splitPane);
		splitPane.setDividerLocation(650);
		splitPane.setEnabled(false);
		
		popUpAutoComplete.setLocation(10, 105);
		
	
	}
}
