package adv.headsoccer_project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import adv.headsoccer_project.model.PolarVector;
import org.junit.jupiter.api.Test;

public class PolarVectorTest {
    @Test
    public void addedPolarVectorShouldReturnCorrectResultPolar() {
        PolarVector vector1 = new PolarVector().fromPolar(1, 0);
        PolarVector vector2 = new PolarVector().fromPolar(1, Math.PI / 2);
        assertEquals(Math.sqrt(2), vector1.add(vector2).getMagnitude(), "Magnitude should be sqrt(2)");
    }

    @Test
    public void addedPolarVectorShouldReturnCorrectResultCartesian() {
        PolarVector vector1 = new PolarVector().fromCartesian(1, 0);
        PolarVector vector2 = new PolarVector().fromCartesian(0, 1);
        vector1.add(vector2);
        assertEquals(Math.sqrt(2), vector1.add(vector2).getMagnitude(), "Magnitude should be sqrt(2)");
    }

    @Test
    public void cartesianConversionShouldBeCorrect() {
        PolarVector vector1 = new PolarVector().fromCartesian(1, 1);
        assertEquals(Math.sqrt(2), vector1.getMagnitude(), "Magnitude should be sqrt(2)");
        assertEquals(Math.PI / 4, vector1.getAngle(), "Angle should be 45 DEG");
    }

    @Test
    public void polarConversionShouldBeCorrect() {
        PolarVector vector1 = new PolarVector().fromPolar(Math.sqrt(2), Math.PI / 4);
        double[] res = vector1.toCartesian();
        assertEquals(1d, res[0], 0.00000000001d, "x = 1?");
        assertEquals(1d, res[1], 0.00000000001d, "y = 1?");
    }

    @Test
    public void xProjectionShouldReturnXVector() {
        PolarVector vector1 = new PolarVector().fromCartesian(1, 1);
        vector1.project(0);
        assertEquals(1.4142135623730951d, vector1.getMagnitude(), 0.00000000001d, "Magnitude should be 1");
        assertEquals(0.7853981633974483d, vector1.getAngle(), 0.00000000001d, "Angle should be 0");
    }

    @Test
    public void yProjectionShouldReturnYVector() {
        PolarVector vector1 = new PolarVector().fromCartesian(1, 1);
        vector1.project(Math.PI / 2);
        assertEquals(1.4142135623730951d, vector1.getMagnitude(), 0.00000000001d, "Magnitude should be 1");
        assertEquals(0.7853981633974483d, vector1.getAngle(), 0.00000000001d, "Angle should be 0");
    }

    @Test
    public void complexProjectionShouldReturnComplexVector() {
        PolarVector vector1 = new PolarVector().fromCartesian(1, 1);
        vector1.project(Math.PI / 4);
        assertEquals(Math.sqrt(2), vector1.getMagnitude(), 0.00000000001d, "Magnitude should be 1");
        assertEquals(Math.PI / 4d, vector1.getAngle(), 0.00000000001d, "Angle should be 0");
    }

    @Test
    public void complexCalculationShouldReturnCorrectResult() {
        PolarVector vector1 = new PolarVector().fromPolar(2, Math.PI / 4);
        PolarVector vector2 = new PolarVector().fromPolar(4, Math.PI);
        double[] res = vector1.add(vector2).toCartesian();
        assertEquals(-4 + Math.sqrt(4) * Math.cos(Math.PI / 4), res[0], 0.00000000001d, "x = sqrt(4)cos(pi/4) - 4?");
        assertEquals(Math.sqrt(4) * Math.sin(Math.PI / 4), res[1], 0.00000000001d, "y = sqrt(4)sin(pi/4)?");
    }
}