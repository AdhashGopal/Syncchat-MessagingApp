package com.chatapp.android.app;

/**
 * created by  Adhash Team on 11/18/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.dialog.CustomAlertDialog;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.widget.AvnNextLTProDemiButton;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegEditText;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.ActivityLauncher;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.SessionManager;

import java.util.Arrays;
import java.util.List;

public class DeleteAccount extends CoreActivity implements View.OnClickListener, TextWatcher {

    ImageView backimg_del;
    private final int selectedCountry = 11;
    AvnNextLTProRegEditText countrycode, phoneNumber;
    AvnNextLTProRegTextView chooseCountry, text1, text2, text3, text4, text5;
    AvnNextLTProDemiTextView text_actionbar_1, text0;
    RelativeLayout changeaccount;
    private AvnNextLTProDemiButton btnDeleteAccount;

    private SessionManager sessionManager;
    private String mCurrentUserMsisdn;

    private List<String> codeList;
    private List<String> countryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_delete);
        btnDeleteAccount = (AvnNextLTProDemiButton) findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(DeleteAccount.this);

        initView();
        initData();
    }

    private void initData() {
        sessionManager = SessionManager.getInstance(DeleteAccount.this);
        mCurrentUserMsisdn = sessionManager.getPhoneNumberOfCurrentUser();

        String[] code = getResources().getStringArray(R.array.country_code);
        String[] country = getResources().getStringArray(R.array.country_list);
        codeList = Arrays.asList(code);
        countryList = Arrays.asList(country);
        Log.e("" + code.length, "" + country.length);
    }

    private void initView() {
        chooseCountry = (AvnNextLTProRegTextView) findViewById(R.id.selectCountry);
        chooseCountry.setOnClickListener(this);
        countrycode = (AvnNextLTProRegEditText) findViewById(R.id.countryCode);
        phoneNumber = (AvnNextLTProRegEditText) findViewById(R.id.phoneNumber);
        backimg_del = (ImageView) findViewById(R.id.backarrow_deleteaccount);
        changeaccount = (RelativeLayout) findViewById(R.id.deleteaccount_r4);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backimg_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchAccount(DeleteAccount.this);
                finish();
            }
        });

        changeaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ActivityLauncher.launchChangenumber(DeleteAccount.this);

            }
        });

        countrycode.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountry || view.getId() == R.id.selectCountryLine || view.getId() == R.id.countryDropDownMain) {
            ActivityLauncher.launchChooseCountryScreen(this, selectedCountry);
        } else if (view.getId() == R.id.okButton) {
            hideKeyboard();
            // showAlertDialog("User verification code", "Code");
        } else if (view.getId() == R.id.btnDeleteAccount) {
            performDeleteAccount();
        }
    }

    private void performDeleteAccount() {
        if (ConnectivityInfo.isInternetConnected(DeleteAccount.this)) {

            if (codeList.contains(countrycode.getText().toString())) {

                if (phoneNumber != null && !phoneNumber.getText().toString().trim().equals("")) {

                    if (phoneNumber.getText().toString().trim().length() > 4) {

                        String msisdn = "+" + countrycode.getText().toString().trim() + phoneNumber.getText().toString().trim();
                        if (msisdn.equals(mCurrentUserMsisdn)) {
                            Intent intent = new Intent(DeleteAccount.this, DeleteAccount2Activity.class);
                            startActivity(intent);
                        } else {
                            CustomAlertDialog dialog = new CustomAlertDialog();
                            dialog.setMessage("This mobile number is not linked with your account");
                            dialog.setPositiveButtonText("Ok");
                            dialog.show(getSupportFragmentManager(), "Wrong number");
                        }
                    } else {
                        Toast.makeText(DeleteAccount.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DeleteAccount.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DeleteAccount.this, "Please enter valid country code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(DeleteAccount.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here it is 2
        if (resultCode != RESULT_CANCELED && requestCode == selectedCountry) {
            String message = data.getStringExtra("MESSAGE");
            String code = data.getStringExtra("CODE");
            chooseCountry.setText(message);
            countrycode.setText(code);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (codeList.contains(charSequence.toString())) {
            int index = codeList.indexOf(charSequence.toString());
            chooseCountry.setText(countryList.get(index));
        } else {
            chooseCountry.setText("Invalid country code");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
