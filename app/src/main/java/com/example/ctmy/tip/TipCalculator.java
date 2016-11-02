package com.example.ctmy.tip;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;


public class TipCalculator extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private static final String SETTING_VALUES = "setting_values";

    private boolean ROUND_BILL = false;
    private boolean ROUND_TIP = false;

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

    SeekBar sbDrawerPercent;
    SeekBar sbDrawerFriends;

    boolean changedsbFriends = false;
    boolean changedsbPercent = false;

    int tipPerc;
    int nFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new drawerMenuListener());

         tvBillAmount = (EditText)findViewById(R.id.txtBill);
         tvTipAmount = (TextView)findViewById(R.id.txtTip);
         tvSplitAmount = (TextView)findViewById(R.id.vTip);
         tvTotalBill = (TextView)findViewById(R.id.vTotalBill);
         tvPercentage = (TextView)findViewById(R.id.lblPercentage);
         tvFriends = (TextView)findViewById(R.id.lblFriends);

        sbPercentValue = (SeekBar)findViewById(R.id.sbPercent);
        sbFriendsValue = (SeekBar)findViewById(R.id.sbFriends);

        sbDrawerFriends = (SeekBar) findViewById(R.id.sbDFriends);
        sbDrawerPercent = (SeekBar) findViewById(R.id.setting_sb_tip);

        final TextView dlblPercent = (TextView)findViewById(R.id.lbl_percent);
        final TextView dlblFriends = (TextView) findViewById(R.id.lbl_friends_number);

        SharedPreferences settings = getSharedPreferences(SETTING_VALUES, 0);
        ROUND_BILL = settings.getBoolean("roundBill", false);
        ROUND_TIP = settings.getBoolean("roundTip", false);

        //Get Values from settings for sliders
        int dTipPercent = settings.getInt("tipPercent", 15);
        int dFriends = settings.getInt("friendsQuantity", 1);
        sbPercentValue.setProgress(dTipPercent);
        sbFriendsValue.setProgress(dFriends+1);

        tvPercentage.setText(String.valueOf(dTipPercent));
        tvFriends.setText(String.valueOf(dFriends+1));
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
        tvBillAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) v;
                editText.performLongClick();
            }
        });

        sbFriendsValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFriends.setText(String.valueOf(progress + 1));
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

        sbPercentValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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

        sbDrawerFriends.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                v.onTouchEvent(event);
                return true;
            }
        });

        sbDrawerFriends.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changedsbFriends = true;
                dlblFriends.setText(String.valueOf(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /** Seekbar used in the DrawerLayout*/
        sbDrawerPercent.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                v.onTouchEvent(event);
                return true;
            }
        });

        sbDrawerPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changedsbPercent = true;
                dlblPercent.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private class drawerMenuListener implements android.support.v4.widget.DrawerLayout.DrawerListener{
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View view){

            CheckBox chkRoundBill = (CheckBox) findViewById(R.id.roundBill);
            CheckBox chkRoundTip = (CheckBox) findViewById(R.id.roundTip);

            SharedPreferences settings = getSharedPreferences(SETTING_VALUES, 0);
            ROUND_BILL = settings.getBoolean("roundBill", false);
            ROUND_TIP = settings.getBoolean("roundTip", false);
            chkRoundBill.setChecked(ROUND_BILL);
            chkRoundTip.setChecked(ROUND_TIP);

            int drawerPercent = settings.getInt("tipPercent", 15);
            int drawerFriends = settings.getInt("friendsQuantity", 1);

            sbDrawerPercent.setProgress(drawerPercent);
            sbDrawerFriends.setProgress(drawerFriends);

            TextView dlblPercent = (TextView)findViewById(R.id.lbl_percent);
            TextView dlblFriends = (TextView) findViewById(R.id.lbl_friends_number);

            dlblPercent.setText(String.valueOf(drawerPercent));
            dlblFriends.setText(String.valueOf(drawerFriends+1));
        }

        @Override
        public void onDrawerClosed (View view){
            SharedPreferences settings = getSharedPreferences(SETTING_VALUES, 0);

            int drawerPercent;
            int drawerFriends;
            //TODO: SAVE VALUES
            SharedPreferences.Editor edit = settings.edit();
            edit.putInt("tipPercent", sbDrawerPercent.getProgress());
            edit.putInt("friendsQuantity", sbDrawerFriends.getProgress());
            edit.commit();

            drawerPercent = settings.getInt("tipPercent", 15);
            drawerFriends = settings.getInt("friendsQuantity", 1);

            //if not changes were made to the seekbar of the drawerlayout
            //don't update values of the main layout.
            if(changedsbPercent){
                tvPercentage.setText(String.valueOf(drawerPercent));
                sbPercentValue.setProgress(drawerPercent);
            }

            if(changedsbFriends){
                tvFriends.setText(String.valueOf(drawerFriends+1));
                sbFriendsValue.setProgress(drawerFriends);
            }

            calculateTip();
            CalculateTotal();
            calculateFriends();

            //set the flags that determines if the seekbar has been move to false(reset)
            changedsbFriends = changedsbPercent = false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {}
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


    void setTxtViewTip(double value){
        tvTipAmount.setText(String.format("%.2f", value));
    }

    public void calculateTip(){

        String strTipPerc = tvPercentage.getText().toString();
        if(!isBillAmountWrong()){
            tipPerc = Integer.parseInt(strTipPerc);
            tipAmount = billAmount*tipPerc/100.0;
            if(ROUND_TIP){
                tipAmount = Math.ceil(tipAmount);
            }

            setTxtViewTip(tipAmount);
        }

    }

    public void CalculateTotal(){

        totalBill =  billAmount + tipAmount;
        if(ROUND_BILL && ROUND_TIP){
            totalBill = billAmount + tipAmount;
            totalBill = Math.ceil(totalBill);
        }else if(ROUND_BILL && !ROUND_TIP) {
            totalBill = Math.ceil(totalBill);
            tipAmount = totalBill-billAmount;
            setTxtViewTip(tipAmount);
        }

        tvTotalBill.setText(String.format("%.2f", totalBill));

    }

    public void calculateFriends(){
        String strlblFriends = tvFriends.getText().toString();

        nFriends = Integer.parseInt(strlblFriends);

        splitAmount = totalBill / nFriends;

        tvSplitAmount.setText(String.format("%.2f", splitAmount));

    }

    /**Drawer functionality  */


    public void onCheckedClicked(View view){
        SharedPreferences settings = getSharedPreferences(SETTING_VALUES, MODE_PRIVATE);
        boolean checked = ((CheckBox) view).isChecked();

        SharedPreferences.Editor edit = settings.edit();
        switch (view.getId()){
            case R.id.roundBill:
                if(checked){
                    ROUND_BILL = true;
                    edit.putBoolean("roundBill", ROUND_BILL);
                }else{
                    ROUND_BILL = false;
                    edit.putBoolean("roundBill", ROUND_BILL);
                }
                break;
            case R.id.roundTip:
                if(checked){
                    ROUND_TIP = true;
                    edit.putBoolean("roundTip", ROUND_TIP);
                }else{
                    ROUND_TIP = false;
                    edit.putBoolean("roundTip", ROUND_TIP);
                }
        }

        edit.commit();
    }

}
