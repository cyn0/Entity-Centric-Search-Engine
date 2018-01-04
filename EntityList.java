import java.util.ArrayList;


public class EntityList {
	public static  ArrayList<String> Phn = new ArrayList<String>();
	
	public static ArrayList<String> Email = new ArrayList<String>();
	
	
	
	public static void initialise(){
		Phn.add("contact");
		Phn.add("call");
		
		
		Email.add("contact");
		Email.add("mail");
	}
	
	public  static ArrayList<String> getEntitiesList(String words){
		
		ArrayList< String> entities = new ArrayList<>();
		String[] word = words.split(" ");
		for(String w : word){
			if(Phn.contains(w)){
				if(!entities.contains("PHN"))
					entities.add("PHN");
				System.out.println("____________Added Phone____________");
			}
			if(Email.contains(w.trim())){
				if(!entities.contains("EML"))
					entities.add("EML");
				System.out.println("____________Added email____________");
			}
		}
		return entities;
	}
	
}
