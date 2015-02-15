import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class Algorithm {
	private HashMap<Integer, Word> wordmap;
	private HashMap<String, Integer> indexmap;
	private int indexnum;
	private SemanticInterface sem;
	private Random rnd;
	private int retindex;
	
	private static final int SENTENCELENGTH = 10;
	
	public Algorithm() {
		wordmap= new HashMap<Integer, Word>();
		indexmap = new HashMap<String, Integer>();
		indexnum=0;
		sem=new NaiveSemantics(wordmap, indexmap);
		rnd = new Random();
	}
	
	public void addIndex(Word w) {
		if(!indexmap.containsKey(w.getForm())) {
			indexmap.put(w.getForm(), indexnum);
			indexnum++;
		}
		
	}
	
	public void addWord(Word w) {
		if(!wordmap.containsKey(indexmap.get(w.getForm()))) {
			wordmap.put(indexmap.get(w.getForm()), w);
		}
	}
	
	public void addFather(Word w, String father, boolean left) {
		wordmap.put(indexmap.get(w.getForm()), w.addFather(father, left));
	}
	
	public void addChild(Word w, String child, boolean left) {
		wordmap.put(indexmap.get(w.getForm()), w.addChild(child, left));
	}
	
	public HashMap<Integer, Word> getWordMap() {
		return wordmap;
	}
	
	public HashMap<String, Integer> getIndexMap() {
		return indexmap;
	}

	public Sentence generateTree(String str) {
		LinkedList<Word> wdList = new LinkedList<Word>();
		LinkedList<Node> nodeList = new LinkedList<Node>();
		
		//go to root
		int stri = indexmap.get(str);
		Word strw = wordmap.get(stri);
		int listSize = 0;
		
		while(!strw.getFatherForm().contains(new Child("ROOT", true))) {
			Child father = sem.getFatherForm(strw.getForm());
			nodeList.add(new Node(strw.getForm(),listSize+1,father.left));
			stri = indexmap.get(father.form);
			strw = wordmap.get(stri);
			listSize++;
			
			if(listSize>=SENTENCELENGTH) {
				break;
			}
		}
		
		//add root
		nodeList.add(new Node("ROOT", -1, rnd.nextBoolean()));
		
		//randomly add children
		while(listSize<SENTENCELENGTH) {
			int listindex = rnd.nextInt(nodeList.size());
			Child child = sem.getChildForm(nodeList.get(listindex).form);
			nodeList.add(new Node(child.form,listindex,child.left));
			listSize++;
		}
		
		
		//add child list to nodes
		int rootindex = -1;
		for(int i=0;i<nodeList.size();i++) {
			//add children
			for(int j=0;j<nodeList.size();j++) {
				if(nodeList.get(j).head==i) {
					nodeList.get(i).addChild(j);
				}
			}
			if(nodeList.get(i).head==-1)
				rootindex=i;
		}
		
		//compress the tree
		retindex=0;
		nodeProcess(rootindex, wdList, nodeList, -1);
		
		
		return new Sentence(wdList, true);
	}
	
	public void nodeProcess(int current, LinkedList<Word> wdList, LinkedList<Node> nodeList, int head) {
		Node root = nodeList.get(current);
		Word rootword = wordmap.get(indexmap.get(root.form));
		//left
		for(int lc : root.childList) {
			if(nodeList.get(lc).left) {
				nodeProcess(lc,wdList,nodeList, current);
			}
		}
		//self
		wdList.add(new Word(retindex,rootword.getForm(),rootword.getLemma(),rootword.getPos(),head));
		//right
		for(int lc : root.childList) {
			if(!nodeList.get(lc).left) {
				nodeProcess(lc,wdList,nodeList, current);
			}
		}
	}
	
}


class Node {
	public String form;
	public int head;
	public boolean left;
	public LinkedList<Integer> childList;
	
	public Node(String form, int head, boolean left) {
		this.form=form;
		this.head=head;
		this.left=left;
		childList = new LinkedList<Integer>();
	}
	
	public void addChild(int i) {
		childList.add(i);
	}
}