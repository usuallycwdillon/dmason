package it.isislab.dmason.sim.app.openAB.Circle;

import java.awt.Color;
import java.util.List;

import it.isislab.dmason.exception.DMasonException;
import it.isislab.dmason.experimentals.tools.batch.data.EntryParam;
import it.isislab.dmason.experimentals.tools.batch.data.GeneralParam;
import it.isislab.dmason.sim.engine.DistributedMultiSchedule;
import it.isislab.dmason.sim.engine.DistributedState;
import it.isislab.dmason.sim.engine.RemotePositionedAgent;
import it.isislab.dmason.sim.field.DistributedField2D;
import it.isislab.dmason.sim.field.continuous.DContinuousGrid2D;
import it.isislab.dmason.sim.field.continuous.DContinuousGrid2DFactory;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.util.Double2D;

public class DCircles extends DistributedState<Double2D> {

	private static final long serialVersionUID = 1L;
	/*public static final double XMIN = 0;
	public static final double XMAX = 200;
	public static final double YMIN = 0;
	public static final double YMAX = 200;*/

	public static final double DIAMETER = 4;

	public DContinuousGrid2D circles = null;

	public static String topicPrefix = "";
	
	public double gridWidth ;
	public double gridHeight ;   
	public int MODE;
	
	public ContinuousPortrayal2D p;
	
	public DCircles() { super();}
	
	public DCircles(GeneralParam params) {
		super(params, new DistributedMultiSchedule<Double2D>(),topicPrefix,params.getConnectionType());
		this.MODE=params.getMode();
		gridWidth=params.getWidth();
		gridHeight=params.getHeight();
	}

	public DCircles(GeneralParam params,List<EntryParam<String, Object>> simParams, String prefix)
	{
		super(params,new DistributedMultiSchedule<Double2D>(), prefix,params.getConnectionType());
		this.MODE=params.getMode();
		gridWidth=params.getWidth();
		gridHeight=params.getHeight();
		topicPrefix = prefix; 
		for (EntryParam<String, Object> entryParam : simParams) {

			try {
				this.getClass().getDeclaredField(entryParam.getParamName()).set(this, entryParam.getParamValue());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		for (EntryParam<String, Object> entryParam : simParams) {

			try {
				System.out.println(this.getClass().getDeclaredField(entryParam.getParamName()).get(this));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	@Override
	public void start()
	{
		super.start();
		
		try 
		{
			circles = DContinuousGrid2DFactory.createDContinuous2D(8.0,gridWidth, gridHeight,this,
					super.AOI,TYPE.pos_i,TYPE.pos_j,super.rows,super.columns,MODE,"circles", topicPrefix,true);
			init_connection();
		} catch (DMasonException e) { e.printStackTrace(); }
		
		DCircle f=new DCircle(this,new Double2D(0,0));

		while(circles.size() != super.NUMAGENTS / super.NUMPEERS)
		{
			f.setPos(circles.getAvailableRandomLocation());

			if(circles.setObjectLocation(f, f.pos))
			{
				Color c=new Color(0,0,0);/*
						this.random.nextInt(255),
						this.random.nextInt(255),
						this.random.nextInt(255));*/
				f.setColor(c);
				schedule.scheduleOnce(f);
				f= new DCircle(this,new Double2D(0,0));
			}
		}
		
	}
	@Override
	public DistributedField2D getField() {
		// TODO Auto-generated method stub
		return circles;
	}

	@Override
	public void addToField(RemotePositionedAgent rm, Double2D loc) {
		circles.setObjectLocation(rm, loc);
	}

	@Override
	public SimState getState() {
		// TODO Auto-generated method stub
		return this;
	}

	
	public static void main(String[] args)
	{
		doLoop(DCircles.class, args);
		System.exit(0);
	}
}