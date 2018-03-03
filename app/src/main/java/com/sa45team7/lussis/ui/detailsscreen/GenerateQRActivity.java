package com.sa45team7.lussis.ui.detailsscreen;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.ui.adapters.ReqDetailAdapter;
import com.sa45team7.lussis.rest.model.Disbursement;
import com.sa45team7.lussis.utils.DateConvertUtil;

public class GenerateQRActivity extends AppCompatActivity {

    private Disbursement disbursement;
    private ImageView qrView;
    private Button toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        String data = getIntent().getStringExtra("disbursement");
        disbursement = new Gson().fromJson(data, Disbursement.class);

        initViews();

        String disbursementId = String.valueOf(disbursement.getDisbursementId());

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(disbursementId,
                    BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bmp = barcodeEncoder.createBitmap(bitMatrix);
            qrView.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        ((TextView) findViewById(R.id.department_text)).setText(disbursement.getDepartmentName());
        String date = DateConvertUtil.convertForDetail(disbursement.getCollectionDate());
        ((TextView) findViewById(R.id.date_text)).setText(date);
        ((TextView) findViewById(R.id.time_text)).setText(disbursement.getCollectionTime());
        ((TextView) findViewById(R.id.collection_text)).setText(disbursement.getCollectionPoint());
        qrView = findViewById(R.id.qr_image);
        toggleButton = findViewById(R.id.toggle_button);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        RecyclerView stationeryListView = findViewById(R.id.stationery_list_view);
        ReqDetailAdapter adapter = new ReqDetailAdapter(disbursement.getDisbursementDetails());
        stationeryListView.setAdapter(adapter);
    }

    private void toggle() {
        if (qrView.getVisibility() == View.GONE) {
            expand(qrView);
            toggleButton.setText(R.string.hide_text);
        } else {
            collapse(qrView);
            toggleButton.setText(R.string.show_text);
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
