package techguns.client.particle;

import techguns.client.ClientProxy;

/**
 * Dynamic light pulse (gun shots, explosions).
 *
 * 1.12 provided the light to the optional Albedo mod (provideLight()). There is no dynamic light API in Forge 1.20.1,
 * so the pulses are only simulated (same API and lifetime), they don't light the world. getRadius/getProgress return the
 * values 1.12 handed to Albedo, for a future light provider.
 */
public class LightPulse {

	private int ttl;
	protected int lifetime;
	protected int fade_in;

	protected double x;
	protected double y;
	protected double z;

	protected float rad_start;
	protected float rad_end;

	protected float r;
	protected float g;
	protected float b;

	public LightPulse(double x, double y, double z, int fade_in, int fade_out, float rad_large, float rad_small, float r, float g, float b) {
		this.lifetime = fade_in+fade_out;
		this.ttl=lifetime;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rad_start = rad_large;
		this.rad_end = rad_small;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public LightPulse(double x, double y, double z, int lifetime, float rad_start, float rad_end, float r, float g, float b) {
		this.fade_in=0;
		this.lifetime = lifetime;
		this.ttl=lifetime;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rad_start = rad_start;
		this.rad_end = rad_end;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Return if still alive after this tick
	 * @return
	 */
	public boolean updateGameTick() {
		--ttl;
		return ttl>0;
	}

	/**
	 * Pulse progress (0-1) like 1.12 provideLight()
	 */
	public float getProgress(float ptt) {
		float existed = (lifetime-ttl)+ptt;

		float prog;
		if (existed<this.fade_in) {
			prog = (existed)/((float)(fade_in));
		} else {
			prog = ((existed-fade_in))/((float)(lifetime-fade_in));
		}
		return prog;
	}

	/**
	 * Current light radius, 1.12 provideLight(): Light.builder().radius(...)
	 */
	public float getRadius() {
		float prog = getProgress(ClientProxy.get().PARTIAL_TICK_TIME);
		float inv_prog = 1f-prog;
		return rad_end*prog+rad_start*inv_prog;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getR() {
		return r;
	}

	public float getG() {
		return g;
	}

	public float getB() {
		return b;
	}
}
