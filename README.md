# naughty [![](https://jitpack.io/v/dzenm/naughty.svg)](https://jitpack.io/#dzenm/naughty)

这是一个查看网络请求的工具

![gif](https://github.com/dzenm/naughty/blob/master/pic/pic.gif)

## 下载 （[查看最新版本](https://github.com/dzenm/naughty/releases/latest)）

release版本使用
```groovy
releaseImplementation 'com.github.dzenm.naughty:naughty-release:1.6.0'
```
debug版本使用
```
debugImplementation 'com.github.dzenm.naughty:naughty-debug:1.6.0'
```

## 使用
在Okhttp的拦截器中添加
```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
if (BuildConfig.DEBUG) {
    builder.addInterceptor(Naughty.getInstance().get(this));
}
OkHttpClient client = builder.build();
```

## 下载 [APK](https://github-production-release-asset-2e65be.s3.amazonaws.com/280431540/54253700-c9da-11ea-98d7-647782a9db74?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20200719%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20200719T081037Z&X-Amz-Expires=300&X-Amz-Signature=35ee69fffcbfb483c6b258c63138dfadc54fb9ae55c9e765249467a52b6c2baf&X-Amz-SignedHeaders=host&actor_id=28523411&repo_id=280431540&response-content-disposition=attachment%3B%20filename%3Dapp-debug.apk&response-content-type=application%2Fvnd.android.package-archive)

