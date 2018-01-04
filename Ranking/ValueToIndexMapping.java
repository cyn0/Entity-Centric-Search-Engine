package Ranking;

import java.util.HashMap;
import java.util.Map;
public class ValueToIndexMapping implements java.io.Serializable {
	private static final long serialVersionUID = -2077767183898369580L;

	private int nextIndex = 0;
	private Map<String, Integer> valueMapping = new HashMap<String, Integer>();
	private Map<Integer, String> indexMapping = new HashMap<Integer, String>();

	public ValueToIndexMapping() {

	}

	public int getIndex(String value) {
		Integer index = valueMapping.get(value);
		if (index == null) {
			index = nextIndex;
			valueMapping.put(value, index);
			indexMapping.put(index, value);
			nextIndex++;
		}
		return index;
	}

	public int getSize() {
		return valueMapping.size();
	}

	public String getValue(int index) {
		return indexMapping.get(index);
	}
}
