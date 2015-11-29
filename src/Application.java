
public class Application {
	public static void main(String[] args){
		APriori a = new APriori();
		a.min_support = 0.01;
		a.getData();
		a.algorithm();
		System.out.println("number of larger sets = " + a.largeSetList.size());
	}
}
