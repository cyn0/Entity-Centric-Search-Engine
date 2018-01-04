package autocomplete;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.hwpf.HWPFDocument;
import org.mozilla.universalchardet.UniversalDetector;
import org.tartarus.snowball.ext.EnglishStemmer;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

import AttributeTable.RDFHelper;

import constant.constants;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.Lin;

import readers.MSWordDocumentParser;
import readers.MSwordReader;

public class test {
	private static ILexicalDatabase db = new NictWordNet();
	protected static final String uri ="http://www.w3.org/2001/vcard-rdf/3.0#";
	
	public static void main(String[] a) throws Exception {
		BufferedReader br = null;
		 
		try {
 
			String csvFilename = "C:\\FYP\\DocRank.csv";
			CSVReader csvReader = new CSVReader(new FileReader(csvFilename), '\t', '\'', 10);
			String[] row = null;
			while((row = csvReader.readNext()) != null) {
			    System.out.println(row[0]
			              + " # " + row[1]
			              + " #  " + row[2]);
			}
			//...
			csvReader.close();
	      
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	




}
    
