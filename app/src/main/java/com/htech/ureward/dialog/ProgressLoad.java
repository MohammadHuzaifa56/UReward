package com.htech.ureward.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.htech.ureward.R;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressLoad extends Dialog {

    @BindView(R.id.dialogTitle)
    TextView tvDialogTitle;

    String title;

    public ProgressLoad(@NonNull Context context, String title) {
        super(context);
        this.title=title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        ButterKnife.bind(this);

        tvDialogTitle.setText(title);

    }
}
