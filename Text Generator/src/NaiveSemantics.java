import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class NaiveSemantics implements SemanticInterface{
	private HashMap<Integer, Word> wordmap;
	private HashMap<String, Integer> indexmap;
	private HashMap<String, LinkedList<String>> tagmap;
	private Random rnd;
	
	public NaiveSemantics(HashMap<Integer, Word> wm, HashMap<String, Integer> im) {
		wordmap = wm;
		indexmap = im;
		tagmap = new HashMap<String, LinkedList<String> >();
		rnd=new Random();
	}
	
	//save possible arc tags to the child(form)
	public void addTag(String form, String tag) {
		if(!tagmap.containsKey(form))
			tagmap.put(form, new LinkedList<String>());
		if(!tagmap.get(form).contains(tag)) {
			tagmap.get(form).add(tag);
		}
	}

	@Override
	//get all possible children for the father(word)
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
	//get one weighted random child for father(word)
	public Child getChildForm(String word) {
		Child[] sl = getChildForms(word);
		//random child if no children found
		if(sl.length<1) {
			String ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			while(ret.equals("ROOT")) {
				ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			}
			return new Child(ret, ApplicationControl.tagList.get(rnd.nextInt(ApplicationControl.tagList.size())), 10-rnd.nextInt(20));
		}
		//random with freq-weight
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
	//get one weighted random child with tag for father(word)
	public Child getChildForm(String word, String tag) {
		Child[] sl = getChildForms(word);
		//random child if no children found
		if(sl.length<1) {
			String ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			while(ret.equals("ROOT")) {
				if(tagmap.containsKey(ret))
					if(tagmap.get(ret).contains(tag))
						break;
				ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			}
			return new Child(ret, tag, 10-rnd.nextInt(20));
		}
		//random with freq-weight, child should have tag
		int freqSum=0;
		boolean found=true;
		for(Child c : sl) {
			if(!tagmap.containsKey(c.form))
				continue;
			if(!tagmap.get(c.form).contains(tag))
				continue;
			freqSum+=c.frequency;
		}
		if(freqSum==0) {
			found=false;
			for(Child c : sl) {
				freqSum+=c.frequency;
			}
		}
		int target = rnd.nextInt(freqSum);
		int ret=0;
		for(Child c : sl) {
			if(!tagmap.containsKey(c.form))
				continue;
			if(found && !tagmap.get(c.form).contains(tag))
				continue;
			target-=c.frequency;
			if(target<=0)
				break;
			ret++;
		}
		//return sl[rnd.nextInt(sl.length)];
		return sl[ret].getChildWithTag(tag);
	}

	@Override
	//get all possible fathers for child(word)
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

	//get weighted random father for child(word)
	@Override
	public Child getFatherForm(String word) {
		Child[] sl = getFatherForms(word);
		if(sl.length<1) {
			String ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			while(ret.equals("ROOT")) {
				ret = wordmap.get(rnd.nextInt(wordmap.size())).getForm();
			}
			return new Child(ret, ApplicationControl.tagList.get(rnd.nextInt(ApplicationControl.tagList.size())), 10-rnd.nextInt(20));
		}
		//weight with freq
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
	//rank the sentence, using sentence length(longer the better) and duplicated word(less the better)
	public int rankSentence(Sentence s) {
		LinkedList<String> uniqueword = new LinkedList<String>();
		int dummywordcount = 0;
		for(Word w : s.getWdList()) {
			if(!uniqueword.contains(w.getForm()))
				uniqueword.add(w.getForm());
			else
				dummywordcount++;
		}
		return rnd.nextInt(1000*s.getWdList().size()-500*dummywordcount);
	}

}
