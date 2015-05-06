package sunderland.leapmotion;

import java.awt.Component;
import java.awt.event.KeyEvent;

import javax.vecmath.Point2d;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import edu.cmu.cs.stage3.alice.core.behavior.LeapMotionKeyTapNavigationBehavior;

public class KeyTapNavigationListener extends Listener {

	private LeapMotionKeyTapNavigationBehavior controllingBehavior;
	private AliceController aliceController;
	private InteractionBox box;
	private Vector fingerPos;
	private final static double Y_THRESHOLD = 0.0;
	private boolean underYThreshold = false;
	private float lastFrameTimeStamp = 0.0f;
	private KeyEvent m_keyEvent;
	
	private SensorTriangle upSectorSensorTriangle = new SensorTriangle(new Point2d(0.0, 0.0), new Point2d(0.5, 0.5), new Point2d(1.0, 0.0));
	private SensorTriangle downSectorSensorTriangle = new SensorTriangle(new Point2d(0.0, 1.0), new Point2d(0.5, 0.5), new Point2d(1.0, 1.0));
	private SensorTriangle leftSectorSensorTriangle = new SensorTriangle(new Point2d(0.0, 0.0), new Point2d(0.0, 1.0), new Point2d(0.5, 0.5));
	private SensorTriangle rightSectorSensorTriangle = new SensorTriangle(new Point2d(1.0, 0.0), new Point2d(0.5, 0.5), new Point2d(1.0, 1.0));
	
	private static enum PressedKey {
		UP_PRESSED,
		DOWN_PRESSED,
		LEFT_PRESSED,
		RIGHT_PRESSED,
		NO_PRESSED
	}
	
	private static PressedKey pressedKey = PressedKey.NO_PRESSED;
		
	public KeyTapNavigationListener(LeapMotionKeyTapNavigationBehavior behavior, AliceController controller) {
		controllingBehavior = behavior;
		aliceController = controller;
	}	
	public void onConnect(Controller c) {
	}

	public void onDisconnect(Controller c) {
	}
	
	public void onFrame(Controller c) {
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
		if (underYThreshold) {
			checkForKeyReleased(frame);
		} else {
			checkForKeyPressed(frame);
		}
	}
	
	private void checkForKeyPressed(Frame frame) {
		if (fingerPos.getY() <= Y_THRESHOLD) {
			underYThreshold = true;
			double x = (double)fingerPos.getX();
			double z = (double)fingerPos.getZ();
			Point2d fingerPoint = new Point2d(x, z);
			
			if (upSectorSensorTriangle.isPointInArea(fingerPoint)) {
				pressedKey = PressedKey.UP_PRESSED;
			} else if (downSectorSensorTriangle.isPointInArea(fingerPoint)) {
				pressedKey = PressedKey.DOWN_PRESSED;
			} else if (leftSectorSensorTriangle.isPointInArea(fingerPoint)) {
				pressedKey = PressedKey.LEFT_PRESSED;
			} else if (rightSectorSensorTriangle.isPointInArea(fingerPoint)) {
				pressedKey = PressedKey.RIGHT_PRESSED;
			} else {
				pressedKey = PressedKey.NO_PRESSED;
			}
			
			if (pressedKey != PressedKey.NO_PRESSED) {
				fireKeyPressed();
			}
			
		}
	}
	
	private void checkForKeyReleased(Frame frame) {
		if (fingerPos.getY() > Y_THRESHOLD) {
			underYThreshold = false;
			fireKeyReleased();
		}
	}
	
	private void fireKeyPressed() {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = null;
		switch (pressedKey) {
			case UP_PRESSED:
				keyCode = KeyEvent.VK_UP;
				break;
			case DOWN_PRESSED:
				keyCode = KeyEvent.VK_DOWN;
				break;
			case LEFT_PRESSED:
				keyCode = KeyEvent.VK_LEFT;
				break;
			case RIGHT_PRESSED:
				keyCode = KeyEvent.VK_RIGHT;
				break;
			case NO_PRESSED:
				break;
		}
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
		System.out.println("Key tap navigation: " + pressedKey.toString() + " pressed");
		
	}
	
	private void fireKeyReleased() {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = null;
		switch (pressedKey) {
			case UP_PRESSED:
				keyCode = KeyEvent.VK_UP;
				break;
			case DOWN_PRESSED:
				keyCode = KeyEvent.VK_DOWN;
				break;
			case LEFT_PRESSED:
				keyCode = KeyEvent.VK_LEFT;
				break;
			case RIGHT_PRESSED:
				keyCode = KeyEvent.VK_RIGHT;
				break;
			case NO_PRESSED:
				break;
		}
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyReleased(keyEvent);
		System.out.println("Key tap navigation: " + pressedKey.toString() + " released" );
		pressedKey = PressedKey.NO_PRESSED;
	}
	
}