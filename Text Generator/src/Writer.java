import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class Writer {
	@SuppressWarnings("unused")
	private String path;
	private BufferedWriter fw;
	
	private static int fieldID=0;
	private static int fieldForm=1;
	private static int fieldLemma=2;
	private static int fieldPos=3;
	private static int fieldMorph=5;
	private static int fieldHead=6;
	private static int fieldRel=7;
	
	public Writer(String path) throws IOException {
		this.path=path;
		fw=new BufferedWriter( new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));;
	}
	//write sentence to file, with CoNLL06 format
	public void write(Sentence s) throws IOException {
		for(Word w : s.getWdList()) {
			if(w.getID()==0)  //skip root
				continue;
			for(int i=0;i<10;i++) {
				if(i==fieldID) {
					fw.write(w.getID()+"\t");
				}
				else if(i==fieldForm) {
					fw.write(w.getForm()+"\t");
				}
				else if(i==fieldLemma) {
					fw.write(w.getLemma()+"\t");
				}
				else if(i==fieldPos) {
					fw.write(w.getPos()+"\t");
				}
				else if(i==fieldMorph) {
					fw.write((w.getMorph()==null?"_":w.getMorph())+"\t");
				}
				else if(i==fieldHead) {
					fw.write(w.getHead()+"\t");
				}
				else if(i==fieldRel) {
					if(/*ApplicationControl.newPredArcTag*/true)
						fw.write((w.getTag()==null?"_":w.getTag())+"\t");
					else
						fw.write((w.getRel()==null?"_":w.getRel())+"\t");
				}
				else if(i==9) {
					fw.write("_\n");
				}
				else {
					fw.write("_\t");
				}
			}
		}
		fw.write("\n");
	}
	
	public void close() throws IOException {
		fw.close();
	}
	
}
