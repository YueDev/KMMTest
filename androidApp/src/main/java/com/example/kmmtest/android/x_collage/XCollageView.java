package com.example.kmmtest.android.x_collage;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import trubo_collage.TCBitmap;
import trubo_collage.TCCollage;
import trubo_collage.TCRectF;
import trubo_collage.TCResult;

/**
 * Created by Yue on 2022/9/15.
 */
public class XCollageView extends View {

    private final TCCollage mCollage = new TCCollage();

    private final CollageType mCollageType = CollageType.CENTER_CROP;

    private final int mBgColor = Color.parseColor("#FFFFFF");

    private final List<XBitmap> mXBitmaps = new ArrayList<>();
    private final Paint mBitmapPaint = new Paint();

    //拼图画布尺寸 这里用float会导致某些比例出现白条 用int好一些
    private int mCollageWidth = 0;
    private int mCollageHeight = 0;
    //画布的偏移量
    private int mOffsetX = 0;
    private int mOffsetY = 0;

    //边距
    private float mInnerPadding = 0.0f;
    private float mOuterPadding = 0.0f;

    private boolean mCanDraw = false;

    private boolean mCanCollage = true;

    private final ValueAnimator mAnimator = ObjectAnimator.ofFloat(0.0f, 1.0f);

    //绘制背景
    private final Paint mBgPaint = new Paint();
    private final RectF mBGRect = new RectF();
    private final RectF mBGStartRect = new RectF();
    private final RectF mBGEndRect = new RectF();



    private ClickListener mClickListener;

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    public XCollageView(Context context) {
        super(context);
        init();
    }

    public XCollageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public XCollageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDensity = Bitmap.DENSITY_NONE;

        mBgPaint.setColor(mBgColor);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);

        mAnimator.setDuration(1000L);
        mAnimator.addUpdateListener(animation -> {
            float progress = animation.getAnimatedFraction();
            for (XBitmap xBitmap : mXBitmaps) {
                switch (mCollageType) {
                    case FIT_CENTER:
                        xBitmap.doAnimator(progress, false);
                        break;
                    case CENTER_CROP:
                        xBitmap.doAnimator(progress, true);
                        break;
                }
            }

            //背景动画
            float l = mBGStartRect.left + (mBGEndRect.left - mBGStartRect.left) * progress;
            float t = mBGStartRect.top + (mBGEndRect.top - mBGStartRect.top) * progress;
            float r = mBGStartRect.right + (mBGEndRect.right - mBGStartRect.right) * progress;
            float b = mBGStartRect.bottom + (mBGEndRect.bottom - mBGStartRect.bottom) * progress;
            mBGRect.set(l, t, r, b);

            postInvalidateOnAnimation();
        });
    }


