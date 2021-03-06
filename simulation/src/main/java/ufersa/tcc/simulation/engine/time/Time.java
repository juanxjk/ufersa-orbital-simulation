package ufersa.tcc.simulation.engine.time;

public class Time {

	public final static long ONE_MILLISECOND = 1;
	public final static long MILLISECONDS_IN_A_SECOND = 1000;

	public final static long ONE_SECOND = 1000;
	public final static long SECONDS_IN_A_MINUTE = 60;

	public final static long ONE_MINUTE = ONE_SECOND * 60;
	public final static long MINUTES_IN_AN_HOUR = 60;

	public final static long ONE_HOUR = ONE_MINUTE * 60;
	public final static long HOURS_IN_A_DAY = 24;
	public final static long ONE_DAY = ONE_HOUR * 24;
	public final static long DAYS_IN_A_YEAR = 365;

	/** [s] Tempo total percorrido */
	private double time = 0;
	/** [s] Default: 1 segundo por iteração. */
	public static double dt = 1f;

	public Time(double dt) {
		Time.dt = dt;
	}

	/**
	 * Avança um passo delta de tempo em segundos.
	 */
	public void step() {
		time += dt;
	}

	public static String formatHMSM(double n) {

		String res = "";
		double duration = n * 1_000;

		duration /= ONE_MILLISECOND;
		int milliseconds = (int) (duration % MILLISECONDS_IN_A_SECOND);
		duration /= ONE_SECOND;
		int seconds = (int) (duration % SECONDS_IN_A_MINUTE);
		duration /= SECONDS_IN_A_MINUTE;
		int minutes = (int) (duration % MINUTES_IN_AN_HOUR);
		duration /= MINUTES_IN_AN_HOUR;
		int hours = (int) (duration % HOURS_IN_A_DAY);
		duration /= HOURS_IN_A_DAY;
		int days = (int) (duration % DAYS_IN_A_YEAR);
		duration /= DAYS_IN_A_YEAR;
		int years = (int) (duration);
		if (days == 0) {
			res = String.format("%02dh%02dm%02ds%03dms", hours, minutes, seconds, milliseconds);
		} else if (years == 0) {
			res = String.format("%ddays %02dh%02dm%02ds%03dms", days, hours, minutes, seconds, milliseconds);
		} else {
			res = String.format("%dyrs %ddays %02dh%02dm%02ds", years, days, hours, minutes, seconds);
		}
		return res;
	}

	public double getTotalTime() {
		return time;
	}

	public void setTotalTime(double time) {
		if (time < 0) {
			throw new IllegalArgumentException("Tempo negativo");
		}
		this.time = time;
	}

	@Override
	public String toString() {
		return formatHMSM(this.time);
	}

}
