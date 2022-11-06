package adv.headsoccer_project.model;

public class PolarVector {
    private double magnitude;
    private double angle;

    public PolarVector() {
        this.magnitude = 0;
        this.angle = 0;
    }

    public PolarVector add(PolarVector other) {
        return new PolarVector().fromPolar(
                Math.sqrt(Math.pow(this.getMagnitude(), 2) + Math.pow(other.getMagnitude(),
                        2)
                        + (2 * this.getMagnitude() * other.getMagnitude()
                                * Math.cos(other.getAngle() - this.getAngle()))),
                this.getAngle() + Math.atan2(other.getMagnitude() * Math.sin(other.getAngle()
                        - this.getAngle()),
                        this.getMagnitude() + other.getMagnitude() * Math.cos(other.getAngle() -
                                this.getAngle()))
                        % (2 * Math.PI));
    }

    public PolarVector add(double x, double y) {
        return add(new PolarVector().fromCartesian(x, y));
    }

    public PolarVector substract(PolarVector other) {
        return add(other.negate());
    }

    public PolarVector negate() {
        return new PolarVector().fromPolar(this.getMagnitude(), this.getAngle() + Math.PI);
    }

    public PolarVector fromCartesian(double x, double y) {
        this.magnitude = Math.hypot(x, y);
        this.angle = Math.atan2(y, x);
        if (Double.isNaN(magnitude) | Double.isNaN(angle)) {
            throw new RuntimeException(String.format("[%.2f, %.2f]", x, y));
        }
        return this;
    }

    public PolarVector fromPolar(double r, double t) {
        this.magnitude = r;
        this.angle = t;
        if (this.magnitude < 0) {
            this.magnitude = Math.abs(this.magnitude);
            this.angle = (this.angle + Math.PI) % (2 * Math.PI);
        }
        return this;
    }

    public PolarVector project(double t) {
        return new PolarVector().fromPolar(Math.cos(t - this.angle) * this.magnitude, t);
    }

    public double[] toCartesian() {
        return new double[] { Math.cos(angle) * magnitude, Math.sin(angle) * magnitude };
    }

    public PolarVector mult(double scalar) {
        return new PolarVector().fromPolar(this.magnitude * scalar, this.angle);
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getAngle() {
        return angle;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return String.format("[%.2f, %.2f] - [%.2f, %.2f]", magnitude, angle, Math.cos(angle) * magnitude,
                Math.sin(angle) * magnitude);
    }
}