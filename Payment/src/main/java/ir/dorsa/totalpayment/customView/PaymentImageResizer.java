package ir.dorsa.totalpayment.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso;

import ir.dorsa.totalpayment.R;


public class PaymentImageResizer extends android.support.v7.widget.AppCompatImageView {

    int imageSrc = 0;
    int width = 0;
    int height = 0;
    boolean isSetBefore = false;

    public PaymentImageResizer(Context context) {
        super(context);
        init(null);
    }

    public PaymentImageResizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PaymentImageResizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(final AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PaymentImageResizer);
            imageSrc = a.getResourceId(R.styleable.PaymentImageResizer_source, R.drawable.paymrnt_bgr);
            a.recycle();
            setImage();
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
         height = MeasureSpec.getSize(heightMeasureSpec);
         width = MeasureSpec.getSize(widthMeasureSpec);
         setImage();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setImage(){
        if (!isSetBefore && width != 0 && height != 0 && imageSrc!=0) {
            isSetBefore = true;
            Picasso.get()
                    .load(PaymentImageResizer.this.imageSrc)
                    .resize(width, 0)
                    .into(PaymentImageResizer.this);
        }
    }

}
