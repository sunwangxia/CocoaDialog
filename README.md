# CocoaDialog

**CocoaDialog** 是一个仿iOS端UIAlertController实现的Android端的对话框控件，提供了普通对话框、带输入的对话框，以及底部弹出的菜单样式可选。只需简单三五行语句，便能引入类似iOS端对话框的效果。  

**效果图**  

![screenshot](https://github.com/swx007/CocoaDialog/blob/master/screenshot/screenshot.gif)

## 新版本改动

由于旧版使用DialogFragment实现存在一定的缺陷，如屏幕切换时发生异常导致应用崩溃等问题。1.1.0版本中新增了Dialog的实现方式，Api相比之前有一定改动，具体使用方法可在Demo中看到。

当然，1.1.0版本还保留了旧版的DialogFragment的实现方式，原类名修改为CocoaDialogFragment，构建方法build更名为create，其他Api与旧版一致。

1.1.0版本优化了alert样式对话框横屏下的展示，同时修复了旧版DialogFragment由于手机屏幕旋转崩溃的问题，不过对话框恢复后按钮及输入框无法恢复，目前尚未找到合适的方法，如果你有好的建议，欢迎联系我。


## 获取CocoaDialog

使用 Gradle:

```
dependencies {
   compile 'com.berwin.cocoadialog:cocoadialog:1.1.0'
}
```

或者 Maven：

```
<dependency>
  <groupId>com.berwin.cocoadialog</groupId>
  <artifactId>cocoadialog</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```

#### 注：如果JCenter中无法找到版本，请在build.gradle文件中添加如下代码：

```
	repositories {
        maven {
            url 'https://dl.bintray.com/berwin/maven'
        }
	}
```

## 如何使用 CocoaDialog

**CocoDialog**的使用方法非常简单，通过调用CocoaDialogFragment.create(CharSequence title, CharSequence message, CocoaDialogStyle preferredStyle)构建出对话框实例或new CocoaDialog.Builder(Context context, CharSequence title, CharSequence message, CococaDialogStyle preferredStyle)构建CocoaDialog.Builder，之后调用addAction设置对话框按钮，之后再调用show方法即可。

```
    CocoaDialog.Builder builder = new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert);  
    builder.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    builder.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
        @Override
        public void onClick(CocoaDialog dialog) {
            Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        }
    }));
    builder.create().show();
```

或

```
	CocoaDialogFragment dialog = CocoaDialogFragment.create("This is the title", "This is a message", CocoaDialogStyle.alert);
	dialog.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    dialog.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialogFragment>() {
        @Override
        public void onClick(CocoaDialogFragment dialog) {
            Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        }
    }));
    dialog.show(getSupportFragmentManager(), "alert");

```

当然，你也可以使用如下的链式操作:


```
	new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert)
    	.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
    	.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
        	@Override
        	public void onClick(CocoaDialog dialog) {
            	Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        	}
    	}))
    	.create().show();

```

或

```
	CocoaDialogFragment.create("This is the title", "This is a message", CocoaDialogStyle.alert)
                        .addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.cancel, null))
                        .show(getSupportFragmentManager(), "alert");
```

## 对话框按钮

**CocoaDialog**中的按钮，即**CocoaDialogAction**，共提供三种样式可供选择，第一种为普通按钮，对应枚举值为CocoaDialogActionStyle.normal，文本颜色为#007AFF；第二种为取消按钮，对应枚举值为CocoaDialogActionStyle.cancel，文本颜色同normal，当对话框为alert样式时，若按钮少于三个，则取消按钮总在左边，若按钮多于三个，则取消按钮总在最下方；若对话框为actionSheet样式时，取消按钮则在最下方；另外还有一种危险操作的样式，对应枚举值为CocoaDialogActionStyle.destructive，样式与普通按钮相同，文本颜色为红色(#FF0000)，可用于提示用户该操作为危险操作。  

**CocoaDialogAction**的构建方法CocoaDialogAction(String title, CocoaDialogActionStyle style, OnClickListener listener)中共有三个参数，第一个参数为按钮显示的文本，第二个参数为按钮的样式，第三个为按钮点击动作的监听器，由于最新版本增加了Dialog实现，为了兼容旧版DialogFragment的实现，该接口使用泛型设计，调用时需要根据当用使用的是CocoaDialog或者CocoaDialogFragment传入对应的类型，当用户按下按钮时将会回调监听器的onClick<T>(T dialog)方法。

***注：虽然回调方法中传入了CocoaDialog或CocoaDialogFragment实例，但内部已经默认将对话框关闭掉了，无需再手动调用dismiss方法。***


## 带输入的对话框

如果使用的是alert样式的**CocoaDialog**，可通过调用addEditText(EditTextConfigurationHandler configurationHandler)或addEditText(Context context, EditTextConfigurationHandler configurationHandler)方法来为对话框添加文本输入框，用户可在EditTextConfigurationHandler的回调方法onEditTextAdded(EditText editText)中对添加到对话框中的输入框进行配置，如果修改输入类型，提示文本等。

> 注意，addEditText方法仅在alert样式下生效。  

文本框的内容可通过遍历 **CocoaDialog** 实例中的数组 ***editTextList*** 获取。

```
    new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert)
    	.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    	.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
        	@Override
        	public void onClick(CocoaDialog dialog) {
            	if (dialog.editTextList.size() > 0 && !TextUtils.isEmpty(dialog.editTextList.get(0).getText())) {
                	Toast.makeText(getBaseContext(), dialog.editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
            	}
        	}
    	}))
    	.addEditText(this, new CocoaDialog.EditTextConfigurationHandler() {
        	@Override
        	public void onEditTextAdded(EditText editText) {
            	editText.setHint("Enter the username.");
        	}
    	})
    	.addEditText(this, new CocoaDialog.EditTextConfigurationHandler() {
        	@Override
        	public void onEditTextAdded(EditText editText) {
            	editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            	editText.setHint("Enter the password.");
        	}
    	})
    	.create().show();
```


## 底部弹出菜单样式

**CocoaDialog**提供两种样式可供选择，一种为普通Alert对话框，一种为底部弹出菜单，如需使用底部弹出菜单样式，只需在构建对话框实例时选用actionSheet样式：

```
    new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.actionSheet)
    	.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    	.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
        	@Override
        	public void onClick(CocoaDialog dialog) {
            	Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        	}
    	}))
    	.create().show();
```


## 反馈

如果遇到问题或者好的建议，请反馈到我的邮箱：berwin.sun@foxmail.com

如果觉得对你有用的话，点一下右上的星星赞一下吧
