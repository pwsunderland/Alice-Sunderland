package sunderland.leapmotion;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.Type;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import edu.cmu.cs.stage3.alice.core.behavior.LeapMotionScreenTapBehavior;

public class ScreenTapListener extends Listener {
	
	private Robot robot;
	private LeapMotionScreenTapBehavior controllingBehavior;
	private AliceController aliceController;
	private final static double Z_THRESHOLD = 0.25;
	private boolean behindZThreshold = false;
	private float lastFrameTimeStamp = 0.0f;
	
	public ScreenTapListener(LeapMotionScreenTapBehavior behavior, AliceController controller) {
		controllingBehavior = behavior;
		aliceController = controller;
	}
	
	
	public void onConnect(Controller c) {
		c.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
	}
	
	public void onDisconnect(Controller c) {
	}
	
	public void onFrame(Controller c) {
		Frame frame = c.frame();
		InteractionBox box = frame.interactionBox();
		for (Finger f : frame.fingers()) {
			if (f.type() == Finger.Type.TYPE_INDEX) {
				Vector fingerPos = f.tipPosition();
				Vector boxFingerPos = box.normalizePoint(fingerPos);
				aliceController.controlMouse(boxFingerPos);
//				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//				robot.mouseMove((int)(screen.width * boxFingerPos.getX()), (int)(screen.height - boxFingerPos.getY() * screen.height));
			}
		}
		fireManualScreenTap(frame);
//		fireLeapScreenTap(frame);
		

	}
	
	private void fireManualScreenTap(Frame frame) {
		Finger indexFinger = frame.fingers().frontmost();
		InteractionBox box = frame.interactionBox();
		for (Finger f : frame.fingers()) {
			if (f.type() == Finger.Type.TYPE_INDEX) {
				indexFinger = f;
			}
		}
		
		Vector fingerPos = box.normalizePoint(indexFinger.tipPosition());
		if (fingerPos.getZ() < Z_THRESHOLD) {
			if (checkValidScreenTap(frame)) {
				fireMouseClick();
			}
		}
		else {
			behindZThreshold = false;
		}
	}
	
	private void fireLeapScreenTap(Frame frame) {
		for (Gesture g : frame.gestures()) {
			if (g.type() == Type.TYPE_SCREEN_TAP) {
				fireMouseClick();
			}
		}
	}
	
	private void fireMouseClick() {
		int screenX = MouseInfo.getPointerInfo().getLocation().x;
		int screenY = MouseInfo.getPointerInfo().getLocation().y;
		Point p = screenToWorldCoordinate(new Point(screenX, screenY));
		
		Component source = controllingBehavior.getAWTComponent();
		int id = 0;
		long when = System.currentTimeMillis();
		int modifiers = 16;
		int clickCount = 1;
		boolean popupTrigger = false;
		
		MouseEvent click = new MouseEvent(source, id, when, modifiers, p.x, p.y, clickCount, popupTrigger);
		
		controllingBehavior.mousePressed(click);
		controllingBehavior.mouseReleased(click);
	}
	
	private boolean checkValidScreenTap(Frame frame) {
		boolean valid;
		if (frame.timestamp() - lastFrameTimeStamp < 1000000) {
			valid = false;
		}
		if (behindZThreshold) {
			valid = false;
		} else {
			behindZThreshold = true;
			valid = true;
		}
		lastFrameTimeStamp = frame.timestamp();
		return valid;
	}
	
	public Point screenToWorldCoordinate(Point screenLoc) {
		Point worldLocationOnScreen = controllingBehavior.getAWTComponent().getLocationOnScreen();
		int x = screenLoc.x - worldLocationOnScreen.x;
		int y = screenLoc.y - worldLocationOnScreen.y;
		return new Point(x,y);
	}

}
