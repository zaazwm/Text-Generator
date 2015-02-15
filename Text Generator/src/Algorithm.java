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
	private LinkedList<SyntaticNode> snList = new LinkedList<SyntaticNode>();
	private LinkedList<SyntaticPattern> tmpSP = new LinkedList<SyntaticPattern>();
	
	private static final int SENTENCELENGTH = 15;
	private static final int MAXTRIAL = 3;
	
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
	
	public void addPattern(Word w, Word father) {
		SyntaticPattern sp = null;
		for(SyntaticPattern ssp : tmpSP) {
			if(ssp.dummy==father.getID()) {
				sp=ssp;
				break;
			}
		}
		if(sp==null) {
			sp=new SyntaticPattern(father.getForm());
			sp.dummy=father.getID();
			tmpSP.add(sp);
		}
		if(w.getID()<father.getID())
			sp.leftTags.add(w.getRel());
		else
			sp.rightTags.add(w.getRel());
	}
	
	public void savePattern() {
		for(SyntaticPattern sp : tmpSP) {
			SyntaticNode tmpSN = new SyntaticNode(sp.form);
			if(snList.contains(tmpSN)) {
				for(SyntaticNode snn : snList) {
					if(snn.equals(tmpSN)) {
						snn.addPattern(sp);
						break;
					}
				}
			}
			else {
				tmpSN.addPattern(sp);
				snList.add(tmpSN);
			}
		}
		tmpSP.clear();
	}
	
	public void addFather(Word w, String father, String tag, int distance) {
		wordmap.put(indexmap.get(w.getForm()), w.addFather(father, tag, distance));
	}
	
	public void addChild(Word w, String child, String tag, int distance) {
		wordmap.put(indexmap.get(w.getForm()), w.addChild(child, tag, distance));
	}
	
	public HashMap<Integer, Word> getWordMap() {
		return wordmap;
	}
	
	public HashMap<String, Integer> getIndexMap() {
		return indexmap;
	}

	public Sentence generateNaiveTree(String str) {
		LinkedList<Word> wdList = new LinkedList<Word>();
		LinkedList<Node> nodeList = new LinkedList<Node>();
		
		//random query if not found
		if(!indexmap.containsKey(str)) {
			//System.out.print(str+" not found, ");
			str = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			//System.out.println("use "+str+" instead");
		}
		
		//go to root
		int stri = indexmap.get(str);
		Word strw = wordmap.get(stri);
		int listSize = 0;
		
		int trial = 0;
		while(!strw.getFatherForm().contains(new Child("ROOT", null, 0))) {
			Child father = sem.getFatherForm(strw.getForm());
			if(trial < MAXTRIAL && nodeList.contains(new Node(father.form,listSize+1, null,father.distance))) {
				trial++;
				continue;
			}
			nodeList.add(new Node(strw.getForm(),listSize+1, strw.getRel(),father.distance));
			stri = indexmap.get(father.form);
			strw = wordmap.get(stri);
			trial=0;
			listSize++;
			
			if(listSize>=SENTENCELENGTH) {
				break;
			}
		}
		
		//direct to root
		Child father = new Child("DUMMY", null, 5-rnd.nextInt(10));
		for(Child f : strw.getFatherForm()) {
			if(f.form.equals("ROOT")) {
				father=f;
				break;
			}
		}
		nodeList.add(new Node(strw.getForm(),listSize+1, "ROOT", father.distance));
		listSize++;
		
		//add root
		nodeList.add(new Node("ROOT", -1, null, rnd.nextInt()));
		listSize++;
		
		//randomly add children
		trial=0;
		while(listSize<=SENTENCELENGTH) {
			int listindex = rnd.nextInt(nodeList.size());
			Child child = sem.getChildForm(nodeList.get(listindex).form);
			if(trial < MAXTRIAL && nodeList.contains(new Node(child.form,listindex, child.tag,child.distance))) {
				trial++;
				continue;
			}
			nodeList.add(new Node(child.form,listindex, child.tag, child.distance));
			trial=0;
			listSize++;
		}
		
		
		//add child list to nodes
		int rootindex = -1;
		for(int i=0;i<nodeList.size();i++) {
			//add children
			for(int j=0;j<nodeList.size();j++) {
				if(nodeList.get(j).head==i) {
					nodeList.get(i).addChild(new Node(new Integer(j).toString(),i, null,nodeList.get(j).distance));
				}
			}
			Collections.sort(nodeList.get(i).childList);
			if(nodeList.get(i).head==-1)
				rootindex=i;
		}
		
		//compress the tree
		retindex=0;
		nodeProcess(rootindex, wdList, nodeList, -1);
		
		
		Sentence ret = new Sentence(wdList, true);
		ret.setQuery(str);
		return ret;
	}
	
	public Sentence generateTree(String str) {
		LinkedList<Word> wdList = new LinkedList<Word>();
		LinkedList<Node> nodeList = new LinkedList<Node>();
		
		//random query if not found
		if(!indexmap.containsKey(str)) {
			str = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
		}
		
		//go to root
		int stri = indexmap.get(str);
		Word strw = wordmap.get(stri);
		int listSize = 0;
		
		int trial = 0;
		while(!strw.getFatherForm().contains(new Child("ROOT", null, 0))) {
			Child father = sem.getFatherForm(strw.getForm());
			if(trial < MAXTRIAL && nodeList.contains(new Node(father.form,listSize+1,null,father.distance))) {
				trial++;
				continue;
			}
			nodeList.add(new Node(strw.getForm(),listSize+1,strw.getRel(),father.distance));
			stri = indexmap.get(father.form);
			strw = wordmap.get(stri);
			trial=0;
			listSize++;
			
			if(listSize>=SENTENCELENGTH) {
				break;
			}
		}
		
		//direct to root
		Child father = new Child("DUMMY", null, 5-rnd.nextInt(10));
		for(Child f : strw.getFatherForm()) {
			if(f.form.equals("ROOT")) {
				father=f;
				break;
			}
		}
		nodeList.add(new Node(strw.getForm(),listSize+1,"ROOT",father.distance));
		listSize++;
		
		//add root
		nodeList.add(new Node("ROOT", -1, null, rnd.nextInt()));
		listSize++;
		
		//randomly add children
		trial=0;
		LinkedList<Integer> Exceplist = new LinkedList<Integer>();
		while(trial <= MAXTRIAL && listSize<=SENTENCELENGTH) {
			if(Exceplist.size()>=nodeList.size())
				break;
			int listindex = rnd.nextInt(nodeList.size());
			if(Exceplist.contains(listindex))
				continue;
			SyntaticNode sn = findSNode(nodeList.get(listindex).form);
			String cTag = null;
			for(Node n : nodeList) {
				if(n.head==listindex) {
					cTag=n.tag;
					break;
				}
			}
			if(cTag==null) {
				trial++;
				continue;
			}
			if(sn==null) {
				Exceplist.add(listindex);
				continue;
			}
			SyntaticPattern sp = sn.getPattern(cTag);
			for(String ltag : sp.leftTags) {
				if(ltag.equals(cTag))
					continue;
				Child child = sem.getChildForm(nodeList.get(listindex).form,ltag);
				while(trial < MAXTRIAL && nodeList.contains(new Node(child.form,listindex,child.tag,child.distance))) {
					trial++;
					child = sem.getChildForm(nodeList.get(listindex).form,ltag);
				}
				nodeList.add(new Node(child.form,listindex,child.tag,0-Math.abs(child.distance)));
				trial=0;
				listSize++;
			}
			for(String rtag : sp.rightTags) {
				if(rtag.equals(cTag))
					continue;
				Child child = sem.getChildForm(nodeList.get(listindex).form,rtag);
				while(trial < MAXTRIAL && nodeList.contains(new Node(child.form,listindex,child.tag,child.distance))) {
					trial++;
					child = sem.getChildForm(nodeList.get(listindex).form,rtag);
				}
				nodeList.add(new Node(child.form,listindex,child.tag,Math.abs(child.distance)));
				trial=0;
				listSize++;
			}
		}
		
		//sentence too short, use naive tree
		trial=0;
		while(listSize<=SENTENCELENGTH) {
			int listindex = rnd.nextInt(nodeList.size());
			Child child = sem.getChildForm(nodeList.get(listindex).form);
			if(trial < MAXTRIAL && nodeList.contains(new Node(child.form,listindex, child.tag,child.distance))) {
				trial++;
				continue;
			}
			nodeList.add(new Node(child.form,listindex, child.tag, child.distance));
			trial=0;
			listSize++;
		}
		
		//add child list to nodes
		int rootindex = -1;
		for(int i=0;i<nodeList.size();i++) {
			//add children
			for(int j=0;j<nodeList.size();j++) {
				if(nodeList.get(j).head==i) {
					nodeList.get(i).addChild(new Node(new Integer(j).toString(),i,null,nodeList.get(j).distance));
				}
			}
			Collections.sort(nodeList.get(i).childList);
			if(nodeList.get(i).head==-1)
				rootindex=i;
		}
		
		//compress the tree
		retindex=0;
		nodeProcess(rootindex, wdList, nodeList, -1);
		
		
		Sentence ret = new Sentence(wdList, true);
		ret.setQuery(str);
		return ret;
	}
	
	public SyntaticNode findSNode(String form) {
		for(SyntaticNode sn : snList) {
			if(sn.getForm().equals(form))
				return sn;
		}
		return null;
	}
	
	public void nodeProcess(int current, LinkedList<Word> wdList, LinkedList<Node> nodeList, int head) {
		Node root = nodeList.get(current);
		Word rootword = wordmap.get(indexmap.get(root.form));
		//left
		for(Node lc : root.childList) {
			if(nodeList.get(new Integer(lc.form)).distance<=0) {
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
	
	public SemanticInterface getSemantic() {
		return sem;
	}
	
}


class Node implements Comparable {
	public String form;
	public int head;
	public String tag;
	public int distance;
	public LinkedList<Node> childList;
	
	public Node(String form, int head, String tag, int distance) {
		this.form=form;
		this.head=head;
		this.tag=tag;
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
	
	public boolean equals( Object o2 )
	{
	   return this.form.equals(((Node)o2).form);
	}
}