package com.example.journal;

import android.content.Intent;
import android.os.Bundle;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRReader extends Fragment implements View.OnClickListener {

    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    private View view;
    private IntentIntegrator scanIntegrator;

    public QRReader() {
        // Required empty public constructor
    }

    public static QRReader newInstance() {
        QRReader fragment = new QRReader();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qr_reader, container, false);

        scanBtn = (Button)view.findViewById(R.id.scan_button);
        formatTxt = (TextView)view.findViewById(R.id.scan_format);
        contentTxt = (TextView)view.findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(this);
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast.makeText(getContext(), contents, Toast.LENGTH_LONG).show();
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan_button) {
            scanIntegrator = IntentIntegrator.forSupportFragment(this);
            scanIntegrator.setOrientationLocked(false);
            scanIntegrator.setPrompt("Scan");
            scanIntegrator.setBeepEnabled(false);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            Intent scan = scanIntegrator.createScanIntent();
            //scan
            scanIntegrator.initiateScan();
        }
    }
}