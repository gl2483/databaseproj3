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
	
	List<String[]> allrecords = new ArrayList<String[]>();
	int record_count = 0;
	double min_support;
	boolean stop = false;
	
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void algorithm(){
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
		}
	}
	

}
