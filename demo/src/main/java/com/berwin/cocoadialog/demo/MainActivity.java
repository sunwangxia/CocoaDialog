package com.berwin.cocoadialog.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.berwin.cocoadialog.CocoaDialog;
import com.berwin.cocoadialog.CocoaDialogAction;
import com.berwin.cocoadialog.CocoaDialogActionStyle;
import com.berwin.cocoadialog.CocoaDialogStyle;
import com.berwin.cocoadialog.EditTextConfigurationHandler;
import com.berwin.cocoadialog.ProgressBarBuildHandler;

import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Handler handler = new Handler();
    TestProgressRunner loading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_alert_simple).setOnClickListener(this);
        findViewById(R.id.btn_alert_ok_cancel).setOnClickListener(this);
        findViewById(R.id.btn_alert_other).setOnClickListener(this);
        findViewById(R.id.btn_alert_with_input).setOnClickListener(this);
        findViewById(R.id.btn_alert_progress_horizontal).setOnClickListener(this);
        findViewById(R.id.btn_action_sheet_ok_cancel).setOnClickListener(this);
        findViewById(R.id.btn_action_sheet_other).setOnClickListener(this);
        findViewById(R.id.btn_custom).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_alert_simple:
                new CocoaDialog.Builder(this, CocoaDialogStyle.alert)
                        .setTitle("This is the title")
                        .setMessage("This is a message")
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .build().show();
                break;
            case R.id.btn_alert_ok_cancel:
                //不需要响应点击事件时listener直接传入null，设置title和message也支持使用strings资源id。
                new CocoaDialog.Builder(this, CocoaDialogStyle.alert)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_message)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .build().show();
                break;
            case R.id.btn_alert_other:
                //超过3个按钮时action会占据整行的空间，cancel按钮会自动保持在最底部，无论何时添加到对话框中
                new CocoaDialog.Builder(this, CocoaDialogStyle.alert)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_message)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "Destructive choice clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "Safe choice clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .build().show();
                break;
            case R.id.btn_alert_with_input:
                // 通过调用getEditTextList可获取到之前通过addEditText添加的EditText的列表，遍历列表可取得用户输入的文本
                new CocoaDialog.Builder(this, CocoaDialogStyle.alert)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_message)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                List<EditText> editTextList = dialog.getEditTextList();
                                if (editTextList != null && editTextList.size() > 0 && editTextList.get(0).length() > 0) {
                                    Toast.makeText(getBaseContext(), editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }))
                        .addEditText(new EditTextConfigurationHandler() {
                            @Override
                            public void onEditTextAdded(EditText editText) {
                                editText.setHint("Enter the username.");
                                editText.setTextSize(14);
                                editText.setTypeface(Typeface.DEFAULT);
                            }
                        })
                        .addEditText(new EditTextConfigurationHandler() {
                            @Override
                            public void onEditTextAdded(EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                editText.setHint("Enter the password.");
                                editText.setTextSize(14);
                                editText.setTypeface(Typeface.DEFAULT);
                            }
                        })
                        .build().show();

                break;
            case R.id.btn_alert_progress_horizontal:
                final CocoaDialog dialog = new CocoaDialog.Builder(this)
                        .setTitle("下载文件")
                        .setMessage("正在拼命加载中...")
                        .addProgressBar(new ProgressBarBuildHandler<ProgressBar>() {
                            @Override
                            public ProgressBar build(Context context) {
                                return new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
                            }
                        }).addAction(new CocoaDialogAction("取消", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                handler.removeCallbacks(loading);
                            }
                        })).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                handler.removeCallbacks(loading);
                            }
                        }).build();
                // 模拟网络下载
                loading = new TestProgressRunner(dialog) {

                    @Override
                    void run(CocoaDialog dialog) {
                        Random random = new Random();
                        int progress = dialog.getProgress() + random.nextInt(10);
                        dialog.setProgress(progress);
                        if (progress < 100) {
                            handler.postDelayed(this, random.nextInt(50) + 100);
                        } else {
                            dialog.dismiss();
                        }
                    }
                };
                dialog.show();
                handler.postDelayed(loading, 100);
                break;
            case R.id.btn_action_sheet_ok_cancel:
                new CocoaDialog.Builder(this, CocoaDialogStyle.actionSheet)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "Cancel clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Take Photo", CocoaDialogActionStyle.destructive, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "Take photo clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Select from Album", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "Select from Album clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .build().show();
                break;
            case R.id.btn_action_sheet_other:
                new CocoaDialog.Builder(this, CocoaDialogStyle.actionSheet)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_message)
                        .addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, null))
                        .addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, null))
                        .build().show();
                break;
            case R.id.btn_custom:
                // CocoaDialogStyle.custom样式为自定义对话框，当Style设置为custom时只有setCustomWidth、setCustomHeight
                // 和setContentView方法会生效，其他方法均会被忽略。
                // 自定义的ContentView中有任何需要交互的控件均需要调用者在外部进行监听处理。
                // customWidth、customHeight 可设置像素值用WindowManager.LayoutParams.MATCH_PARENT和
                // WindowManager.LayoutParams.WRAP_CONTENT，若设置为0或除-1和-2之外的其他负数则默认使用WindowManager.LayoutParams.WRAP_CONTENT
                new CocoaDialog.Builder(this, CocoaDialogStyle.custom)
                        .setCustomWidth(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setCustomHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setCustomContentView(LayoutInflater.from(this).inflate(R.layout.loading_dialog, null))
                        .build().show();
                break;
        }
    }

    abstract class TestProgressRunner implements Runnable {

        private CocoaDialog mDialog;

        TestProgressRunner(CocoaDialog dialog) {
            this.mDialog = dialog;
        }

        @Override
        public void run() {
            run(mDialog);
        }

        abstract void run(CocoaDialog dialog);
    }
}
