package com.create.sidhu.movbox.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.R;

public class TransparentProgressDialog extends Dialog {

    private ImageView iv;
    //private TextView tv;

    public TransparentProgressDialog(Context context) {
        super(context);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.alpha = 0.8f;
        getWindow().getDecorView().setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        iv = new ImageView(context);
//        tv = new TextView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.logo_gif_size), (int)context.getResources().getDimension(R.dimen.logo_gif_size));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(16, 16, 0, 0);

        layout.setPadding(40, 40, 40, 40);
        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.main_logo)
                        .error(R.drawable.main_logo)
                )
                .load(R.raw.main_logo_spinner_white)
                .into(iv);
        layout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        iv.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//        tv.setText("Loading...");
//        tv.setTextSize(20);
//        tv.setTextColor(context.getResources().getColor(R.color.colorTextPrimary));

        layout.addView(iv, imageParams);
//        layout.addView(tv, textParams);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
    }
}
