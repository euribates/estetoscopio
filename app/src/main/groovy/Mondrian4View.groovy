package es.euribates.estetoscopio

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import groovy.transform.ToString

class Mondrian4View extends View {

    @ToString
    class SquareValue {
        int value
        int color
    }

    static final String LOGTAG = 'MONDRIAN'

    private Resources res = null
    private Paint paint = null
    private int info_color, warning_color, error_color, panic_color
    private int black_color, white_color
    private List<SquareValue> values = []
    int sum_values = 0

    Mondrian4View(Context context) {
        super(context)
        initialize()
    }

    Mondrian4View(Context context, AttributeSet attrs) {
        super(context, attrs)
        initialize()
    }


    void initialize() {
        Log.d(LOGTAG, 'Mondrian4View.initilize() starts')
        this.res = getResources()
        this.info_color = this.res?.getColor(R.color.info)
        this.warning_color = this.res?.getColor(R.color.warning)
        this.error_color = this.res?.getColor(R.color.error)
        this.panic_color = this.res?.getColor(R.color.panic)
        this.black_color = this.res?.getColor(R.color.black)
        this.white_color = this.res?.getColor(R.color.white)
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG)
        this.paint?.style = Paint.Style.FILL_AND_STROKE
        Random rnd = new Random(System.currentTimeMillis())
        this.setValues(rnd.nextInt(100), rnd.nextInt(100), rnd.nextInt(100), rnd.nextInt(100))
        Log.d(LOGTAG, 'Mondrian4View.initilize() ends')
    }

    void setValues(int x1=0, int x2=0, int x3=0, int x4=0) {
        Log.d(LOGTAG, 'Mondrian4View.setValues starts')
        try {
            values = [
                new SquareValue(value: x1, color: info_color),
                new SquareValue(value: x2, color: warning_color),
                new SquareValue(value: x3, color: error_color),
                new SquareValue(value: x4, color: panic_color),
            ].findAll({ it.value > 0 })
            values.sort({ it.value })
            sum_values = values*.value.sum()
        } catch (Exception err) {
            Log.e(LOGTAG, "Error en Mondrian4View.setValues: $err")
        } finally {
            Log.i(LOGTAG, 'Mondrian4View.setValues ends')
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(100, 100)
    }

    protected void onDraw(Canvas canvas) {
        Log.d(LOGTAG, 'Mondrian4View.onDraw starts')
        try {
            int x1, x2, x3
            int a, c
            switch (values.size()) {
                case 4:
                    x1 = values[0].value
                    x2 = values[1].value
                    x3 = values[2].value
                    a = Math.round((x1 + x3) / sum_values * 100.0) as Integer
                    c = Math.round((x1 + x2) / sum_values * 100.0) as Integer
                    paint.color = values[0].color
                    canvas.drawRect(0, 0, a, c, paint)
                    paint.color = values[1].color
                    canvas.drawRect(a, 0, 100, c, paint)
                    paint.color = values[2].color
                    canvas.drawRect(0, c, a, 100, paint)
                    paint.color = values[3].color
                    canvas.drawRect(a, c, 100, 100, paint)
                    paint.color = black_color
                    paint.strokeWidth = 3
                    canvas.drawLine(a, 0, a, 100, paint)
                    canvas.drawLine(0, c, 100, c, paint)
                    break
                case 3:
                    x1 = values[0].value
                    x2 = values[1].value
                    a = Math.round((x1 / (x1 + x2) * 100.0)) as Integer
                    c = Math.round(((x1 + x2)/ sum_values * 100.0)) as Integer
                    paint.color = values[0].color
                    canvas.drawRect(0, 0, a, c, paint)
                    paint.color = values[1].color
                    canvas.drawRect(a, 0, 100, c, paint)
                    paint.color = values[2].color
                    canvas.drawRect(0, c, 100, 100, paint)
                    paint.color = black_color
                    paint.strokeWidth = 3
                    canvas.drawLine(a, 0, a, c, paint)
                    canvas.drawLine(0, c, 100, c, paint)
                    break
                case 2:
                    x1 = values[0].value
                    a = Math.round((x1 / this.sum_values * 100.0)) as Integer
                    paint.color = values[0].color
                    canvas.drawRect(0, 0, a, 100, paint)
                    paint.color = values[1].color
                    canvas.drawRect(a, 0, 100, 100, paint)
                    paint.color = black_color
                    paint.strokeWidth = 3
                    canvas.drawLine(a, 0, a, 100, paint)
                    break
                case 1:
                    paint.color = values[0].color
                    canvas.drawRect(0, 0, 100, 100, paint)
                    break
                default:
                    Log.d(LOGTAG, 'Case 0')
                    Bitmap mondrian_bitmap = BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.mondrian
                    )
                    // canvas.drawBitmap(mondrian_bitmap, 0, new Rect(0,0,100,100), null)


                    Rect source = new Rect(0,0, mondrian_bitmap.height, mondrian_bitmap.width)
                    canvas.drawBitmap(mondrian_bitmap, source, new Rect(0,0,100,100), null)
            }
            this.postInvalidate()
        } catch (Exception err) {
            Log.e(LOGTAG, "error:$err")
        } finally {
            Log.i(LOGTAG, 'Mondrian4View.onDraw ends')
        }
    }
}
