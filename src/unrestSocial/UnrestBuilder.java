package unrestSocial;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
//import repast.simphony.parameter.Parameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;

import unrestSocial.Region;

public class UnrestBuilder implements ContextBuilder<Object>{
	public double neighborhood_size;
	public int window_size;
	public double window;
	public int currentstep;
	// Tamil Nadu - 32, Andhra Pradesh - 13, Himachal Pradesh - 12
	public int region_num = 12;
	public int run_index;

	@Override
	public Context<Object> build(Context<Object> context) {
		// TODO Auto-generated method stub
		repast.simphony.parameter.Parameters params = RunEnvironment.getInstance().getParameters();
		neighborhood_size= (double)params.getValue("neighborhood");
		window= (double)params.getValue("window")*10;
		
		window_size = (int)window;
		run_index = (int)params.getValue("randomSeed");
		
//	    should match with /UnrestSocial.rs/context.xml
		context.setId("UnrestSocial");
		
//		Create a Geography based context
		GeographyParameters<Object> geoparams = new GeographyParameters<Object>();
		GeographyFactory factory = GeographyFactoryFinder.createGeographyFactory(null);
		Geography<Object> geography = factory.createGeography("Geography", context, geoparams);
		GeometryFactory fac = new GeometryFactory();
		
		List<Region> newRegion = allRegion();
		for(Region reg: newRegion) {
			System.out.println(this.currentstep);
			context.add(reg);
			Coordinate coord = new Coordinate(reg.givelong(), reg.givelat());
			Point geom = fac.createPoint(coord);
			geography.move(reg, geom);
		}
//		Specify how long the simulation should run here (47 ticks)
		RunEnvironment.getInstance().endAt(47);
		
		return context;
	}	
	public List<Region> allRegion(){
		List<Region> regions = new ArrayList<Region>();
		for(int i=0; i<region_num; i++){
			System.out.println(i);
			Region r1 = new Region(i, this.currentstep, this.neighborhood_size, this.window_size, this.run_index);
//			Region r2 = new Region(i, this.currentstep+1, this.neighborhood_size, this.window_size);
			regions.add(r1);
//			regions.add(r2);
		}
//		this.moveDate();
		return regions;
	}
	
//	move the current day in the context forward
//	@ScheduledMethod(start=1, interval=1, priority = 5)
//	public void moveDate() {
//		System.out.println("increase time step");
//		this.currentstep = this.currentstep+1;
//	}
}
