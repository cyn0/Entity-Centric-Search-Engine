package AttributeTable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import PlingStem.PlingStemmer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import constant.constants;

public class RDFHelper {
	protected static final String uri ="http://www.w3.org/2001/vcard-rdf/3.0#";
	private static String RDFTripleFile = "C:\\FYP\\Short.ntriples";
	
	static FileOutputStream fouttriple;
	static FileOutputStream fout;
	static Model model1,model2;
	
	public static void init()
	{
		try {
			fouttriple = new FileOutputStream(RDFTripleFile);
			fout = new FileOutputStream(constants.RDFFile);
			model1 = ModelFactory.createDefaultModel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void initFromBuilder()
	{
		try {
			fouttriple = new FileOutputStream(RDFTripleFile);
			fout = new FileOutputStream(constants.RDFFile,true);
		    model2 = ModelFactory.createDefaultModel();
			} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String parseRelation(String relation){
		if(relation.length()<3)
			relation = "other";
		
		relation = relation.replaceAll("[^a-zA-Z ]", "");
	//	relation = relation.trim().replaceAll(" ", "< >");
		relation = relation.trim().replaceAll(" ", "%20");
//		
		relation = relation.toLowerCase();
		relation = stemWord(relation);
		
		if(relation.length()<3)
			relation = "other";
		//System.out.println("R___"+relation);
		
		return relation;
	}
	
	public static void  addProperytoRDFFromBuilder(String entity1, String entity2, String relation,String f){
		
		relation = parseRelation(relation);
		Property property = model2.createProperty(uri , relation);
		
		System.out.println(entity2);
		Resource E1	= model2.createResource(uri + entity1);
		Resource E2 = model2.createResource(uri + entity2);
		
		E1.addProperty(property, E2);
		
	}

	public static ArrayList<String> getPropertyOfResourse(String entity){
		ArrayList<String> predicates = new ArrayList<String>();
		Model model = ModelFactory.createDefaultModel();
		InputStream in = FileManager.get().open(constants.RDFFile );
		if (in == null)
		    throw new IllegalArgumentException("File: not found");
		model.read(in,null);
		
		Resource E1	= model.createResource(uri + entity);
		System.out.println("Getting the property of " + entity);
		StmtIterator iter = E1.listProperties();
		
		while(iter.hasNext()){
			String temp = iter.next().getPredicate().toString();
			int start = temp.indexOf("#")+1; 
			temp = temp.substring(start);
			System.out.println("P___" +temp);
			predicates.add(temp);
		}
		if(predicates.size() < 1){
		
		}
		return predicates;
	}
	public static void  addProperytoRDF(String entity1, String entity2, String relation,String f){
	
		relation = parseRelation(relation);
		Property property = model1.createProperty(uri , relation);
		entity2 = entity2.replaceAll("[^a-zA-Z0-9 ]", "");
		entity2 = entity2.replaceAll("'", "");
		Resource E1	= model1.createResource(uri + entity1);
		Resource E2 = model1.createResource(uri + entity2);
		
		E1.addProperty(property, E2);
	
	}
	
	public static void writeToFileFrom()
	{
		try{
			model2.write(System.out);
			fout.close();
			model2.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void writeToFile()
	{	
		
		try{
			model1.write(fout);
			fout.close();
			model1.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	public static StringBuffer readRdf(String entity,String relation){
		
		if(entity =="" || relation =="")
			return null;
		
		StringBuffer results = new StringBuffer();
		
		entity = entity.toLowerCase();
		
		relation = relation.toLowerCase();
		//relation = relation.replaceAll(" ", "%20");
		relation = stemWord(relation);
		System.out.println("r___" + relation);
		System.out.println("READING AND DISPLAYING RESUTS");
		Model model = ModelFactory.createDefaultModel();
		// read the RDF/XML file
		
		InputStream in = FileManager.get().open(constants.RDFFile );
		if (in == null)
		    throw new IllegalArgumentException("File: not found");
		model.read(in,null);
		//model.write(System.out);
		
		ResIterator iter=model.listSubjectsWithProperty(model.createProperty(uri , relation));
		System.out.print("________________ANSWER________________");
		while(iter.hasNext())
		{
			Resource temp=iter.nextResource();
		
			//System.out.println(temp.getLocalName());
			
			if(temp.getLocalName().toLowerCase().contains(entity.toLowerCase()))
			{
				
				String tempstr = temp.getProperty(model.createProperty(uri , relation)).getObject().toString();
				int index = tempstr.indexOf('#')+1;
				//System.out.println(tempstr.substring(index));
				results.append(tempstr.substring(index) + " ");
			}
			
			else if(temp.getPropertyResourceValue(model.createProperty(uri , relation)).toString().contains(entity))
			{
				
				results.append(temp.getLocalName() + " ");
			}
		}
		try {
			in.close();
		} catch (Exception e) {	
			e.printStackTrace();
		}
		return results;
	}
	
	public static String stemWord(String word){
		 PlingStemmer stemmer = new PlingStemmer();
		 return stemmer.stem(word);
	}

}
