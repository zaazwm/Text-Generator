import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class DistributionalSemantics implements SemanticInterface{
	
	private HashMap<String, LinkedList<SemNode>> distMap;
	private HashMap<String, LinkedList<SemNode>> fatherMap;
	
	private static final int WINDOWSIZE = 5;
	private static final double SIMTHRESHOLD = 0.8;

	public DistributionalSemantics(LinkedList<Sentence> sList) {
		distMap = new HashMap<String, LinkedList<SemNode>>();
		fatherMap = new HashMap<String, LinkedList<SemNode>>();
		for(Sentence s : sList) {
			int wi = 0;
			for(Word w : s.getWdList()) {
				for(int i=wi-WINDOWSIZE;i<wi&&i>=0;i++) {
					//if(s.getWdList().get(i).getHead()==w.getID())
						saveToDistMap(w.getForm(),s.getWdList().get(i).getForm(),s.getWdList().get(i).getRel());
					//if(w.getHead()==s.getWdList().get(i).getID())
						saveToFatherMap(w.getForm(),s.getWdList().get(i).getForm(),w.getRel());
				}
				for(int i=wi+1;i<=wi+WINDOWSIZE&&i<s.getWdList().size();i++) {
					//if(s.getWdList().get(i).getHead()==w.getID())
						saveToDistMap(w.getForm(),s.getWdList().get(i).getForm(),s.getWdList().get(i).getRel());
					//if(w.getHead()==s.getWdList().get(i).getID())
						saveToFatherMap(w.getForm(),s.getWdList().get(i).getForm(),w.getRel());
				}
				
				wi++;
			}
		}
	}
	
	private void saveToDistMap(String root, String form, String tag) {
		if(distMap.containsKey(root)) {
			LinkedList<SemNode> tl = distMap.get(root);
			boolean foundmark = false;
			for(SemNode sn : tl) {
				if(sn.form.equals(form) && sn.tag.equals(tag)) {
					foundmark = true;
					sn.freq++;
					break;
				}
			}
			if(!foundmark) {
				tl.add(new SemNode(form,root));
			}
			distMap.put(root, tl);
		}
		else {
			LinkedList<SemNode> tl = new LinkedList<SemNode>();
			tl.add(new SemNode(form,root));
			distMap.put(root, tl);
		}
	}
	
	private void saveToFatherMap(String root, String form, String tag) {
		if(fatherMap.containsKey(root)) {
			LinkedList<SemNode> tl = fatherMap.get(root);
			boolean foundmark = false;
			for(SemNode sn : tl) {
				if(sn.form.equals(form) && sn.tag.equals(tag)) {
					foundmark = true;
					sn.freq++;
					break;
				}
			}
			if(!foundmark) {
				tl.add(new SemNode(form,root));
			}
			fatherMap.put(root, tl);
		}
		else {
			LinkedList<SemNode> tl = new LinkedList<SemNode>();
			tl.add(new SemNode(form,root));
			fatherMap.put(root, tl);
		}
	}
	
	@Override
	public void addTag(String form, String tag) {
		//Should have finished!
	}
	
	public String findSym(String word) {
		LinkedList<String> retList = findSyms(word);
		
		if(retList.isEmpty())
			return null;
		return retList.get(ApplicationControl.rnd.nextInt(retList.size()));
	}
	
	public LinkedList<String> findSyms(String word) {
		LinkedList<String> retList = new LinkedList<String>();
		
		// cosine similarity
		for(String wd : distMap.keySet()) {
			double above = 0.0;
			double below1 = 0.0;
			double below2 = 0.0;
			
			if(!distMap.containsKey(word))
				break;
			
			LinkedList<SemNode> tsnl = distMap.get(wd);
			for(SemNode sn : distMap.get(word)) {
				if(tsnl.contains(sn)) {
					int tf = 0;
					for(SemNode tsn : tsnl) {
						if(tsn.equals(sn)) {
							tf=tsn.freq;
							break;
						}
					}
						
					above+=sn.freq*tf;
				}
				below1+=sn.freq*sn.freq;
			}
			for(SemNode tsn : tsnl) {
				below2+=tsn.freq*tsn.freq;
			}
			
			double sim = above/(Math.sqrt(below1)*Math.sqrt(below2));
			if(sim>SIMTHRESHOLD)
				retList.add(wd);
		}
		
		return retList;
	}
	
	public String findSymFather(String word) {
		LinkedList<String> retList = findSymFathers(word);
		
		if(retList.isEmpty())
			return null;
		return retList.get(ApplicationControl.rnd.nextInt(retList.size()));
	}
	
	public LinkedList<String> findSymFathers(String word) {
		LinkedList<String> retList = new LinkedList<String>();
		
		// cosine similarity
		for(String wd : fatherMap.keySet()) {
			double above = 0.0;
			double below1 = 0.0;
			double below2 = 0.0;
			
			if(!fatherMap.containsKey(word))
				break;
			
			LinkedList<SemNode> tsnl = fatherMap.get(wd);
			for(SemNode sn : fatherMap.get(word)) {
				if(tsnl.contains(sn)) {
					int tf = 0;
					for(SemNode tsn : tsnl) {
						if(tsn.equals(sn)) {
							tf=tsn.freq;
							break;
						}
					}
						
					above+=sn.freq*tf;
				}
				below1+=sn.freq*sn.freq;
			}
			for(SemNode tsn : tsnl) {
				below2+=tsn.freq*tsn.freq;
			}
			
			double sim = above/(Math.sqrt(below1)*Math.sqrt(below2));
			if(sim>SIMTHRESHOLD)
				retList.add(wd);
		}
		
		return retList;
	}

	@Override
	public Child[] getChildForms(String word) {
		if(!distMap.containsKey(word))
			return null;
		Child[] ret = new Child[distMap.get(word).size()];
		int i = 0;
		for(SemNode sn : distMap.get(word)) {
			ret[i]=new Child(sn.form,sn.tag,ApplicationControl.rnd.nextInt(WINDOWSIZE));
					
			i++;
		}
		return ret;
	}

	@Override
	public Child getChildForm(String word) {
		Child[] allChildren = getChildForms(word);
		if(allChildren==null) {
			Random rnd=ApplicationControl.rnd;
			return new Child((String)distMap.keySet().toArray()[rnd.nextInt(distMap.keySet().size())], ApplicationControl.tagList.get(rnd.nextInt(ApplicationControl.tagList.size())), 10-rnd.nextInt(20));
		}
		
		Child ret = allChildren[ApplicationControl.rnd.nextInt(allChildren.length)];
		
		LinkedList<String> syms = findSyms(ret.form);
		int retindex = ApplicationControl.rnd.nextInt(syms.size()+syms.size()/2);
		if(retindex<syms.size())
			return new Child(syms.get(retindex),ret.tag,ret.distance);
		else
			return ret;
	}

	@Override
	public Child getChildForm(String word, String tag) {
		Child[] allChildren = getChildForms(word);
		if(allChildren==null) {
			Random rnd=ApplicationControl.rnd;
			return new Child((String)distMap.keySet().toArray()[rnd.nextInt(distMap.keySet().size())], tag, 10-rnd.nextInt(20));
		}
		int count=0;
		for(Child c : allChildren) {
			if(c.tag.equals(tag))
				count++;
		}
		if(count<=0)
			return getChildForm(word);
		
		
		Child[] tagChildren = new Child[count];
		int i = 0;
		for(Child c : allChildren) {
			if(c.tag.equals(tag)) {
				tagChildren[i]=c;
				i++;
			}
		}
		
		Child ret = tagChildren[ApplicationControl.rnd.nextInt(tagChildren.length)];
		
		LinkedList<String> syms = findSyms(ret.form);
		int retindex = ApplicationControl.rnd.nextInt(syms.size()+syms.size()/2);
		if(retindex<syms.size())
			return new Child(syms.get(retindex),ret.tag,ret.distance);
		else
			return ret;
	}

	@Override
	public Child[] getFatherForms(String word) {
		Child[] ret = new Child[fatherMap.get(word).size()];
		int i = 0;
		for(SemNode sn : fatherMap.get(word)) {
			ret[i]=new Child(sn.form,sn.tag,ApplicationControl.rnd.nextInt(WINDOWSIZE));
					
			i++;
		}
		return ret;
	}

	@Override
	public Child getFatherForm(String word) {
		Child[] allChildren = getChildForms(word);
		if(allChildren==null) {
			Random rnd=ApplicationControl.rnd;
			return new Child((String)fatherMap.keySet().toArray()[rnd.nextInt(fatherMap.keySet().size())], ApplicationControl.tagList.get(rnd.nextInt(ApplicationControl.tagList.size())), 10-rnd.nextInt(20));
		}
		Child ret = allChildren[ApplicationControl.rnd.nextInt(allChildren.length)];
		
		LinkedList<String> syms = findSymFathers(ret.form);
		int retindex = ApplicationControl.rnd.nextInt(syms.size()+syms.size()/2);
		if(retindex<syms.size())
			return new Child(syms.get(retindex),ret.tag,ret.distance);
		else
			return ret;
	}

	@Override
	public int rankSentence(Sentence s) {
		Integer score=0;
		
		int wi=0;
		for(Word w : s.getWdList()) {
			if(distMap.containsKey(w.getForm())) {
				LinkedList<SemNode> snList = distMap.get(w.getForm());
				
				for(int i=wi-WINDOWSIZE;i<wi&&i>=0;i++) {
					for(SemNode sn : snList) {
						if(s.getWdList().get(i).getForm().equals(sn.form)&&s.getWdList().get(i).getRel().equals(sn.tag))
							score+=sn.freq*10;
						else if(s.getWdList().get(i).getForm().equals(sn.form))
							score+=sn.freq;
						else if(s.getWdList().get(i).getRel().equals(sn.tag))
							score+=sn.freq/2;
					}
				}
				for(int i=wi+1;i<=wi+WINDOWSIZE&&i<s.getWdList().size();i++) {
					for(SemNode sn : snList) {
						if(s.getWdList().get(i).getForm().equals(sn.form)&&s.getWdList().get(i).getRel().equals(sn.tag))
							score+=sn.freq*10;
						else if(s.getWdList().get(i).getForm().equals(sn.form))
							score+=sn.freq;
						else if(s.getWdList().get(i).getRel().equals(sn.tag))
							score+=(sn.freq+1)/2;
					}
				}
			}
			
			
			wi++;
		}
		
		return score;
	}

}

class SemNode implements Comparable {
	public String form;
	public String tag;
	public Integer freq;
	
	public SemNode(String f) {
		form=f;
	}
	
	public SemNode(String f, String t) {
		form=f;
		tag=t;
		freq=1;
	}
	
	public SemNode(String f, String t, int fq) {
		form=f;
		tag=t;
		freq=fq;
	}

	@Override
	public int compareTo(Object o) {
		return ((SemNode)o).freq-this.freq;
	}

	public boolean equals( Object o2 )
	{
	   return this.form.equals(((SemNode)o2).form) /*& this.tag.equals(((SemNode)o2).tag)*/;
	}
}
