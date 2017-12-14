CocoaDialog
========

**CocoaDialog** 是一个仿iOS端UIAlertController实现的Android端的对话框控件，提供了普通Alert模式的对话框、带输入的Alert对话框，以及底部弹出的菜单样式可选。只需简单改句代码，便能实现类似iOS端对话框的效果。  

**废话少说，先上效果图**  

![screenshot](https://github.com/swx007/CocoaDialog/blob/master/screenshot/screenshot.gif)

**获取CocoaDialog**
--------
使用 Gradle:
```
dependencies {
   compile 'com.berwin.cocoadialog:lib:1.0.0'
}
```
或者 Maven：
```
<dependency>
  <groupId>com.berwin.cocoadialog</groupId>
  <artifactId>lib</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

**如何使用 CocoaDialog**
--------

**CocoDialog**的使用方法非常简单，通过调用CocoaDialog.build(CharSequence title, CharSequence message, CocoaDialogStyle preferredStyle)方法构建出对话框实例，然后调用addAction设置对话框按钮，之后再调用show方法即可。

```
	CocoaDialog dialog = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert);  
                dialog.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialog dialog) {
                        Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
                    }
                }));
                dialog.show(getSupportFragmentManager(), "alert");
```
当然，你也可以使用如下的链式操作:

```
	CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, null))
                        .show(getSupportFragmentManager(), "alert");
```

**对话框按钮**
--------
**CocoaDialog**中的按钮，即**CocoaDialogAction**，共提供三种样式可供选择，第一种为普通按钮，对应枚举值为CocoaDialogActionStyle.normal，文本颜色为#007AFF；第二种为取消按钮，对应枚举值为CocoaDialogActionStyle.cancel，文本颜色同normal，当对话框为alert样式时，若按钮少于三个，则取消按钮总在左边，若按钮多于三个，则取消按钮总在最下方；若对话框为actionSheet样式时，取消按钮则在最下方；另外还有一种危险操作的样式，对应枚举值为CocoaDialogActionStyle.destructive，样式与普通按钮相同，文本颜色为红色(#FF0000)，可用于提示用户该操作为危险操作。  
**CocoaDialogAction**的构建方法CocoaDialogAction(String title, CocoaDialogActionStyle style, OnClickListener listener)中共有三个参数，第一个参数为按钮显示的文本，第二个参数为按钮的样式，第三个为按钮点击动作的监听器，用户按下按钮将会回调监听器的onClick(CocoaDialog dialog)方法。***虽然回调方法中传回了CocoaDialog实例，但内部已经默认将对话框关闭掉了，无需再手动调用dismiss方法。***


**带输入的对话框**
--------
如果使用的是alert样式的**CocoaDialog**，则可通过调用addEditText(Context context, EditTextConfigurationHandler configurationHandler)方法来为对话框添加文本输入框，用户可在EditTextConfigurationHandler的回调方法onEditTextAdded(EditText editText)中对添加到对话框中的输入框进行配置，如果修改输入类型，提示文本等。***注意，addEditText方法仅在alert样式下生效。***  
文本框的内容可通过遍历 **CocoaDialog** 实例中的数组 ***editTextList*** 获取。

```
CocoaDialog dialog = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.alert);
                dialog.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialog dialog) {
                        if (dialog.editTextList.size() > 0 && !TextUtils.isEmpty(dialog.editTextList.get(0).getText())) {
                            Toast.makeText(getBaseContext(), dialog.editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
                dialog.addEditText(this, new CocoaDialog.EditTextConfigurationHandler() {
                    @Override
                    public void onEditTextAdded(EditText editText) {
                        editText.setHint("Enter the username.");
                    }
                });
                dialog.addEditText(this, new CocoaDialog.EditTextConfigurationHandler() {
                    @Override
                    public void onEditTextAdded(EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setHint("Enter the password.");
                    }
                });
                dialog.show(getSupportFragmentManager(), "alert");
```


**底部弹出菜单样式**
--------
CocoaDialog提供两种样式可供选择，一种为普通Alert对话框，一种为底部弹出菜单，如需使用底部弹出菜单样式，只需在构建对话框实例时选用actionSheet样式：

```
CocoaDialog dialog = CocoaDialog.build("This is the title", "This is a message", CocoaDialogStyle.actionSheet);
                dialog.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
                dialog.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
                    @Override
                    public void onClick(CocoaDialog dialog) {
                        Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
                    }
                }));
                dialog.show(getSupportFragmentManager(), "actionSheet");
```


**反馈**
--------
如果遇到问题或者好的建议，请反馈到我的邮箱：berwin.sun@foxmail.com

如果觉得对你有用的话，点一下右上的星星赞一下吧
