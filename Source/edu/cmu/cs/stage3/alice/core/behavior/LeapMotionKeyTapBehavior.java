package edu.cmu.cs.stage3.alice.core.behavior;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import sunderland.leapmotion.AliceController;
import sunderland.leapmotion.KeyTapListener;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;

// Sunderland Leap Motion addition

public class LeapMotionKeyTapBehavior extends TriggerBehavior implements KeyListener{

	public final IntegerProperty keyCode = new IntegerProperty(this, "keyCode", null);
	public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, RenderTarget[].class);
	private RenderTarget[] m_renderTargets = null;
	
	private KeyTapListener keyTapListener = new KeyTapListener(this, controller);
	
	public void manufactureAnyNecessaryDetails() {
		if (details.size() == 1) {
			Variable code = new Variable();
			code.name.set("code");
			code.setParent(this);
			code.valueClass.set(Integer.class);
			details.add(code);
		}
	}
	
	public void manufactureDetails() {
		super.manufactureDetails();
		Variable keyChar = new Variable();
		keyChar.name.set("keyChar");
		keyChar.setParent(this);
		keyChar.valueClass.set(Character.class);
		details.add(keyChar);
		
		Variable code = new Variable();
		code.name.set("code");
		code.setParent(this);
		code.valueClass.set(Integer.class);
		details.add(code);
	}
	
	private void updateDetails(KeyEvent keyEvent) {
		for (int i=0; i<details.size(); i++) {
			Variable detail = (Variable)details.get(i);
			if (detail.name.getStringValue().equals("keyChar")) {
				detail.value.set(new Character(keyEvent.getKeyChar()));
			} else if (detail.name.getStringValue().equals("code")) {
				detail.value.set(new Integer(keyEvent.getKeyCode()));
			}
		}
	}
	
	private boolean checkKeyCode(KeyEvent keyEvent) {
		int actualValue = keyEvent.getKeyCode();
		int requiredValue = keyCode.intValue(actualValue);
		return actualValue == requiredValue;
	}
	
	public void keyPressed(KeyEvent keyEvent) {
	}

	public void keyReleased(KeyEvent keyEvent) {
		updateDetails(keyEvent);
		if (checkKeyCode(keyEvent)) {
			trigger(keyEvent.getWhen()*0.001);
		}
	}

	public void keyTyped(KeyEvent keyEvent) {
	}
	
	protected void started(World world, double time) {
		controller.addListener(keyTapListener);
		super.started(world, time);
		m_renderTargets = (RenderTarget[])renderTargets.get();
		if (m_renderTargets == null) {
			m_renderTargets = (RenderTarget[])world.getDescendants(RenderTarget.class);
		}
		for (int i=0; i<m_renderTargets.length; i++) {
			m_renderTargets[i].addKeyListener(this);
		}
	}
	
	protected void stopped(World world, double time) {
		controller.addListener(keyTapListener);
		super.stopped(world, time);
		for (int i=0; i<m_renderTargets.length; i++) {
			m_renderTargets[i].removeKeyListener(this);
		}
		m_renderTargets = null;
	}
	
	public Component getAWTComponent() {
		if (m_renderTargets != null) {
			return m_renderTargets[0].getAWTComponent();
		} else {
			return null;
		}
	}
}
