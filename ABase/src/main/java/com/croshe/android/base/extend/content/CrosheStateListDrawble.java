package com.croshe.android.base.extend.content;

import android.annotation.SuppressLint;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

@SuppressLint("NewApi")
public class CrosheStateListDrawble extends StateListDrawable {

	private View view;
	private float pressedAlpha;
	private float pressedScale=1.0f;
	private boolean isScaled;
	private boolean isAnimaed;
	private Animation touchDownAnimation;
	private Animation touchUpAnimation;

	public boolean isScaled() {
		return isScaled;
	}

	public void setScaled(boolean isScaled) {
		this.isScaled = isScaled;
	}

	public Animation getTouchDownAnimation() {
		return touchDownAnimation;
	}

	public void setTouchDownAnimation(Animation touchDownAnimation) {
		this.touchDownAnimation = touchDownAnimation;
	}

	public Animation getTouchUpAnimation() {
		return touchUpAnimation;
	}

	public void setTouchUpAnimation(Animation touchUpAnimation) {
		this.touchUpAnimation = touchUpAnimation;
	}

	public CrosheStateListDrawble(View view){
		this.view=view;
	}

	public float getPressedAlpha() {
		return pressedAlpha;
	}

	public boolean isAnimaed() {
		return isAnimaed;
	}

	public void setAnimaed(boolean isAnimaed) {
		this.isAnimaed = isAnimaed;
	}

	public float getPressedScale() {
		return pressedScale;
	}



	public void setPressedScale(float pressedScale) {
		this.pressedScale = pressedScale;
	}



	public void setPressedAlpha(float pressedAlpha) {
		this.pressedAlpha = pressedAlpha;
	}


	public View getView() {
		return view;
	}



	public void setView(View view) {
		this.view = view;
	}



	@Override
	public boolean selectDrawable(int idx) {
		// TODO Auto-generated method stub
		if(view!=null){


			if(idx==0){
				if(Build.VERSION.SDK_INT>11){
					if(pressedAlpha>0)view.setAlpha(pressedAlpha);
				}else{
					if(pressedAlpha>0)setAlpha((int)(pressedAlpha*255));
				}

				if(Build.VERSION.SDK_INT>11&&view!=null&&pressedScale!=1.0f&&pressedScale!=0&&!isScaled){
					view.clearAnimation();
					ScaleAnimation scaleAnimation= new ScaleAnimation(1f,pressedScale, 1,pressedScale,
							Animation.RELATIVE_TO_SELF,0.5f,
							Animation.RELATIVE_TO_SELF,0.5f);
					scaleAnimation.setDuration(50);
					scaleAnimation.setFillAfter(true);
					scaleAnimation.setInterpolator(new DecelerateInterpolator());
					view.startAnimation(scaleAnimation);
					isScaled=true;
				}

				if(touchDownAnimation!=null&&!isAnimaed){
					touchDownAnimation.setFillAfter(true);
					view.startAnimation(touchDownAnimation);
					isAnimaed=true;
				}
			}else if(idx==1){
				if(Build.VERSION.SDK_INT>11){
					if(pressedAlpha>0)view.setAlpha(1);
				}else{
					if(pressedAlpha>0)setAlpha(255);
				}

				if(Build.VERSION.SDK_INT>11&&view!=null&&pressedScale!=1&&isScaled){
					view.clearAnimation();
					ScaleAnimation scaleAnimation= new ScaleAnimation(pressedScale,1f, pressedScale,1f,
							Animation.RELATIVE_TO_SELF,0.5f,
							Animation.RELATIVE_TO_SELF,0.5f);
					scaleAnimation.setDuration(50);
					scaleAnimation.setFillAfter(true);
					scaleAnimation.setInterpolator(new DecelerateInterpolator());
					view.startAnimation(scaleAnimation);
					isScaled=false;
				}
				if(touchUpAnimation!=null&&isAnimaed){
					view.startAnimation(touchUpAnimation);
					isAnimaed=false;
				}
			}
		}
		return super.selectDrawable(idx);
	}


}
