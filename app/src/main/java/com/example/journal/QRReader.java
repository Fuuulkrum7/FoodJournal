package com.example.journal;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultRegistry;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRReader extends Fragment implements View.OnClickListener {

    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    private View view;
    private ScanContract contract;

    public QRReader() {
        contract = new ScanContract();
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan_button) {

            ScanOptions options = new ScanOptions();
            options.setPrompt("Finding friend's QR");
            options.setBeepEnabled(false);

            getActivity().startActivity(contract.createIntent(getActivity(), options));

            /*scanIntegrator = IntentIntegrator.forSupportFragment(this);
            scanIntegrator.setOrientationLocked(false);
            scanIntegrator.setPrompt("Scan");
            scanIntegrator.setBeepEnabled(false);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            Intent scan = scanIntegrator.createScanIntent();
            //scan
            scanIntegrator.initiateScan();*/
        }
    }
}