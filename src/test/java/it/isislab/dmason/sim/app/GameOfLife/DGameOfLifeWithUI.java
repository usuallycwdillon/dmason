package it.isislab.dmason.sim.app.GameOfLife;

import java.awt.Color;

import javax.swing.JFrame;

import it.isislab.dmason.experimentals.tools.batch.data.GeneralParam;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.grid.FastValueGridPortrayal2D;

public class DGameOfLifeWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;
	public static String name;

	public static String topicPrefix = "";

	@Override
	public Object getSimulationInspectedObject() { return state; }  // non-volatile

    FastValueGridPortrayal2D gridPortrayal = new FastValueGridPortrayal2D();


	public DGameOfLifeWithUI(GeneralParam args) 
	{ 
		super(new DGameOfLife(args));

		name=String.valueOf(args.getI())+""+(String.valueOf(args.getJ()));
	}

	public static String getName() { return "Peer: <"+name+">"; }

	@Override
	public void start()
	{
		super.start();
		setupPortrayals();
	}

	@Override
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals()
	{
		DGameOfLife gol = (DGameOfLife)state;

		gridPortrayal.setField(gol.grid);
		gridPortrayal.setMap(
		            new sim.util.gui.SimpleColorMap(
		                new Color[] {new Color(0,0,0,0), Color.blue}));

		gol.p = gridPortrayal;

		display.reset();

		display.repaint();
	}

	@Override
	public void init(Controller c)
	{
		super.init(c);

		// make the displayer
		display = new Display2D(600,600,this);
		display.setBackdrop(Color.black);

		displayFrame = display.createFrame();
		displayFrame.setTitle("Game Of Life");
		c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		display.attach( gridPortrayal, "Life" );
	}

	@Override
	public void quit()
	{
		super.quit();

		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}
}
