package techguns.util;

import java.util.Random;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import techguns.Techguns;

public class MathUtil {
	
	public static final double D2R = Math.PI/180.0;
	public static final double R2D = 180.0/Math.PI;

	/**
	 * sin with argument in degrees (0-360)
	 * @param arg
	 * @return
	 */
    public static float sin360(float arg){
    	return Mth.sin(arg / 180.0F * (float)Math.PI);
    }
    /**
	 * cos with argument in degrees (0-360)
	 * @param arg
	 * @return
	 */
    public static float cos360(float arg){
    	return Mth.cos(arg / 180.0F * (float)Math.PI);
    }
	
	public static Vec2 polarOffsetXZ(float x, float z, float radius, float angle) {
    	x = (float) (x + (radius*Mth.cos(angle)));
    	z = (float) (z + (radius*Mth.sin(angle)));

    	return new Vec2(x, z);
    }
	
	public static float clamp(float value, float min, float max) {
		if (value>max) {
			return max;
		} else if (value<min) {
			return min;
		} else {
			return value;
		}
	}
	
	public static double clamp(double value, double min, double max) {
		if (value>max) {
			return max;
		} else if (value<min) {
			return min;
		} else {
			return value;
		}
	}
	
	public static int clamp(int value, int min, int max) {
		if (value>max) {
			return max;
		} else if (value<min) {
			return min;
		} else {
			return value;
		}
	}
	
	public static int randomInt(Random rand, int min, int max) {
    	if (max >= min)
    		return min+rand.nextInt((max-min)+1);
    	else
    		return max+rand.nextInt((min-max)+1);
    }
    
    public static float randomFloat(Random rand, float min, float max) {
    	return min+(rand.nextFloat()*(max-min));
    }
    
    public static Vec3 getInterpolatedEntityPos(Entity entityIn) {
    	double partialTicks = (double)Techguns.proxy.getPartialTickTime();
        double d0 = entityIn.xo + (entityIn.getX() - entityIn.xo) * partialTicks;
        double d1 = entityIn.yo + (entityIn.getY() - entityIn.yo) * partialTicks;
        double d2 = entityIn.zo + (entityIn.getZ() - entityIn.zo) * partialTicks;
        return new Vec3(d0,d1,d2);
    }
    
    /**
     * X and Y methods are Vec3.rotatePitch and Vec3.rotateYaw
     * @return
     */
    public static Vec3 rotateVec3AroundZ(Vec3 vec, float angle) {
        float f1 = Mth.cos(angle);
        float f2 = Mth.sin(angle);
        double d0 = vec.x * (double)f1 + vec.y * (double)f2;
        double d1 = vec.y * (double)f1 - vec.x * (double)f2;
        double d2 = vec.z;
        return new Vec3(d0,d1,d2);
    }
    
    /**
     * Return if all passed integers are withing bounds
     * @param lowerBound
     * @param upperBound
     * @param values
     * @return
     */
    public static boolean allInRange(int lowerBound, int upperBound, Integer... values) {
    	for(int i=0;i<values.length;i++) {
    		if(values[i]<lowerBound || values[i]> upperBound) {
    			return false;
    		}
    	}
    	
		return true;
    }
    
    public static int min(Integer... values) {
    	int min=Integer.MAX_VALUE;
    	for(int i=0;i<values.length;i++) {
    		if(values[i]<min) {
    			min=values[i];
    		}
    	}
    	
		return min;
    }
    
    public static int max(Integer... values) {
    	int max=Integer.MIN_VALUE;
    	for(int i=0;i<values.length;i++) {
    		if(values[i]>max) {
    			max=values[i];
    		}
    	}
    	
		return max;
    }
    
