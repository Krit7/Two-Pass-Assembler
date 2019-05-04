
public class Literal_Row {
	static int counter = 0;
	int Lit_no;
	String name;
	int Address = -1;
	String value = "";

	public Literal_Row() {

	}

	public Literal_Row(String name, int Address, String value) {
		Lit_no = ++counter;
		this.name = name;
		this.Address = Address;
		this.value = value;
	}

	public Literal_Row(String name, String value) {
		Lit_no = ++counter;
		this.name = name;
		this.value = value;
	}

	public Literal_Row(String name) {
		Lit_no = ++counter;
		this.name = name;
	}

	public Literal_Row(String name, int Address) {
		Lit_no = ++counter;
		this.name = name;
		this.Address = Address;
	}

	public void UpdateAddress(int Address) {
		this.Address = Address;
	}
}
