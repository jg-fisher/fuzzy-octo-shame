package com.jed.actor;

import com.jed.util.Vector2f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * 
 * @author jlinde, Peter Colapietro
 *
 */
public class PolygonBoundary extends Boundary {

    /**
     * 
     */
    private double rightBound = 0;
    
    /**
     * 
     */
    private double leftBound = 0;
    
    /**
     * 
     */
    private double upperBound = 0;
    
    /**
     * 
     */
    private double lowerBound = 0;

    /**
     * 
     * @param position position vector
     * @param vertices array of vertices
     */
    public PolygonBoundary(final Vector2f position, @Nonnull final Vector2f[] vertices) {
        super(position, vertices);

        //Find Max Bounds for quad tree
        for (final Vector2f vertex: vertices) {
            if (vertex.x > rightBound) {
                rightBound = vertex.x;
            }
            if (vertex.x < leftBound) {
                leftBound = vertex.x;
            }
            if (vertex.y < upperBound) {
                upperBound = vertex.y;
            }
            if (vertex.y > lowerBound) {
                lowerBound = vertex.y;
            }
        }
    }

    @Override
    public double getRightBound() {
        return getOwner().getPosition().x + getPosition().x + rightBound;
    }

    @Override
    public double getLeftBound() {
        return getOwner().getPosition().x + getPosition().x + leftBound;
    }

    @Override
    public double getUpperBound() {
        return getOwner().getPosition().y + getPosition().y + upperBound;
    }

    @Override
    public double getLowerBound() {
        return getOwner().getPosition().y + getPosition().y + lowerBound;
    }

    @Override
    public int getWidth() {
        return (int) (rightBound - leftBound);
    }

    @Override
    public int getHeight() {
        return (int) (lowerBound - upperBound);
    }

    @Override
    public void render() {
        //Bounding Box
        GL11.glColor3f(1f, 0, 0);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (final Vector2f vertex : vertices) {
            getOwner().drawChildVertex2f(getPosition().x + vertex.x, getPosition().y + vertex.y);
        }
        GL11.glEnd();
    }

}
