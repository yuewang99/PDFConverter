import java.util.Scanner;

public class Converter {
	static String str = "";
	public static void PDFToTxt(Scanner sc) {
		while (sc.hasNextLine()) {
//			s = s+sc.nextLine()+" ";
			System.out.print(sc.nextLine()+" ");
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Paste from PDF: ");

//		while (sc.hasNextLine()) {
//			//System.out.print(sc.nextLine());
//			s = s+sc.nextLine();
//			break;
//		}
		PDFToTxt(sc);
//		System.out.println();


	}

}
