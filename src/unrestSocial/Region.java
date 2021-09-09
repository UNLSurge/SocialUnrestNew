package unrestSocial;

import java.util.Arrays;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import unrestSocial.Probability;



public class Region {
	
	String region_name;
	int region_index;
	int marksize;
	int timestep;
	int prevstep;
	int window_size;
	int run_index;
	double lon;
	double lat;
	double neighborhood_size;
	double samestate;
	double changestate;
	
//	List<String> district = Arrays.asList("Ariyalur", "Chennai","Coimbatore","Cuddalore",
//			"Dharmapuri","Dindigul","Erode","Kanchipuram","Kanyakumari","Karur","Krishnagiri",
//			"Madurai","Nagapattinam","Namakkal","Nilgiris","Perambalur","Pudukkottai","Ramanathapuram",
//			"Salem","Sivaganga","Thanjavur","Theni","Thoothukudi","Tiruchirappalli","Tirunelveli",
//			"Tiruppur","Tiruvallur","Tiruvannamalai","Tiruvarur","Vellore","Viluppuram","Virudhunagar");
	
	
	
//	List<String> district = Arrays.asList("Anantapur", "Chittoor", "East-Godavari", "Guntur", "Kadapa", 
//			"Krishna", "Kurnool", "Nellore", "Prakasam", "Srikakulam", "Visakhapatnam", "Vizianagaram", "West-Godavari");
	
	
	
	List<String> district = Arrays.asList("Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu", 
			"Lahul-Spiti", "Mandi", "Shimla", "Sirmaur", "Solan", "Una");
	
	String district_name;
	String path1 = "./data/hp/latlong.csv";
	
	CsvParser cr = new CsvParser(path1);
	List<List<Double>> data1 = cr.csvReader();
	
	private String region_current_state;
	private String region_next_state;
	
//	private double same_sate_proba;
//	private double state_change_proba;
	
	private double state_color;
	
//	public double latitude;
//	public double longitude;
	
	public Region(int region, 
			int timestep,
			double neighborhood_size,
			int window_size, int run_index) {
		this.region_index = region;
		this.timestep = timestep;
		this.neighborhood_size = neighborhood_size;
		this.window_size = window_size;
		this.run_index = run_index;
	}
	
	public double givelat() {
		return this.data1.get(region_index).get(0);
	}
	
	public double givelong() {
		return this.data1.get(region_index).get(1);
	}
	
	public int givedatasize() {
		return data1.size();
	}
	
	//-----------get methods------------//x
	
	public String getNextstate() {
		return this.region_next_state;
	}
	
	public String getCurrentstate() {
		return this.region_current_state;
	}
	
	public double getState_color() {
		return state_color;
	}
	
	public double getSamestate() {
		return this.samestate;
	}
	
	public double getChangestate() {
		return this.changestate;
	}
	
	public String getDistrictname() {
		return this.district_name;
	}
	
	public int getTimestep() {
		return this.timestep;
	}
	
	public int getRegion() {
		return this.region_index;
	}
	
	public int getRunIndex() {
		return this.run_index;
	}
	
	//-----------set methods-------------//
	
	public void setNextstate(String a) {
		this.region_next_state = a;
	}
	
	public void setCurrentstate(String a) {
		this.region_current_state = a;
	}
	
	public void setStatecolor(double a) {
		this.state_color = a;
	}
	
	public void setState_color(double state_color) {
		this.state_color = state_color;
	}
	
	public void setSamestate(double same) {
		this.samestate = same;
	}
	
	public void setChangestate(double change) {
		this.changestate = change;
	}
	
	public void setDistrictname(String name) {
		this.district_name = name;
	}
	
	public void setCurrenttime(int time) {
		this.timestep = time;
	}
	
	public void setRunIndex(int run) {
		this.run_index = run;
	}
	
	@ScheduledMethod(start= 0, interval=1, priority=1)
	public void prevtime() {
		this.prevstep = getTimestep();
	}
	
	
	@ScheduledMethod(start= 0, interval=1, priority=1)
	public void time() {
		this.timestep = this.prevstep +1;
	}
	
	
	@ScheduledMethod(start = 0, interval=1, priority=1)
	public void next() {
		Probability pregion = new Probability(this.region_index, this.timestep, this.window_size, this.neighborhood_size, this.run_index);
		setSamestate(pregion.calcProbability().get(0));
		setChangestate(pregion.calcProbability().get(1));
		setNextstate(pregion.nextState());
	}
	
	@ScheduledMethod(start = 0, interval=1, priority=5)
	public void current() {
		Determinestate dregion = new Determinestate(this.region_index, this.timestep);
		setCurrentstate(dregion.regionCurrentstate());
	}
	
	@ScheduledMethod(start = 0, interval=1, priority=5)
	public void district() {
		setDistrictname(district.get(getRegion()));
	}
	
	@ScheduledMethod(start=0, interval=1, priority=3)
	public void calculate() {
		String state = getNextstate();
		if (state == "R") {
			setState_color(12);
		}
		if (state == "S") {
			setState_color(2);
		}
		if (state == "I") {
			setState_color(8);
		}
	}
}
