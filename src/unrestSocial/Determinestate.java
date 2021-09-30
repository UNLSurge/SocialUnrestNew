package unrestSocial;

import java.util.List;
import java.util.ArrayList;

import unrestSocial.CsvParser;
import unrestSocial.Neighbor;

/*
This class determines the current state and list of Susceptible, Infected and Recovered
states at current time step and the list of neighbors which are in Susceptible, Infected and
Recovered state at current time step. 

*/

public class Determinestate {
	
	public Determinestate() {
		
	}
	
	CsvParser c1 = new CsvParser();
	List<List<Double>> data = c1.csvReader();
	
	
	int regions;
	int current;
	
	public Determinestate(int r, int n) {
		this.regions = r;
		this.current = n;
	}
	

	/*
	 * method to find the event count for each time step in the given region
	 * takes the region number(index) as the argument
	 * returns the list of event counts in that region for all time step
	 * 
	 */
	public List<Double> giveEventCount(int r){
		int t = 48;
		List<Double> region = new ArrayList<>();
		for(int i=0; i<t; i++) {
			int index = (r)*t + (i);
			double eventcount = this.data.get(index).get(6);
			region.add(eventcount);
		}
		return region;	
	}
	
	/*
	 * method which assigns "SIR" to each time step of the region
	 * if there are events the region is in infected state
	 * if there are no events the region is either susceptible or
	 * recovered
	 * takes a list of event counts as argument
	 * returns the list of state
	 */
	public List<String> giveState(int r){
		List<Double> reg = new ArrayList<>();
		reg = this.giveEventCount(r);
		List<String> regstate = new ArrayList<>();
		
		for (Double i: reg) {
			if(i == 0) {
				regstate.add("S");
			}
			else {
				regstate.add("I");
			}
		}
		return regstate;
	}
	
	/*
	 * method to give the full list of state of each region for all time step
	 */
	public List<List<String>> giveFullstate(int num_r){
		List<List<String>> fullstate = new ArrayList<>();
		List<String> state = new ArrayList<>();
		for(int i=0; i<num_r; i++) {
			state = giveState(i);
			System.out.print(i + " : ");
			System.out.println(state);
			fullstate.add(state);
		}
		return fullstate;
		
	}
	
	/*
	 * method to get a list of regions which are in each state
	 * takes number of regions as argument and time stamp as an argument.
	 * return the list of region in three state
	 */
	public List<List<Integer>> determineStatelist(){
		
		List<List<String>> fullstate = new ArrayList<>();
		
		fullstate = giveFullstate(32); // Tamil Nadu
//		fullstate = giveFullstate(12); // Himachal Pradesh
//		fullstate = giveFullstate(13); // Andhra Pradesh 
		List<List<Integer>> state = new ArrayList<>();
		List<Integer> stateS = new ArrayList<>();
		List<Integer> stateI = new ArrayList<>();
		
		for(int i=0; i<fullstate.size(); i++) {
			if(fullstate.get(i).get(this.current) == "I") {
				stateI.add(i);
			}
			else {
				stateS.add(i);
			}
		}
		state.add(stateS);
		state.add(stateI);
		
		return state;
	}
	
	/*
	 * method which finds the intersection between the list of region which are
	 * in each state (SIR) and the actual neighbors of region.
	 * 
	 * This method takes the neighborsize as input and returns the list of list
	 * of neighbor in each state.
	 * 
	 */
	
	public List<List<Integer>> neighborStatelist(double neighborsize){
		Neighbor nb = new Neighbor(this.regions, this.current,  neighborsize);
		List<Integer> neigh = new ArrayList<>();
		neigh = nb.findNeighbor();
		List<List<Integer>> state = new ArrayList<>();
		List<List<Integer>> neighstate = new ArrayList<>();
		List<Integer> neighS = new ArrayList<>();
		List<Integer> neighI = new ArrayList<>();
		
		state = determineStatelist();
		for(int i=0; i<neigh.size(); i++) {
			int current = neigh.get(i);
			if(state.get(0).contains(current)){
				neighS.add(neigh.get(i));
			}
			if(state.get(1).contains(current)){
				neighI.add(neigh.get(i));
			}
		}
		neighstate.add(neighS);
		neighstate.add(neighI);
		
		return neighstate;
	}
	
	public List<List<Double>> neighborSharedBoundarylist(double ns){
		Neighbor nb = new Neighbor(this.regions, this.current,  ns);
		List<Integer> neigh = new ArrayList<>();
		List<Double> percent = new ArrayList<>();
		neigh = nb.findNeighbor();
		percent = nb.getInfluence();
		
//		System.out.println(neigh.size());
//		System.out.println(neigh);
//
//		System.out.println(percent.size());
//		System.out.println(percent);
		
		List<List<Integer>> state = new ArrayList<>();
		List<List<Double>> sharedpercent = new ArrayList<>();
		List<Double> sharedS = new ArrayList<>();
		List<Double> sharedI = new ArrayList<>();
		
		state = determineStatelist();
//		System.out.println(state.get(0));
		for(int i=0; i<neigh.size(); i++) {
			int current = neigh.get(i);
			if(state.get(0).contains(current)){
				sharedS.add(percent.get(i));
			}
			if(state.get(1).contains(current)){
				sharedI.add(percent.get(i));
			}
		}
		sharedpercent.add(sharedS);
		sharedpercent.add(sharedI);
		
		return sharedpercent;
	}
	
	/*
	 * method to get a the current state the region is in.
	 * 
	 */
	public String regionCurrentstate() {
		String str = new String();
		List<List<Integer>> statelist = new ArrayList<>();
		statelist = determineStatelist();
		for(int i=0; i<statelist.get(0).size(); i++) {
//			System.out.println(statelist.get(0).get(i));
			if(statelist.get(0).get(i) == this.regions) {
				str = "S";
			}	
		}
		for(int i=0; i<statelist.get(1).size(); i++) {
			if(statelist.get(1).get(i) == this.regions) {
				str = "I";
			}	
		}
		return str;
	}
	
	
//	// Main Method - used for debugging
	public static void main(String[] args) {
		Determinestate d1 = new Determinestate(10, 0);
//		System.out.println(d1.determineStatelist());
//		System.out.println(d1.giveFullstate(32));
		d1.giveFullstate(32);
//		System.out.println(d1.neighborStatelist(0.5));
//		System.out.println(d1.regionCurrentstate());

		
//		for (Integer i: d1.neighborStatelist(0.5).get(1)) {
//			System.out.println(i);
//
//		}
//		System.out.println(d1.neighborStatelist(0.5).get(1));
//		System.out.println(d1.giveFullstate(3));
//		d1.giveState(0);
//		System.out.println(d1.giveState(1));
//		System.out.println(d1.giveEventCount(0));
	}
}

