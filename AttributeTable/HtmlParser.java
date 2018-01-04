/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AttributeTable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HtmlParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	RDFHelper.init();
    	File[] files = new File("C:\\datasets\\google\\files").listFiles();
    
    	int i=0;
    	for (File f: files) {
    		//if(i++>200) break;
    		if(!(f.getName().contains(".html")))
    				continue;
    		Document doc=null;
    		try {  
    			String html = f.getCanonicalPath();
    			doc = Jsoup.parse(new File(html), "UTF-8");
    		} catch (IOException ex) {
    			Logger.getLogger(HtmlParser.class.getName()).log(Level.SEVERE, null, ex);
    			ex.printStackTrace();
    		}
    		String entity1 = f.getName().replace(".html","").replaceAll("[^a-zA-Z0-9\\s]", "");
    		System.out.println("E__"+entity1);
    		
        Elements tables = doc.select("table[class^=infobox]");
        for(Element table : tables)
        {

	        if(table==null)
	        {
	        	System.out.print("TABLE NOT FOUND");
	        	continue;
	        }
	        Elements trs = table.select("tr");
	        Iterator trIter = trs.iterator();
	        
	        while (trIter.hasNext()) {
	            Element tr = (Element)trIter.next();    //each row corresponds to an entry
	          
	            Elements ths = tr.select("th");         //table heading will give the relation name
	            if(ths.size()<=0)
	                continue;
	            
	            
	            Elements tds = tr.select("td");         //table data will give the relation value
	            if(tds.size()<=0)
	            	continue;
	            
	            Iterator thIter = ths.iterator();
	            Element th=(Element)thIter.next();
	            String relation = th.text().replaceAll("\\?", "");
	            
	            Iterator tdIter = tds.iterator();
	            Element td=(Element)tdIter.next();
	            String entity2 = td.text().replaceAll("\\[(.*?)\\]", "");
	            //System.out.println();
	            
	            RDFHelper.addProperytoRDF(entity1, entity2, relation, "filePath");
	           
	        }
        
        }
    }
    	RDFHelper.writeToFile();
    }
}
