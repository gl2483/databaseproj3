import java.util.*;

public class AssociationComparator implements Comparator<Association> {

	public int compare(Association item1, Association item2) {
		if(item1.confidence > item2.confidence) {
			return -1;
		}else if(item1.confidence < item2.confidence) {
			return 1;
		}else {
			return 0;
		}
	}

}
