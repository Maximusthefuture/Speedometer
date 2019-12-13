package com.example.speedometer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class SpeedometerCustomView extends View {

    public static final int MAX_SPEED = 270;
    public static final Paint CIRCLE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final String TAG = "SpeedometerCustomView";
    private static final Paint ARROW_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint SPEEDOMETER_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float STROKE_WIDTH = 50f;
    private static final float RADIUS = 300f;
    private static final int MAX_ANGLE = 240 ;
    private Paint mSpeedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSpeedStaticTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect mTextBounds = new Rect();
    private double maxSpeed = MAX_SPEED;
    private int mSpeed;
    private static final int START_SPEED = 0;
    private int mTextSizeSpeedDymamic;
    private int mTextSizeSpeedStatic;
    private int mStrokeWidth;
    private float mNeedleHeight = 0.32f;

    public SpeedometerCustomView(Context context) {
        super(context, null, 0);

    }

    public SpeedometerCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);

    }

    public SpeedometerCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float mSpeed) {
        this.mSpeed = (int) mSpeed;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mStrokeWidth / 2, mStrokeWidth / 2);
        drawArrow(canvas);
        drawSpeedText(canvas);
        drawSpeedometer(canvas);

    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.mSpeed = mSpeed;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mSpeed = savedState.mSpeed;
    }

    private void drawArrow(Canvas canvas) {
        RectF oval = getOval(canvas, 0.9f);
        float radius = oval.width() * 0.35f + 10;

        float angle = -30 + (float) (getSpeed() / getMaxSpeed() * 240);
        canvas.drawLine(
                (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * 40 * 0.5f),
                (float) (oval.centerY() - Math.sin(angle / 180  * Math.PI) * 40 * 0.5f), // поднять повыше
                (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * (radius)),
                (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * (radius)),
                ARROW_PAINT);

        canvas.drawCircle(oval.centerY(), oval.centerY(), 20, CIRCLE);
        Log.d(TAG, "drawArrow() called with: angle = [" + angle + "]");
        Log.d(TAG, "drawArrow() called with: ovalStartY = [" + (oval.centerY() - Math.sin(angle / 180 * Math.PI) * 40 * 0.5f) + "]");
        Log.d(TAG, "drawArrow() called with: radius = [" + radius + "]");
        Log.d(TAG, "drawArrow() called with: angle = [" + angle + "]");

    }

    private void drawSpeedText(Canvas canvas) {
        RectF oval = getOval(canvas, 0.9f);
        float radius = oval.width() * 0.35f + 10;
        final String speedString = stringFormat(mSpeed);
        final String speedStartString = stringFormat(START_SPEED);
        final String speedMaxString = stringFormat(MAX_SPEED);
        getTextBounds(speedString);
        float txtY = oval.centerY() + radius;
        float textX = oval.width() / 2f   - mTextBounds.width() + radius + mTextBounds.right;
        float x = oval.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left + oval.left;
        float y = getOval(canvas, 1.2f).height() / 2f  + mTextBounds.height() / 2f - mTextBounds.bottom + getOval(canvas, 1.2f).top;
        canvas.drawText(speedString, x , y, mSpeedTextPaint);
        canvas.drawText(speedStartString, 0, txtY, mSpeedStaticTextPaint);
        canvas.drawText(speedMaxString, textX, txtY, mSpeedStaticTextPaint);
    }

    private void drawSpeedometer(Canvas canvas) {
        RectF oval = getOval(canvas, 0.9f);
        canvas.drawArc(oval, RADIUS / 2,  MAX_ANGLE , false, SPEEDOMETER_PAINT);
    }

    private void getTextBounds(String speedString) {
        mSpeedTextPaint.getTextBounds(speedString, 0, speedString.length(), mTextBounds);
        mSpeedStaticTextPaint.getTextBounds(speedString, 0, speedString.length(), mTextBounds);
    }

    private void configureSpeedTextPaint() {
        mSpeedTextPaint.setColor(Color.BLACK);
        mSpeedTextPaint.setTextSize(mTextSizeSpeedDymamic);
        mSpeedStaticTextPaint.setColor(Color.BLACK);
        mSpeedStaticTextPaint.setTextSize(mTextSizeSpeedStatic);

    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        final Resources.Theme theme = context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.SpeedometerCustomView,
                R.attr.speedometerStyle, 0);

        try {
            mTextSizeSpeedDymamic = typedArray.getDimensionPixelSize(R.styleable.SpeedometerCustomView_textSize, getResources().getDimensionPixelSize(R.dimen.defaultTextSize));
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.SpeedometerCustomView_strokeWidth, getResources().getDimensionPixelSize(R.dimen.default_stroke_width));
            mTextSizeSpeedStatic = typedArray.getDimensionPixelSize(R.styleable.SpeedometerCustomView_textSize, getResources().getDimensionPixelSize(R.dimen.static_speed_text_size));

        } finally {
            typedArray.recycle();
        }
    }


    private String stringFormat(int speed) {
        return String.format(getResources().getString(R.string.speed_template), speed);
    }

    private RectF getOval(Canvas canvas, float factor) {
        RectF oval;
        final int canvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        final int canvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        if (canvasHeight * 2 >= canvasWidth) {
            oval = new RectF(0, 0, canvasWidth * factor, canvasWidth * factor);
        } else {
            oval = new RectF(0, 0, canvasHeight * 2 * factor, canvasHeight * 2 * factor);
        }
//        oval.offset((canvasWidth - oval.width()) / 2 + getPaddingLeft(), (canvasHeight * 2 - oval.height() / 2 + getPaddingTop()));
        return oval;
    }


    private void init(Context context, AttributeSet attrs) {
        extractAttributes(context, attrs);
        initArrowPaint();
        initSpeedometerPaint();
        configureSpeedTextPaint();
    }

    private void initArrowPaint() {
        ARROW_PAINT.setStrokeWidth(10);
        ARROW_PAINT.setStyle(Paint.Style.STROKE);
        ARROW_PAINT.setColor(Color.RED);
        CIRCLE.setColor(Color.RED);
        CIRCLE.setStyle(Paint.Style.FILL);
        CIRCLE.setStrokeWidth(20);
    }

    private void initSpeedometerPaint() {
        Shader shader = new LinearGradient(0, RADIUS, RADIUS * 4, RADIUS,
                new int[]{Color.GREEN, Color.YELLOW, Color.RED},
                new float[]{0f, 0.7f, 1f}, Shader.TileMode.CLAMP);
        SPEEDOMETER_PAINT.setStyle(Paint.Style.STROKE);
        SPEEDOMETER_PAINT.setStrokeWidth(STROKE_WIDTH);
        SPEEDOMETER_PAINT.setShader(shader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(TAG, "onMeasure() called with: widthMeasureSpec = [" + widthMeasureSpec + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        getTextBounds(stringFormat(MAX_SPEED));
        getTextBounds(stringFormat(START_SPEED));
        int requestedSize = (int) (Math.max(mTextBounds.width(), mTextBounds.height()) + Math.PI * mStrokeWidth);
        final int suggestedMinimumSize = Math.max(getSuggestedMinimumHeight(), getSuggestedMinimumWidth());
        requestedSize = Math.max(suggestedMinimumSize, requestedSize);
        final int resolvedWidth = resolveSize(requestedSize + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);
        final int resolvedHeight = resolveSize(requestedSize + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        final int resolvedSize = Math.min(resolvedHeight, resolvedWidth);
        setMeasuredDimension(resolvedSize, resolvedSize);
    }


    static class SavedState extends BaseSavedState {

        private int mSpeed;

        public SavedState(Parcel source) {
            super(source);
            mSpeed = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mSpeed);
        }

        private static final class StateCreator implements Parcelable.Creator<SavedState> {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }
    }


}
