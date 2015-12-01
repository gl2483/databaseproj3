import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Volume implements Serializable, Cloneable {
	public String Road;
	public String Date;
	public int Hour;
	public String CrossStreet;
	public String direction;
	public int volume;
	public String Borough;
	public String zip;
	public String personInjured;
	public String personKilled;
	public int pedestrianInjured;
	public int perdestrianKilled;
	public int cyclistInjured;
	public int cyclistKilled;
	public int motoristInjured;
	public int motoristKilled;
	public String CFV1;
	public String CFV2;
	public String CFV3;
	public String CFV4;
	public String CFV5;
	public String VTC1;
	public String VTC2;
	public String VTC3;
	public String VTC4;
	public String VTC5;
	
	public static void main(String[] args){
		try {
			parse();
		} catch (IOException | CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public static String expandStreet(String street) {
		String[] arr = street.split("\\s+");
		String name = arr[arr.length-1];
		String expanded = null;
		if(name.equals("ave")) {
			expanded = "avenue";
		}else if(name.equals("st")) {
			expanded = "street";
		}else if(name.equals("pl")) {
			expanded = "place";
		}else if(name.equals("rd")) {
			expanded = "road";
		}else if(name.equals("blvd")) {
			expanded = "boulevard";
		}else if(name.equals("apt")) {
			expanded = "apartment";
		}else if(name.equals("cswy")) {
			expanded = "causeway";
		}else if(name.equals("hwy")) {
			expanded = "highway";
		}
		else {
			return street;
		}
		
		String newStr = "";
		int i = 0;
		for(;i<arr.length-1;i++) {
			newStr += arr[i]+" ";
		}
		newStr += expanded;
		return newStr;
	}
	
	public static void parse() throws IOException, CloneNotSupportedException{
		File f = new File("data/Traffic_Volume_Counts__2012-2013_.csv");
		BufferedReader buf = new BufferedReader(new FileReader(f));
		String line;
		HashMap<String, List<Volume>> map = new HashMap<String, List<Volume>>();
		while((line = buf.readLine()) != null){
			//System.out.println(line);
			String[] arr = line.split(";");
			for(int k = 0; k < 2; k++){
				
				Volume v = new Volume();
				boolean badLine = false;
				for(int i = 0; i < 29; i++){
					switch(i){
					case 0: v.Road = expandStreet(arr[i].toLowerCase().trim());
							if(v.Road == null || v.Road.equals("")) badLine = true;
							break;
					case 1: if(k==0) v.CrossStreet = expandStreet(arr[i].toLowerCase().trim());
							break;
					case 2: if(k==1) v.CrossStreet = expandStreet(arr[i].toLowerCase().trim());
							break;
					case 3: v.direction = arr[i].toLowerCase().trim();
							break;
					case 4: v.Date = arr[i].trim();
							if(v.Date == null || v.Date.equals("")) badLine = true;
							break;
					default: Volume v1 = (Volume) v.clone();
							 v1.Hour = i-5;
							 double volume = Double.parseDouble(arr[i].trim());
							 if(volume < .1) {
							 	badLine = true;
							 	break;
							 }
							 double mod = volume % 50;
							 if(mod >= 25) {
							 	v1.volume = (int) (volume + (50 - mod));
							 } else {
							 	v1.volume = (int) (volume - mod);
							 }
							 
							 
							 String key = v1.Date+v1.Hour+v1.Road+v1.CrossStreet;
							 if(map.containsKey(key)){
								 List<Volume> lv = map.get(key);
								 lv.add(v1);
								 map.put(key, lv);
							 }
							 else{
								 List<Volume> lv = new ArrayList<Volume>();
								 lv.add(v1);
								 map.put(key, lv);
							 }
					}
				}
			}
		}
		
		File t = new File("data/NYPD_Motor_Vehicle_Collisions.csv");
		buf = new BufferedReader(new FileReader(t));
		HashMap<String, List<Volume>> newMap = new HashMap<String, List<Volume>>();
		while( (line = buf.readLine() )!= null ){
			String[] array = line.split(";");
			//System.out.println(line);
			Volume newV = new Volume();
			boolean badLine = false;
			for(int j = 0; j < 4; j++) {
				for(int i = 0; i < array.length; i++){
					switch(i){
					case 0: newV.Date = array[i].trim();
							break;
					case 1: newV.Hour = Integer.parseInt(array[i].split(":")[0].trim());
							break;
					case 2: newV.Borough = array[i].toLowerCase().trim();
							if(newV.Borough == null || newV.Borough.equals("") || newV.Borough.equals("null")) badLine = true;
							break;
					case 3: newV.zip = array[i].trim();
							if(newV.zip == null || newV.zip.equals("") || newV.zip.equals("null")) badLine = true;
							break;
					case 4: newV.Road = expandStreet(array[i].toLowerCase().trim());
							break;
					case 5: newV.CrossStreet = expandStreet(array[i].toLowerCase().trim());
							break;
					case 6: newV.personInjured = array[i].trim();
							if(newV.personInjured.equals("0")) newV.personInjured = null;
							break;
					case 7: newV.personKilled = array[i].trim();
							if(newV.personKilled.equals("0")) newV.personKilled = null;
							break;
					case 14: if(j < 2) {
								newV.CFV1 = array[i].toLowerCase().trim();
							}else {
								break;
							}
							if(newV.CFV1.equals("unknown") || newV.CFV1.equals("unspecified")) newV.CFV1 = null;
							break;
					case 15: if(j > 2) {
								newV.CFV1 = array[i].toLowerCase().trim();
							}else {
								break;
							}
							if(newV.CFV1.equals("unknown") || newV.CFV1.equals("unspecified")) newV.CFV1 = null;
							break;
					case 19: if(j == 0 || j==2) {
								newV.VTC1 = array[i].toLowerCase().trim();
							}else {
								break;
							}
							if(newV.VTC1.equals("unknown") || newV.VTC1.equals("unspecified")) newV.VTC1 = null;
							break;
					case 20: if(j==1 || j == 3) {
								newV.VTC1 = array[i].toLowerCase().trim();
							}else {
								break;
							}
							if(newV.VTC1.equals("unknown") || newV.VTC1.equals("unspecified")) newV.VTC1 = null;
							break;
					}
					
					if(badLine) break;
				}
				
				if(badLine) break;
				String key = newV.Date+newV.Hour+newV.Road+newV.CrossStreet;
				List<Volume> list = new ArrayList<Volume>();
				list.add(newV);
				newMap.put(key, list);
				
			}
		
			/*if(map.containsKey(key)){
				List<Volume> lv = map.get(key);
				for(Volume v : lv){
					v.merge(newV);
				}
				newMap.put(key, lv);
			}
			else{
				List<Volume> lv = new ArrayList<Volume>();
				lv.add(newV);
				newMap.put(key, lv);
			}*/
			
		}
		
		File newFile = new File("data/final_data.csv");
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		
		System.out.println("size: "+newMap.size());
		for(Map.Entry<String, List<Volume>> entry : newMap.entrySet()){
			for(Volume v : entry.getValue()){
				String l = "";
				l = v.Date +","+ v.Hour +","+ v.Road +","+ v.CrossStreet + ","+
				    v.Borough  +","+ v.zip +","+ v.personInjured +","+ v.personKilled +","+
					v.CFV1 + "," +v.VTC1 + '\n';
				writer.write(l);
			}
		}
		writer.flush();
	}
	
	
	void merge(Volume v){
		this.Borough = v.Borough;
		this.zip = v.zip;
		this.personInjured = v.personInjured;
		this.personKilled = v.personKilled;
		this.pedestrianInjured = v.pedestrianInjured;
		this.perdestrianKilled = v.perdestrianKilled;
		this.cyclistInjured = v.cyclistInjured;
		this.cyclistKilled = v.cyclistKilled;
		this.motoristInjured = v.motoristInjured;
		this.motoristKilled = v.motoristKilled;
		this.CFV1 = v.CFV1;
		this.CFV2 = v.CFV2;
		this.CFV3 = v.CFV3;
		this.CFV4 = v.CFV4;
		this.CFV5 = v.CFV5;
		this.VTC1 = v.VTC1;
		this.VTC2 = v.VTC2;
		this.VTC3 = v.VTC3;
		this.VTC4 = v.VTC4;
		this.VTC5 = v.VTC5;
	}

}
