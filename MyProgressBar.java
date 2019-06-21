package com.example.mymoney.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import com.example.mymoney.R;

public class MyProgressBar extends View {
    private int outerRoundBorderColor_init = getResources().getColor(R.color.back_gray11);
    private int outerRoundBorderColor_cover = getResources().getColor(R.color.type_blue1);
    private int innerRoundBorderColor =getResources().getColor(R.color.type_blue1);
    private int innerRoundColor=getResources().getColor(R.color.skyblue);
    // 10px
    private int mRoundWidth = 10;
    private float mProgressTextSize = 15;

    private int mProgressTextColor = Color.BLUE;

    private Paint mPaint, mTextPaint;
    private int mMax = 100;
    private int mProgress = 0;

    SweepGradient mSweepGradient;

    private Matrix matrix;



     //旋转的角度

    private int degree = 0;

    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyProgressBar);
        outerRoundBorderColor_init = array.getColor(R.styleable.MyProgressBar_outerRoundBorderColor_init, outerRoundBorderColor_init);
        outerRoundBorderColor_cover = array.getColor(R.styleable.MyProgressBar_outerRoundBorderColor_cover, outerRoundBorderColor_cover);
        innerRoundBorderColor = array.getColor(R.styleable.MyProgressBar_innerRoundBorderColor, innerRoundBorderColor);
        innerRoundColor = array.getColor(R.styleable.MyProgressBar_innerRoundColor, innerRoundColor);
        mRoundWidth = (int) array.getDimension(R.styleable.MyProgressBar_roundBorderWidth, dip2px(10));
        mProgressTextSize = array.getDimensionPixelSize(R.styleable.MyProgressBar_progressTextSize,
                sp2px(mProgressTextSize));
        mProgressTextColor = array.getColor(R.styleable.MyProgressBar_progressTextColor, mProgressTextColor);

        array.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        matrix = new Matrix();

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextSize(mProgressTextSize);



    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width,height),Math.min(width,height));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int mWidth = getWidth();

        //圆形渐变色
        if(mSweepGradient == null){
            mSweepGradient = new SweepGradient(mWidth / 2, mWidth / 2, new int[]{Color.TRANSPARENT,innerRoundColor}, null);
        }

        // 画外圆初始化边框颜色
        int radius = mWidth / 2;
        mPaint.setAntiAlias(true);
        mPaint.setColor(outerRoundBorderColor_init);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(radius,radius,radius-mRoundWidth,mPaint);


        //画内圆边框颜色
        mPaint.setAntiAlias(true);
        mPaint.setColor(innerRoundBorderColor);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(radius,radius,radius/3+5/2,mPaint);

       //画雷达扫描
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mSweepGradient);
        canvas.drawCircle(mWidth / 2, mWidth / 2, radius / 3, mPaint);

        //画笔重置
        mPaint.reset();

        //使用Matrix旋转
        mSweepGradient.setLocalMatrix(matrix);
        matrix.setRotate(degree, mWidth / 2, mWidth / 2);
        degree++;
        if (degree > 360) {
            degree = 0;
        }
        postInvalidate();


       //进度为0只有文字
        if (mProgress == 0) {
            // 画进度文字
            String text = mProgress + "%";
            @SuppressLint("DrawAllocation") Rect rect=new Rect();
            mTextPaint.getTextBounds(text,0,text.length(),rect);
            float dx=getWidth()/2-rect.width()/2;
            @SuppressLint("DrawAllocation") Paint.FontMetricsInt fontMetricsInt=new Paint.FontMetricsInt();
            int dy=(fontMetricsInt.bottom - fontMetricsInt.top)/2-fontMetricsInt.bottom;
            float baseLine=getHeight()/2+dy;
            canvas.drawText(text,dx,baseLine,mTextPaint);
            return;
        }

        //画外圆进度边框颜色
        @SuppressLint("DrawAllocation") RectF rectF=new RectF(mRoundWidth,mRoundWidth,
                getWidth()-mRoundWidth,getHeight()-mRoundWidth);
        float percent=(float)mProgress/mMax;
        mPaint.setAntiAlias(true);
        mPaint.setColor(outerRoundBorderColor_cover);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rectF,-90,360*percent,false,mPaint);


        // 画进度文字
        String text = ((int) (percent * 100)) + "%";
        @SuppressLint("DrawAllocation") Rect rect=new Rect();
        mTextPaint.getTextBounds(text,0,text.length(),rect);
        float dx=getWidth()/2-rect.width()/2;
        @SuppressLint("DrawAllocation") Paint.FontMetricsInt fontMetricsInt=new Paint.FontMetricsInt();
        canvas.drawText(text,dx,getHeight()/7*6,mTextPaint);
    }


    public synchronized void setMax(int max) {
        if (max < 0) {
            max=100;
        }
        this.mMax = max;
    }



    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            progress=0;
        }
        this.mProgress = progress;
        invalidate();
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private float dip2px(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

}
