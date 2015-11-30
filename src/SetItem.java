
public class SetItem {
	public int rowIndex; //index in each row in csv file.
	public String value; //actual value in cell in csv file.

	public SetItem(int index, String val) {
		rowIndex = index;
		value = val;
	}
	
	public boolean equals(SetItem otherItem) {
		if(rowIndex == otherItem.rowIndex && value.equals(otherItem.value)) return true;
		return false;
	}
	
	public int hashCode() {
		return rowIndex + value.hashCode();
	}
}
