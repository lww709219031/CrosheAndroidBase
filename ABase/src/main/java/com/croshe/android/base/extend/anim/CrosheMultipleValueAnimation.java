package com.croshe.android.base.extend.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CrosheMultipleValueAnimation extends Animation {
	private float[] startValues;
	private float[] endValues;
	private MultipleValueChange multipleValueChange;
	private MultipleValueAndInvertValueChange multipleValueAndInvertValueChange;


	public void setValues(float[] startValues,float[] endValues){
		if(startValues.length!=endValues.length){
			throw  new RuntimeException("startValues.length!=endValues.length");
		}
		this.startValues=startValues;
		this.endValues=endValues;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float[] newValues=new float[startValues.length];
		float[] invertValues=new float[startValues.length];
		for (int i=0;i<startValues.length;i++){
			newValues[i]=calcuValue(startValues[i],endValues[i],interpolatedTime);
			invertValues[i]=calcuValue(endValues[i],startValues[i],interpolatedTime);
		}
		if(multipleValueChange!=null){
			multipleValueChange.multipleValueChange(newValues);
		}
		if(multipleValueAndInvertValueChange!=null){
			multipleValueAndInvertValueChange.multipleValueChange(newValues,invertValues);
		}
	}


	private float calcuValue(float startValue,float endValue,float interpolatedTime){
		float newValue;
		if(startValue<endValue){
			newValue=(startValue+Math.abs(endValue-startValue)*interpolatedTime);
		}else{
			newValue=(startValue-Math.abs(endValue-startValue)*interpolatedTime);
		}
		return newValue;
	}



	public MultipleValueChange getMultipleValueChange() {
		return multipleValueChange;
	}

	public void setMultipleValueChange(MultipleValueChange multipleValueChange) {
		this.multipleValueChange = multipleValueChange;
	}

	public interface MultipleValueChange{
		void multipleValueChange(float[] newValues);
	}

	public MultipleValueAndInvertValueChange getMultipleValueAndInvertValueChange() {
		return multipleValueAndInvertValueChange;
	}

	public void setMultipleValueAndInvertValueChange(MultipleValueAndInvertValueChange multipleValueAndInvertValueChange) {
		this.multipleValueAndInvertValueChange = multipleValueAndInvertValueChange;
	}

	public interface MultipleValueAndInvertValueChange{
		void multipleValueChange(float[] newValues, float[] invertValues);
	}
}
