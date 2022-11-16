package com.example.kmmtest.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale

//image loader
suspend fun getBitmapWithRes(context: Context, imageResId: Int): Bitmap? {
    val request = ImageRequest.Builder(context)
        .data(imageResId)
        .allowConversionToBitmap(true)
        .size(512)
        .scale(Scale.FIT)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .allowHardware(false)
        .build()
    val drawable = context.imageLoader.execute(request).drawable
    return (drawable as? BitmapDrawable)?.bitmap
}

val imageResList = listOf(
    R.drawable.test1,
    R.drawable.test2,
    R.drawable.test3,
    R.drawable.test4,
    R.drawable.test5,
    R.drawable.test6,
    R.drawable.test7,
    R.drawable.test8,
    R.drawable.test9,
)


val ratioList = listOf(
    0.0f,
    1.0f / 1.0f,
    9.0f / 16.0f,
    4.0f / 3.0f,
    16.0f / 9.0f,
)



val borderList = listOf(
    0.0f,
    4.0f,
    8.0f,
    16.0f,
)