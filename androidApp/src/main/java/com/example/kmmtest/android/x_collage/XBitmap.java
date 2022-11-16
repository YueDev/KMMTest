package com.example.kmmtest.android.x_collage;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;

import java.util.UUID;

/**
 * Created by Yue on 2022/9/15.
 */
public class XBitmap {

    private final Uri mUri;

    private final Bitmap mBitmap;
    private final Matrix mMatrix = new Matrix();
    private final String mId = UUID.randomUUID().toString();

    //原始rect 无边距
    private final RectF mOriginRect = new RectF();

    //显示rect 计算边距
    private final RectF mRect = new RectF();
    private final RectF mStartRect = new RectF();
    private final RectF mEndRect = new RectF();

    //判断是否是上下左右的边，这个在collage完成后判断一次就行，动画途中不要判断
    private boolean mIsLeft = false;
    private boolean mIsTop = false;
    private boolean mIsRight = false;
    private boolean mIsBottom = false;

    //对原图bitmap进行编辑的matrix，图片更改的话会传过来
    private Matrix mBitmapMatrix;

    public Matrix getBitmapMatrix() {
        return mBitmapMatrix;
    }

    public void setBitmapMatrix(Matrix bitmapMatrix) {
        mBitmapMatrix = bitmapMatrix;
    }

    public XBitmap(Uri uri, Bitmap bitmap) {
        mBitmap = bitmap;
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public String getId() {
        return mId;
    }

    public RectF getRect() {
        return mRect;
    }

    public RectF getOriginRect() {
        return mOriginRect;
    }


    //检查是否位于边界 拼图后调用一次即可
    public void checkBounds(float originLeft,
                            float originTop,
                            float originRight,
                            float originBottom,
                            float collageWidth,
                            float collageHeight) {
        mOriginRect.set(originLeft, originTop, originRight, originBottom);
        mIsLeft = mOriginRect.left == 0;
        mIsTop = mOriginRect.top == 0;
        mIsRight = mOriginRect.right == collageWidth;
        mIsBottom = mOriginRect.bottom == collageHeight;
    }


    //根据padding计算出rect
    public RectF getRectWithPadding(float outPadding, float innerPadding) {
        //内边距四边都有， 外边距判断下
        float l = mOriginRect.left + innerPadding;
        float t = mOriginRect.top + innerPadding;
        float r = mOriginRect.right - innerPadding;
        float b = mOriginRect.bottom - innerPadding;

        if (mIsLeft) l += outPadding;
        if (mIsTop) t += outPadding;
        if (mIsRight) r -= outPadding;
        if (mIsBottom) b -= outPadding;

        return new RectF(l, t, r, b);
    }


    //根据mRect计算矩阵，绘制用
    public void calculateXBitmapMatrix(boolean isCenterCrop) {

        float w = mRect.width();
        float h = mRect.height();

        //有些低分辨率的时候 会出现很小的一条白边，盲猜是draw bitmap的时候出现的
        //这里让缩放稍微大一个像素 可以解决
        float scaleW = (w + 1) / mBitmap.getWidth();
        float scaleH = (h + 1) / mBitmap.getHeight();

        float scale;
        if (isCenterCrop)
            scale = Math.max(scaleW, scaleH);
        else
            scale = Math.min(scaleW, scaleH);

        float centerX = mRect.centerX();
        float centerY = mRect.centerY();

        float dx = centerX - mBitmap.getWidth() / 2f;
        float dy = centerY - mBitmap.getHeight() / 2f;

        mMatrix.reset();
        mMatrix.postTranslate(dx, dy);
        mMatrix.postScale(scale, scale, centerX, centerY);
    }


    public static Matrix calculateXBitmapMatrix(Bitmap bitmap, RectF rect, boolean isCenterCrop) {
        float w = rect.width();
        float h = rect.height();

        float scaleW = (w + 1) / bitmap.getWidth();
        float scaleH = (h + 1) / bitmap.getHeight();

        float scale;
        if (isCenterCrop)
            scale = Math.max(scaleW, scaleH);
        else
            scale = Math.min(scaleW, scaleH);

        float centerX = rect.centerX();
        float centerY = rect.centerY();

        float dx = centerX - bitmap.getWidth() / 2f;
        float dy = centerY - bitmap.getHeight() / 2f;

        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(dx, dy);
        matrix.postScale(scale, scale, centerX, centerY);
        return matrix;
    }


    //开始动画传递参数
    public void startAnimator(RectF newRect) {
        startAnimator(newRect.left, newRect.top, newRect.right, newRect.bottom);
    }

    //开始动画传递参数
    public void startAnimator(float newLeft, float newTop, float newRight, float newBottom) {
        mStartRect.set(mRect);
        mEndRect.set(newLeft, newTop, newRight, newBottom);
    }


    //动画执行的时候刷新
    public void doAnimator(float progress, boolean isCenterCrop) {

        float l = mStartRect.left + (mEndRect.left - mStartRect.left) * progress;
        float t = mStartRect.top + (mEndRect.top - mStartRect.top) * progress;
        float r = mStartRect.right + (mEndRect.right - mStartRect.right) * progress;
        float b = mStartRect.bottom + (mEndRect.bottom - mStartRect.bottom) * progress;
        mRect.set(l, t, r, b);

        calculateXBitmapMatrix(isCenterCrop);
    }


}
