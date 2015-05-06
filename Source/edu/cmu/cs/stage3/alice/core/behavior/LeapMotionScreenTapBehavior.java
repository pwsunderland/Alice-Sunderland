package edu.cmu.cs.stage3.alice.core.behavior;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import sunderland.leapmotion.AliceController;
import sunderland.leapmotion.ScreenTapListener;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.IntegerProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.question.PickQuestion;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;

// Sunderland Leap Motion addition

public class LeapMotionScreenTapBehavior extends TriggerBehavior implements java.awt.event.MouseListener {
	public final IntegerProperty requiredModifierMask = new IntegerProperty(this, "requiredModifierMask", new Integer(0));
	public final IntegerProperty excludedModifierMask = new IntegerProperty(this, "excludedModifierMask", new Integer(0));
	public final ElementArrayProperty renderTargets = new ElementArrayProperty(this, "renderTargets", null, RenderTarget[].class);
		
	public final TransformableProperty onWhat = new TransformableProperty(this, "onWhat", null);

	protected MouseEvent m_pressedEvent = null;
	protected RenderTarget[] m_renderTargets = null;
	public long m_clickTimeThreshold = 300;
	public int m_clickDistanceThresholdSquared = 100;
	private ScreenTapListener screenTapListener = new ScreenTapListener(this, controller);
	
	public void manufactureAnyNecessaryDetails() {
		if (details.size() == 2) {
			Question what = new PickQuestion();
			what.name.set("what");
			what.setParent(this);
			details.add(what);
		}
		for (int i=0; i<details.size(); i++) {
			Object o = details.get(i);
			if (o instanceof PickQuestion) {
				((PickQuestion) o).name.set("what");
			}
		}
	}
	
	public void manufactureDetails() {
		super.manufactureDetails();
		
		Variable x = new Variable();
		x.name.set("x");
		x.setParent(this);
		x.valueClass.set(Number.class);
		details.add(x);
		
		Variable y = new Variable();
		y.name.set("y");
		y.setParent(this);
		y.valueClass.set(Number.class);
		details.add(y);
		
		manufactureAnyNecessaryDetails();
	}
	
	protected void updateDetails(MouseEvent mouseEvent) {
		for (int i=0; i<details.size(); i++) {
			Expression detail = (Expression)details.get(i);
			if (detail.name.getStringValue().equals("x")) {
				((Variable)detail).value.set(new Double(mouseEvent.getX()));
			} else if (detail.name.getStringValue().equals("y")) {
				((Variable)detail).value.set(new Double(mouseEvent.getY()));
			} else if (detail.name.getStringValue().equals("what")) {
				((PickQuestion)detail).setMouseEvent(mouseEvent);
			}
		}
	}

	protected boolean checkModifierMask(InputEvent e) {
		int modifiers = e.getModifiers();
		Integer requiredModifierMaskValue = (Integer)requiredModifierMask.getValue();
		Integer excludedModifierMaskValue = (Integer)excludedModifierMask.getValue();
		int required = 0;
		if (requiredModifierMaskValue != null) {
			required = requiredModifierMaskValue.intValue();
		}
		int excluded = 0;
		if (excludedModifierMaskValue != null) {
			excluded = excludedModifierMaskValue.intValue();
		}
		return ((modifiers&required) == required) && ((modifiers&excluded) == 0);
	}	

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		m_pressedEvent = e;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isEnabled.booleanValue()) {
			if (checkModifierMask(e)) {
				updateDetails(e);
				Transformable onWhatValue = onWhat.getTransformableValue();
				boolean success;
				if (onWhatValue != null) {
					PickInfo pickInfo = RenderTarget.pick(e);
					if (pickInfo != null && pickInfo.getCount() > 0) {
						Model model = (Model)(pickInfo.getVisualAt(0).getBonus());
						success = onWhatValue == model || onWhatValue.isAncestorOf(model);
					} else {
						success = false;
					}
				} else {
					success = true;
				}
				if (success) {
					updateDetails(e);
					trigger(e.getWhen()*0.001);
				}
			}
		}
		
	}
	
	protected void started(World world, double time) {
		controller.addListener(screenTapListener);

		super.started(world, time);
		m_renderTargets = (RenderTarget[])renderTargets.get();
		if (m_renderTargets==null) {
			m_renderTargets = (RenderTarget[])world.getDescendants(RenderTarget.class);
		}
		for (int i=0; i<m_renderTargets.length; i++) {
			m_renderTargets[i].addMouseListener(this);
		}
		
		
	}
	
	protected void stopped(World world, double time) {
		controller.removeListener(screenTapListener);
		
		super.stopped(world, time);
		for (int i=0; i<m_renderTargets.length; i++) {
			m_renderTargets[i].removeMouseListener(this);
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
