package com.jed.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.jed.state.DiscoState;
import com.jed.state.GameStateManager;
import com.jed.state.PlayState;

/**
 * @author jlinde
 * 
 */
public class MotherBrain {

	private static MotherBrain instance;

	public int WIDTH = 1024;
	public int HEIGHT = 768;

	private long lastFrame;
	private int fps;
	private long lastFPS;
	
	private GameStateManager stateManager;

	public static MotherBrain getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		instance = new MotherBrain();
		instance.start();
	}

	private void init() {
		stateManager = new GameStateManager();

		stateManager.push(new DiscoState(stateManager));
		stateManager.push(new DiscoState(stateManager));
		stateManager.push(new DiscoState(stateManager));
		stateManager.push(new DiscoState(stateManager));
		stateManager.push(new DiscoState(stateManager));
		stateManager.push(new PlayState(stateManager));
//		
//		MainMenu one = new MainMenu(stateManager);
//		one.setDaString("Hello there!");
//		one.setCoords(new Vector(20,20));
//		
//		stateManager.push(one);
		
		getDelta();
		lastFPS = getTime();
	}

	public void start() {
		try {

			// DisplayMode displayMode = Display.getDesktopDisplayMode();
			// HEIGHT = displayMode.getHeight();
			// WIDTH = displayMode.getWidth();

			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setFullscreen(true);
			Display.create();

		} catch (LWJGLException e) {
			System.out.println("An exception occurred while creating the display");
			e.printStackTrace();
			System.exit(1);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		init();

		while (!Display.isCloseRequested()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			//int delta = getDelta();
			
			updateFPS();
			
			stateManager.update();
			stateManager.draw();

			Display.update();
			
			Display.sync(120);
		}

		Display.destroy();
	}

	public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		return delta;
	}

	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}

		fps++;
	}

}