package unrestSocial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import unrestSocial.CsvParser;
//import java.util.ArrayList;

public class Tsim {

	int i;
	int j;
	int n;
	
	public static List<List<Double>> prevtsim = new ArrayList<>();
	
	public Tsim() {
		
	}
	
	CsvParser c1 = new CsvParser();
	List<List<Double>> data = c1.csvReader();
	
	public Tsim(int i, int j, int n) {
		this.i = i;
		this.j= j;
		this.n= n;
			
	}

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
	public double aplha(int i,int n) {
		double sim;
//		if(n != 0) {
//			List<Double> l1 = getVector(i, n);
//			List<Double> l2 = getVector(i, n-1);
//			double now = get_intensity(i, n);
//			double prev = get_intensity(i, n-1);
//			
//			if(now == 0 && prev == 0) {
//				sim = 0.5;
//			}
//			else if (now != 0 && prev != 0) {
//				if(prev > now) {
//					sim = 0.5;
//				}
//				else {
//					sim = cosine_sim(l1,l2);
//				}
//			}
//			else {
//				sim = 0.5;
//			}
//		}
//		else {
////			System.out.println("this");
//			sim = 0.5;
//		}
//		System.out.println(sim);
		sim = 0.4; 
		return sim;
	}
	
	public double similarity(int i, int j,int n) {
//		System.out.println(i + " " + " " + j + " " + n);
		try {
			List<Double> l1 = getVector(i,n);
			List<Double> l2 = getVector(j,n);
			Double sim = cosine_sim(l1,l2);
			return sim;
		} catch (ArrayIndexOutOfBoundsException exception) {
			return 0;
		}
	}
	
	public  double calc_t() {

		double alpha;
		double sim;
		double t;
		double previous_t;
		
		alpha = aplha(this.i,this.n);
		sim = similarity(this.i,this.j,this.n);
		
		if (this.n<=2) {
			previous_t = (1-alpha)*sim;
		}
		else {
	//		previous_t = new Tsim(this.i,this.j,this.n-1).calc_t();
			previous_t = getPrevtsim(this.i, this.j, this.n-1);
//			System.out.println(prevtsim);
//			System.out.println(previous_t);
		}
		
		t = (alpha * previous_t + (1 - alpha) *sim);
		
		setPrevtsim(i, j, n, t);
		
//		prevtsim = removeDuplicates(prevtsim);
		return t;
	
	}
	
	public double getPrevtsim(int i, int j, int n) {
		double t = 0;
		for(int k=0; k<prevtsim.size(); k++) {
			if(prevtsim.get(k).get(0) == (double)i && prevtsim.get(k).get(1) == (double)j && prevtsim.get(k).get(2) == (double)n) {
				t = prevtsim.get(k).get(3);
			}
		}
		return t;
	}
	
	public void setPrevtsim(int i, int j, int n, double tsim) {
		List<Double> plist = Arrays.asList((double)i, (double)j, (double)n, tsim);
		prevtsim.add(plist);
	}
	
	public List<List<Double>> removeDuplicates(List<List<Double>> list) 
    { 
		List<List<Double>> newlist = new ArrayList<>(); 
        for (List<Double> l: list) { 
            if (!newlist.contains(l)) { 
                newlist.add(l); 
            } 
        } 

        return newlist; 
    } 
	
}
