package come.slideselector;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.IntEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public final class AnimatedSelector extends Drawable implements Animatable {

	private static final int MOVE_DURATION_MS = 200;

	private View mView;

	private Drawable mDrawable;

	ValueAnimator mAnimator;

	PositionEvaluator mPositionEvaluator;

	private int mLeftOffset = 0 ;

	private int mTopOffset = 0;

	private int mWidthOffset = 0;
	private int mHeightOffset = 0;
	
	private Context mContext;

	public AnimatedSelector(Context context,View view, Drawable drawable) {
		mContext = context;
		mTopOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.update_itemfocus_offset_top);
		mLeftOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.update_itemfocus_offset_left);
		mWidthOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.update_itemfocus_offset_with);
		mHeightOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.update_itemfocus_offset_height);
		
		mView = view;
		mDrawable = drawable;
		mPositionEvaluator = new PositionEvaluator();
		mAnimator = ValueAnimator.ofObject(mPositionEvaluator);
		mAnimator.setDuration(MOVE_DURATION_MS);
		mAnimator.setInterpolator(new DecelerateInterpolator(2.0f));
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Rect bounds = getBounds();
				Rect newBounds = (Rect) animation.getAnimatedValue();
				if (!bounds.equals(newBounds)) {
					AnimatedSelector.super.setBounds(newBounds.left,
							newBounds.top, newBounds.right, newBounds.bottom);
					mView.setX(newBounds.left + mLeftOffset);
					mView.setY(newBounds.top + mTopOffset);
				};
			}
		});
	}

	private int selectIndex = -1;
	private int hight = -1;

	public void setIndex(int index, int hight) {
		selectIndex = index;
		this.hight = hight;
	}

	@SuppressLint("NewApi")
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		// Animate setting the bounds.
		Rect bounds = getBounds();
		if (bounds.isEmpty()) {
			super.setBounds(left, top, right, bottom);
			mView.setX(left + mLeftOffset);
			mView.setY(left + mTopOffset);
			setSelectorDimensions(right - left, bottom - top + 8);

			if (selectIndex > 0 && selectIndex * 91 <= hight) {
				firstStartAnim();
			} else {
 				ensureViewVisible();
			}
		} else if (bounds.left != left || bounds.top != top) {
			if (mAnimator.isRunning()) {
				mAnimator.cancel();
				mAnimator
						.setDuration(MOVE_DURATION_MS
								- ((long) mAnimator.getAnimatedFraction() * MOVE_DURATION_MS));
//				System.out.println(">>>>>>>>>>>>>>>>>>"+mAnimator.getDuration());
			} else {
				mAnimator.setDuration(MOVE_DURATION_MS);
			}
			mAnimator.setObjectValues(copyBounds(), new Rect(left, top, right,
					bottom));
			mAnimator.setEvaluator(mPositionEvaluator);
			mAnimator.start();
		}
	}

	public void firstStartAnim() {

		if (mAnimator.isRunning()) {
			mAnimator.end();
			mAnimator
					.setDuration(0);
		}
		Rect rect = copyBounds();
		mAnimator.setObjectValues(rect, new Rect(rect.left, rect.top - 1,
				rect.right, rect.bottom));
		mAnimator.setEvaluator(mPositionEvaluator);
		mAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {

				ensureViewVisible();
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		});

		mAnimator.start();
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public int getOpacity() {
		return mDrawable.getOpacity();
	}

	@Override
	public boolean getPadding(Rect padding) {
		return mDrawable.getPadding(padding);
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public boolean isRunning() {
		return mAnimator.isRunning();
	}

	@Override
	public void start() {
		mAnimator.start();
	}

	@Override
	public void stop() {
		mAnimator.end();
	}

	public void setLeftOffset(int left) {
		mLeftOffset += left;
	}

	public void setTopOffset(int top) {
		mTopOffset += top;
	}

	public void ensureViewVisible() {
		if (mView.getVisibility() != View.VISIBLE && !mView.isInTouchMode()) {
			mView.setVisibility(View.VISIBLE);
		}
	}

	public void hideView() {
		if (mView != null) {
			 mView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Sets the size of the selector view.
	 */
	private void setSelectorDimensions(int width, int height) {
		LayoutParams params = (RelativeLayout.LayoutParams) mView
				.getLayoutParams();
		params.width = width + mWidthOffset;
		params.height = height + mHeightOffset;
		mView.setLayoutParams(params);
	}

	public int getmWidthOffset() {
		return mWidthOffset;
	}

	public void setmWidthOffset(int mWidthOffset) {
		this.mWidthOffset += mWidthOffset;
	}
	
	public int getmHeightOffset() {
		return mHeightOffset;
	}

	public void setmHeightOffset(int mHeightOffset) {
		this.mHeightOffset = mHeightOffset;
	}

	private static class PositionEvaluator implements TypeEvaluator {

		private IntEvaluator mEvaluator;

		public PositionEvaluator() {
			mEvaluator = new IntEvaluator();
		}

		@SuppressLint("NewApi")
		@Override
		public Object evaluate(float fraction, Object startValue,
				Object endValue) {
			Rect startRect = (Rect) startValue;
			Rect endRect = (Rect) endValue;
			int left = (Integer) mEvaluator.evaluate(fraction, startRect.left,
					endRect.left);
			int top = (Integer) mEvaluator.evaluate(fraction, startRect.top,
					endRect.top);
			return new Rect(left, top, left + startRect.width(), top
					+ startRect.height());
		}
	}
}
