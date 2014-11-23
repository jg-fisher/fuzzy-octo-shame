package com.jed.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jed.actor.AbstractEntity;
import com.jed.core.MotherBrainConstants;
import com.jed.util.Vector3f;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import com.jed.actor.Player;
import com.jed.core.Collision;
import com.jed.core.QuadTree;
import com.jed.util.Rectangle;
import com.jed.util.Util;

import javax.annotation.Nullable;


/**
 * 
 * @author jlinde, Peter Colapietro
 *
 * TODO Decouple from com.jed.core.MotherBrain / com.jed.core.MotherBrainConstants
 *
 */
public class GameMap extends AbstractDisplayableState {

    /**
     *
     */
    private int width;

    /**
     *
     */
    private int height;

    /**
     *
     */
    private int tileWidth;

    /**
     *
     */
    private int tileHeight;

    /**
     * 
     */
    private String tileSetPath;

    /**
     * 
     */
    //TODO: this should be set when the map loads...
    private static final Vector3f POSITION = new Vector3f(0, 0);

    /**
     * 
     */
    private List<MapTile> tiles;

    /**
     * 
     */
    @Nullable
    private Texture texture;

    /**
     * 
     */
    private Player player;
    
    /**
     * 
     */
    private Stack<AbstractEntity> scene;

    /**
     * 
     */
    private QuadTree quadTree;

    /**
     * 
     */
    private float gravity = 0.21875f;
    
    /**
     * 
     */
    private boolean isDebugViewEnabled;

    @Override
    public void entered() {
        texture = Util.loadTexture(tileSetPath);

        //TODO: initialize scene Stack by some data contained in the map i.e. start position or something like that...
        scene = new Stack<>();
        player = new Player(new Vector3f(50, 200), 256, 256, this);
        scene.push(player);

        quadTree = new QuadTree(
                new Vector3f(0, 0), 0,
                new Rectangle(
                        width * tileWidth,
                        height * tileHeight),
                this);

        //TODO: the quad tree should come pre-populated with all map tiles
        // and should only add and remove map entities...

        //Draw the map once prior to the first update to ensure the quad tree
        //is populated with map tiles on the first frame
        drawMap();
    }

    /**
     * 
     */
    public void keyPress() {
        player.keyPressEvent();
    }

    @Override
    public void update() {
        tiles.forEach(each -> { each.setColliding(false); each.setEvaluating(false); });
        scene.forEach(quadTree::insert);
        detectCollisions();
        scene.forEach(AbstractEntity::update);
        scrollMap();
    }

    /**
     * 
     */
    private void scrollMap() {
        if (player.movement.y > 0) {
            if ((player.position.y + (player.height / 2) - POSITION.y) > MotherBrainConstants.HEIGHT / 2) {
                if (POSITION.y + player.movement.y > height * tileHeight - MotherBrainConstants.HEIGHT) {
                    POSITION.y = height * tileHeight - MotherBrainConstants.HEIGHT;
                } else {
                    POSITION.y += player.movement.y;
                }
            }
        } else if (player.movement.y < 0) {
            if ((player.position.y + (player.height / 2) - POSITION.y) < MotherBrainConstants.HEIGHT / 2) {
                if (player.movement.y + POSITION.y < 0) {
                    POSITION.y = 0;
                } else {
                    POSITION.y += player.movement.y;
                }
            }
        }

        if (player.movement.x > 0) {
            if ((player.position.x + (player.width / 2) - POSITION.x) > MotherBrainConstants.WIDTH / 2) {
                if (POSITION.x + player.movement.x > width * tileWidth - MotherBrainConstants.WIDTH) {
                    POSITION.x = width * tileWidth - MotherBrainConstants.WIDTH;
                } else {
                    POSITION.x += player.movement.x;
                }
            }
        } else if (player.movement.x < 0) {
            if ((player.position.x + (player.width / 2) - POSITION.x) < MotherBrainConstants.WIDTH / 2) {
                if (player.movement.x + POSITION.x < 0) {
                    POSITION.x = 0;
                } else {
                    POSITION.x += player.movement.x;
                }
            }
        }
    }

