import java.util.LinkedList;


public class SyntaticNode {
	private String form;
	private LinkedList<SyntaticPattern> spList;
	
	private static final int TRIAL = 5;
	
	public SyntaticNode(String form) {
		this.form=form;
		spList = new LinkedList<SyntaticPattern>();
	}
	
	public String getForm() {
		return form;
	}
	
	public SyntaticPattern getPattern() {
		return spList.get(ApplicationControl.rnd.nextInt(spList.size()));
	}
	
	//get pattern containing the tag
	public SyntaticPattern getPattern(String containingTag) {
		LinkedList<SyntaticPattern> tmpList = new LinkedList<SyntaticPattern>();
		//if no valid tag, get random pattern
		if(containingTag==null) {
			//containingTag = ApplicationControl.tagList.get(ApplicationControl.rnd.nextInt(ApplicationControl.tagList.size()));
			return getPattern();
		}
		for(SyntaticPattern sp : spList) {
			if(sp.leftTags.contains(containingTag) || sp.rightTags.contains(containingTag))
				tmpList.add(sp);
		}
		//if no containing pattern found, get random pattern
		if(tmpList.size()<1)
			return getPattern();
		return tmpList.get(ApplicationControl.rnd.nextInt(tmpList.size()));
	}
	
	public LinkedList<SyntaticPattern> getPatternList() {
		return spList;
	}
	
	public SyntaticNode addPattern(SyntaticPattern sp) {
		if(!spList.contains(sp))
			spList.add(sp);
		return this;
	}
	
	public boolean equals( Object o2 ) {
		return this.form.equals(((SyntaticNode)o2).form);
	}
}

class SyntaticPattern {
	public LinkedList<String> leftTags;
	public LinkedList<String> rightTags;
	public String form;
	
	public SyntaticPattern(String form) {
		leftTags = new LinkedList<String>();
		rightTags = new LinkedList<String>();
		this.form=form;
	}
	
	public SyntaticPattern addLeft(String tag) {
		leftTags.add(tag);
		return this;
	}
	
	public SyntaticPattern addRight(String tag) {
		rightTags.add(tag);
		return this;
	}
	
	public boolean contains(String tag) {
		return (leftTags.contains(tag) || rightTags.contains(tag));
	}
	
	public boolean equals( Object o2 )
	{
	   SyntaticPattern sp2 = (SyntaticPattern)o2;
	   if(leftTags.size()!=sp2.leftTags.size() || rightTags.size()!=sp2.rightTags.size())
		   return false;
	   for(String t : leftTags) {
		   if(!sp2.leftTags.contains(t))
			   return false;
	   }
	   for(String t : rightTags) {
		   if(!sp2.rightTags.contains(t))
			   return false;
	   }
	   
	   return true;
	}
}