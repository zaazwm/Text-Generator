import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


public class ApplicationControl {
	public static Random rnd = new Random();
	
	public static final int TREESETSIZE = 100;
	
	public static void runCML(String file) throws IOException {
		Reader r;
		if(file.endsWith("conll06")) {
			r = new Reader06(file);
		}
		else
			r = new Reader(file);
		//Reader06 r = new Reader06(file);
		
		Algorithm algo = new Algorithm();
		while(r.hasNext()) {
			Sentence s;
			if(r instanceof Reader06)
				s = ((Reader06)r).readNext();
			else
				s = r.readNext();
			
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
				Sentence[] out = new Sentence[TREESETSIZE];
				for(int i=0;i<TREESETSIZE;i++)
					out[i]=algo.generateTree(str);
				int outindex = -1;
				int outscore = Integer.MIN_VALUE;
				for(int i=0;i<TREESETSIZE;i++) {
					int curscore=algo.getSemantic().rankSentence(out[i]);
					if(curscore>outscore) {
						outscore=curscore;
						outindex=i;
					}
				}
				for(Word w : out[outindex].getWdList()) {
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
