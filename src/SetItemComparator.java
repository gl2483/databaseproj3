import java.util.*;

public class SetItemComparator implements Comparator<SetItem> {

	public int compare(SetItem item1, SetItem item2) {
		if(item1.rowIndex > item2.rowIndex) {
			return 1;
		}else if(item1.rowIndex < item2.rowIndex) {
			return -1;
		}else {
			return item1.value.compareTo(item2.value);
		}
	}

}
