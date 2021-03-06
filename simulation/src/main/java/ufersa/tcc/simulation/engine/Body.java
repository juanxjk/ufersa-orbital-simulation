package ufersa.tcc.simulation.engine;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import ufersa.tcc.simulation.Simulation;
import ufersa.tcc.simulation.engine.time.Time;

public class Body {
	public double x, y; // [m]
	public double mass; // [kg]
	public double vx, vy; // [m/s]
	public double radius = 10e3; // [m] Tamanho padrão do objeto

	private PApplet screen;
	private int[] rgb = { 125, 125, 125 };
	private String bodyName = "Não identificado";
	public double maxDistance = 0;
	public double maxDistanceTime;
	public double maxDistanceVelX;
	public double maxDistanceVelY;
	public double maxDistance_Moon_Rocket_VectorX;
	public double maxDistance_Moon_Rocket_VectorY;

	public double minMoonDistance = Double.MAX_VALUE;
	public double minMoonDistanceTime;
	public double minMoonDistanceMoon_PosX;
	public double minMoonDistanceMoon_PosY;
	public double minMoonDistancePosX;
	public double minMoonDistancePosY;
	public double minMoonDistanceVelX;
	public double minMoonDistanceVelY;

	public double deltaV2X;
	public double deltaV2Y;
	public double minDeltaV2 = Double.MAX_VALUE;
	public double minDeltaV2X;
	public double minDeltaV2Y;
	public double minDeltaV2YTime;

	// Trails
	LinkedList<PointXY> trail = new LinkedList<PointXY>();
	int maxTrailLength = 3000;

	public final static double G = 6.67408e-11; // m^3 kg^-1 s^-2

	public static double calcOrbitalRadiusFrom(double orbitalPeriod, Body b) {
		return Math.pow(Body.G * b.mass * Math.pow(orbitalPeriod, 2) / (4 * Math.pow(Math.PI, 2)), (1.0 / 3));
	}

	/**
	 * @param screen
	 *            Tela de display.
	 * @param x
	 *            posição x.
	 * @param y
	 *            posição y.
	 * @param radius
	 *            Tamanho do raio do circulo.
	 */
	public Body(PApplet screen, double x, double y, double radius, double mass, double vx, double vy) {
		this.screen = screen;
		this.x = x; // [m]
		this.y = y; // [m]
		this.radius = radius; // [m]
		this.mass = mass; // [kg]
		this.vx = vx; // [m/s]
		this.vy = vy; // [m/s]
	}

	protected Body(Body body) {
		this(body.screen, body.x, body.y, body.radius, body.mass, body.vx, body.vy);

	}

	public double calcDistance(Body b) {
		return sqrt(pow(this.x - b.x, 2) + pow(this.y - b.y, 2));
	}

	public void interact(Body b, double t) {
		final double deltaX = b.x - this.x; // [m]
		final double deltaY = b.y - this.y; // [m]
		final double dist2 = pow(deltaX, 2) + pow(deltaY, 2); // [m^2]
		final double dist = sqrt(dist2); // [m]
		final double force = G * b.mass * this.mass / dist2; // [kg] * [m/s^2]
		final double accelX = force * deltaX / (dist * this.mass); // [m/s^2]
		final double accelY = force * deltaY / (dist * this.mass); // [m/s^2]
		this.vx += accelX * t; // [m/s]
		this.vy += accelY * t; // [m/s]
		// this.x += vx * t; // [m]
		// this.y += vy * t; // [m]
	}

	public void draw() {
		screen.translate(screen.width / 2, screen.height / 2);
		screen.fill(rgb[0], rgb[1], rgb[2], 200);
		float posx = (float) (Simulation.screenScale * this.x);
		float posy = (float) (Simulation.screenScale * this.y);
		float lengthx = (float) (this.radius * Simulation.screenObjectScale);
		float lengthy = (float) (this.radius * Simulation.screenObjectScale);
		this.updateTrail(this.x, this.y);
		showTrail(posx, posy);
		screen.ellipse(posx, posy, lengthx, lengthy);
		screen.translate(-screen.width / 2, -screen.height / 2);
	}

	protected void updateTrail(double x, double y) {
		PointXY newPoint = new PointXY((float) x, (float) y);
		if (screen.isLooping())
			trail.addFirst(newPoint);
		while (trail.size() > maxTrailLength)
			trail.removeLast();
	}

