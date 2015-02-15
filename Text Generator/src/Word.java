import java.util.LinkedList;


public class Word {
	private int _ID;
	private String _form;
	private String _lemma;
	private String _pos;
	private String _morph;
	private int _head;
	private String _rel;
	private int _projectiveID;
	private LinkedList<Integer> _children;
	private int _MPCID;
	private int _MPChead;
	private String _tag;
	private LinkedList<Child> _chform;
	private LinkedList<Child> _faform;
	//to save information of each word/token
	public Word(int id, String form, String lemma, String pos, String morph, int head, String rel) {
		_ID=id;
		_form=form;
		_lemma=lemma;
		_pos=pos;
		_morph=morph;
		_head=head;
		_rel=rel;
		_tag=null;
		_children=new LinkedList<Integer>();
		_MPCID=-1;
		_MPChead=-1;
		_chform=new LinkedList<Child>();
		_faform=new LinkedList<Child>();
	}
	
	public Word(int id, String form, String lemma, String pos, int head, String rel) {
		_ID=id;
		_form=form;
		_lemma=lemma;
		_pos=pos;
		_morph=null;
		_head=head;
		_rel=rel;
		_tag=null;
		_children=new LinkedList<Integer>();
		_MPCID=-1;
		_MPChead=-1;
		_chform=new LinkedList<Child>();
		_faform=new LinkedList<Child>();
	}
	
	public Word(int id, String form, String lemma, String pos, int head) {
		_ID=id;
		_form=form;
		_lemma=lemma;
		_pos=pos;
		_morph=null;
		_head=head;
		_rel=null;
		_tag=null;
		_children=new LinkedList<Integer>();
		_MPCID=-1;
		_MPChead=-1;
		_chform=new LinkedList<Child>();
		_faform=new LinkedList<Child>();
	}
	
	public Word(int id, String form, String lemma, String pos, int head, String tag, boolean marker) {
		_ID=id;
		_form=form;
		_lemma=lemma;
		_pos=pos;
		_morph=null;
		_head=head;
		_rel=null;
		_tag=tag;
		_children=new LinkedList<Integer>();
		_MPCID=-1;
		_MPChead=-1;
		_chform=new LinkedList<Child>();
		_faform=new LinkedList<Child>();
	}

	public int getID() {
		return _ID;
	}

	public void setID(int _ID) {
		this._ID = _ID;
	}

	public String getForm() {
		return _form;
	}

	public void setForm(String _form) {
		this._form = _form;
	}

	public String getLemma() {
		return _lemma;
	}

	public void setLemma(String _lemma) {
		this._lemma = _lemma;
	}

	public String getPos() {
		return _pos;
	}

	public void setPos(String _pos) {
		this._pos = _pos;
	}

	public String getMorph() {
		return _morph;
	}

	public void setMorph(String _morph) {
		this._morph = _morph;
	}

	public int getHead() {
		return _head;
	}

	public void setHead(int _head) {
		this._head = _head;
	}

	public String getRel() {
		return _rel;
	}

	public void setRel(String _rel) {
		this._rel = _rel;
	}

	public int getProjectiveID() {
		return _projectiveID;
	}

	public void setProjectiveID(int _projectiveID) {
		this._projectiveID = _projectiveID;
	}

	public LinkedList<Integer> getChildren() {
		return _children;
	}

	public void addChildren(Integer cid) {
		this._children.add(cid);
	}

	public int getMPCID() {
		return _MPCID;
	}

	public void setMPCID(int MPCID) {
		this._MPCID = MPCID;
	}

	public int getMPChead() {
		return _MPChead;
	}

	public void setMPChead(int MPChead) {
		this._MPChead = MPChead;
	}

	public String getTag() {
		return _tag;
	}

	public void setTag(String tag) {
		this._tag = tag;
	}
	
	public Word addFather(String form, String tag, int distance) {
		distance+=(3-ApplicationControl.rnd.nextInt(6));
		if(_faform.contains(new Child(form, tag, distance))) {
			for(Child c : _faform) {
				if(c.form.equals(form)) {
					c.distance=(c.distance*c.frequency+distance)/(c.frequency+1);
					c.frequency++;
				}
			}
			return this;
		}
		_faform.add(new Child(form, tag, distance));
		return this;
	}
	
	public LinkedList<Child> getFatherForm() {
		return _faform;
	}
	
	public Word addChild(String form, String tag, int distance) {
		distance+=(3-ApplicationControl.rnd.nextInt(6));
		if(_chform.contains(new Child(form, tag, distance))) {
			for(Child c : _chform) {
				if(c.form.equals(form)) {
					c.distance=(c.distance*c.frequency+distance)/(c.frequency+1);
					c.frequency++;
				}
			}
			return this;
		}
		_chform.add(new Child(form, tag, distance));
		return this;
	}
	
	public LinkedList<Child> getChildForm() {
		return _chform;
	}
	
	
}

class Child {
	public String form;
	public String tag;
	public int distance;
	public int frequency;
	public Child(String form, String tag, int distance) {
		this.form=form;
		this.tag=tag;
		this.distance=distance;
		frequency=1;
	}
	
	public boolean equals( Object o2 )
	{
		if(this.form.equals("ROOT") && ((Child)o2).form.equals("ROOT"))
			return true;
	    return (this.form.equals(((Child)o2).form)&&this.tag.equals(((Child)o2).tag));
	}
}