    /**
     * 
     */
    private void detectCollisions() {
        final List<AbstractEntity> returnObjects = new ArrayList<>(scene.size());
        final List<Collision> collisions = new CopyOnWriteArrayList<>();
        for (final AbstractEntity entity : scene) {
            quadTree.retrieve(returnObjects, entity);
            //Detect all collisions that might occur this frame
            returnObjects.stream().filter(returnObject -> !returnObject.equals(entity)).forEach(returnObject -> {
                final Collision collision = new Collision(entity, returnObject, isDebugViewEnabled());
                //Detect all collisions that might occur this frame
                if (collision.detectCollision()) {
                    collisions.add(collision);
                }
            });

            //Sort Collisions, resolve soonest depending on type in following order:
            //    OVERLAPS
            //    SWEPT Y
            //    SWEPT X
            final Iterator<Collision> it = collisions.iterator();
            while (collisions.size() > 0) {
                Collections.sort(collisions);
                collisions.get(0).resolveCollision();
                collisions.remove(0);
                while (it.hasNext()) {
                    final Collision each = it.next();
                    if (!each.detectCollision()) {
                        collisions.remove(each);
                    }
                }
            }
        }

    }


    @Override
    public void render() {
        quadTree.clear();
        drawMap();
        if(isDebugViewEnabled()) {
            quadTree.render();
        }
        for (AbstractEntity each : scene) {
            each.render();
        }
    }

    /**
     * 
     */
    private void drawMap() {
        Color.white.bind();
        texture.bind();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        final float tileOffsetY = POSITION.y / tileHeight;
        final double pixelOffsetY = tileHeight * (tileOffsetY % 1);

        final float tileOffsetX = POSITION.x / tileWidth;
        final double pixelOffsetX = tileWidth * (tileOffsetX % 1);

        int tileIndex = (int) (width * (Math.floor(tileOffsetY)) + tileOffsetX);

        final int rows = (MotherBrainConstants.HEIGHT / tileHeight + (pixelOffsetY == 0 ? 0 : 1));
        final int columns = (MotherBrainConstants.WIDTH / tileWidth + (pixelOffsetX == 0 ? 0 : 1));
        final int nextRow = width - columns;

        MapTile mapTile;
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                mapTile = tiles.get(tileIndex);
                if (mapTile.getTileId() != 0) {
                    mapTile.render();
                    quadTree.insert(mapTile);
                }
                tileIndex++;
            }
            tileIndex += nextRow;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
    
    /**
     * @param x x
     * @param y y
     */
    public void drawChildVertex2f(float x, float y) {
        GL11.glVertex2f(x - POSITION.x, y - POSITION.y);
    }

    /**
     * @param tileSetPath path to tile set 
     * 
     */
    public void setTileSetPath(String tileSetPath) {
        this.tileSetPath = tileSetPath;
    }

    /**
     *
     * @param width width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     *
     * @param height height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     *
     * @return gravity
     */
    public float getGravity() {
        return gravity;
    }

    /**
     *
     * @return tileWidth
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     *
     * @param tileWidth tileWidth
     */
    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    /**
     *
     * @return tileHeight
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     *
     * @param tileHeight tileHeight
     */
    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    /**
     *
     * @param gravity gravity
     */
    public void setGravity(Float gravity) {
        this.gravity = gravity;
    }

    /**
     *
     * @param tiles tiles
     */
    public void setTiles(final List<MapTile> tiles) {
        this.tiles = tiles;
    }

    /**
     *
     * @return tiles
     */
    public List<MapTile> getTiles() {
        return tiles;
    }
    
    /**
     * 
     * @param isDebugViewEnabled isDebugViewEnabled
     */
    public void setDebugViewEnabled(boolean isDebugViewEnabled) {
        this.isDebugViewEnabled = isDebugViewEnabled;
    }
    
    /**
     * 
     * @return isDebugViewEnabled
     */
    boolean isDebugViewEnabled() {
        return isDebugViewEnabled;
    }
}
