package com.example.journal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class QRReader extends Fragment {

    private Button scanBtn;
    private TextView formatTxt, contentTxt;

    static {
        System.loadLibrary("iconv");
    }

    public QRReader() {
        // Required empty public constructor
    }

    public static QRReader newInstance() {
        QRReader fragment = new QRReader();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qr_reader, container, false);

        scanBtn = (Button)view.findViewById(R.id.scan_button);
        formatTxt = (TextView)view.findViewById(R.id.scan_format);
        contentTxt = (TextView)view.findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Тут нужно сделать сканирование.
                /*Вроде это делается так:

                    if(v.getId()==R.id.scan_button){
                    //scan
                    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                    scanIntegrator.initiateScan();
                    }

                 */
            }


            /*public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                //Это уже для обработки результата сканирования.
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

                if (scanningResult != null) {
                    //Если что-то нашли
                    String scanContent = scanningResult.getContents();
                    String scanFormat = scanningResult.getFormatName();
                }
                else{
                    Toast toast = Toast.makeText(view.getContext(),
                            "Не удалось считать код!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }*/
        });
        return view;
    }
}