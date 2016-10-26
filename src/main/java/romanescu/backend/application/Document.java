package romanescu.backend.application;

public class Document {
	private final String id;
	private final String index;
	private final String type;
	private final String source;

	public Document(String id, String index, String type, String source) {
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

	public String getSource() {
		return source;
	}

}
