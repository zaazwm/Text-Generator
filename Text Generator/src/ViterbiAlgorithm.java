import java.util.HashMap;

//all probabilities are log-prob
public class ViterbiAlgorithm {
	public HMM model;
	public ViterbiNode[][] vNodes;
	public ViterbiAlgorithm() {
		
	}
	
	public void setModel(HMM model) {
		this.model=model;
		vNodes = new ViterbiNode[model.links][model.depth];
	}
	
	public void generateVNodes() {
		for(int j=model.depth-1;j>=0;j--) {
			for(int i=0;i<model.links;i++) {
				if(j==model.depth-1) {
					vNodes[i][j]=new ViterbiNode(model.matrix[i][j].label,model.matrix[i][j].oberservation.label,model.matrix[i][j].oberservation.inLinks.get(model.matrix[i][j].oberservation.inLinks.keySet().iterator().next()));
				}
				else {
					int maxi=-1;
					double maxp=Double.MIN_VALUE;
					for(int k=0;k<model.links;k++) {
						if(vNodes[k+1][j].prob+model.matrix[k+1][j].inLinks.get(model.matrix[i][j].label)>maxp) {
							maxp=vNodes[k+1][j].prob+model.matrix[k+1][j].inLinks.get(model.matrix[i][j].label);
							maxi=k;
						}
					}
					if(maxi==-1)
						maxp=0.0D;
					vNodes[i][j]=new ViterbiNode(model.matrix[i][j].label,model.matrix[i][j].oberservation.label,model.matrix[i][j].oberservation.inLinks.get(model.matrix[i][j].oberservation.inLinks.keySet().iterator().next())+maxp);
					vNodes[i][j].next=maxi;
				}
			}
		}
	}
	
	public double getMaxProb() {
		double maxp=Double.MIN_VALUE;
		for(int i=0;i<model.links;i++) {
			if(vNodes[i][0].prob>maxp)
				maxp=vNodes[i][0].prob;
		}
		return maxp;
	}
	
	public String[] getMaxString() {
		double maxp=Double.MIN_VALUE;
		int current=-1;
		String[] ret = new String[model.depth];
		for(int i=0;i<model.links;i++) {
			if(vNodes[i][0].prob>maxp) {
				maxp=vNodes[i][0].prob;
				current=i;
			}
		}
		for(int i=0;i<model.depth;i++) {
			ret[i]=vNodes[current][i].label;
			current=vNodes[current][i].next;
		}
		return ret;
	}
	
	public String[] getMaxOb() {
		double maxp=Double.MIN_VALUE;
		int current=-1;
		String[] ret = new String[model.depth];
		for(int i=0;i<model.links;i++) {
			if(vNodes[i][0].prob>maxp) {
				maxp=vNodes[i][0].prob;
				current=i;
			}
		}
		for(int i=0;i<model.depth;i++) {
			ret[i]=vNodes[current][i].ob;
			current=vNodes[current][i].next;
		}
		return ret;
	}
}

class HMM {
	public HMMNode[][] matrix;
	public int links;
	public int depth;
	public HMM(int depth, int links) {
		matrix = new HMMNode[links][depth];
		this.links=links;
		this.depth=depth;
	}
}

class HMMNode{ 
	public String label;
	public HashMap<String, Double> inLinks;
	public HMMNode oberservation;
	public HMMNode(String label) {
		this.label=label;
		inLinks = new HashMap<String, Double>();
	}
}

class ViterbiNode {
	public String label;
	public String ob;
	public Double prob;
	public int next;
	public ViterbiNode(String label, String ob, Double oProb) {
		this.label=label;
		this.ob=ob;
		prob=oProb;
		next=-1;
	}
}