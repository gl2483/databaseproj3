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
	public int personInjured;
	public int personKilled;
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
		while( (line = buf.readLine() )!= null ){
			String[] array = line.split(";");
			//System.out.println(line);
			Volume newV = new Volume();
			for(int i = 0; i < array.length; i++){
				switch(i){
				case 0: newV.Date = array[i].trim();
						break;
				case 1: newV.Hour = Integer.parseInt(array[i].split(":")[0].trim());
						break;
				case 2: newV.Borough = array[i].toLowerCase().trim();
						break;
				case 3: newV.zip = array[i].trim();
						break;
				case 4: newV.Road = expandStreet(array[i].toLowerCase().trim());
						break;
				case 5: newV.CrossStreet = expandStreet(array[i].toLowerCase().trim());
						break;
				case 6: newV.personInjured = Integer.parseInt(array[i].trim());
						break;
				case 7: newV.perdestrianKilled = Integer.parseInt(array[i].trim());
						break;
				case 8: newV.pedestrianInjured = Integer.parseInt(array[i].trim());
						break;
				case 9: newV.perdestrianKilled = Integer.parseInt(array[i].trim());
						break;
				case 10: newV.cyclistInjured = Integer.parseInt(array[i].trim());
						break;
				case 11: newV.cyclistKilled = Integer.parseInt(array[i].trim());
						break;
				case 12: newV.motoristInjured = Integer.parseInt(array[i].trim());
						break;
				case 13: newV.motoristKilled = Integer.parseInt(array[i].trim());
						break;
				case 14: newV.CFV1 = array[i].toLowerCase().trim();
						if(newV.CFV1.equals("unknown") || newV.CFV1.equals("unspecified")) newV.CFV1 = null;
						break;
				case 15: newV.CFV2 = array[i].toLowerCase().trim();
						if(newV.CFV2.equals("unknown") || newV.CFV2.equals("unspecified")) newV.CFV2 = null;
						break;
				case 16: newV.CFV3 = array[i].toLowerCase().trim();
						if(newV.CFV3.equals("unknown") || newV.CFV3.equals("unspecified")) newV.CFV3 = null;
						break;
				case 17: newV.CFV4 = array[i].toLowerCase().trim();
						if(newV.CFV4.equals("unknown") || newV.CFV4.equals("unspecified")) newV.CFV4 = null;
						break;
				case 18: newV.CFV5 = array[i].toLowerCase().trim();
						if(newV.CFV5.equals("unknown") || newV.CFV5.equals("unspecified")) newV.CFV5 = null;
						break;
				case 19: newV.VTC1 = array[i].toLowerCase().trim();
						if(newV.VTC1.equals("unknown") || newV.VTC1.equals("unspecified")) newV.VTC1 = null;
						break;
				case 20: newV.VTC2 = array[i].toLowerCase().trim();
						if(newV.VTC2.equals("unknown") || newV.VTC2.equals("unspecified")) newV.VTC2 = null;
						break;
				case 21: newV.VTC3 = array[i].toLowerCase().trim();
						if(newV.VTC3.equals("unknown") || newV.VTC3.equals("unspecified")) newV.VTC3 = null;
						break;
				case 22: newV.VTC4 = array[i].toLowerCase().trim();
						if(newV.VTC4.equals("unknown") || newV.VTC4.equals("unspecified")) newV.VTC4 = null;
						break;
				case 23: newV.VTC5 = array[i].toLowerCase().trim();
						if(newV.VTC5.equals("unknown") || newV.VTC5.equals("unspecified")) newV.VTC5 = null;
						break;
				}
				
			}
			
			String key = newV.Date+newV.Hour+newV.Road+newV.CrossStreet;
			if(map.containsKey(key)){
				List<Volume> lv = map.get(key);
				for(Volume v : lv){
					v.merge(newV);
				}
			}
			else{
				List<Volume> lv = new ArrayList<Volume>();
				lv.add(newV);
				map.put(key, lv);
			}
			
		}
		
		File newFile = new File("data/final_data.csv");
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
		
		
		for(Map.Entry<String, List<Volume>> entry : map.entrySet()){
			for(Volume v : entry.getValue()){
				String l = "";
				l = v.Date +","+ v.Hour +","+ v.Road +","+ v.CrossStreet +","+ v.direction +","+
				    v.Borough  +","+v.volume +","+ v.zip +","+ v.personInjured +","+ v.personKilled +","+
					v.pedestrianInjured +","+ v.perdestrianKilled +","+ v.cyclistInjured +","+ v.cyclistKilled +","+
				    v.motoristInjured +","+ v.motoristKilled +","+ v.CFV1 +","+ v.CFV2 +","+v.CFV3 +","+v.CFV4 +","+
					v.CFV5 +","+v.VTC1 +","+v.VTC2 +","+v.VTC3 +","+v.VTC4 +","+v.VTC5+ '\n';
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
