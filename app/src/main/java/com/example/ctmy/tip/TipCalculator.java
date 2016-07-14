package com.example.ctmy.tip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;



public class TipCalculator extends AppCompatActivity {

    public double billAmount;
    public double tipAmount;
    public double totalBill;
    public double splitAmount;

    public EditText tvBillAmount;
    public TextView tvTipAmount;
    public TextView tvSplitAmount;
    public TextView tvTotalBill;

    public TextView tvPercentage;
    public TextView tvFriends;
    //viewers displayed on the UI.
    SeekBar sbPercentValue;
    SeekBar sbFriendsValue;

    int tipPerc;
    int nFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

         tvBillAmount = (EditText)findViewById(R.id.txtBill);
         tvTipAmount = (TextView)findViewById(R.id.txtTip);
         tvSplitAmount = (TextView)findViewById(R.id.vTip);
         tvTotalBill = (TextView)findViewById(R.id.vTotalBill);
         tvPercentage = (TextView)findViewById(R.id.lblPercentage);
         tvFriends = (TextView)findViewById(R.id.lblFriends);

        sbPercentValue = (SeekBar)findViewById(R.id.sbPercent);
        sbFriendsValue = (SeekBar)findViewById(R.id.sbFriends);

        //Show calculations when the app opens
        calculateTip();
        CalculateTotal();
        calculateFriends();



        tvBillAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTip();
                CalculateTotal();
                calculateFriends();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sbFriendsValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFriends.setText(String.valueOf(progress+1));
                calculateTip();
                CalculateTotal();
                calculateFriends();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbPercentValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPercentage.setText(String.valueOf(progress));
                calculateTip();
                CalculateTotal();
                calculateFriends();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    boolean isBillAmountWrong(){
        String strbillAmount = tvBillAmount.getText().toString();

        if(strbillAmount.equals("")) {
            billAmount = 0.0;
            tipAmount = 0.0;
            splitAmount = 0.0;
            tvTipAmount.setText("0.00");
            tvTotalBill.setText("0.00");
            tvSplitAmount.setText("0.00");
            return true;
        }else {
            billAmount = Double.parseDouble(strbillAmount);
            return false;
        }
    }


    
    public void calculateTip(){

        String strTipPerc = tvPercentage.getText().toString();
        if(!isBillAmountWrong()){
            tipPerc = Integer.parseInt(strTipPerc);
            tipAmount = billAmount*tipPerc/100.0;
            tvTipAmount.setText(String.format("%.2f", tipAmount));
        }

    }

    public void CalculateTotal(){

        totalBill =  billAmount + tipAmount;
        tvTotalBill.setText(String.format("%.2f", totalBill));
    }

    public void calculateFriends(){
        String strlblFriends = tvFriends.getText().toString();

        nFriends = Integer.parseInt(strlblFriends);

        splitAmount = totalBill / nFriends;

        tvSplitAmount.setText(String.format("%.2f", splitAmount));

    }
}
