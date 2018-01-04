import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import AttributeTable.HtmlParser;


public class docconverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File[] files = new File("C:\\datasets\\google\\files").listFiles();
		File newdocfile;
	BufferedWriter bw; 
		int i=0,count=0;
    	for (File f: files) {
    		//if(i++>10) break;
    		if(!(f.getName().contains(".html")))
    				continue;
    		Document doc=null;
    		
    		try {  
    			String html = f.getCanonicalPath();
    			doc = Jsoup.parse(new File(html), "UTF-8");
    		
    			newdocfile = new File("C:\\datasets\\docs\\"+f.getName().replace(".html", ".txt"));
    			 bw = new BufferedWriter(new FileWriter(newdocfile));
    			 String content = doc.text();
    			 int index = content.lastIndexOf("References");   
    			 if(index==-1)
    			 {
    				
    				 bw.write(content.substring(0, content.lastIndexOf("Privacy policy")));
    				 count++;
    			 }
    			 else
    				 bw.write(content.substring(0, index));
    			 
    			 bw.close();
    			
    		} catch (IOException ex) {
    			Logger.getLogger(HtmlParser.class.getName()).log(Level.SEVERE, null, ex);
    			ex.printStackTrace();
    		
    		}
    	
	}
System.out.print("DOC WITHOUT REFERENCES"+count);
	}
}
