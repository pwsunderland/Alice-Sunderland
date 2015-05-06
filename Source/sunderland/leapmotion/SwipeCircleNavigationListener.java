package sunderland.leapmotion;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.LinkedList;


import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

import edu.cmu.cs.stage3.alice.core.behavior.LeapMotionSwipeCircleNavigationBehavior;

public class SwipeCircleNavigationListener extends Listener {

	private LeapMotionSwipeCircleNavigationBehavior controllingBehavior;
	private AliceController aliceController;
	private InteractionBox box;
	private Vector fingerPos;
	private final static double Y_THRESHOLD = 0.0;
	private boolean underYThreshold = false;
	private float lastKeyReleaseTimeStamp = 0.0f;
	private KeyEvent m_keyEvent;
	
	private final static long SWIPE_PRESS_TIME = 10000000;
	
	private boolean handDetected = false;
	
	private static enum PressedKey {
		UP_PRESSED,
		DOWN_PRESSED,
		LEFT_PRESSED,
		RIGHT_PRESSED,
	}
		
	
	private LinkedList<PressedKey> activeKeyPresses = new LinkedList<PressedKey>();
	
	public SwipeCircleNavigationListener(LeapMotionSwipeCircleNavigationBehavior behavior, AliceController controller) {
		controllingBehavior = behavior;
		aliceController = controller;
	}
	
	public void onConnect(Controller c) {
		c.enableGesture(Gesture.Type.TYPE_CIRCLE);
		c.enableGesture(Gesture.Type.TYPE_SWIPE);
	}
	
	public void onDisconnect(Controller c) {
	}
	
	public void onFrame(Controller c) {
		Frame frame = c.frame();
		if (frame.hands().count() == 0) {
			releaseActiveKeyPresses(frame);
		}
		if (frame.hands().count() > 0 && !handDetected) {
			handDetected = true;
			System.out.println("hand detected");
		} else if (frame.hands().count() == 0 && handDetected) {
			handDetected = false;
			System.out.println("hand NOT detected");
		}
		box = frame.interactionBox();
		for (Gesture g : frame.gestures()) {
			if (g.state() == State.STATE_START) {
				if (g.type() == Gesture.Type.TYPE_CIRCLE) {
					System.out.println("circle start: " + g.id());
					CircleGesture circle = new CircleGesture(g);
					boolean clockwise = isCircleClockwise(circle);
					if (clockwise) {
						fireUpKeyPressed(circle);
					} else {
						fireDownKeyPressed(circle);
					}
				} else if (g.type() == Gesture.Type.TYPE_SWIPE) {
					System.out.println("swipe start: " + g.id());
					SwipeGesture swipe = new SwipeGesture(g);
					boolean leftSwipe = swipeDirection(swipe);
					if (leftSwipe) {
						fireLeftKeyPressed(swipe);
					} else {
						fireRightKeyPressed(swipe);
					}
				}
			} else if (g.state() == State.STATE_STOP) {
				if (g.type() == Gesture.Type.TYPE_CIRCLE) {
					releaseActiveKeyPresses(frame);
					System.out.println("circle stop: " + g.id());
					CircleGesture circle = new CircleGesture(g);
					boolean clockwise = isCircleClockwise(circle);
					System.out.println("clockwise: " + clockwise);
					if (clockwise) {
						fireUpKeyReleased(circle);
					} else {
						fireDownKeyReleased(circle);
					}
				} else if (g.type() == Gesture.Type.TYPE_SWIPE) {
					System.out.println("swipe stop: " + g.id());
					SwipeGesture swipe = new SwipeGesture(g);
					boolean leftSwipe = swipeDirection(swipe);
					if (leftSwipe) {
						fireLeftKeyReleased();
					} else {
						fireRightKeyReleased();
					}
				}
			}

		}
		
	}
	
	private void fireUpKeyPressed(CircleGesture circle) {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = KeyEvent.VK_UP;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		activeKeyPresses.add(PressedKey.UP_PRESSED);
		controllingBehavior.keyPressed(keyEvent);
	}
	
	private void fireDownKeyPressed(CircleGesture circle) {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = KeyEvent.VK_DOWN;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		activeKeyPresses.add(PressedKey.DOWN_PRESSED);
		controllingBehavior.keyPressed(keyEvent);
	}
	
	private void fireLeftKeyPressed(SwipeGesture swipe) {
		
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = KeyEvent.VK_LEFT;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
	}
	
	private void fireRightKeyPressed(SwipeGesture swipe) {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = KeyEvent.VK_RIGHT;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		controllingBehavior.keyPressed(keyEvent);
	}
	
	private void fireUpKeyReleased(CircleGesture circle) {
		if (!activeKeyPresses.isEmpty()) {
			Component source = controllingBehavior.getAWTComponent();
			int id = KeyEvent.KEY_LAST;
			long when = System.currentTimeMillis();
			int modifiers = 0;
			char keyChar = KeyEvent.CHAR_UNDEFINED;
			Integer keyCode = KeyEvent.VK_UP;
			KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
			controllingBehavior.keyReleased(keyEvent);
		}
	}
	
	private void fireDownKeyReleased(CircleGesture circle) {
		if (!activeKeyPresses.isEmpty()) {
			Component source = controllingBehavior.getAWTComponent();
			int id = KeyEvent.KEY_LAST;
			long when = System.currentTimeMillis();
			int modifiers = 0;
			char keyChar = KeyEvent.CHAR_UNDEFINED;
			Integer keyCode = KeyEvent.VK_DOWN;
			KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
			controllingBehavior.keyReleased(keyEvent);	
		}
	}
	
	private void fireLeftKeyReleased() {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = KeyEvent.VK_LEFT;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);

		controllingBehavior.keyReleased(keyEvent);	
	}
	
	private void fireRightKeyReleased() {
		Component source = controllingBehavior.getAWTComponent();
		int id = KeyEvent.KEY_LAST;
		long when = System.currentTimeMillis();
		int modifiers = 0;
		char keyChar = KeyEvent.CHAR_UNDEFINED;
		Integer keyCode = KeyEvent.VK_RIGHT;
		KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		
		controllingBehavior.keyReleased(keyEvent);	
	}
	
	private void releaseActiveKeyPresses(Frame frame) {
		while (!activeKeyPresses.isEmpty()) {
			PressedKey key = activeKeyPresses.poll();
			Integer keyCode = null;
			switch (key) {
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
			}
			Component source = controllingBehavior.getAWTComponent();
			int id = KeyEvent.KEY_LAST;
			long when = System.currentTimeMillis();
			int modifiers = 0;
			char keyChar = KeyEvent.CHAR_UNDEFINED;
			KeyEvent keyEvent = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
			controllingBehavior.keyReleased(keyEvent);
			lastKeyReleaseTimeStamp = frame.timestamp();
		}
	}
	
	
	public boolean isCircleClockwise(CircleGesture circle) {
		// method for determining whether CircleGesture is clockwise or counterclockwise acquired from:
		// Coding Basics, "How To: Make a Java Leap Motion Mouse" at https://www.youtube.com/watch?v=ucttSu-XPb8 
		if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * @param swipe 
	 * @return True if swipe is to the left, false if swipe is to the right
	 */
	public boolean swipeDirection(SwipeGesture swipe) {
		Vector direction = swipe.direction();
		if (direction.getX() < 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
}
