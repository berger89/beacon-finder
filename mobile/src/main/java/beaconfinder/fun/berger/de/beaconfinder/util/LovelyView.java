/*INSERT YOUR PACKAGE NAME*/
package beaconfinder.fun.berger.de.beaconfinder.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import beaconfinder.fun.berger.de.beaconfinder.R;

/**
 * LovelyView demonstrates a custom view
 * for Mobiletuts+ tutorial - Android SDK: Creating Custom Views
 * <p/>
 * The view displays a circle with a text string displayed in the middle.
 * Circle color, text and text color can all be set in layout XML or Java
 * - see the main app Activity
 * <p/>
 * The view also refers to attributes specified in the app res/values/attrs XML
 */

public class LovelyView extends View {

    //circle and text colors
    private int circleCol, labelCol;
    //label text
    private String circleText;
    //paint for drawing custom view
    private Paint circlePaint;

//    private float x = 0;
//    private float y = 0;

    /**
     * Constructor method for custom view
     * - calls superclass method and adds custom processing
     *
     * @param context
     * @param attrs
     */
    public LovelyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //paint object for drawing in onDraw
        circlePaint = new Paint();

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.LovelyView, 0, 0);

        try {
            //get the text and colors specified using the names in attrs.xml
            circleText = a.getString(R.styleable.LovelyView_circleLabel);
            circleCol = a.getInteger(R.styleable.LovelyView_circleColor, 0);//0 is default
            labelCol = a.getInteger(R.styleable.LovelyView_labelColor, 0);
        } finally {
            a.recycle();
        }
    }

    /**
     * Override the onDraw method to specify custom view appearance using canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        //get half of the width and height as we are working with a circle
        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;
        //get the radius as half of the width or height, whichever is smaller
        //subtract ten so that it has some space around it
        int radius = 0;
        if (viewWidthHalf > viewHeightHalf)
            radius = viewHeightHalf - 10;
        else
            radius = viewWidthHalf - 10;

        //set drawing properties
        circlePaint.setStyle(Style.FILL);
        circlePaint.setAntiAlias(true);
        //set the paint color using the circle color specified
        circlePaint.setColor(circleCol);
        //draw the circle using the properties defined
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);

        //set the text color using the color specified
        circlePaint.setColor(labelCol);
        //set text properties
        circlePaint.setTextAlign(Paint.Align.CENTER);
        circlePaint.setTextSize(20);
        //draw the text using the string attribute and chosen properties
        canvas.drawText(circleText, viewWidthHalf, viewHeightHalf, circlePaint);
    }

    //each custom attribute should have a get and set method
    //this allows updating these properties in Java
    //we call these in the main Activity class

    /**
     * Get the current circle color
     *
     * @return color as an int
     */
    public int getCircleColor() {
        return circleCol;
    }

    /**
     * Set the circle color
     *
     * @param newColor new color as an int
     */
    public void setCircleColor(int newColor) {
        //update the instance variable
        circleCol = newColor;
        //redraw the view
        invalidate();
        requestLayout();
    }

    /**
     * Get the current text label color
     *
     * @return color as an int
     */
    public int getLabelColor() {
        return labelCol;
    }

    /**
     * Set the label color
     *
     * @param newColor new color as an int
     */
    public void setLabelColor(int newColor) {
        //update the instance variable
        labelCol = newColor;
        //redraw the view
        invalidate();
        requestLayout();
    }

    /**
     * Get the current label text
     *
     * @return text as a string
     */
    public String getLabelText() {
        return circleText;
    }

    /**
     * Set the label text
     *
     * @param newLabel text as a string
     */
    public void setLabelText(String newLabel) {
        //update the instance variable
        circleText = newLabel;
        //redraw the view
        invalidate();
        requestLayout();
    }
}
