//package com.example.ctmy.tip;
package com.ctmyapplications.thetipper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.renderscript.ScriptGroup;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.os.Handler;
import android.widget.Toast;


public class TipCalculator extends AppCompatActivity {

    /**
     * licencing variables
     */

    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlBqQ8AgFgIwPKV7/jxYNeCRp8zkxPFsVqFaxcGH9t9ZJKfgjdyKHfSNpNQHgcsLnAsvqKalQKxu2rCSbMHa46gg9mhpWiC1KSC6ZLIdGcB7RhCoax33cpaGvkxc0TOnsGTpnwENC0oahs4GCxwLlIothzV2iDewz1b2/HOIKJBvlq1IAJpsJ0CMKHJd3GsIn9jqKhs5YydfNu/c+cDtL7UBve3F6oYg1Gty1g2iWVzGkADMnu9IfwxQo9ng6hz9J3MhXuXFau3Ya4u2IN5TgzU3xuTaZ1vovYFnXx6SyCEpccsE5v0oyJSDN9FCWqE5FW5YLJAbCGmu7xm0d2clOoQIDAQAB";

    private static final byte[] SALT = new byte[] {
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
            -45, 77, -117, -36, -113, -11, 32, -64, 89
    };


    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    private Handler mHandler;

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
        /**
         * Instatiation of variables for licencing
         */

        //Construc the LicenseCheckerCallback.
        mHandler = new Handler();
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();

        //construct the LicenseChecker with a Policy.


        mChecker = new LicenseChecker(this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY
                );

        //check licence
        mChecker.checkAccess(mLicenseCheckerCallback);



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
        sbFriendsValue.setProgress(dFriends);

        dlblPercent.setText(String.valueOf(dTipPercent));
        dlblFriends.setText(String.valueOf(dFriends + 1));

        tvPercentage.setText(String.valueOf(dTipPercent));
        tvFriends.setText(String.valueOf(dFriends+1));


        sbDrawerPercent.setProgress(dTipPercent);
        sbDrawerFriends.setProgress(dFriends);

        tvBillAmount.requestFocus();

        View parent = findViewById(R.id.tip_bill);
        parent.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                if(hasFocus){
                    hideKeyboard(view);
                    tvBillAmount.requestFocus();
                }
            }
        });

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

        tvTipAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Show message when both tip and bill are rounded.
               double tipBillAndTip = totalBill - billAmount;
               Context c = getApplicationContext();
               Toast tipWithBillAndTipRounded = Toast.makeText(c, String.format("%.2f Actual Tip ", tipBillAndTip), Toast.LENGTH_LONG);
               tipWithBillAndTipRounded.show();
                System.out.println("funciona click");
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



    private class drawerMenuListener implements DrawerLayout.DrawerListener{
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

            //set the flags that determines if the seekbar has been moved to false(reset)
            changedsbFriends = changedsbPercent = false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {}
    }

    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    private class MyLicenseCheckerCallback  implements LicenseCheckerCallback{

        public void allow(int reason){
            if(isFinishing()){
                return;
            }
            // Should allow user access.
            //Modify UI wich I don't want
            /* The message shown if the user has a original copy of the app*/
            //displayResult(getString(R.string.allow));

        }

        public void dontAllow(int policyReason){
            if(isFinishing()){
                // Don't update UI if Activity is finishing.
                return;
            }
            /* It should show dialog to prevent this user from accessing the app */
            //displayResult(getString(R.string.dont_allow));
            //displayDialog(policyReason == Policy.RETRY);
        }

        public void applicationError(int errorCode){
            if(isFinishing()){
                return;
            }

            /// String result = String.format(getString(R.string.application_error), errorCode);
            /// displayResult(result);
        }
    }

    private void displayResult(final String result){

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(getBaseContext(),result,Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* private void displayDialog(final boolean showRetry){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showDialog(showRetry?1:0);
                //TODO
            }
        });
    }*/

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mChecker.onDestroy();
    }

}
