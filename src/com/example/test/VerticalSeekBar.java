package com.example.test;

import android.view.MotionEvent;
import android.widget.SeekBar;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * 垂直SeekBar类
 */
public class VerticalSeekBar extends SeekBar
{
	private OnSeekBarChangeListener mOnSeekBarChangeListener; // SeekBar改变监听器
	
	private int padCorrect = 32; // 边缘留白校正
	
	/**
	 * 构造函数
	 * 
	 * @param context 上下文环境
	 */
	public VerticalSeekBar(Context context)
	{
		super(context);
	}
	
	/**
	 * 构造函数
	 * 使用xml文件布局控件时自动调用
	 */
	public VerticalSeekBar(Context context,AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * 构造函数
	 * 使用xml文件布局控件时自动调用
	 */
	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	/**
	 * 设置SeekBar改变监听器
	 */
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l)
	{
		mOnSeekBarChangeListener = l;
	}
	
	protected void onProgressRefresh(float scale, boolean fromUser)
	{
		if (mOnSeekBarChangeListener != null)
		{
			mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), fromUser);
		}
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(h, w, oldh, oldw);
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}
	
	protected void onDraw(Canvas c)
	{
		c.rotate(-90);
		c.translate(-getHeight(), 0);
		
		super.onDraw(c);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isEnabled())
		{
			return false;
		}
		
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			int h = getHeight();
			float tempy =  h - event.getY();
			tempy = Math.max(tempy, padCorrect);
			tempy = Math.min(tempy, h - padCorrect);
			tempy -= padCorrect;
			int p = (int)(getMax() * tempy / (h - 2 * padCorrect));
			if (mOnSeekBarChangeListener != null) {
	            mOnSeekBarChangeListener.onProgressChanged(this, p, true);
	        }
			setProgress(p);
			break;
		case MotionEvent.ACTION_UP:
			if (mOnSeekBarChangeListener != null) {
	            mOnSeekBarChangeListener.onStopTrackingTouch(this);
	        }
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

	@Override
	public synchronized void setProgress(int progress)
	{
		super.setProgress(progress);
		onSizeChanged(getWidth(),getHeight(), 0, 0);
	}
}