	private void showTrail(float x, float y) {
		screen.stroke(75);
		if (trail.size() >= 2) {
			PointXY currPoint, lastPoint = trail.get(0);
			for (int i = 0; i < trail.size(); i++) {
				currPoint = trail.get(i);
				float lastX = (float) (lastPoint.x * Simulation.screenScale);
				float lastY = (float) (lastPoint.y * Simulation.screenScale);
				float currX = (float) (currPoint.x * Simulation.screenScale);
				float currY = (float) (currPoint.y * Simulation.screenScale);
				screen.line(lastX, lastY, currX, currY);
				lastPoint = currPoint;
			}
		}

		screen.stroke(0);
	}

	private class PointXY {
		public float x, y;

		public PointXY(float px, float py) {
			x = px;
			y = py;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PointXY) {
				if (((PointXY) obj).x == this.x && ((PointXY) obj).y == this.y)
					return true;
				else
					return false;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return "VelX" + this.vx;
	}

	public void showInfo(World w, double x, double y) {
		int line;
		List<String> strings = new ArrayList<>();
		strings.add(this.getName());
		strings.add(String.format("Position: (%g, %g) [km]", this.x * 1e-3, this.y * 1e-3));
		strings.add(String.format("Mass: %g [km]", this.mass));
		strings.add(String.format("Radius: %g [km]", this.radius * 1e-3));
		strings.add(String.format("Speed: %g (%.3f, %.3f) [m/s]", this.getSpeed(), this.vx, this.vy));
		strings.add(String.format("Max earth's distance: %.3f) [km]", this.maxDistance * 1e-3));
		strings.add(String.format("Max earth's distance time: %.3f) [s]", this.maxDistanceTime));
		strings.add(String.format("Max earth's distance vel: (%.3f, %.3f) [m/s]", this.maxDistanceVelX,
				this.maxDistanceVelY));
		strings.add(String.format("Max earth's distance Moon-Rocket Vector: (%.3f, %.3f) [km]",
				this.maxDistance_Moon_Rocket_VectorX * 1e-3, this.maxDistance_Moon_Rocket_VectorY * 1e-3));

		strings.add(String.format("Min Moon's distance: %.3f) [km]", this.minMoonDistance * 1e-3));
		strings.add(String.format("Min Moon's distance time: %.3f) [s]", this.minMoonDistanceTime));
		strings.add(String.format("Min Moon's distance - Moon Pos : (%.3f, %.3f) [m]", this.minMoonDistanceMoon_PosX,
				this.minMoonDistanceMoon_PosY));
		strings.add(String.format("Min Moon's distance pos: (%.3f, %.3f) [m]", this.minMoonDistancePosX,
				this.minMoonDistancePosY));
		strings.add(String.format("Min Moon's distance vel: (%.3f, %.3f) [m/s]", this.minMoonDistanceVelX,
				this.minMoonDistanceVelY));
		strings.add(String.format("Delta V2: (%.3f, %.3f) [m/s]", this.deltaV2X, this.deltaV2Y));
		strings.add(String.format("Min Delta V2: %.3f (%.3f, %.3f) [m/s]", this.minDeltaV2, this.minDeltaV2X,
				this.minDeltaV2Y));
		strings.add(String.format("Min Delta V2 Time: %.3f [s]", this.minDeltaV2YTime));

		for (Body b : w.getBodies()) {
			if (!this.equals(b)) {
				strings.add(String.format("Distance to %s: %g [km] ", b.getName(), this.calcDistance(b) * 1e-3));
			}
		}
		String t = "";
		for (String s : strings) {
			t += "\n" + s;
		}
		screen.text(t, (float) x, (float) y);
	}

	public double getSpeed() {
		return sqrt(pow(this.vx, 2) + pow(this.vy, 2));
	}

	public void setName(String name) {
		this.bodyName = name;
	}

	public String getName() {
		return this.bodyName;
	}

	public void setPercentageVelocity(double percentage) {
		double _percent = percentage / 100;
		this.vx *= _percent;
		this.vy *= _percent;
	}

	public double getDistanceFromCenter() {
		return sqrt(pow(this.x, 2) + pow(this.y, 2));
	}

}
