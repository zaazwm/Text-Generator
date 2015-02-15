import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


public class ApplicationControl {
	public static Random rnd = new Random();
	
	public static void runCML(String file) throws IOException {
		Reader r = new Reader(file);
		//Reader06 r = new Reader06(file);
		
		Algorithm algo = new Algorithm();
		while(r.hasNext()) {
			Sentence s = r.readNext();
			for(Word w : s.getWdList()) {
				algo.addIndex(w);
				algo.addWord(w);
			}
			for(Word w : s.getWdList()) {
				if(w.getHead()==-1)
					continue;
				String headform = s.getWdList().get(w.getHead()).getForm();
				algo.addFather(w, headform, w.getID()-w.getHead());
				algo.addChild(s.getWdList().get(w.getHead()), w.getForm(), w.getID()-w.getHead());
			}
		}
		
		boolean mark=true;
		BufferedReader buf=new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		String str;
		while(mark) {
			System.out.println("Type a word:");
			str=buf.readLine();
			if(str.equals("exit"))
				mark=false;
			else {
				//System.out.println("readed "+str);
				//System.out.println("if contains? "+algo.getIndexMap().containsKey(str));
				Sentence out = algo.generateTree(str);
				for(Word w : out.getWdList()) {
					if(w.getForm().equals("ROOT"))
						continue;
					System.out.print(w.getForm());
					if(r instanceof Reader06)
						System.out.print(" ");
				}
				System.out.print("\n");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		if(args.length<1)
			return;
		String fileread = args[0];
		runCML(fileread);
	}

}
