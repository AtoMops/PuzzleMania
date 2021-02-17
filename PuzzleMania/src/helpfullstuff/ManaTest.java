package helpfullstuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/* hier haben wir die stabile ManaTest-Methode
 * --> das Ding läuft stabil
 * 
 * kann sein das man das noch einfacher schreiben kann aber erstmal klappt das ^^'
 * 
 */


public class ManaTest {

	/* durchgetestet --> läuft! ^^
	 * --> müssen wir noch in Methode packen
	 * 
	 */
	
	public static void main(String[] args) {

		// testlist
		List<String> inList = new ArrayList<String>();
		inList.add("hydra_red");
		inList.add("hydra_red");
		inList.add("hydra_blue");
		inList.add("hydra_blue");
		inList.add("hydra_red");
		inList.add("hydra_red");
		inList.add("hydra_red");
		inList.add("hydra_blue");
		
		
		// first check for frequency (which Mana does appear > 2)
		List<String> opt = inList.stream().filter(i -> Collections.frequency(inList, i) > 2).distinct()
				.collect(Collectors.toList());
		
		List<Integer> freqCount = new ArrayList<Integer>();
		for (int j = 0; j < opt.size(); j++) {
			System.out.println("testing for: " + opt.get(j));
			Integer cnt  = Collections.frequency(inList, opt.get(j)); 
			freqCount.add(cnt);
		}
		
		// this is WHAT appeared more than 2 times
		System.out.println("opt is: ");
		opt.stream().forEach(System.out::println);
		
		// this is HOW often it appeared (we know already that min freq is 3)
		System.out.println("frequency for opt is: ");
		freqCount.stream().forEach(System.out::println);
		
		
		for (int j = 0; j < opt.size(); j++) {
			System.out.println("testing for: " + opt.get(j));
			List<Integer> cList = chkCount(opt.get(j), inList, freqCount.get(j));
			System.out.println("count for " + opt.get(j) + " is: " + cList.size());
			cList.stream().forEach(System.out::println);
		}
		
	}
	
	private static List<Integer> chkCount(String testString, List<String> testList, Integer freqCount) {

		int pos = 0;
		List<String> conList = new ArrayList<String>();
		List<Integer> posList = new ArrayList<Integer>();
		
		Iterator<String> itr = testList.listIterator();   

        // way to test if no double rows can appear 
        if (freqCount < 6 || freqCount == 8) { // for <6 no double rows can appear; 8 means its the whole row
        	System.out.println("normal test");
			while (itr.hasNext()) {
					if (itr.next() == testString) {
						conList.add(testString);
						System.out.println("match: " + testString + "; pos is: " + pos);
						posList.add(pos);
					} else if ((conList.size() < 3)) {
						conList.clear();
						posList.clear();
					} else {
						return posList;
					}
					pos++;
			}
        }
        
        // way to test if doubles rows may appear (this CAN happen but does not have to)
        if (freqCount >= 6 && freqCount != 8) {
        	System.out.println("special test");
        	while (itr.hasNext()) {
				if (itr.next() == testString) {
					conList.add(testString);
					System.out.println("match: " + testString + "; pos is: " + pos);
					posList.add(pos);
				} else if ((conList.size() < 3)) {
					conList.clear();
					posList.clear();
				} else if(conList.size() == 3 || conList.size() == 4) {  
					System.out.println("found 3 or 4 in row");
					// remove already found positions
					for (int i = 0; i < posList.size(); i++) {
						testList.set(posList.get(i), "");
					}
					System.out.println("new testList is: " );
					testList.stream().forEach(System.out::println);
					
					System.out.println("freqCount: " + freqCount);
					System.out.println("posList.size(): " + posList.size());
					
					if (conList.size() == 3) {
						System.out.println("is 3");
						System.out.println("last pos ist: " + posList.get(posList.size()-1));
						int lastPos = posList.get(posList.size()-1);
						
						if (lastPos == 2 || lastPos == 3) { // nur in diesen Fällen kann noch eine 2. Reihe der gleichen Farbe vorkommen
							int posSub = 0;
							Iterator<String> itr2 = testList.listIterator();  
							while (itr2.hasNext()) {
								if (itr2.next() == testString) {
									conList.add(testString);
									System.out.println("match in sublist: " + testString + "; pos is: " + posSub);
									posList.add(posSub);
								} else if ((conList.size() < 3)) {
									conList.clear();
									posList.clear();
								}
								posSub++;
							}
						} else {
							return posList;
						}
							return posList;
						} // end 3 test
					
					// nur test
					if (conList.size() == 4) {
						System.out.println("is 4");
						System.out.println("last pos ist: " + posList.get(posList.size()-1));
						int lastPos = posList.get(posList.size()-1);
						
						if (lastPos == 3) { // nur in diesem Fall kann noch eine 2. Reihe der gleichen Farbe vorkommen
							int posSub = 0;
							Iterator<String> itr2 = testList.listIterator();  
							while (itr2.hasNext()) {
								if (itr2.next() == testString) {
									conList.add(testString);
									System.out.println("match in sublist: " + testString + "; pos is: " + posSub);
									posList.add(posSub);
								} else if ((conList.size() < 3)) {
									conList.clear();
									posList.clear();
								}
								posSub++;
							}
							}else {
								return posList;
							}
								return posList;
							} // end 4 test
					
        		} else if ((conList.size() > 3) && (posList.size() < 6)){
					System.out.println("found subrow < 6");	
					return posList;
				} else if ((conList.size() > 3) && (posList.size() < 7)){
					System.out.println("found subrow < 7");
					return posList;
				}
				pos++;
        	}       	
		}
		return posList;
	} 
}
