import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;


public class ApplicationControl {
	public static Random rnd = new Random();
	
	public static final int TREESETSIZE = 100;
	
	public static LinkedList<String> tagList = new LinkedList<String>();
	
	public static void runCML(String file) throws IOException {
		//file reader for conll09 files, also supporting conll06
		Reader r;
		if(file.endsWith("conll06")) {
			r = new Reader06(file);
		}
		else
			r = new Reader(file);
		//Reader06 r = new Reader06(file);
		int preprocessing=0;
		Algorithm algo = new Algorithm();
		int sentenceindex = 0;
		while(r.hasNext()) {
			//read sentence
			Sentence s;
			if(r instanceof Reader06)
				s = ((Reader06)r).readNext();
			else
				s = r.readNext();
			//add basic word information
			for(Word w : s.getWdList()) {
				algo.addIndex(w);
				algo.addWord(w);
				if(w.getHead()!=-1)
					algo.addPattern(w, s.getWdList().get(w.getHead()),sentenceindex);
			}
			//add tree information
			for(Word w : s.getWdList()) {
				if(w.getHead()==-1)
					continue;
				String headform = s.getWdList().get(w.getHead()).getForm();
				algo.addFather(w, headform, w.getRel(), w.getID()-w.getHead());
				algo.addChild(s.getWdList().get(w.getHead()), w.getForm(), s.getWdList().get(w.getHead()).getRel(), w.getID()-w.getHead());
				if(!tagList.contains(w.getRel()))
					tagList.add(w.getRel());
			}

			algo.saveSentence(s);
			sentenceindex++;
			preprocessing++;
			if(preprocessing%1000==0)
				System.out.println(preprocessing + " sentences readed");
		}
		
		algo.savePattern();
		//algo.postInitialization();
		
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
				//rand the generated trees
				for(int i=0;i<TREESETSIZE;i++) {
					int curscore=algo.getSemantic().rankSentence(out[i]);
					if(curscore>outscore) {
						outscore=curscore;
						outindex=i;
					}
				}
				if(!out[outindex].getQuery().equals(str))
					System.out.println(str+" not found, "+"use "+out[outindex].getQuery()+" instead");
				String output = new String("\n");
				System.out.println("index\tword\thead\ttag");
				for(Word w : out[outindex].getWdList()) {
					if(w.getForm().equals("ROOT"))
						continue;
					System.out.print(w.getID()+"\t"+w.getForm()+"\t"+w.getHead()+"\t"+w.getRel()+"\n");
					output+=w.getForm();
//					System.out.print(w.getForm());
					if(r instanceof Reader06) {
						output+=" ";
//						System.out.print(" ");
					}
				}
				System.out.print(output+"\n\n");
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
