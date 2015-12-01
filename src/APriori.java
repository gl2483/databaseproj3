import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.*;


public class APriori {
	
	List<TreeSet<SetItem>> prevItemSets;  
	HashMap<TreeSet<SetItem>, int[]> prevItemSetsMap;
	HashMap<TreeSet<SetItem>, int[]> allValidItemSets;
	
	Set<Association> associationRules;
	
	List<String[]> allrecords = new ArrayList<String[]>();
	
	int record_count = 0;
	int numIndexes = 1;
	double min_support, min_confidence;
	String filePath;	
	
	public APriori(String filepath, double support, double confidence) {
		this.min_support = support;
		this.min_confidence = confidence;
		this.filePath = filepath;
		prevItemSets = new ArrayList<TreeSet<SetItem>>();
		prevItemSetsMap = new HashMap<TreeSet<SetItem>, int[]>();
		allValidItemSets = new HashMap<TreeSet<SetItem>, int[]>();
		associationRules = new TreeSet<Association>();
	}
	
	
	public void getData(){
		File f = new File(filePath);
		try {
			BufferedReader buf = new BufferedReader(new FileReader(f));

			String line;
			while( (line = buf.readLine()) != null){
				String[] array = line.split(",");
				//simply an array
				allrecords.add(array);
			}
			record_count = allrecords.size();
			numIndexes = record_count / 32 + 1;
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static int[] insertIntoByteArray(int[] array, int index) {
		int arrOffset = index / 32;
		int intOffset = index % 32;
		array[arrOffset] |= (1 << intOffset);
		return array;
	}
	
	public static int getNumBitsSet(int[] array) {
		int count = 0;
		for(int val : array) {
			count += Integer.bitCount(val);
		}
		return count;
	}
	
	public static int[] arrayBitIntersection(int[] arr1, int[] arr2) {
		for(int i = 0; i<arr1.length; i++) {
			arr1[i] &= arr2[i];
		}
		return arr1;
	}
	
	
	public void generateOneItemSets() {
		Comparator<SetItem> comp = new SetItemComparator();
		TreeMap<SetItem, int[]> map = new TreeMap<SetItem, int[]>(comp);
		
		int rowNum = 0;
		int count = 0;
		for(String[] row : allrecords) {
			for(int i=0; i<row.length; i++) {
				if(row[i] == null || row[i].equals("") || row[i].equals("null")) continue;
				
				String item = row[i];
				if(i == 7) {
					if(!row[i].equals("0")) item = "true";
				}else if(i == 6) {
					if(Integer.parseInt(row[i]) > 1) {
						item = "> 1";
						count++;
					}
				}
				SetItem newItem = new SetItem(i, item);
				
				if(map.containsKey(newItem)) {
					int[] rows = map.get(newItem);
					rows = insertIntoByteArray(rows, rowNum);
					map.put(newItem, rows);
				}else {
					int[] rows = new int[numIndexes];
					rows = insertIntoByteArray(rows, rowNum);
					map.put(newItem, rows);
				}
			}
			rowNum++;
		}

		for(Map.Entry<SetItem, int[]> item : map.entrySet()) {
			double sup = ((double) getNumBitsSet(item.getValue())) / ((double) record_count);
			if(sup >= min_support) {
				TreeSet<SetItem> newSet = new TreeSet<SetItem>(comp);
				newSet.add(item.getKey());
				prevItemSets.add(newSet);
				prevItemSetsMap.put(newSet, item.getValue());
				allValidItemSets.put(newSet, item.getValue());
			}
		}
		
	}
	
	public boolean isValid(TreeSet<SetItem> set) {
		for(SetItem item : set) {
			TreeSet<SetItem> subSet = (TreeSet<SetItem>) set.clone();
			subSet.remove(item);
			if(!prevItemSetsMap.containsKey(subSet)) return false;
		}
		return true;
	}
	
	public void addRules(TreeSet<SetItem> set, int[] rows) {
		for(SetItem item : set) {
			TreeSet<SetItem> subSet = (TreeSet<SetItem>) set.clone();
			subSet.remove(item);
			int[] suppLeft = allValidItemSets.get(subSet);
			double conf = (double) getNumBitsSet(rows) / (double) getNumBitsSet(suppLeft);
			Association rule = new Association(subSet, item, conf);
			if(rule.confidence >= min_confidence) associationRules.add(rule);
		}
	}
	
	public List<TreeSet<SetItem>> generateCandidateItemSets() {
		List<TreeSet<SetItem>> candidates = new ArrayList<TreeSet<SetItem>>();
		HashMap<TreeSet<SetItem>, int[]> newMap = new HashMap<TreeSet<SetItem>, int[]>();
		int count = 0;
		for(int i = 0; i<prevItemSets.size(); i++) {

			for(int j = i+1; j<prevItemSets.size(); j++) {
				TreeSet<SetItem> curSet = new TreeSet<SetItem>(prevItemSets.get(i));
				int size = curSet.size();
				
				TreeSet<SetItem> joinSet = prevItemSets.get(j);
				SetItem joinItem = joinSet.last();
				
				boolean flag = false;
				for(SetItem item : curSet) {
					if(item.rowIndex == joinItem.rowIndex) {
						flag = true;
						break;
					}
				}
				
				if(flag) continue;
				
				curSet.add(joinItem);
				if(size == curSet.size()) continue;
				
				if(isValid(curSet)) {
					count++;
					int[] curRows = Arrays.copyOf(prevItemSetsMap.get(prevItemSets.get(i)), prevItemSetsMap.get(prevItemSets.get(i)).length); 
					int[] joinRows = Arrays.copyOf(prevItemSetsMap.get(prevItemSets.get(j)), prevItemSetsMap.get(prevItemSets.get(j)).length);
					
					curRows = arrayBitIntersection(curRows, joinRows);
					
					if(((double) getNumBitsSet(curRows) / (double) record_count) >= min_support) {
						candidates.add(curSet);
						addRules(curSet, curRows);
						newMap.put(curSet, curRows);
						allValidItemSets.put(curSet, curRows);
					}
				}
				
				
			}
		}
		prevItemSetsMap = newMap;
		prevItemSets = candidates;
		
		return candidates;
	}
	
	
	public void algorithm(){
	
		generateOneItemSets();
		
		if(prevItemSets.size() == 0) {
			System.out.println("There are no sets found with support above the threshold of "+min_support);
			return;
		}
		
		int i = 2;
		List<TreeSet<SetItem>> candidateItemSets = generateCandidateItemSets();
		while(candidateItemSets.size() > 0) {
			candidateItemSets = generateCandidateItemSets();
			i++;
		}
			
		Comparator<Map.Entry<TreeSet<SetItem>, int[]>> comp = new SetCountComparator();
		Comparator<Association> confComp = new AssociationComparator();
		
		PriorityQueue<Map.Entry<TreeSet<SetItem>, int[]>> pQueue = new PriorityQueue<Map.Entry<TreeSet<SetItem>, int[]>>(allValidItemSets.size(), comp);
		PriorityQueue<Association> confQueue = new PriorityQueue<Association>(associationRules.size(), confComp);
		
		for(Map.Entry<TreeSet<SetItem>, int[]> entry : allValidItemSets.entrySet()) {
			pQueue.add(entry);
		}
		
		for(Association rule : associationRules) {
			confQueue.add(rule);
		}
		
		PrintWriter out;
		try {
			File output = new File("output.txt");
			out = new PrintWriter(output);
		}catch(IOException e) {
			e.printStackTrace();
			return;
		}
		
		out.println("==Frequent itemsets (min_sup="+(min_support * 100)+"%)");
		Map.Entry<TreeSet<SetItem>, int[]> entry;
		
		while((entry = pQueue.poll()) != null) {
			out.print("[");
			int k = 0;
			for(SetItem item : entry.getKey()) {
				switch(item.rowIndex){
				case 1: out.print("Hour: "+item.value);
						break;
				case 2: out.print("Road: "+item.value);
						break;
				case 3: out.print("CrossStreet: "+item.value);
						break;
				case 5: out.print("Zip: "+item.value);
						break;
				case 6: out.print("Injuries: "+item.value);
						break;
				case 7: out.print("Persons Killed: "+item.value);
						break;
				case 8: out.print("Contrib Factor: "+item.value);
						break;
				case 9: out.print("Vehicle: "+item.value);
						break;			
				default: out.print(item.value);	
				}
				
				k++;
				if(k != entry.getKey().size()) {
					out.print(",");
				}	
			}
			out.println("], "+((double)getNumBitsSet(entry.getValue()) / (double) record_count * 100)+"%");
			out.flush();
		}
		
		out.println("\n");
		out.println("==High-confidence association rules (min_conf="+(min_confidence * 100)+"%)");
		Association rule;
		while((rule = confQueue.poll()) != null) {
			out.print("[");
			int k = 0;
			for(SetItem item : rule.left) {
				switch(item.rowIndex){
				case 1: out.print("Hour: "+item.value);
						break;
				case 2: out.print("Road: "+item.value);
						break;
				case 3: out.print("CrossStreet: "+item.value);
						break;
				case 5: out.print("Zip: "+item.value);
						break;
				case 6: out.print("Injuries: "+item.value);
						break;
				case 7: out.print("Killed: "+item.value);
						break;
				case 8: out.print("Contrib Factor: "+item.value);
						break;
				case 9: out.print("Vehicle: "+item.value);
						break;			
				default: out.print(item.value);	
				}
				
				k++;
				if(k != rule.left.size()) {
					out.print(",");
				}	
			}
			out.print("] => [");
			
			switch(rule.right.rowIndex){
				case 1: out.print("Hour: "+rule.right.value);
						break;
				case 2: out.print("Road: "+rule.right.value);
						break;
				case 3: out.print("CrossStreet: "+rule.right.value);
						break;
				case 5: out.print("Zip: "+rule.right.value);
						break;
				case 6: out.print("Injuries: "+rule.right.value);
						break;
				case 7: out.print("Killed: "+rule.right.value);
						break;
				case 8: out.print("Contrib Factor: "+rule.right.value);
						break;
				case 9: out.print("Vehicle: "+rule.right.value);
						break;			
				default: out.print(rule.right.value);	
			}
			
			TreeSet<SetItem> temp = rule.left;
			temp.add(rule.right);
			
			double support = (double) getNumBitsSet(allValidItemSets.get(temp)) / (double)record_count;
			out.println("] (Conf: "+(rule.confidence * 100)+"%, Supp: "+(support * 100) + "%)");
			out.flush();
			
			
		}
		
		
		
	
		/*
	
		int k = 1;
		
		while(!stop){
			
			//init L1 to get the List of one item set
			if(k == 1){
				
				//loop through all possible values in data
				for(String[] array : allrecords){
					for(int i = 0; i < array.length; i++){
						//check not null
						if(array[i] != null){
							//using hashCode as the key
							int key = array[i].hashCode();
							
							if(largeSetList.containsKey(key)){//key not exists
								//increase the count of itemSet to calculate support
								int count = support_count.get(key);
								support_count.put(key, count+1);
							}
							else{
								//add new set to the list
								Set<String> set = new TreeSet<String>();
								set.add(array[i]);
								largeSetList.put(set.hashCode(), set);
								support_count.put(set.hashCode(), 1);
							}
						}
					}
				}
				
				System.out.println("check1 = " + largeSetList.size());
				//check if the candidate is higher than minimum support
				List<Integer> remove = new ArrayList<Integer>();
				for(Map.Entry<Integer, Set<String>> entry : largeSetList.entrySet()){
					double support = support_count.get(entry.getKey()) / record_count;
					//System.out.println("support = " + support);
					if(support < min_support){
						remove.add(entry.getKey());
					}
				}
				//remove candidate if below min support
				for(Integer in : remove){
					largeSetList.remove(in);
				}
				
				
				if(largeSetList.isEmpty())
					stop = true;
				
				support_count.clear();
				
				System.out.println("here = " + largeSetList.size());
			}
			// k>1
			else{
				System.out.println("there = " + largeSetList.size());
				
				//loop through all largeSet and add an item to the set
				for(Map.Entry<Integer, Set<String>> entry : largeSetList.entrySet()){
					
					//loop through all possible data
					for(String[] array : allrecords){
						for(int i = 0; i < array.length; i++){
							//check not null
							if(array[i] != null){
								//check does not already exist 
								if(!entry.getValue().contains(array[i])){
									int key = entry.getKey() + array[i].hashCode();
									if(candidateSetList.containsKey(key)){
										//update count of itemSet to calculate support
										int count = support_count.get(key);
										support_count.put(key, count+1);
									}
									else{
										//add an item to the largeSet to create candidateSet
										entry.getValue().add(array[i]);
										candidateSetList.put(entry.getValue().hashCode(), entry.getValue());
										support_count.put(entry.getValue().hashCode(), 1);
									}
								}
							}
						}
					}
				}

				System.out.println("check2 = " + candidateSetList.size());
				
				//check if the candidate is higher than minimum support
				List<Integer> remove = new ArrayList<Integer>();
				for(Map.Entry<Integer, Set<String>> entry : candidateSetList.entrySet()){
					double support = support_count.get(entry.getKey()) / record_count;
					//System.out.println("support = " + support);
					if(support < min_support){
						remove.add(entry.getKey());
					}
				}
				//remove candidate if below min support
				for(Integer in : remove){
					candidateSetList.remove(in);
				}
				
				//stop if candiateSet is empty cannot get larger sets
				if(candidateSetList.isEmpty())
					stop = true;
				
				
				//update largeSet and re-initialize
				largeSetList.clear();
				largeSetList.putAll(candidateSetList);
				candidateSetList.clear();
				support_count.clear();
			}
			
			k++;
		}*/
	}
	

}
