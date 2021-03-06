package com.jed.state;

import com.jed.actor.AbstractEntity;
import com.jed.actor.Ball;
import com.jed.actor.CircleBoundary;
import com.jed.core.MotherBrainConstants;
import com.jed.core.QuadTree;
import com.jed.util.Rectangle;
import com.jed.util.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * 
 * @author jlinde, Peter Colapietro
 *
 * TODO Decouple from com.jed.core.MotherBrain / com.jed.core.MotherBrainConstants
 *
 */
public class DiscoState extends AbstractGameState {

    /**
     * 
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoState.class);

    /**
     * 
     */
    private QuadTree quadTree;

    /**
     * 
     */
    private Stack<Ball> scene;

    /**
     * 
     */
    private int width;
    
    /**
     * 
     */
    private int height;

    @Override
    public void entered() {
        width = MotherBrainConstants.WIDTH;
        height = MotherBrainConstants.HEIGHT;

        scene = new GameEntityStack<>();
        quadTree = new QuadTree(new Vector2f(0, 0), 0, new Rectangle(width, height), this);

        Random rand = new Random();

        int randW, randH, randR;
        float randXS, randYS, randRed, randGreen, randBlue;

        for (int i = 0; i < 25; i++) {
            randW = rand.nextInt(1024) + 1;
            randH = rand.nextInt(768) + 1;
            randR = rand.nextInt(25) + 1;
            randXS = rand.nextFloat() * 2;
            randYS = rand.nextFloat() * 2;
            randRed = rand.nextFloat();
            randGreen = rand.nextFloat();
            randBlue = rand.nextFloat();

            Ball newBall = new Ball(
                    new Vector2f(randW, randH),
                    new Vector2f(randXS, randYS),
                    new CircleBoundary(randR),
                    25,
                    randRed, randGreen, randBlue);
            scene.push(newBall);
        }

        /*
        Ball ball1 = new Ball(new Vector(50,50), new Vector(.05f,.03f), 30, 25);
        scene.push(ball1);

        Ball ball2 = new Ball(new Vector(100,100), new Vector(-.04f,0.05f), 10, 25);
        scene.push(ball2);

        Ball ball3 = new Ball(new Vector(800,300), new Vector(.04f,0.07f), 100, 25);
        scene.push(ball3);

        Ball ball4 = new Ball(new Vector(200,50), new Vector(.05f,.03f), 10, 25);
        scene.push(ball4);

        Ball ball5 = new Ball(new Vector(50,400), new Vector(-.04f,0.05f), 10, 25);
        scene.push(ball5);

        Ball ball6 = new Ball(new Vector(1000,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball6);

        Ball ball7 = new Ball(new Vector(500,35), new Vector(.05f,.03f), 15, 25);
        scene.push(ball7);

        Ball ball8 = new Ball(new Vector(600,100), new Vector(-.04f,0.05f), 10, 25);
        scene.push(ball8);

        Ball ball9 = new Ball(new Vector(270,300), new Vector(.04f,0.07f), 100, 25);
        scene.push(ball9);

        Ball ball10 = new Ball(new Vector(890,50), new Vector(.05f,.03f), 10, 25);
        scene.push(ball10);

        Ball ball11 = new Ball(new Vector(65,400), new Vector(-.04f,0.05f), 10, 25);
        scene.push(ball11);

        Ball ball12 = new Ball(new Vector(88,220), new Vector(.04f,0.07f), 10, 25);
        scene.push(ball12);

        Ball ball13 = new Ball(new Vector(18,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball13);

        Ball ball14 = new Ball(new Vector(500,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball14);

        Ball ball15 = new Ball(new Vector(600,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball15);

        Ball ball16 = new Ball(new Vector(700,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball16);

        Ball ball17 = new Ball(new Vector(900,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball17);

        Ball ball18 = new Ball(new Vector(601,20), new Vector(.4f,0.07f), 5, 25);
        scene.push(ball18);*/
    }

    @Override
    public void update() {
        quadTree.clear();
        scene.forEach(quadTree::insert);
        handleCollisions();
    }

    @Override
    public void render() {
        quadTree.render();
        scene.forEach(Ball::render);
    }

