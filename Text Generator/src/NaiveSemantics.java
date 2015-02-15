import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class NaiveSemantics implements SemanticInterface{
	private HashMap<Integer, Word> wordmap;
	private HashMap<String, Integer> indexmap;
	private Random rnd;
	
	public NaiveSemantics(HashMap<Integer, Word> wm, HashMap<String, Integer> im) {
		wordmap = wm;
		indexmap = im;
		rnd=new Random();
	}

	@Override
	public Child[] getChildForms(String word) {
		int i = -1;
		Child[] ret = null;
		if(indexmap.containsKey(word)) {
			i = indexmap.get(word);
			LinkedList<Child> sl = wordmap.get(i).getChildForm();
			ret = new Child[sl.size()];
			int j=0;
			for(Child s : sl) {
				ret[j]=s;
				j++;
			}
		}
		return ret;
	}

	@Override
	public Child getChildForm(String word) {
		Child[] sl = getChildForms(word);
		if(sl.length<1) {
			String ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			while(ret.equals("ROOT")) {
				ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			}
			return new Child(ret, 10-rnd.nextInt(20));
		}
		int freqSum=0;
		for(Child c : sl) {
			freqSum+=c.frequency;
		}
		int target = rnd.nextInt(freqSum);
		int ret=0;
		for(Child c : sl) {
			target-=c.frequency;
			if(target<=0)
				break;
			ret++;
		}
		//return sl[rnd.nextInt(sl.length)];
		return sl[ret];
	}

	@Override
	public Child[] getFatherForms(String word) {
		int i = -1;
		Child[] ret = null;
		if(indexmap.containsKey(word)) {
			i = indexmap.get(word);
			LinkedList<Child> sl = wordmap.get(i).getFatherForm();
			ret = new Child[sl.size()];
			int j=0;
			for(Child s : sl) {
				ret[j]=s;
				j++;
			}
		}
		return ret;
	}

	@Override
	public Child getFatherForm(String word) {
		Child[] sl = getFatherForms(word);
		if(sl.length<1) {
			String ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			while(ret.equals("ROOT")) {
				ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			}
			return new Child(ret, 10-rnd.nextInt(20));
		}
		int freqSum=0;
		for(Child c : sl) {
			freqSum+=c.frequency;
		}
		int target = rnd.nextInt(freqSum);
		int ret=0;
		for(Child c : sl) {
			target-=c.frequency;
			if(target<=0)
				break;
			ret++;
		}
		//return sl[rnd.nextInt(sl.length)];
		return sl[ret];
	}

	@Override
	public int rankSentence(Sentence s) {
		return rnd.nextInt(10000);
	}

}
