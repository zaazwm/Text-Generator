import java.util.Collections;
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
	
	public void addFather(Word w, String father, int distance) {
		wordmap.put(indexmap.get(w.getForm()), w.addFather(father, distance));
	}
	
	public void addChild(Word w, String child, int distance) {
		wordmap.put(indexmap.get(w.getForm()), w.addChild(child, distance));
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
		
		if(!indexmap.containsKey(str))
			return new Sentence(wdList, true);
		
		//go to root
		int stri = indexmap.get(str);
		Word strw = wordmap.get(stri);
		int listSize = 0;
		
		while(!strw.getFatherForm().contains(new Child("ROOT", 0))) {
			Child father = sem.getFatherForm(strw.getForm());
			nodeList.add(new Node(strw.getForm(),listSize+1,father.distance));
			stri = indexmap.get(father.form);
			strw = wordmap.get(stri);
			listSize++;
			
			if(listSize>=SENTENCELENGTH) {
				break;
			}
		}
		
		//add root
		nodeList.add(new Node("ROOT", -1, rnd.nextInt()));
		
		//randomly add children
		while(listSize<SENTENCELENGTH) {
			int listindex = rnd.nextInt(nodeList.size());
			Child child = sem.getChildForm(nodeList.get(listindex).form);
			nodeList.add(new Node(child.form,listindex,child.distance));
			listSize++;
		}
		
		
		//add child list to nodes
		int rootindex = -1;
		for(int i=0;i<nodeList.size();i++) {
			//add children
			for(int j=0;j<nodeList.size();j++) {
				if(nodeList.get(j).head==i) {
					nodeList.get(i).addChild(new Node(new Integer(j).toString(),i,nodeList.get(j).distance));
				}
			}
			Collections.sort(nodeList.get(i).childList);
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
		for(Node lc : root.childList) {
			if(nodeList.get(new Integer(lc.form)).distance<0) {
				nodeProcess(new Integer(lc.form),wdList,nodeList, current);
			}
		}
		//self
		wdList.add(new Word(retindex,rootword.getForm(),rootword.getLemma(),rootword.getPos(),head));
		//right
		for(Node lc : root.childList) {
			if(nodeList.get(new Integer(lc.form)).distance>0) {
				nodeProcess(new Integer(lc.form),wdList,nodeList, current);
			}
		}
	}
	
}


class Node implements Comparable {
	public String form;
	public int head;
	public int distance;
	public LinkedList<Node> childList;
	
	public Node(String form, int head, int distance) {
		this.form=form;
		this.head=head;
		this.distance=distance;
		childList = new LinkedList<Node>();
	}
	
	public void addChild(Node i) {
		childList.add(i);
	}

	@Override
	public int compareTo(Object o) {
		return this.distance-((Node)o).distance;
	}
}