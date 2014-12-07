package com.jed.actor;

import com.jed.core.Displayable;
import com.jed.state.State;
import com.jed.util.Vector2f;
import org.colapietro.lang.LangConstants;
import org.colapietro.lang.NotImplementedException;
import org.colapietro.lwjgl.physics.Collidable;

import javax.annotation.Nonnull;

/**
 *
 * Abstract class whose subclasses are displayable, collidable, and contain various states. It is also
 * composed of a position and movement vector, acceleration, and a {@literal Boundary}.
 * 
 * @author jlinde, Peter Colapietro
 *
 */
public abstract class AbstractEntity implements Displayable, State, Collidable {

    /**
     * 
     */
    private Vector2f position;
    
    /**
     * 
     */
    @Nonnull
    private final Boundary bounds;
    
    /**
     * 
     */
    private Vector2f movement;
    
    /**
     * 
     */
    private float acceleration = 0;

    /**
     *
     * @param position position vector.
     * @param movement movement vector.
     * @param bounds entity's bounds.
     */
    protected AbstractEntity(Vector2f position, Vector2f movement, @Nonnull Boundary bounds) {
        this.position = position;
        this.bounds = bounds;
        this.bounds.setOwner(this);
        this.movement = movement;
    }

    @Override
    public void drawChildVertex2f(float x, float y) throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void entered() throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void leaving() throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void update() throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void render() throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void collideDown(AbstractEntity entity) throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void collideUp(AbstractEntity entity) throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void collideLeft(AbstractEntity entity) throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    @Override
    public void collideRight(AbstractEntity entity) throws NotImplementedException {
        throw new NotImplementedException(LangConstants.NOT_IMPLEMENTED_YET_MESSAGE);
    }

    /**
     *
     * @return acceleration
     */
    public float getAcceleration() {
        return acceleration;
    }

    /**
     *
     * @param acceleration acceleration
     */
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    /**
     *
     * @return bounds
     */
    @Nonnull
    public Boundary getBounds() {
        return bounds;
    }

    /**
     *
     * @return position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     *
     * @param position position
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }

    /**
     *
     * @return movement
     */
    public Vector2f getMovement() {
        return movement;
    }

    /**
     *
     * @param movement movement
     */
    public void setMovement(Vector2f movement) {
        this.movement = movement;
    }

    //TODO Implement Equals/hashCode
}
