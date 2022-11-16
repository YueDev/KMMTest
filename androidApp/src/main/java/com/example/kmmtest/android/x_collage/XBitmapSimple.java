package com.example.kmmtest.android.x_collage;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;

/**
 * Created by Yue on 2022/10/31.
 */
//简易版的XBitmap 把uri 和bitmap放到一起好处理
public class XBitmapSimple {
    private Uri mUri;
    private Bitmap mBitmap;
    //是否更改过 更改过使用low res
    private boolean mChanged;

    //更改过的matrix
    private Matrix mMatrix;

    public XBitmapSimple(Uri uri, Bitmap bitmap) {
        mUri = uri;
        mBitmap = bitmap;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public boolean isChanged() {
        return mChanged;
    }

    public void setChanged(boolean changed, Matrix matrix) {
        mChanged = changed;
        mMatrix = matrix;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }
}
