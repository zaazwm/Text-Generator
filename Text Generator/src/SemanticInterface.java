
public interface SemanticInterface {
	public void addTag(String form, String tag);
	
	public Child[] getChildForms(String word);
	public Child getChildForm(String word);
	public Child getChildForm(String word, String tag);
	
	public Child[] getFatherForms(String word);
	public Child getFatherForm(String word);
	
	public int rankSentence(Sentence s);
	
}
