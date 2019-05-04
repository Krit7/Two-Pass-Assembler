
public class Symbol_Row {
	static int counter = 0;
	int Sym_no;
	String name;
	int Address = -1;
	String value = "";

	public Symbol_Row() {

	}

	public Symbol_Row(String name, int Address, String value) {
		Sym_no = ++counter;
		this.name = name;
		this.Address = Address;
		this.value = value;
	}

	public Symbol_Row(String name) {
		Sym_no = ++counter;
		this.name = name;
	}

	public Symbol_Row(String name, int Address) {
		Sym_no = ++counter;
		this.name = name;
		this.Address = Address;
	}

	public void UpdateAddress(int Address) {
		this.Address = Address;
	}

}
