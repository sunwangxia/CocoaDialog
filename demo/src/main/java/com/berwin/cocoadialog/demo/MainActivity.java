package com.berwin.cocoadialog.demo;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.berwin.cocoadialog.CocoaDialog;
import com.berwin.cocoadialog.CocoaDialogAction;
import com.berwin.cocoadialog.CocoaDialogActionStyle;
import com.berwin.cocoadialog.CocoaDialogInterface;
import com.berwin.cocoadialog.CocoaDialogStyle;
import com.berwin.cocoadialog.EditTextConfigurationHandler;
import com.berwin.cocoadialog.ProgressBarBuildHandler;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_alert_simple:
                new CocoaDialog.Builder(this, CocoaDialogStyle.alert)
                        .setTitle("This is the title")
                        .setMessage("This is a message")
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                Toast.makeText(getBaseContext(), "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .create().show();
                /*CocoaDialogFragment.create("This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .show(getSupportFragmentManager(), "alert");*/
                break;
            case R.id.btn_alert_ok_cancel:
                //不需要响应点击事件时listener直接传入null
                new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialogInterface dialog) {
                                Toast.makeText(getBaseContext(), "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .create().show();
                /*CocoaDialogFragment.create("This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "OK clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .show(getSupportFragmentManager(), "alert");*/
                break;
            case R.id.btn_alert_other:
                new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialogInterface dialog) {
                                Toast.makeText(getBaseContext(), "Destructive choice clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialogInterface dialog) {
                                Toast.makeText(getBaseContext(), "Safe choice clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .create().show();
                /*CocoaDialogFragment.create("Other Alert Title", "Other alert message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "Destructive choice clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "Safe choice clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .show(getSupportFragmentManager(), "alert");*/
                break;
            case R.id.btn_alert_with_input:
                new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                if (dialog.editTextList.size() > 0 && !TextUtils.isEmpty(dialog.editTextList.get(0).getText())) {
                                    Toast.makeText(getBaseContext(), dialog.editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
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
                        .create().show();
                /*CocoaDialogFragment.create("This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {

                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                if (dialog.editTextList.size() > 0 && !TextUtils.isEmpty(dialog.editTextList.get(0).getText())) {
                                    Toast.makeText(getBaseContext(), dialog.editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }))
                        .addEditText(this, new EditTextConfigurationHandler() {
                            @Override
                            public void onEditTextAdded(EditText editText) {
                                editText.setHint("Enter the username.");
                                editText.setTextSize(14);
                                editText.setTypeface(Typeface.DEFAULT);
                            }
                        })
                        .addEditText(this, new EditTextConfigurationHandler() {
                            @Override
                            public void onEditTextAdded(EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                editText.setHint("Enter the password.");
                                editText.setTextSize(14);
                                editText.setTypeface(Typeface.DEFAULT);
                            }
                        })
                        .show(getSupportFragmentManager(), "alert");*/

                break;
            case R.id.btn_alert_progress_horizontal:
                final CocoaDialog dialog = new CocoaDialog.Builder(this)
                        .setTitle("下载文件")
                        .setMessage("正在拼命加载中...")
                        .addProgressBar(new ProgressBarBuildHandler<ProgressBar>() {
                            @Override
                            public ProgressBar build(Context context) {
//                                return (ProgressBar) LayoutInflater.from(context).inflate(R.layout.horizontal_progressbar, null);
//                                return (ProgressBar) LayoutInflater.from(context).inflate(R.layout.progressbar, null);
                                return new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
                            }
                        }).create();
                /*// 注意：由于DialogFragment在onAttach之前是拿不到Context的，所以这里回调中传进来的Context为null，请忽略此参数，使用当前Context来构建ProgressBar
                final CocoaDialogFragment dialog = CocoaDialogFragment.create("下载文件", "正在拼命加载中...", CocoaDialogStyle.alert)
                        .addProgressBar(new ProgressBarBuildHandler<ProgressBar>() {
                            @Override
                            public ProgressBar build(Context context) {
//                                return (ProgressBar) LayoutInflater.from(getBaseContext()).inflate(R.layout.horizontal_progressbar, null);
//                                return (ProgressBar) LayoutInflater.from(getBaseContext()).inflate(R.layout.progressbar, null);
                                return new ProgressBar(getBaseContext(), null, android.R.attr.progressBarStyleHorizontal);
                            }
                        });*/
                dialog.setCancelable(false);
                // 模拟网络下载
                final Handler handler = new Handler();
                final Runnable loading = new Runnable() {
                    @Override
                    public void run() {
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
                dialog.addAction(new CocoaDialogAction("取消", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialogInterface dialog) {
                        handler.removeCallbacks(loading);
                    }
                }));
                dialog.show();
//                dialog.show(getSupportFragmentManager(), "Alert with ProgressBar");
                handler.postDelayed(loading, 100);
                break;
            case R.id.btn_action_sheet_ok_cancel:
                //不需要title和message可调用单一参数的build方法，之后如果需要可通过setter方法进行设置
                new CocoaDialog.Builder(this, CocoaDialogStyle.actionSheet)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialogInterface dialog) {
                                Toast.makeText(getBaseContext(), "Cancel clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Take Photo", CocoaDialogActionStyle.destructive, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialogInterface dialog) {
                                Toast.makeText(getBaseContext(), "Take photo clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Select from Album", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialogInterface dialog) {
                                Toast.makeText(getBaseContext(), "Select from Album clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .create().show();
                /*CocoaDialogFragment.create(CocoaDialogStyle.actionSheet)
                        .addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "Cancel clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Take Photo", CocoaDialogActionStyle.destructive, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "Take photo clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addAction(new CocoaDialogAction("Select from Album", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
                            @Override
                            public void onClick(CocoaDialogFragment dialog) {
                                Toast.makeText(getBaseContext(), "Select from Album clicked.", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .show(getSupportFragmentManager(), "actionSheet");*/
                break;
            case R.id.btn_action_sheet_other:
                new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.actionSheet)
                        .addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, null))
                        .addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, null))
                        .create().show();
                /*CocoaDialogFragment.create("This is the title", "This is a message", CocoaDialogStyle.actionSheet)
                        .addAction(new CocoaDialogAction("Destructive Choice", CocoaDialogActionStyle.destructive, null))
                        .addAction(new CocoaDialogAction("Safe Choice", CocoaDialogActionStyle.normal, null))
                        .show(getSupportFragmentManager(), "actionSheet");*/
                break;
        }
    }
}
