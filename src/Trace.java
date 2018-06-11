import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

/**
 * @author racim
 *
 */
public class Trace {

	private final SimpleStringProperty type;

	private final SimpleStringProperty id;
	private final SimpleStringProperty returnT;
	private final SimpleStringProperty signature;
	private final SimpleStringProperty types;
	private final SimpleStringProperty values;
	private final SimpleStringProperty length;
	private final SimpleStringProperty depth;
	private final SimpleStringProperty objectHach;
	private final SimpleStringProperty value;

	public Trace(String type, String returnT, String signature, String types, String values, String length,
			String depth, String objectHach, String id, String value) {
		this.id = new SimpleStringProperty(id);
		this.type = new SimpleStringProperty(type);
		this.returnT = new SimpleStringProperty(returnT);
		this.signature = new SimpleStringProperty(signature);
		this.types = new SimpleStringProperty(types);
		this.values = new SimpleStringProperty(values);
		this.length = new SimpleStringProperty(length);
		this.depth = new SimpleStringProperty(depth);
		this.objectHach = new SimpleStringProperty(objectHach);
		this.value = new SimpleStringProperty(value);
		// TODO Auto-generated constructor stub

	}

	public String getType() {
		return type.get();
	}

	public String getId() {
		return id.get();
	}

	public String getReturnT() {
		return returnT.get();
	}

	public String getSignature() {
		return signature.get();
	}

	public String getTypes() {
		return types.get();
	}

	public String  getValues() {
		return values.get();
	}

	public String  getLength() {
		return length.get();
	}

	public String  getDepth() {
		return depth.get();
	}

	public String getObjectHach() {
		return objectHach.get();
	}

	public String getValue() {
		return value.get();
	}
	
	

}