    /**
     * get average from multiple ints, rounded to next int.
     * @param values
     * @return
     */
    public static int getAverageHeight(Integer... values) {
    	int sum=0;
    	for(int i=0;i<values.length;i++) {
    		sum+=values[i];
    	}
		return (int) Math.round(sum/((double)values.length));
    }
    
    
    /**
     * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
     * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
     * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
     */
    public static float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks)
    {
        float f;

        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return prevYawOffset + partialTicks * f;
    }
    
    public static Vec2d polarOffsetXZ(double x, double z, double radius, double angle) {
    	x = x + (radius*Math.cos(angle));
    	z = z + (radius*Math.sin(angle));
    	
    	return new Vec2d(x, z);
    }
    
    public static class Vec2d{
    	public double x;
    	public double y;
    	
    	public Vec2d(double x, double y){
    		this.x=x;
    		this.y=y;
    	}
    	
    	/**
    	 * returns the squared vector length;
    	 * @return
    	 */
    	public double lenSquared(){
    		return x*x + y*y;
    	}
    	
    	public double len(){
    		return Math.sqrt(this.lenSquared());
    	}
    	
    	/**
    	 * normalizes THIS vector
    	 */
    	public void normalize(){
    		double len = this.len();
    		if(len >0){
	    		double f = 1/len;
				x=x*f;
				y=y*f;
    		}
    	}
    	
    	/**
    	 * Gets a new vec2 that is a normalized version of THIS
    	 * @return
    	 */
    	public Vec2d getNormalized(){
    		double len = this.len();
    		if(len >0){
	    		double f = 1/len;
				return new Vec2d(this.x*f, this.y*f);
    		}
    		return new Vec2d(0,0);
    	}
    	
    	/**
    	 * Returns Vector A - Vector B
    	 * @param A
    	 * @param B
    	 * @return
    	 */
    	public static Vec2d substract(Vec2d A, Vec2d B){
    		return new Vec2d(B.x-A.x, B.y-A.y);
    	}
    	
    	/**
    	 * Returns Vector A + Vector B
    	 * @param A
    	 * @param B
    	 * @return
    	 */
    	public static Vec2d add(Vec2d A, Vec2d B){
    		return new Vec2d(B.x+A.x, B.y+A.y);
    	}
    	
    	/**
    	 * get new vector from THIS position to endpoint position
    	 * @param endpoint
    	 * @return
    	 */
    	public Vec2d getVecTo(Vec2d endpoint){
    		return substract(endpoint, this);
    	}
    	
    	/**
    	 * get new vector from startpoint position to THIS position
    	 * @param endpoint
    	 * @return
    	 */
    	public Vec2d getVecFrom(Vec2d startpoint){
    		return substract(this,startpoint);
    	}
    	
    	/**
    	 * return dot product with other vector: this dot other
    	 * @param other
    	 * @return
    	 */
    	public double dot(Vec2d other){
    		return (this.x*other.x+this.y*other.y);
    	}
    	
		/**
		 * get a new vec2 rotated by degree
		 */
    	public Vec2d getRotated(float degree){
    		double ynew = y*cos360(degree) - x*sin360(degree);
    		double xnew = y*sin360(degree) + x*cos360(degree);
    		return new Vec2d(xnew,ynew);
    	}
    }
    
    public static Vec3 rotateVector(Vec3 vec, Vec3 axis, double theta) {
    	double u = axis.x;
    	double v = axis.y;
    	double w = axis.z;
    	
    	double xPrime = u*(u*vec.x + v*vec.y + w*vec.z)*(1d - Math.cos(theta)) 
                + vec.x*Math.cos(theta)
                + (-w*vec.y + v*vec.z)*Math.sin(theta);
		double yPrime = v*(u*vec.x + v*vec.y + w*vec.z)*(1d - Math.cos(theta))
		                + vec.y*Math.cos(theta)
		                + (w*vec.x - u*vec.z)*Math.sin(theta);
		double zPrime = w*(u*vec.x + v*vec.y + w*vec.z)*(1d - Math.cos(theta))
		                + vec.z*Math.cos(theta)
		                + (-v*vec.x + u*vec.y)*Math.sin(theta);
		return new Vec3(xPrime, yPrime, zPrime);
    }
	public static boolean inRange(int val, int min, int max) {
		return val>=min && val <=max;
	}
	
    /**
     * Absolute value of integer
     * @param i
     */
    public static int abs(int i){
    	if(i<0){
    		return -i;
    	}
    	return i;
    }
}
