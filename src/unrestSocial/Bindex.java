package unrestSocial;

import java.util.List;
import unrestSocial.CsvParser;

/*
	Bindex class calculates the bij value for each agent, bij is the unrest transmission
	probability of each region. The value of bij is dependent on the region intensity
	If the region intensity is zero the value returned by this class is also zero.

*/
public class Bindex {
	int i,j,n;
	
	CsvParser cobj = new CsvParser();
	
	List<List<Double>> data= cobj.csvReader();
	
	//constructor with region i and region j index and time step as the input
	public Bindex(int i, int j, int n) {
		this.i = i;
		this.j = j;
		this.n = n;	
	}
	
	//method to get the vector of region i in time n
	public List<Double> getVector(int i, int n){
		int t = 48;
		int index = (i)*t + (n);
//		System.out.println("for i: "+ i + " for n: " + n + " index: " + index);
		if(index < 0) {
			return this.data.get(0).subList(1, 6);
		}
		else {
			return this.data.get(index).subList(1, 6);
		}
	}
	//method to calculate cosine similarity between two vectors
	public double cosine_sim(List<Double>l1, List<Double> l2) {
			
			double dotProduct = 0;
			double norm1 = 0;
			double norm2 = 0;
			
			for(int i=0; i< l1.size(); i++) {
				
				dotProduct += l1.get(i) * l2.get(i);
				norm1 += Math.pow(l1.get(i), 2);
				norm2 += Math.pow(l2.get(i), 2);						
				
			}	
			
			return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
	}
	
	// method to get the region intensity at time step n
	public double get_intensity(int i, int n) {
		int t = 48;
		int index = (i)*t + (n);
		if(index < 0) {
			return this.data.get(0).get(7);
		}
		else {
			return this.data.get(index).get(7);
		}
	}
	
	//method to get similarity
	public double calc_sim(int i,int j, int n) {
		List<Double> l1 = getVector(i,n);
		List<Double> l2 = getVector(j,n);
		Double sim = cosine_sim(l1,l2);
		return sim;
		
	}
	
	//method to calculate bij
	public double calc_b() {
		Double sim = calc_sim(this.i, this.j, this.n);
		Double intensity = get_intensity(this.j,n);
		Double z = sim * intensity;
//		System.out.println(intensity);
		Double result = 1 / (1 + Math.exp(-z));
		return result;
	}
	
//	public static void main(String[] args) {
//		Bindex bex1 = new Bindex(0, 1, 0);
//		System.out.println(bex1.calc_b());
//		System.out.println(bex1.getVector(31, 0));
//	}
}
