package cn.andy.textflickerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import cn.andy.textflickerview.R;

/**
 * 文字闪烁TextView
 * <p>
 * Created by yangzhizhong
 */

public class TextFlickerView extends android.support.v7.widget.AppCompatTextView {

    private int textFlickerItemWidth;
    private int textFlickerItemColor;
    private int textFlickerColor;

    private Matrix mShadowMatrix;
    private LinearGradient mLinearGradient;
    private ValueAnimator mValueAnimator;
    private int mRepeatCount = Integer.MAX_VALUE;

    public TextFlickerView(Context context) {
        this(context, null);
    }

    public TextFlickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFlickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextFlickerView);
        setTextFlickerItemWidth(typedArray.getInt(R.styleable.TextFlickerView_TextFlickerItemWidth, 40));
        setTextFlickerItemColor(typedArray.getColor(R.styleable.TextFlickerView_TextFlickerItemColor, Color.GREEN));
        setTextFlickerColor(typedArray.getColor(R.styleable.TextFlickerView_TextFlickerColor, Color.RED));
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tryInitEngine(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLinearGradient != null) {
            mLinearGradient.setLocalMatrix(mShadowMatrix);
        }
    }

    private void tryInitEngine(int width) {
        if (mShadowMatrix == null) {
            if (width > 0) {
                //控制阴影的Matrix，通过Matrix的变化来实现闪光的滑过效果
                mShadowMatrix = new Matrix();
                //因为使用了LinearGradient,所以Paint本身的color将毫无意义，所以colors的起始点的色值必须和本来色值一致
                int currentTextColor = getCurrentTextColor();
                //渐变色层.x0,y0是起点坐标，x1，y1是终点坐标
                mLinearGradient = new LinearGradient(0, 0, textFlickerItemWidth, 0, new int[]{currentTextColor, textFlickerItemColor, textFlickerColor}, null, Shader.TileMode.CLAMP);
                //画笔设置Shader
                getPaint().setShader(mLinearGradient);
                //使用属性动画作为引擎，数值从-SHADOW变化到TextView本身的宽度。间隔时间未1500ms
                mValueAnimator = ValueAnimator.ofFloat(-textFlickerItemWidth, width).setDuration(1500);
                mValueAnimator.setInterpolator(new LinearInterpolator());
                mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        //Matrix移动来实现闪光滑动
                        mShadowMatrix.setTranslate(value, 0);
                        invalidate();
                    }
                });
                mValueAnimator.setRepeatCount(mRepeatCount);
            }
        }
    }

    public void setDuration(@IntRange(from = 1, to = 15) int second) {
        mRepeatCount = second - 1;
        if (mValueAnimator != null) {
            mValueAnimator.setRepeatCount(mRepeatCount);
        }
    }

    public void start() {
        if (mValueAnimator == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryInitEngine(getWidth());
                    if (mValueAnimator != null) {
                        mValueAnimator.start();
                    }
                }
            }, 100);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    private void release() {
        if (mValueAnimator != null) {
            mValueAnimator.removeAllListeners();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        mShadowMatrix = null;
        mLinearGradient = null;
    }

    public void setTextFlickerItemWidth(int textFlickerItemWidth) {
        this.textFlickerItemWidth = textFlickerItemWidth;
    }

    public void setTextFlickerItemColor(int textFlickerItemColor) {
        this.textFlickerItemColor = textFlickerItemColor;
    }

    public void setTextFlickerColor(int textFlickerColor) {
        this.textFlickerColor = textFlickerColor;
    }
}
