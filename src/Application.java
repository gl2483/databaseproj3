
public class Application {
	public static void main(String[] args){
		if(args.length != 3){
			System.out.println("Usage: ./run.sh <File name> <min support> <min confidence>");
			return;
		}
		String filename = args[0];
		double support = Double.parseDouble(args[1]);
		double conf = Double.parseDouble(args[2]);
		
		if(support > 1 || support <= 0){
			System.out.println("Min support has to be in range 0 < min support <= 1");
			return;
		}
		if(conf > 1 || conf <= 0){
			System.out.println("Min confidence has to be in range 0 < min confidence <= 1");
			return;
		}

		APriori a = new APriori(filename, support, conf);
		a.getData();
		a.algorithm();
	}
}
