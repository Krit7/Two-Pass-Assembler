import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Assembler {
	int input_line_no;
	int lc;
	static List<Row> Mot;
	static List<String> Pot;
	static List<Symbol_Row> Sym;
	static List<Literal_Row> Lit;
	static PrintWriter pass2;
	static PrintWriter pass1;

	public Assembler() {
		input_line_no = 0;
		lc = 1;
		imperative_op();
		assembly_directives_op();
		Sym = new LinkedList<>();
		Lit = new LinkedList<>();
	}

	public void imperative_op() {
		Mot = new ArrayList<>();
		Mot.add(new Row("CLA", "0000"));
		Mot.add(new Row("LAC", "0001"));
		Mot.add(new Row("SAC", "0010"));
		Mot.add(new Row("ADD", "0011"));
		Mot.add(new Row("SUB", "0100"));
		Mot.add(new Row("BRZ", "0101"));
		Mot.add(new Row("BRN", "0110"));
		Mot.add(new Row("BRP", "0111"));
		Mot.add(new Row("INP", "1000"));
		Mot.add(new Row("DSP", "1001"));
		Mot.add(new Row("MUL", "1010"));
		Mot.add(new Row("DIV", "1011"));
		Mot.add(new Row("STP", "1100"));
	}

	public void assembly_directives_op() {
		Pot = new ArrayList<>();
		Pot.add("START");
		Pot.add("DS");
		Pot.add("DW");
		Pot.add("DC");
		Pot.add("LTORG");
		Pot.add("END");
	}

	public void pass_one() throws Exception {
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
		pass1 = new PrintWriter(new FileWriter("pass1.txt"), true);
		String line;
		int check_end_statement = -1;
		while ((line = input.readLine()) != null) {
			input_line_no++;
			if (!comment(line)) {
				String word[] = line.split("	");
				if (PotCheck(word[1])) {
					PotSearch(word);
					if (word[1].equals("END")) {
						check_end_statement = 1;
						if (word.length > 2 && !word[2].isEmpty() && !word[2].startsWith(";")) {
							System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
						}
						break;
					}
					lc++;
				} else if (MotCheck(word[1])) {
					line = lc + "	";
					for (int i = 0; i < word.length - 1; i++) {
						line = line + word[i] + "	";
					}
					if (!word[word.length - 1].startsWith(";")) {
						line = line + word[word.length - 1];
					}

					System.out.println(line);

					int check = SearchLabelInSymTable(word);
					if (check == 1) {
						System.out.println(
								"Error in in input line no. " + input_line_no + ", " + word[0] + " already used");
					} else if (MotSearch(word, check) != -1) {

						pass1.println(line);
					}
					lc++;
				} else {
					System.out.println("Error in in input line no. " + input_line_no + ", " + word[1] + " isn't valid");
				}

			}
		}
		input.close();
		if (check_end_statement == -1) {
			System.out.println("Error: No END statement");
			System.out.println("Still Printing the Machine code");
		}
		System.out.println();
		System.out.println("----------Symbol Table----------");
		System.out.println();
		System.out.println("No." + "	" + "Symbol" + "	" + "Value" + "	" + "Address");
		for (int i = 0; i < Sym.size(); i++) {
			System.out.println(
					Sym.get(i).Sym_no + "	" + Sym.get(i).name + "	" + Sym.get(i).value + "	" + Sym.get(i).Address);
		}
		System.out.println();
		System.out.println("----------Literal Table----------");
		System.out.println();
		System.out.println("No." + "	" + "Literal" + "	" + "Value" + "	" + "Address");
		for (int i = 0; i < Lit.size(); i++) {
			System.out.println(
					Lit.get(i).Lit_no + "	" + Lit.get(i).name + "	" + Lit.get(i).value + "	" + Lit.get(i).Address);
		}
	}

	public void pass_two() throws Exception {
		pass2 = new PrintWriter(new FileWriter("pass2.txt"), true);
		System.out.println("IC" + "	" + "	" + "OP" + "	" + "Address");
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("pass1.txt")));
		String line;

		while ((line = input.readLine()) != null) {
			String word[] = line.split("	");
			word[0] = Binaryconverter(Integer.parseInt(word[0]));
			if (!word[2].equals("STP") && !word[2].equals("CLA")) {
				if (word[3].contains("=")) {
					word[3] = Lit_Add(word[3]);
				} else {
					word[3] = Sym_Add(word[3]);
				}
			}
			word[2] = opcode(word[2]);
			String out = (word[0] + "	" + word[2] + "	");
			if (word.length > 3) {
				out += word[3];
			}
			pass2.println(out);
		}
		input.close();
	}

	public void machine_code() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("pass2.txt")));
		String line;
		while ((line = input.readLine()) != null) {
			System.out.println(line);
		}
		input.close();
	}

	public boolean PotCheck(String s) {
		for (int i = 0; i < Pot.size(); i++) {
			if (Pot.get(i).equals(s)) {
				return true;
			}
		}
		return false;
	}

	public int PotSearch(String[] word) {
		// check if operands no of operands are appropriate
		if (word[1].equals("START")) {
			if (word.length == 3) {
				lc = Integer.parseInt(word[2]) - 1;
			} else if (word.length == 2) {
				lc = 0;
			} else if (word.length > 3 && !word[3].isEmpty() && !word[3].startsWith(";")) {
				System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
				return -1;
			}
		} else if (word[1].equals("END") || word[1].equals("LTORG")) {
			int flag = 0;
			for (int i = 0; i < Lit.size(); i++) {
				if (Lit.get(i).Address == -1) {
					Lit.get(i).Address = lc;
					lc++;
					flag = 1;
				}
			}
			if (flag == 1)
				lc--;// subtracting the extra one
		} else if (word[1].equals("DC") || word[1].equals("DW") || word[1].equals("DS")) {
			if (word[2].contains(",")) {
				System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
				return -1;
			}
			if (word.length > 3 && !word[3].isEmpty() && !word[3].startsWith(";")) {
				System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
				return -1;
			}
			if (word.length < 3) {
				System.out.println("Error in in input line no. " + input_line_no + ", not enough operands");
				return -1;
			}
			int i = SearchSymTable(word[0]);
			if (i >= 0 && Sym.get(i).Address == -1) {
				Sym.get(i).UpdateAddress(lc);
				Sym.get(i).value = word[2];
			} else if (i >= 0 && Sym.get(i).Address != -1) {
				// error
				System.out.println("Error in in input line no. " + input_line_no + ", " + word[0] + " already used");
			} else {
				addsymbol(word);
			}
		}
		return 0;
	}

	public void addsymbol(String[] word) {
		Sym.add(new Symbol_Row(word[0], lc, word[2]));
	}

	public void addliteral(String[] word) {
		Lit.add(new Literal_Row(word[0], lc, word[2]));
	}

	public boolean MotCheck(String s) {
		for (int i = 0; i < Mot.size(); i++) {
			if (Mot.get(i).op_name.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public int MotSearch(String word[], int check) {
		if (word[1].equalsIgnoreCase("CLA") || word[1].equalsIgnoreCase("STP")) {
			if (word.length > 2 && !word[2].isEmpty() && !word[2].startsWith(";")) {
				System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
				return -1;
			}

		} else {
			if (word[2].contains(",")) {
				System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
				return -1;
			}
			if (word[1].equalsIgnoreCase("LAC") || word[1].equalsIgnoreCase("ADD") || word[1].equalsIgnoreCase("SUB")
					|| word[1].equalsIgnoreCase("MUL") || word[1].equalsIgnoreCase("DIV")
					|| word[1].equalsIgnoreCase("DSP")) {
				if (word.length > 3 && !word[3].isEmpty() && !word[3].startsWith(";")) {
					System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
					return -1;
				}
				if (word.length < 3) {
					System.out.println("Error in in input line no. " + input_line_no + ", not enough operands");
					return -1;
				}
				if (word[2].contains("=")) {
					int i = SearchLitTable(word[2]);
					if (i == -1) {
						Lit.add(new Literal_Row(word[2], word[2].substring(2, word[2].indexOf("'", 2))));
					}
				} else {
					int i = SearchSymTable(word[2]);
					if (i == -1) {
						Sym.add(new Symbol_Row(word[2]));
					}
				}

			} else if (word[1].equalsIgnoreCase("BRZ") || word[1].equalsIgnoreCase("BRN")
					|| word[1].equalsIgnoreCase("BRP") || word[1].equalsIgnoreCase("SAC")
					|| word[1].equalsIgnoreCase("INP")) {
				if (word.length > 3 && !word[3].isEmpty() && !word[3].startsWith(";")) {
					System.out.println("Error in in input line no. " + input_line_no + ", too many operands");
					return -1;
				}
				if (word.length < 3) {
					System.out.println("Error in in input line no. " + input_line_no + ", not enough operands");
					return -1;
				}
				int i = SearchSymTable(word[2]);
				if (i == -1) {
					Sym.add(new Symbol_Row(word[2]));
				}
				// check at last whether word[2] has been provided with address or not-done

			}
		}
		return 0;
	}

	public int SearchLabelInSymTable(String word[]) {
		if (word[0].equals("")) {
			return 0;// no label
		} else {
			int j = SearchSymTable(word[0]);
			if (j != -1) {
				if (Sym.get(j).Address != -1) {
					return 1;// label present with address
				} else {
					Sym.get(j).UpdateAddress(lc);
					return 2;// label present without address
				}
			} else {
				Sym.add(new Symbol_Row(word[0], lc));
				return 3;// label made with address
			}
		}
	}

	public int SearchSymTable(String s) {
		for (int i = 0; i < Sym.size(); i++) {
			if (Sym.get(i).name.equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public int SearchLitTable(String s) {
		for (int i = 0; i < Lit.size(); i++) {
			if (Lit.get(i).name.equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) throws Exception {
		Assembler A = new Assembler();
		System.out.println("------------- Input --------------");
		System.out.println();
		A.pass_one();
		System.out.println();
		A.error();
		System.out.println();
		System.out.println("-------------Machine code --------");
		System.out.println();
		A.pass_two();
		A.machine_code();

	}

	public boolean comment(String line) {
		if (line.isEmpty())
			return true;
		String word[] = line.split("	");
		for (int i = 0; i < word.length; i++) {
			if (!word[i].startsWith(";") && !word[i].isEmpty()) {
				return false;
			}
			if (word[i].startsWith(";")) {
				return true;
			}
		}
		return false;

	}

	public String opcode(String name) {
		for (int i = 0; i < Mot.size(); i++) {
			if (Mot.get(i).op_name.equals(name)) {
				return Mot.get(i).bin_opcode;
			}
		}
		return null;
	}

	public String Sym_Add(String name) {
		for (int i = 0; i < Sym.size(); i++) {
			if (Sym.get(i).name.equals(name)) {
				int add = Sym.get(i).Address;
				return Binaryconverter(add);
			}
		}
		return null;
	}

	public String Lit_Add(String name) {
		for (int i = 0; i < Lit.size(); i++) {
			if (Lit.get(i).name.equals(name)) {
				int add = Lit.get(i).Address;
				return Binaryconverter(add);
			}
		}
		return null;
	}

	static String Binaryconverter(int n) {
		String ans = "";
		if (n < 0) {
			return ans;
		}
		for (; n > 0;) {
			ans = n % 2 + ans;
			n = n / 2;
		}
		for (int i = ans.length(); i < 8; i++) {
			ans = "0" + ans;
		}
		return ans;
	}

	public void error() {
		for (int i = 0; i < Sym.size(); i++) {
			if (Sym.get(i).Address == -1) {
				System.out.println("Error: " + Sym.get(i).name + " hasn't been provided with address");
			}
		}
	}

}
