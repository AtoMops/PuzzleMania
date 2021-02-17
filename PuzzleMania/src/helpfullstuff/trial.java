package helpfullstuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class trial {
	
	public static void main(String[] args) {
	
		Map<String,String> mapIDImages = new LinkedHashMap<String,String>(); 
		
		/*ID: #65, Image: EMPTY#65
		ID: #55, Image: EMPTY#55
		ID: #45, Image: EMPTY#45
		ID: #35, Image: EMPTY#35
		ID: #25, Image: hydra_blue
		ID: #15, Image: hydra_red
		ID: #05, Image: hydra_green
		 */
		
		mapIDImages.put("#65", "hydra_green");
		mapIDImages.put("#55", "hydra_red");
		mapIDImages.put("#45", "EMPTY#45");
		mapIDImages.put("#35", "EMPTY#35");
		mapIDImages.put("#25", "hydra_blue");
		mapIDImages.put("#15", "EMPTY#35");
		mapIDImages.put("#05", "hydra_green");
		
		
		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){ //using map.entrySet() for iteration  
			  System.out.println("ID: " + entry.getKey() + ", Image: " + entry.getValue());   
		  }
		  System.out.println();
		  
		  
		  List<String> lstImg = new ArrayList<String>();
		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){ 
			  if (!entry.getValue().contains("EMPTY")) {
				  lstImg.add(entry.getValue());
			}
		  }
		  
		  lstImg.stream().forEach(System.out::println);
		  
		  
		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){
			  if (entry.getValue().contains("EMPTY")) {
				  mapIDImages.replace(entry.getKey(), "EMPTY");
			  }
		  }
		  

		  while (mapIDImages.containsValue("EMPTY") && !lstImg.isEmpty()) {
			  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){
				  System.out.println("running: " + entry.getValue());
				  if (!lstImg.isEmpty()) {
					  mapIDImages.put(entry.getKey(),  lstImg.get(0));
					  lstImg.remove(0);
					  System.out.println("lstImg.size(): " + lstImg.size());
				  } else {
					  mapIDImages.put(entry.getKey(), "EMPTY");
				  }
			  }
		  }
		  
		  
		  System.out.println();
		  for (Map.Entry<String,String> entry : mapIDImages.entrySet()){ //using map.entrySet() for iteration  
			  System.out.println("ID: " + entry.getKey() + ", Image: " + entry.getValue());   
		  }
		  
		
			
	}
	
}
