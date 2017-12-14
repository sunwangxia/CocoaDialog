package com.berwin.cocoadialog.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.berwin.cocoadialog.CocoaDialog;
import com.berwin.cocoadialog.CocoaDialogStyle;
import com.berwin.cocoadialog.CocoaDialogAction;
import com.berwin.cocoadialog.CocoaDialogActionStyle;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_alert_simple).setOnClickListener(this);
        findViewById(R.id.btn_alert_ok_cancel).setOnClickListener(this);
        findViewById(R.id.btn_alert_other).setOnClickListener(this);
        findViewById(R.id.btn_alert_with_input).setOnClickListener(this);
        findViewById(R.id.btn_action_sheet_ok_cancel).setOnClickListener(this);
        findViewById(R.id.btn_action_sheet_other).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_alert_simple:
                CocoaDialog dialog = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert);
                dialog.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, null));
                dialog.show(getSupportFragmentManager(), "alert");


                CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, null))
                        .show(getSupportFragmentManager(), "alert");

                break;
            case R.id.btn_alert_ok_cancel:
                CocoaDialog dialog1 = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert);
                dialog1.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog1.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialog dialog) {
                        Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
                    }
                }));
                dialog1.show(getSupportFragmentManager(), "alert");
                break;
            case R.id.btn_alert_other:
                CocoaDialog dialog2 = CocoaDialog.build("Other Alert Title", "Other alert message", CocoaDialogStyle.alert);
                dialog2.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog2.addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, null));
                dialog2.addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, null));
                dialog2.show(getSupportFragmentManager(), "alert");
                break;
            case R.id.btn_alert_with_input:
                CocoaDialog dialog3 = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert);
                dialog3.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog3.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialog dialog) {
                        if (dialog.editTextList.size() > 0 && !TextUtils.isEmpty(dialog.editTextList.get(0).getText())) {
                            Toast.makeText(getBaseContext(), dialog.editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
                dialog3.addEditText(this, new CocoaDialog.EditTextConfigurationHandler() {
                    @Override
                    public void onEditTextAdded(EditText editText) {
                        editText.setHint("Enter the username.");
                    }
                });
                dialog3.addEditText(this, new CocoaDialog.EditTextConfigurationHandler() {
                    @Override
                    public void onEditTextAdded(EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setHint("Enter the password.");
                    }
                });
                dialog3.show(getSupportFragmentManager(), "alert");

                break;
            case R.id.btn_action_sheet_ok_cancel:
                CocoaDialog dialog4 = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.actionSheet);
                dialog4.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog4.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialog dialog) {
                        Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
                    }
                }));
                dialog4.show(getSupportFragmentManager(), "actionSheet");
                break;
            case R.id.btn_action_sheet_other:
                CocoaDialog dialog5 = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.actionSheet);
                dialog5.addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, null));
                dialog5.addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, null));
                dialog5.show(getSupportFragmentManager(), "actionSheet");
                break;
        }


    }
}
