# AOP-onActivityResult

####告别onActivityResult，用实现AOP方式获取startActivityForResult结果。
每次我们使用startActivityForResult方法时，如果需要接收返回的结果，总得在onActivityResult中进行处理。
如果业务比较复杂，有多个界面携带数据返回到同一界面中，需要在 onActivityResult 方法中根据resultCode来判断到底是从哪个界面返回的，onActivityResult 中塞满了 if else ，代码太臃肿，影响代码的阅读。
下面我们就尝试用AOP的方式来避免使用onActivityResult，提高代码可读性。

####先来看下最终实现的目标代码：
```
Intent intent = new Intent(MainActivity.this, OneActivity.class);
ActivityResultManager.getInstance()
        .startActivityForResult(MainActivity.this, intent, 100, new ResultCallBack() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if(resultCode == Activity.RESULT_OK && data!=null){
                    String source = data.getStringExtra("source");
                    textView.setText(source);
                }
            }
        });
```
可以看到在ActivityResultManager的startActivityForResult方法中添加了一个回调，回调方法和Activity的onActivityResult一样，可以在里面处理其他界面返回的数据。
####startActivityForResult的实现
```
public interface ResultCallBack {
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}

private HashMap<String, HashMap<Integer,ResultCallBack>> mMap;

public void startActivityForResult(Activity context, Intent intent, int requestCode, ResultCallBack callBack){
    if(context==null||intent==null) return;
    if(callBack!=null){
        //初始化mMap
        if(mMap==null) mMap=new HashMap<>();
        //mMap中数据的key是Activity的类名，value是resultMap
        //resultMap中数据的key是requestCode，value是ResultCallBack对象
        HashMap<Integer,ResultCallBack> resultMap = mMap.get(context.getClass().getName());
        if (resultMap == null) {
            resultMap = new HashMap<>();
            mMap.put(context.getClass().getName(),resultMap);
        }
        resultMap.put(requestCode, callBack);
    }
    //调用Activity的startActivityForResult方法
    context.startActivityForResult(intent, requestCode);
}
```
可以看到在startActivityForResult方法中，在调用Activity的startActivityForResult方法之前，根据Activity的类名和requestCode，
把回调ResultCallBack的对象存入类型为HashMap<String, HashMap<Integer,ResultCallBack>>的Map中，以便后面可以根据Activity和requestCode
获取ResultCallBack对象，下面先简单介绍下AOP。

####AOP
AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。
在Android中实现它的常用工具是AspectJ。AspectJ是AOP的Java语言的实现，是一个代码生成工具。
参考<a href="https://www.jianshu.com/p/f90e04bcb326">AOP 之 AspectJ 全面剖析 in Android</a>

<a href="http://www.jianshu.com/p/c4d7d0e678af">原文链接</a>
####AspectJ的引入
在library的build.gradle中添加依赖
```
dependencies {
    ...
    implementation 'org.aspectj:aspectjrt:1.8.9'
}
```
在app项目的build.gradle里应用插件
```
apply plugin: 'android-aspectjx'
```
在项目根目录的build.gradle里依赖AspectJX
```
classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.0'
```
####创建Aspect类
创建了一个名为ActivityResultAspect的类，给这个类添加@Aspect注解，代码如下：
```
@Aspect
public class ActivityResultAspect {

    @After("execution(* android.app.Activity.onActivityResult(..))")
    public void onActivityResultMethod(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        int requestCode = (int) args[0];
        int resultCode = (int) args[1];
        Intent data = (Intent) args[2];
        Activity activity = (Activity) joinPoint.getTarget();
        ActivityResultManager.getInstance().afterActivityResult(activity, requestCode, resultCode, data);
    }

}
```
这里使用execution以Activity的onActivityResult方法执行时为切点，触发Aspect类。而execution里面的字符串是触发条件，也是具体的切点。
第一个 * 表示任意返回值类型，括号里的 .. 表示任意多个参数。joinPoint.getArgs()获取onActivityResult方法的参数，joinPoint.getTarget()获取连接点所在的目标对象，这里是Activity。
@After表示切入点之后执行，也就是在Activity的onActivityResult方法执行后，执行我们的onActivityResultMethod方法。
这样每次执行Activity的onActivityResult方法后，就会调用我们的onActivityResultMethod方法，在这个方法里获取了onActivityResult方法的参数，
然后传入了afterActivityResult方法。
####afterActivityResult
```
public void afterActivityResult(Activity context, int requestCode, int resultCode, Intent data){
    if(mMap!=null) {
        HashMap<Integer, ResultCallBack> resultMap = mMap.get(context.getClass().getName());
        if (resultMap != null) {
            ResultCallBack callBack = resultMap.get(requestCode);
            if (callBack != null) {
                callBack.onActivityResult(requestCode, resultCode, data);
                resultMap.remove(requestCode);
                if(resultMap.size()==0){
                    mMap.remove(context.getClass().getName());
                    if(mMap.size()==0){
                        mMap=null;
                    }
                }
            }
        }
    }
}
```
可以看到afterActivityResult方法就是根据Activity和requestCode从mMap中获取获取回调ResultCallBack对象，并调用回调方法。
这样就可以在回调方法里处理其他界面返回的数据了。

####总结
1.把回调ResultCallBack对象根据Activity和requestCode存入Map中。
2.使用AOP方式创建一个Aspect类，以Activity的onActivityResult方法执行时为切点，注入我们的代码，获取onActivityResult的参数，传入afterActivityResult方法。
3.在afterActivityResult方法中获取ResultCallBack对象，并调用回调方法。

<img src="https://github.com/Stubborn-boy/AOP-onActivityResult/blob/master/pic.gif" />