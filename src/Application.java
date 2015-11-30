
public class Application {
	public static void main(String[] args){
		final long start = System.currentTimeMillis();
		APriori a = new APriori(0.05, 0.01);
		a.getData();
		a.algorithm();
		System.out.println("number of larger sets = " + a.largeSetList.size());
		final long end = System.currentTimeMillis();
		System.out.println("Total time: "+ (end - start));
	}
}