    /**
     *
     */
    private void handleCollisions() {//TODO Test handleCollisions().
        boolean collide = false;
        final List<AbstractEntity> returnObjects = new ArrayList<>();
        for (Ball aScene : scene) {
            quadTree.retrieve(returnObjects, aScene);
            for (AbstractEntity returnObject : returnObjects) {
                if (!returnObject.equals(aScene)) {
                    final Ball p2 = (Ball) returnObject;
                    if (detectCollision(aScene, p2)) {
                        if (!collide) {
                            LOGGER.debug("Handling Collisions");
                            collide = true;
                        }
                        collide(aScene, p2);
                    }
                }
            }
            returnObjects.clear();
        }

        //Boundary collisions
        for (Ball each : scene) {
            double yPosition = each.getPosition().y;
            double xPosition = each.getPosition().x;
            float radius = each.getRadius();

            //Alter the movement vector, move the ball in the opposite
            //direction, then
            //Adjust the position vector so that the ball
            //does not get stuck in the wall
            if (yPosition + radius >= height) {
                each.getMovement().y = each.getMovement().y * -1;
                each.getPosition().y = height - each.getRadius();
            } else if (yPosition - radius <= 0) {
                each.getMovement().y = each.getMovement().y * -1;
                each.getPosition().y = each.getRadius();
            }

            if (xPosition + radius >= width) {
                each.getMovement().x = each.getMovement().x * -1;
                each.getPosition().x = width - each.getRadius();
            } else if (xPosition - radius <= 0) {
                each.getMovement().x = each.getMovement().x * -1;
                each.getPosition().x = each.getRadius();
            }
        }
    }

    /**
     * 
     * @param p1 ball one
     * @param p2 ball two
     * @return if ball one and two collided or not
     */
    private boolean detectCollision(@Nonnull Ball p1, @Nonnull Ball p2) {
        /**
         * Subtract p2's movement vector from p1 the resultant vector
         * Represents where the two balls will collide, if they do
         * by assuming p2 is static
         */
        Vector2f mv = p1.getMovement().subtract(p2.getMovement());

        /**
         * The movement vector must be at least the distance between
         * the centers of the circles minus the radius of each.
         * If it is not, then there is no way that the circles
         * will collide.
         */
        double dist = p1.getPosition().distance(p2.getPosition());
        double sumRadii = p1.getRadius() + p2.getRadius();
        dist -= sumRadii;
        double mvMagnitude = mv.magnitude();
        if (mvMagnitude < dist) {
            return false;
        }

        /**
         * Find c, the vector from the center of p1 to the center of p2
         */
        Vector2f c = p2.getPosition().subtract(p1.getPosition());

        /**
         * Normalize the movement vector to determine if p1 is moving towards p2
         */
        Vector2f mvN = mv.normalize();

        /**
         * Dot product of the normalized movement vector and the difference
         * between the position vectors, this will tell how far along the
         * movement vector the ball will collide with the other, if
         * the result is zero or less, the balls will never collide
         */
        double d = mvN.dotProduct(c);
        if (d <= 0) {
            return false;
        }


        /**
         * f is the distance between the center of p1 and the movement vector
         * if this distance is greater than the radii squared, the balls
         * will never touch
         */
        double lengthC = c.magnitude();
        double f = (lengthC * lengthC) - (d * d);

        double sumRadiiSquared = sumRadii * sumRadii;
        if (f >= sumRadiiSquared) {
            return false;
        }

        /**
         * Math#sqrt(double) represents the distance between the 90 degree intersection
         * of the point on the movement vector and the ball minus the
         * 2 radii of the balls. So it's the square root of the distance along the movement
         * vector where the balls WILL intersect
         */
        double t = sumRadiiSquared - f;

        /**
         * If there is no such right triangle with sides length of
         * sumRadii and Math#sqrt(double), T will probably be less than 0.
         * Better to check now than perform a square root of a
         * negative number.
         */
        if (t < 0) {
            return false;
        }

        /**
         * The distance the circle has to travel along
         *    move vector is D - Math#sqrt(double)
         */
        double mvDistance = d - Math.sqrt(t);

        /**
         * Finally, make sure that the distance A has to move
         * to touch B is not greater than the magnitude of the
         * movement vector.
         */
        if (mvMagnitude < mvDistance) {
            return false;
        }

        //TODO: fix this to adjust the ball
        /**
         * Adjust the displacement of p1 so that it doesn't become "entwined"
         * with the other ball. Place it right where the collision would have occurred
         */
        LOGGER.debug("result = " + mv.magnitude() / p1.getMovement().magnitude());
        return true;
    }

    /**
     * 
     * @param p1 ball one
     * @param p2 ball two
     */
    private void collide(@Nonnull Ball p1, @Nonnull Ball p2) {
        // First, find the normalized vector n from the center of
        // circle1 to the center of circle2
        Vector2f n = (p1.getPosition().subtract(p2.getPosition())).normalize();

        // Find the length of the component of each of the movement
        // vectors along n.
        // a1 = v1 . n
        // a2 = v2 . n
        double a1 = p1.getMovement().dotProduct(n);
        double a2 = p2.getMovement().dotProduct(n);

        double optimizedP = (2.0 * (a1 - a2)) / (p1.mass() + p2.mass());

        // Calculate v1', the new movement vector of circle1
        // v1' = v1 - optimizedP * m2 * n
        Vector2f v1 = p1.getMovement().subtract(n.scale((float) (optimizedP * p2.mass())));

        // Calculate v1', the new movement vector of circle1
        // v2' = v2 + optimizedP * m1 * n
        Vector2f v2 = p2.getMovement().add(n.scale((float) (optimizedP * p1.mass())));

        p1.setMovement(v1);
        p2.setMovement(v2);
    }
}
