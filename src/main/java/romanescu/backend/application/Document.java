package romanescu.backend.application;

import java.util.Map;

public class Document {
	private final String id;
	private final String index;
	private final String type;
	private final Map<String, Object> source;

	public Document(String id, String index, String type, Map<String, Object> source) {
		this.id = id;
		this.index = index;
		this.type = type;
		this.source = source;
	}

	public String getId() {
		return id;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getSource() {
		return source;
	}

}
