/*
 * 
 * Copyright 2017 Hoang Cong Tung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * 
 * This custom view/widget was inspired and guided by:
 * 
 * HoloCircleSeekBar - Copyright 2012 Jes�s Manzano
 * HoloColorPicker - Copyright 2012 Lars Werkman (Designed by Marie Schweiz)
 * CircularSeekBar - Copyright 2013 Matt Joseph
 *
 */

package com.congtung.circularseekbarplus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircularSeekBarPlus extends CircularSeekBar {
    public boolean isFirstInit = true;
    protected float[] mStartPointerPositionXY = new float[2];
    protected float radius;
    private boolean isSecondPointMoving = true;

    public CircularSeekBarPlus(Context context) {
        super(context);
    }

    public CircularSeekBarPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularSeekBarPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Only draw a part from CircularSeekBar. Don't use effect
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);
        canvas.drawPath(mCirclePath, mCirclePaint);
        // canvas.drawPath(mCircleProgressPath, mCircleProgressGlowPaint);
        canvas.drawPath(mCircleProgressPath, mCircleProgressPaint);
        //canvas.drawPath(mCirclePath, mCircleFillPaint);
        //canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius +
        //    mPointerHaloWidth, mPointerHaloPaint);
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius,
            mPointerPaint);
        if (mUserIsMovingPointer) {
            // canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius +
            //    mPointerHaloWidth + (mPointerHaloBorderWidth / 2f), mPointerHaloBorderPaint);
        }
        canvas.drawCircle(mStartPointerPositionXY[0], mStartPointerPositionXY[1], mPointerRadius,
            mPointerPaint);
    }

    @Override
    protected void recalculateAll() {
        calculateTotalDegrees();
        calculatePointerAngle();
        calculateProgressDegrees();
        initRects();
        initPaths();
        if (isSecondPointMoving == true)
            calculatePointerXYPosition();
        if (isFirstInit) {
            radius = (float) Math.sqrt(mPointerPositionXY[0] * mPointerPositionXY[0] +
                mPointerPositionXY[1] * mPointerPositionXY[1]);
            calculateStartPointerXYPosition();
            isFirstInit = false;
        } else if (isSecondPointMoving == false)
            calculateStartPointerXYPosition();
    }

    private void calculateStartPointerXYPosition() {
        mStartPointerPositionXY[0] = radius * (float) Math.cos(Math.toRadians(mStartAngle));
        mStartPointerPositionXY[1] = radius * (float) Math.sin(Math.toRadians(mStartAngle));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchEnabled)
            return false;
        // Convert coordinates to our internal coordinate system
        float x = event.getX() - getWidth() / 2;
        float y = event.getY() - getHeight() / 2;
        if (Math.sqrt(x * x + y * y) > radius * 1.2f)
            return false;
        if (MathUtils.distance(x, y, mPointerPositionXY[0], mPointerPositionXY[1]) < radius / 2 &&
            MathUtils.distance(x, y, mStartPointerPositionXY[0], mStartPointerPositionXY[1]) < radius / 2)
            return false;
        float touchAngle;
        touchAngle = (float) ((java.lang.Math.atan2(y, x) / Math.PI * 180) % 360); // Verified
        touchAngle = (touchAngle < 0 ? 360 + touchAngle : touchAngle); // Verified
        if (MathUtils.isANearerPointThanB(mPointerPositionXY[0], mPointerPositionXY[1], x, y,
            mStartPointerPositionXY[0], mStartPointerPositionXY[1])) {
            setProgressBasedOnAngle(touchAngle);
            isSecondPointMoving = true;
        } else {
            // mStartAngle = touchAngle;
            setStartAngle(touchAngle);
            //setProgressBasedOnStartAngle(touchAngle);
            isSecondPointMoving = false;
        }
        recalculateAll();
        invalidate();
        return true;
    }

    protected void setStartAngle(float angle) {
        mStartAngle = angle;
        calculateTotalDegrees();
        calculateProgressDegrees();
        mProgress = (float) mMax * mProgressDegrees / mTotalCircleDegrees;
    }

    @Override
    protected void calculatePointerAngle() {
        float progressPercent = ((float) mProgress / (float) mMax);
        mPointerPosition = (progressPercent * mTotalCircleDegrees) + mStartAngle;
        mPointerPosition = mPointerPosition % 360f;
    }

    @Override
    protected void initPaths() {
        mCirclePath = new Path();
        mCirclePath.addArc(mCircleRectF, mStartAngle, 360);
        mCircleProgressPath = new Path();
        mCircleProgressPath.addArc(mCircleRectF, mStartAngle, mProgressDegrees);
    }
}