//    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            if (mIsShowWaterMark
//                    && mClickListener != null
//                    && mWaterMarkRect.contains(e.getX(), e.getY())) {
//                mClickListener.onClickWaterMark();
//            }
//        }
//    });

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN
                && mClickListener != null
        ) {
            mClickListener.onClickWaterMark();
            return true;
        }
        return super.onTouchEvent(e);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mCanDraw) return;

        //绘制背景
        canvas.drawRect(mBGRect, mBgPaint);

        //绘制图片
        for (XBitmap xBitmap : mXBitmaps) {
            canvas.save();
            canvas.clipRect(xBitmap.getRect());
            canvas.drawBitmap(xBitmap.getBitmap(), xBitmap.getMatrix(), mBitmapPaint);
            canvas.restore();
        }

    }

    public void setData(List<XBitmapSimple> bitmaps, float outerPadding, float innerPadding, float ratio) {
        post(() -> {
            //计算画布尺寸
            calculateCollageSize(ratio);
            //背景rect
            mBGRect.set(mOffsetX, mOffsetY, mOffsetX + mCollageWidth, mOffsetY + mCollageHeight);
            mBGStartRect.set(mBGRect);
            mBGEndRect.set(mBGRect);

            mOuterPadding = outerPadding;
            mInnerPadding = innerPadding;

            mXBitmaps.clear();
            for (XBitmapSimple xBitmapSimple : bitmaps) {
                XBitmap xBitmap = new XBitmap(xBitmapSimple.getUri(), xBitmapSimple.getBitmap());
                if (xBitmapSimple.isChanged()) {
                    //如果原图编辑过，则把编辑的matrix传过来
                    xBitmap.setBitmapMatrix(xBitmapSimple.getMatrix());
                }
                mXBitmaps.add(xBitmap);
            }

            List<TCBitmap> tcBitmaps = new ArrayList<>();
            for (XBitmap xBitmap : mXBitmaps) {
                tcBitmaps.add(new TCBitmap(xBitmap.getId(), xBitmap.getBitmap().getWidth(), xBitmap.getBitmap().getHeight()));
            }

            mCollage.init(tcBitmaps);

            mCanDraw = true;
            collage();
        });
    }


    //根据比例计算画布大小和偏移量需要确保拿到view的宽高，放到post里比较好
    //计算mWaterMarkRect
    private void calculateCollageSize(float ratio) {
        mCollageWidth = getMeasuredWidth();
        mCollageHeight = getMeasuredHeight();
        mOffsetX = 0;
        mOffsetY = 0;

        if (ratio == 0f) {
            return;
        }

        float collageRatio = mCollageWidth * 1.0f / mCollageHeight;
        if (collageRatio > ratio) {
            //view比ratio宽 保留高度
            mCollageWidth = (int) (mCollageHeight * ratio);
        } else {
            mCollageHeight = (int) (mCollageWidth / ratio);
        }

        mOffsetX = (getMeasuredWidth() - mCollageWidth) / 2;
        mOffsetY = (getMeasuredHeight() - mCollageHeight) / 2;
    }


    //设置边距 无动画
    public void setCollagePadding(float outerPadding, float innerPadding) {
        if (!mCanCollage) return;
        if (mAnimator.isStarted()) return;
        mOuterPadding = outerPadding;
        mInnerPadding = innerPadding;

        for (XBitmap xBitmap : mXBitmaps) {
            RectF newRect = xBitmap.getRectWithPadding(outerPadding, innerPadding);
            xBitmap.getRect().set(newRect);
            setXBitmapMatrix(xBitmap);
        }
        invalidate();
    }

    //设置边距 动画  返回值 是否有效设置

    public boolean setCollagePaddingAnimated(float outerPadding, float innerPadding) {

        if (!mCanCollage) return false;
        if (mAnimator.isStarted()) return false;

        mOuterPadding = outerPadding;
        mInnerPadding = innerPadding;
        mAnimator.setDuration(200);

        for (XBitmap xBitmap : mXBitmaps) {
            RectF newRect = xBitmap.getRectWithPadding(outerPadding, innerPadding);
            xBitmap.startAnimator(newRect);
        }

        mBGStartRect.set(mBGRect);
        mBGEndRect.set(mBGRect);

        mAnimator.start();
        return true;
    }


    //拼图 无动画
    public void collage() {
        if (mCollageWidth == 0.0f || mCollageHeight == 0.0f) return;
        if (!mCanCollage) return;
        mCanCollage = false;
        new Thread(() -> {
            TCResult result = mCollage.collage(mCollageWidth, mCollageHeight, 0.0);
            if (result != null) {
                for (XBitmap xBitmap : mXBitmaps) {
                    TCRectF tcRect = result.get(xBitmap.getId());

                    xBitmap.checkBounds(tcRect.getLeft(), tcRect.getTop(), tcRect.getRight(), tcRect.getBottom(), mCollageWidth, mCollageHeight);
                    RectF newRect = xBitmap.getRectWithPadding(mOuterPadding, mInnerPadding);
                    xBitmap.getRect().set(newRect);

                    xBitmap.getOriginRect().offset(mOffsetX, mOffsetY);
                    xBitmap.getRect().offset(mOffsetX, mOffsetY);

                    setXBitmapMatrix(xBitmap);

                }
                postInvalidate();
            }
            mCanCollage = true;
        }).start();
    }

    //拼图 动画版本
    public void collageAnimated() {
        if (!mCanCollage) return;
        if (mAnimator.isStarted()) return;
        new Thread(() -> {
            TCResult result = mCollage.collage(mCollageWidth, mCollageHeight, 0.0);
            if (result != null) {
                for (XBitmap xBitmap : mXBitmaps) {
                    TCRectF tcRect = result.get(xBitmap.getId());

                    xBitmap.checkBounds(tcRect.getLeft(), tcRect.getTop(), tcRect.getRight(), tcRect.getBottom(), mCollageWidth, mCollageHeight);
                    RectF newRect = xBitmap.getRectWithPadding(mOuterPadding, mInnerPadding);

                    xBitmap.getOriginRect().offset(mOffsetX, mOffsetY);
                    newRect.offset(mOffsetX, mOffsetY);

                    xBitmap.startAnimator(newRect);
                }
                mAnimator.setDuration(500L);

                mBGStartRect.set(mBGRect);
                mBGEndRect.set(mBGRect);

                post(mAnimator::start);
            }
        }).start();
    }

    //设置比例  返回值代表是否有效
    public boolean setRatioAnimated(float ratio) {
        if (!mCanCollage) return false;
        if (mAnimator.isStarted()) return false;
        //按照原尺寸计算背景和水印，动画的起始rect
        mBGStartRect.set(mOffsetX, mOffsetY, mOffsetX + mCollageWidth, mOffsetY + mCollageHeight);
        //计算画布尺寸
        calculateCollageSize(ratio);
        new Thread(() -> {
            TCResult result = mCollage.collage(mCollageWidth, mCollageHeight, 0.0);
            if (result != null) {
                for (XBitmap xBitmap : mXBitmaps) {
                    TCRectF tcRect = result.get(xBitmap.getId());

                    xBitmap.checkBounds(tcRect.getLeft(), tcRect.getTop(), tcRect.getRight(), tcRect.getBottom(), mCollageWidth, mCollageHeight);
                    RectF newRect = xBitmap.getRectWithPadding(mOuterPadding, mInnerPadding);

                    xBitmap.getOriginRect().offset(mOffsetX, mOffsetY);
                    newRect.offset(mOffsetX, mOffsetY);

                    xBitmap.startAnimator(newRect);
                }
                //尺寸更改后，计算动画的end rect
                mBGEndRect.set(mOffsetX, mOffsetY, mOffsetX + mCollageWidth, mOffsetY + mCollageHeight);
                mAnimator.setDuration(500L);
                post(mAnimator::start);
            }
        }).start();
        return true;
    }


    public void setXBitmapMatrix(XBitmap xBitmap) {
        switch (mCollageType) {
            case FIT_CENTER:
                xBitmap.calculateXBitmapMatrix(false);
                break;
            case CENTER_CROP:
                xBitmap.calculateXBitmapMatrix(true);
                break;
        }
    }


    @Nullable
    public Bitmap getResultBitmap(int maxSize) {

        if (mAnimator.isStarted()) return null;
        if (!mCanCollage) return null;

        int width = mCollageWidth;
        int height = mCollageHeight;

        //最大5120
        int size = Math.min(maxSize, 5120);

        // maxSize:max(width, height)
        float scale = 1.0f;

        if (size > 0) {
            //size有效 计算实际宽高
            if (width > height) {
                scale = size * 1.0f / width;
                width = size;
                height *= scale;
            } else {
                scale = size * 1.0f / height;
                width *= scale;
                height = size;
            }
        }


        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(mBgColor);


        canvas.scale(scale, scale);
        canvas.translate(-mOffsetX, -mOffsetY);

        for (XBitmap xBitmap : mXBitmaps) {
            canvas.save();
            canvas.clipRect(xBitmap.getRect());
            canvas.drawBitmap(xBitmap.getBitmap(), xBitmap.getMatrix(), mBitmapPaint);
            canvas.restore();
        }

        return result;
    }


    public interface ClickListener {
        void onClickWaterMark();
    }
}
