package com.zhengyuan.emworkshopitemtransforbook2.util;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2018/9/29.
 */

public class ShowLogInfoDialog extends Dialog {

    public ShowLogInfoDialog(@NonNull Context context) {
        super(context);
    }

    public ShowLogInfoDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //this.setContentView(com.Ieasy.ieasyware.R.layout.mydialog);
    }
}
