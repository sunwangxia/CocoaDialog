# CocoaDialog

**CocoaDialog** 是一个仿iOS端UIAlertController实现的Android端的对话框控件，提供了普通对话框、带输入的对话框，以及底部弹出的菜单样式可选。只需简单三五行语句，便能引入类似iOS端对话框的效果。  

**效果图**  

![screenshot](https://github.com/swx007/CocoaDialog/blob/master/screenshot/screenshot.gif)

## 获取CocoaDialog

请在app/build.gradle文件中添加如下内容:

```
dependencies {
   compile 'com.berwin.cocoadialog:cocoadialog:1.3.3'
}
```

或者使用JitPack，在project/build.gradle文件中添加如下内容:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

然后在app/build.gradle文件中添加如下内容:

```
dependencies {
   compile 'com.github.SunBerwin:CocoaDialog:v1.3.3'
}
```

## 如何使用 CocoaDialog

**CocoDialog**的使用方法非常简单，首先实例化CocoaDialog.Builder，配置对话框的标题，信息及按钮等信息，之后调用build()方法构建对话框，再调用show()进行显示，示例代码如下：

```
    CocoaDialog.Builder builder = new CocoaDialog.Builder(this, CocoaDialogStyle.alert);  
    builder.setTitle("Title for CocoaDialog");
    builder.setMessage("This is a message.");
    builder.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    builder.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener<CocoaDialog>() {
        @Override
        public void onClick(CocoaDialog dialog) {
            Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        }
    }));
    builder.build().show();
```

当然，你也可以使用如下的链式操作:


```
	new CocoaDialog.Builder(this, CocoaDialogStyle.alert)
	    .setTitle("Title for CocoaDialog")
	    .setMessage("This is a message.")
    	.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null))
    	.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
        	@Override
        	public void onClick(CocoaDialog dialog) {
            	Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        	}
    	}))
    	.build().show();

```

## 底部弹出菜单样式

**CocoaDialog**提供两种样式可供选择，一种为普通Alert对话框，一种为底部弹出菜单，如需使用底部弹出菜单样式，只需在构建对话框实例时选用actionSheet样式，示例代码如下：

```
    new CocoaDialog.Builder(this, CocoaDialogStyle.actionSheet)
    	.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    	.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
        	@Override
        	public void onClick(CocoaDialog dialog) {
            	Toast.makeText(getBaseContext(), "The ok button is clicked.", Toast.LENGTH_SHORT).show();
        	}
    	}))
    	.build().show();
```


## 自定义ContentView

**1.3.0版本** 增加了自定义ContentView的支持，使用时只需在构建时传入**CocoaDialogStyle.custom**样式，之后调用setCustomContentView(View)，并通过setCustomWidth(int)和setCustomHeight(int)来设置需要的宽高即可。

使用custom样式时必须调用setCustomContentView(View)，否则会抛出异常。

setCustomWidth(int)和setCustomHeight(int)参数为像素值，同时也支持WindowManager.LayoutParams.MATCH\_PARENT 和 WindowManager.LayoutParams.WRAP\_CONTENT，设置为0或其他非法数值默认使用WindowManager.LayoutParams.WRAP\_CONTENT。


> 注意：使用自定义ContentView时会使用其他方法(如setTitle、setMessage、addAction等)失效，如果ContentView中有需要与用户交互的控件需要在外面进行监听处理。

示例代码如下：

```
    new CocoaDialog.Builder(this, CocoaDialogStyle.custom)
                        .setCustomWidth(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setCustomHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setCustomContentView(LayoutInflater.from(this).inflate(R.layout.loading_dialog, null))
                        .build().show();
```

## 对话框按钮

**CocoaDialog**中的按钮，即**CocoaDialogAction**，共提供三种样式可供选择:

* 普通按钮，对应枚举值为CocoaDialogActionStyle.normal，文本颜色为#007AFF
* 取消按钮，对应枚举值为CocoaDialogActionStyle.cancel，文本颜色同normal，当对话框为alert样式时，若按钮少于三个，则取消按钮总在左边，若按钮多于三个，则取消按钮总在最下方；若对话框为actionSheet样式时，取消按钮则在最下方；
* 危险操作，对应枚举值为CocoaDialogActionStyle.destructive，样式与普通按钮相同，文本颜色为红色(#FF0000)，可用于提示用户该操作为危险操作。  

**CocoaDialogAction**的构建方法CocoaDialogAction(String title, CocoaDialogActionStyle style, OnClickListener listener)中共有三个参数：

* title: 按钮显示的文本，类型为String
* style: 按钮的样式，类型为枚举类型CocoaDialogActionStyle
* listener: 按钮点击事件监听器，按钮点击会回调监听器的onClick方法

> 注：虽然回调方法中传入了CocoaDialog实例，但由于内部已经默认将对话框关闭掉了，调用者无需手动调用dismiss方法来取消对话框。

## 带输入的对话框

如果使用的是alert样式的**CocoaDialog**，可通过调用addEditText(EditTextConfigurationHandler configurationHandler)方法来为对话框添加文本输入框，用户可在EditTextConfigurationHandler的回调方法onEditTextAdded(EditText editText)中对添加到对话框中的输入框进行配置，如果修改输入类型，提示文本等。

> 注意，addEditText方法仅在alert样式下生效，且与addProgressBar方法互斥，两个方法同时调用只有最后调用的方法会生效。  

通过调用 **CocoaDialog.getEditTextList()** 可获取到添加到对话框中的所有输入框，遍历该列表可获取所有文本框的实例，可进一步获取到用户输入的文本内容，列表中的顺序与添加顺序相同。

示例代码如下：

```
    new CocoaDialog.Builder(this, "This is the title", "This is a message", CocoaDialogStyle.alert)
    	.addAction(new CocoaDialogAction("Cancel", CocoaDialogActionStyle.cancel, null));
    	.addAction(new CocoaDialogAction("OK", CocoaDialogActionStyle.normal, new CocoaDialogAction.OnClickListener() {
        	@Override
        	public void onClick(CocoaDialog dialog) {
        	    List<EditText> editTextList = dialog.getEditTextList();
            	if (editTextList != null && editTextList.size() > 0 && editTextList.get(0).length() > 0) {
                	Toast.makeText(getBaseContext(), editTextList.get(0).getText(), Toast.LENGTH_SHORT).show();
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
    	.build().show();
```


## 带进度条的对话框

如果使用的是alert样式的**CocoaDialog**，可通过调用addProgressBar(ProgressBarBuildHandler handler)方法来为对话框添加一个进度条，需在ProgressBarBuildHandler的回调方法build(Context context)中构建并返回一个ProgressBar或其任意子类，之后可通过调用 **CocoaDialog.setProgress(int progress)** 或 **CocoaDialog.getProgress()** 修改或获取当前进度条的进度。

> 注意，addProgressBar方法仅在alert样式下生效，且与addEditText方法互斥，两个方法同时调用只有最后调用的方法会生效。  

```
    final CocoaDialog dialog = new CocoaDialog.Builder(this)
                        .setTitle("下载文件")
                        .setMessage("正在拼命加载中...")
                        .addProgressBar(new ProgressBarBuildHandler/*<ProgressBar>*/() { // 1.3.2之后移除了此处无意义泛型，旧版还需使用泛型
                            @Override
                            public ProgressBar build(Context context) {
                                return new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
                            }
                        }).addAction(new CocoaDialogAction("取消", CocoaDialogActionStyle.cancel, new CocoaDialogAction.OnClickListener() {
                            @Override
                            public void onClick(CocoaDialog dialog) {
                                // 取消下载的操作
                            }
                        })).build();
    dialog.show();
    new Handler().postDelayed(new Runnable(){
    	public void run() {
    		dialog.setProgress(50);
    }, 1000);
```

## 反馈

如果遇到问题或者好的建议，请反馈到我的邮箱：berwin.sun@foxmail.com

如果觉得对你有用的话，点一下右上的星星赞一下吧
