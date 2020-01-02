package com.croshe.android.base.extend.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CrosheValueAnimation extends Animation {
	private float startValue;
	private float endValue;
	private float currValue;
	private ValueChange valueChange;
	
	
	public float getCurrValue() {
		return currValue;
	}

	public void setCurrValue(float currValue) {
		this.currValue = currValue;
	}

	public float getStartValue() {
		return startValue;
	}

	public void setStartValue(float startValue) {
		this.startValue = startValue;
	}

	public float getEndValue() {
		return endValue;
	}

	public void setEndValue(int endValue) {
		this.endValue = endValue;
	}

	public ValueChange getValueChange() {
		return valueChange;
	}

	public void setValueChange(ValueChange valueChange) {
		this.valueChange = valueChange;
	}
	
	
	public void setValues(float startValue,float endValue){
		this.startValue=startValue;
		this.endValue=endValue;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float newValue;
		if(startValue<endValue){
			newValue=(startValue+Math.abs(endValue-startValue)*interpolatedTime);
		}else{
			newValue=(startValue-Math.abs(endValue-startValue)*interpolatedTime);
		}
		if(valueChange!=null){
			valueChange.valueChange(newValue, currValue);
		}
		currValue=newValue;
    }
	
	public interface ValueChange{
		void valueChange(float newValue, float oldValue);
	}
}
