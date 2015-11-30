import java.util.*;
public class Association implements Comparable<Association>{

	public TreeSet<SetItem> left;
	public SetItem right;
	public double confidence;
	
	public Association(TreeSet<SetItem> left, SetItem right, double conf) {
		this.left = left;
		this.right = right;
		this.confidence = conf;
	}
	
	public boolean equals(Association otherItem) {
		if(left.equals(otherItem.left) && right.equals(otherItem.right) && confidence == confidence) return true;
		return false;
	}
	
	public int hashCode() {
		return left.hashCode() + right.hashCode() + (int)confidence;
	}
	
	public int compareTo(Association other) {
		Comparator<Association> comp = new AssociationComparator();
		return comp.compare(this, other);
	}
}
