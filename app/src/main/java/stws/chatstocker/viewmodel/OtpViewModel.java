package stws.chatstocker.viewmodel;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import stws.chatstocker.ConstantsValues;
import stws.chatstocker.view.LoginActivity;

import javax.inject.Inject;

public class OtpViewModel extends ViewModel implements ConstantsValues {
    private String pin1,pin2,pin3,pin4,pin5,pin6,verificationId;
    @Inject
    public OtpViewModel(){

    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setPin1(String pin1) {
        this.pin1 = pin1;
    }

    public String getPin1() {
        return pin1;
    }

    public String getPin2() {
        return pin2;
    }

    public void setPin2(String pin2) {
        this.pin2 = pin2;
    }

    public String getPin3() {
        return pin3;
    }

    public void setPin3(String pin3) {
        this.pin3 = pin3;
    }

    public String getPin4() {
        return pin4;
    }

    public void setPin4(String pin4) {
        this.pin4 = pin4;
    }

    public String getPin5() {
        return pin5;
    }

    public void setPin5(String pin5) {
        this.pin5 = pin5;
    }

    public String getPin6() {
        return pin6;
    }

    public void setPin6(String pin6) {
        this.pin6 = pin6;
    }
    public  void OnVerify(View view){
        Intent intent=new Intent();
        intent.putExtra(KEY_OTP,pin1+""+pin2+""+pin3+""+pin4+""+pin5+""+pin6);
        intent.putExtra(KEY_VERIFICATION_ID,getVerificationId());
//        intent.putExtra(KEY_VERIFICATION_ID)
        ((AppCompatActivity)view.getContext()).setResult(101,intent);
        ((AppCompatActivity)view.getContext()).finish();

    }
}
