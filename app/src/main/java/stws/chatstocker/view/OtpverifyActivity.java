package stws.chatstocker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import stws.chatstocker.ConstantsValues;
import stws.chatstocker.R;
import stws.chatstocker.databinding.ActivityOtpverifyBinding;
import stws.chatstocker.utils.ViewModelFactory;
import stws.chatstocker.viewmodel.OtpViewModel;

import javax.inject.Inject;

public class OtpverifyActivity extends AppCompatActivity implements ConstantsValues {
    private ActivityOtpverifyBinding activityOtpverifyBinding;
    @Inject
    ViewModelFactory viewModelFactory;
    OtpViewModel otpViewModel;
    private EditText edtPin1, edtPin2, edtPin3, edtPin4, edtPin5, edtPin6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpverifyBinding = DataBindingUtil.setContentView(this, R.layout.activity_otpverify);
        edtPin1 = activityOtpverifyBinding.edtPin1;
        edtPin2 = activityOtpverifyBinding.edtPin2;
        edtPin3 = activityOtpverifyBinding.edtPin3;
        edtPin4 = activityOtpverifyBinding.edtPin4;
        edtPin5 = activityOtpverifyBinding.edtPin5;
        edtPin6 = activityOtpverifyBinding.edtPin6;
        otpViewModel = ViewModelProviders.of(this).get(OtpViewModel.class);
        otpViewModel.setVerificationId(getIntent().getStringExtra(KEY_VERIFICATION_ID));
        activityOtpverifyBinding.setViewModel(otpViewModel);
        edtPin1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtPin2.requestFocus();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        changeEditextFocus(edtPin1, edtPin2);
        changeEditextFocus(edtPin2, edtPin3);
        changeEditextFocus(edtPin3, edtPin4);
        changeEditextFocus(edtPin4, edtPin5);
        changeEditextFocus(edtPin5, edtPin6);
//        changeEditextFocus(edtPin1,edtPin2);

//        setContentView(R.layout.activity_otpverify);
    }

    private void changeEditextFocus(EditText edtWatcher, final EditText edtFocus) {
        edtWatcher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtFocus.requestFocus();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


}
