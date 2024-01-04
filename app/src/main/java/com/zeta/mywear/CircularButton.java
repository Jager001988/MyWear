package com.zeta.mywear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class CircularButton extends AppCompatButton {

    private Paint paint;

    public CircularButton(Context context) {
        super(context);
        init();
    }

    public CircularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);  // Colore del testo e del bordo del bottone
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        // Disegna il cerchio di sfondo
        canvas.drawCircle(width / 2f, height / 2f, radius, paint);

        // Chiama il metodo di disegno della classe padre per disegnare il testo
        super.onDraw(canvas);
    }
}