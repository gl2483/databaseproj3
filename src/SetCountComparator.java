import java.util.*;

public class SetCountComparator implements Comparator<Map.Entry<TreeSet<SetItem>, int[]>> {

	public int compare(Map.Entry<TreeSet<SetItem>, int[]> item1, Map.Entry<TreeSet<SetItem>, int[]> item2) {
		if(APriori.getNumBitsSet(item1.getValue()) > APriori.getNumBitsSet(item2.getValue())) {
			return -1;
		}else if(APriori.getNumBitsSet(item1.getValue()) < APriori.getNumBitsSet(item2.getValue())) {
			return 1;
		}else {
			return 0;
		}
	}

}
