import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class APriori {
	
	//the list of large set
	HashMap<Integer,Set<String>> largeSetList = new HashMap<Integer,Set<String>>();
	//the list of candidates
	HashMap<Integer,Set<String>> candidateSetList = new HashMap<Integer,Set<String>>();
	//to keep the count of a candidate set
	HashMap<Integer, Integer> support_count = new HashMap<Integer, Integer>();
	
	
	
	List<TreeSet<SetItem>> prevItemSets;  
	HashMap<TreeSet<SetItem>, Integer> prevItemSetsMap;
	List<TreeSet<SetItem>> allValidItemSets;
	
	List<String[]> allrecords = new ArrayList<String[]>();
	int record_count = 0;
	double min_support, min_confidence;
	boolean stop = false;
	
	public APriori(double support, double confidence) {
		this.min_support = support;
		this.min_confidence = confidence;
		prevItemSets = new ArrayList<TreeSet<SetItem>>();
		prevItemSetsMap = new HashMap<TreeSet<SetItem>, Integer>();
		allValidItemSets = new ArrayList<TreeSet<SetItem>>();
	}
	
	
	public void getData(){
		File f = new File("data/final_data.csv");
		try {
			BufferedReader buf = new BufferedReader(new FileReader(f));

			String line;
			while( (line = buf.readLine()) != null){
				String[] array = line.split(",");
				//simply an array
				allrecords.add(array);
			}
			record_count = allrecords.size();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<TreeSet<SetItem>> checkCandidateThresh(List<TreeSet<SetItem>> list) {
		List<TreeSet<SetItem>> candidates = new ArrayList<TreeSet<SetItem>>();
		Comparator<SetItem> comp = new SetItemComparator();
		TreeMap<SetItem, Integer> map = new TreeMap<SetItem, Integer>(comp);
		
		System.out.println("Number of sets to check support for: "+list.size());
		int i = 0;
		for(TreeSet<SetItem> set : list) {
			int count = 0;
			
			//System.out.println("i: "+i);
			//i++;
			for(String[] row : allrecords) {
				boolean hasAll = true;
				for(SetItem item : set) {
					if(!item.value.equals(row[item.rowIndex])) {
						hasAll = false;
						break;
					}
				}
				
				if(hasAll) count++;
			}
			
			if(((double) count / (double) record_count) >= min_support) {
				candidates.add(set);
			}
		}
		
		return candidates;
	}
	
	
	public void generateOneItemSets() {
		Comparator<SetItem> comp = new SetItemComparator();
		TreeMap<SetItem, Integer> map = new TreeMap<SetItem, Integer>(comp);
		
		for(String[] row : allrecords) {
			for(int i=0; i<row.length; i++) {
				if(row[i] == null || row[i].equals("") || row[i].equals("null")) continue;
				SetItem newItem = new SetItem(i, row[i]);
				
				if(map.containsKey(newItem)) {
					int count = map.get(newItem);
					map.put(newItem, ++count);
				}else {
					map.put(newItem, 1);
				}
			}
		}
		
		for(Map.Entry<SetItem, Integer> item : map.entrySet()) {
			double sup = (double) item.getValue() / (double) record_count;
			if(sup >= min_support) {
				//System.out.println("Entry: (row: "+item.getKey().rowIndex+", "+ item.getKey().value +"), count: "+item.getValue());
				TreeSet<SetItem> newSet = new TreeSet<SetItem>(comp);
				newSet.add(item.getKey());
				prevItemSets.add(newSet);
				prevItemSetsMap.put(newSet, 0);
			}
		}
		
		System.out.println("num one sets: "+prevItemSets.size());
		
	}
	
	public boolean isValid(TreeSet<SetItem> set) {
		for(SetItem item : set) {
			TreeSet<SetItem> subSet = (TreeSet<SetItem>) set.clone();
			subSet.remove(item);
			if(!prevItemSetsMap.containsKey(subSet)) return false;
		}
		return true;
	}
	
	public List<TreeSet<SetItem>> generateCandidateItemSets() {
		List<TreeSet<SetItem>> candidates = new ArrayList<TreeSet<SetItem>>();
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
					candidates.add(curSet);
				}
			}
		}
		
		return checkCandidateThresh(candidates);
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
		
			System.out.println("size 2: There are "+candidateItemSets.size()+" sets");
			prevItemSets = candidateItemSets;
			prevItemSetsMap = new HashMap<TreeSet<SetItem>, Integer>();
			for(TreeSet<SetItem> set : candidateItemSets) {
				//System.out.println("new set");
				//for(SetItem item : set) System.out.println("item: "+item.rowIndex+", "+item.value);
				prevItemSetsMap.put(set, 0);
				allValidItemSets.add(set);
			}
			
		
			candidateItemSets = generateCandidateItemSets();
		}
		
		System.out.println("num of total sets: "+allValidItemSets.size());
		
	
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
