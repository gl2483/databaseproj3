
public class Application {
	public static void main(String[] args){
		String filename = args[0];
		double support = Double.parseDouble(args[1]);
		double conf = Double.parseDouble(args[2]);

		final long start = System.currentTimeMillis();
		APriori a = new APriori(filename, support, conf);
		a.getData();
		a.algorithm();
		System.out.println("number of larger sets = " + a.largeSetList.size());
		final long end = System.currentTimeMillis();
		System.out.println("Total time: "+ (end - start));
	}
}
