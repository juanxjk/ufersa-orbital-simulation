/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 National Aeronautics and Space Administration and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can
 * redistribute it and/or modify it under the terms of the
 * NASA Open Source Agreement
 * 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NASA Open Source Agreement for more details.
 *
 * You should have received a copy of the NASA Open Source Agreement
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package jat.examples.jat3D.Copernicus1;

import jat.core.ephemeris.DE405Plus;
import jat.coreNOSA.cm.cm;
import jat.coreNOSA.util.FileUtil;
import jat.jat3D.CapturingCanvas3D;
import jat.jat3D.Colors;
import jat.jat3D.Orbit;
import jat.jat3D.Planet3D;
import jat.jat3D.RGBAxes3D;
import jat.jat3D.jat_behavior;
import jat.jat3D.jat_light;
import jat.jat3D.jat_view;
import jat.jat3D.loader.ThreeDStudioObject;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Locale;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.swing.Timer;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.applet.MainFrame;

/**
 *
 * @author Tobias Berthold
 * @version 1.0
 */
/*
public class MarsOrbit extends Applet implements ActionListener
{
	private static final long serialVersionUID = 3274233655698395964L;
	BranchGroup BG_root;
	BranchGroup BG_vp;
	TransformGroup TG_scene;
	ThreeDStudioObject carrier;
	Planet3D earth, mars;
	RGBAxes3D mainaxis;
	Orbit carrierorbit = null;
	Point3d origin = new Point3d(0.0f, 0.0f, 0.0f);
	BoundingSphere bounds = new BoundingSphere(origin, 1.e10); //100000000.0
	ControlPanel panel;
	public CapturingCanvas3D c;
	Timer animControl;
	double alpha;
	int i = 0;
	int steps = 8500;

	public MarsOrbit()
	{
		// Get path of this class, frames will be saved in subdirectory frames
		String b = FileUtil.getClassFilePath("jat.demo.vr.Copernicus1", "MarsOrbit");
		System.out.println(b);

		//Applet window
		setLayout(new BorderLayout());
		c = createCanvas(b + "frames/");
		add("Center", c);
		panel = new ControlPanel(BG_root);
		add("South", panel);

		// 3D Objects
		BG_root = new BranchGroup();
		BG_root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		BG_root.setBounds(bounds);
		TG_scene = new TransformGroup();
		//TG_scene.addChild(earth = new Planet3D(this, Planet3D.EARTH));
		TG_scene.addChild(mars = new Planet3D( DE405Plus.body.MARS,1));
		TG_scene.addChild(mainaxis = new RGBAxes3D(4000.0f));
		TG_scene.addChild(carrier = new ThreeDStudioObject( "carrier.3DS", 1.f));
		carrier.set_attitude(0. * Math.PI, 0.0 * Math.PI, 0.1 * Math.PI);
		//carrier.set_position(cm.earth_radius + 1000., -2000., 1000.);
		BG_root.addChild(TG_scene);
		BG_root.addChild(carrierorbit = new Orbit(cm.mars_radius + 500., 0.02, 5.0, 0.0, 0.0, 0.0, Colors.pink, steps));
		BG_root.addChild(carrierorbit = new Orbit(8000., 0.02, -45.0, 0.0, 0.0, 300.0, Colors.pink, steps));

		// Lights
		BG_root.addChild(jat_light.DirectionalLight(bounds));
		jat_light.setDirection(0.f, 1.f, -0.5f);
		//BG_root.addChild(jat_light.AmbientLight(bounds));

		// View
		//BG_vp = jat_view.view(5418., -5239., 5304., c, 1.f, 100000.f);
		BG_vp = jat_view.view(5418., -5239., 5304., c, 1.f, 100000.f);

		// Behaviors
		jat_behavior.behavior(BG_root, BG_vp, bounds);
		jat_behavior.xyz_Behavior.set_translate(100.f);

		VirtualUniverse universe = new VirtualUniverse();
		Locale locale = new Locale(universe);
		locale.addBranchGraph(BG_root);
		locale.addBranchGraph(BG_vp);

		// Have Java 3D perform optimizations on this scene graph.
		//BG_root.compile();

		// Use Timer for animation
		int delayValue = 50; // milliseconds
		animControl = new Timer(delayValue, this);
		animControl.start();
	}

	// This method is called each time a timer event occurs
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("" + i);
		i++;
		if (i > steps)
			i = 0;

		if (i < 300)
		{
			alpha += 0.001;
			mars.set_attitude(Math.PI / 2., alpha, 0);

			Vector3d V_view = new Vector3d(0.f, 0.f, 0.0f);
			Transform3D T3D = new Transform3D();
			jat_view.TG_vp.getTransform(T3D);
			T3D.get(V_view);
			panel.label.setText("  x " + (long)V_view.x + "  y " + (long)V_view.y + "  z " + (long)V_view.z);

			//jat_view.set_view_position(0.,50.-alpha*10,50.);
			//jat_view.set_view_direction(origin);
			//panel.label.setText(i + "  Time " + (long)orb1.t[i] + "  x " + (long)orb1.x[i] + "  y " + (long)orb1.y[i]);
			//		panel.label.setText(i + "  Time " + (long)orb1.t[i] + "  x " + (long)orb1.x[i] + "  y " + (long)orb1.y[i]);

			//carrier.set_attitude(alpha,alpha,0);
			carrier.set_position(carrierorbit.x[i], carrierorbit.y[i], carrierorbit.z[i]);
			jat_view.set_view_direction(carrierorbit.x[i], carrierorbit.y[i], carrierorbit.z[i]);

			// Take frame screenshot
			try
			{
				Thread.sleep(10);
				//System.out.println("waiting..");
			} catch (Exception f)	{	};
			//c.takePicture();					
		}
	}

	private CapturingCanvas3D createCanvas(String frames_path)
	{
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		GraphicsConfiguration gc1 =
			GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
		return new CapturingCanvas3D(gc1, frames_path);
	}

	public void init()
	{
	}

	public static void main(String[] args)
	{
		MarsOrbit sh = new MarsOrbit(); // Applet
		MainFrame m = new MainFrame(sh, 800, 600);
		m.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
	}
}
*/