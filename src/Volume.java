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
			System.out.println("ajsdlkjalsdk");
			parse();
		} catch (IOException | CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void parse() throws IOException, CloneNotSupportedException{
		File f = new File("/data/Traffic_Volume_Counts__2012-2013_.csv");
		BufferedReader buf = new BufferedReader(new FileReader(f));
		String line;
		HashMap<String, List<Volume>> map = new HashMap<String, List<Volume>>();
		while((line = buf.readLine()) != null){
			System.out.println(line);
			String[] arr = line.split(";");
			for(int k = 0; k < 2; k++){
				
				Volume v = new Volume();
				for(int i = 0; i < 29; i++){
					switch(i){
					case 0: v.Road = arr[i].toLowerCase();
							break;
					case 1: if(k==0) v.CrossStreet = arr[i].toLowerCase();
							break;
					case 2: if(k==1) v.CrossStreet = arr[i].toLowerCase();
							break;
					case 3: v.direction = arr[i].toLowerCase();
							break;
					case 4: v.Date = arr[i];
							break;
					default: Volume v1 = (Volume) v.clone();
							 v1.Hour = i-5;
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
		
		File t = new File("/data/NYPD_Motor_Vehicle_Collisions.csv");
		buf = new BufferedReader(new FileReader(t));
		while( (line = buf.readLine() )!= null ){
			String[] array = line.split(";");
			System.out.println(line);
			Volume newV = new Volume();
			for(int i = 0; i < array.length; i++){
				switch(i){
				case 0: newV.Date = array[i];
						break;
				case 1: newV.Hour = Integer.parseInt(array[i].split(":")[0]);
						break;
				case 2: newV.Borough = array[i].toLowerCase();
						break;
				case 3: newV.zip = array[i];
						break;
				case 4: newV.Road = array[i].toLowerCase();
						break;
				case 5: newV.CrossStreet = array[i].toLowerCase();
						break;
				case 6: newV.personInjured = Integer.parseInt(array[i]);
						break;
				case 7: newV.perdestrianKilled = Integer.parseInt(array[i]);
						break;
				case 8: newV.pedestrianInjured = Integer.parseInt(array[i]);
						break;
				case 9: newV.perdestrianKilled = Integer.parseInt(array[i]);
						break;
				case 10: newV.cyclistInjured = Integer.parseInt(array[i]);
						break;
				case 11: newV.cyclistKilled = Integer.parseInt(array[i]);
						break;
				case 12: newV.motoristInjured = Integer.parseInt(array[i]);
						break;
				case 13: newV.motoristKilled = Integer.parseInt(array[i]);
						break;
				case 14: newV.CFV1 = array[i].toLowerCase();
						break;
				case 15: newV.CFV2 = array[i].toLowerCase();
						break;
				case 16: newV.CFV3 = array[i].toLowerCase();
						break;
				case 17: newV.CFV4 = array[i].toLowerCase();
						break;
				case 18: newV.CFV5 = array[i].toLowerCase();
						break;
				case 19: newV.VTC1 = array[i].toLowerCase();
						break;
				case 20: newV.VTC2 = array[i].toLowerCase();
						break;
				case 21: newV.VTC3 = array[i].toLowerCase();
						break;
				case 22: newV.VTC4 = array[i].toLowerCase();
						break;
				case 23: newV.VTC5 = array[i].toLowerCase();
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
		
		File newFile = new File("/data/final_data.csv");
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
