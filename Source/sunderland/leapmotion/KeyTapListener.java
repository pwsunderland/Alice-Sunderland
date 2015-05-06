package sunderland.leapmotion;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.vecmath.Point2d;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import edu.cmu.cs.stage3.alice.core.behavior.LeapMotionKeyTapBehavior;

public class KeyTapListener extends Listener {

	private Robot robot;
	private LeapMotionKeyTapBehavior controllingBehavior;
	private AliceController aliceController;
	private InteractionBox box;
	private Vector fingerPos;
	private final static double Y_THRESHOLD = 0.0;
	private boolean underYThreshold = false;
	private float lastFrameTimeStamp = 0.0f;
	private KeyEvent m_keyEvent;
	
	private SensorTriangle upSectorTriangle = new SensorTriangle(new Point2d(0.0, 0.0), new Point2d(0.5, 0.5), new Point2d(1.0, 0.0));
	private SensorTriangle downSectorTriangle = new SensorTriangle(new Point2d(0.0, 1.0), new Point2d(0.5, 0.5), new Point2d(1.0, 1.0));
	private SensorTriangle leftSectorTriangle = new SensorTriangle(new Point2d(0.0, 0.0), new Point2d(0.0, 1.0), new Point2d(0.5, 0.5));
	private SensorTriangle rightSectorTriangle = new SensorTriangle(new Point2d(1.0, 0.0), new Point2d(0.5, 0.5), new Point2d(1.0, 1.0));
	
	public KeyTapListener(LeapMotionKeyTapBehavior behavior, AliceController controller) {
		controllingBehavior = behavior;
		aliceController = controller;
	}
	
	public void onConnect(Controller c) {
	}

	public void onDisconnect(Controller c) {
	}
	
	public void onFrame(Controller c) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		Frame frame = c.frame();
		box = frame.interactionBox();
		Finger indexFinger = frame.fingers().frontmost();
		for (Finger f : frame.fingers()) {
			if (f.type() == Finger.Type.TYPE_INDEX) {
				indexFinger = f;
			}
		}
		fingerPos = box.normalizePoint(indexFinger.tipPosition());
		aliceController.controlMouse(fingerPos);
		checkForKeyTap(frame);
	}
	
	private void checkForKeyTap(Frame frame) {
		if (!underYThreshold && frame.timestamp() - lastFrameTimeStamp < 1000000) {
			if (fingerPos.getY() <= Y_THRESHOLD) {
				underYThreshold = true;
				double x = (double)fingerPos.getX();
				double z = (double)fingerPos.getZ();
				Point2d fingerPoint = new Point2d(x, z);
				
				if (upSectorTriangle.isPointInArea(fingerPoint)) {
					fireUpKeyTap();
				} else if (downSectorTriangle.isPointInArea(fingerPoint)) {
					fireDownKeyTap();
				} else if (leftSectorTriangle.isPointInArea(fingerPoint)) {
					fireLeftKeyTap();
				} else if (rightSectorTriangle.isPointInArea(fingerPoint)) {
					fireRightKeyTap();
				}
			} else {
				underYThreshold = false;
			}
		} else if (fingerPos.getY() <= Y_THRESHOLD) {
			underYThreshold = true;
		} else {
			underYThreshold = false;
		}
		
		lastFrameTimeStamp = frame.timestamp();
	}
	
	private void fireUpKeyTap() {
		Component source = controllingBehavior.getAWTComponent();
		int id = 0;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		int keyCode = KeyEvent.VK_UP;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
		controllingBehavior.keyReleased(keyEvent);
		System.out.println("Key Tap: up");
	}
	
	private void fireDownKeyTap() {
		Component source = controllingBehavior.getAWTComponent();
		int id = 0;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		int keyCode = KeyEvent.VK_DOWN;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
		controllingBehavior.keyReleased(keyEvent);
		System.out.println("Key Tap: down");
	}
	
	private void fireLeftKeyTap() {
		Component source = controllingBehavior.getAWTComponent();
		int id = 0;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		int keyCode = KeyEvent.VK_LEFT;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
		controllingBehavior.keyReleased(keyEvent);
		System.out.println("Key Tap: left");
	}
	
	private void fireRightKeyTap() {
		Component source = controllingBehavior.getAWTComponent();
		int id = 0;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		int keyCode = KeyEvent.VK_RIGHT;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
		controllingBehavior.keyReleased(keyEvent);
		System.out.println("Key Tap: right");
	}
	
}
