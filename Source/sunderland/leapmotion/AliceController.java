package sunderland.leapmotion;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.ArrayList;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class AliceController {

	public static Controller controller;
	private static ArrayList<Listener> listeners = new ArrayList<Listener>();
	public static Robot robot;
	
	public AliceController() {
		controller = new Controller();
	}
	
	public void addListener(Listener listener) {
		controller.addListener(listener);
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		try {
			controller.removeListener(listener);
			listeners.remove(listener);
		}
		catch (Exception e) {
			e.printStackTrace();
			controller.removeListener(listener);
		}		
	}
	
	public void removeAllListeners() {
		for (Listener l : listeners) {
			controller.removeListener(l);
		}
		listeners.clear();
	}
	
	public void controlMouse(Vector normalizedFingerPosition) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int)(screen.width * normalizedFingerPosition.getX());
		int y = (int)(screen.height - normalizedFingerPosition.getY() * screen.height);
		try {
			robot = new Robot();
			robot.mouseMove(x, y);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		
	}

}
