plugins {
    id("com.android.application")
}

android {
    namespace = "com.lucaslu.flowtest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lucaslu.flowtest"
        minSdk = 24
        targetSdk = 34
        // CI 从标签自动注入版本号，本地构建给默认值，无需手改代码
        versionCode = (project.findProperty("appVersionCode") ?: "1").toString().toInt()
        versionName = (project.findProperty("appVersionName") ?: "0.0.0").toString()

        // 只打包 arm64-v8a：现代安卓手机基本都是 arm64，包体和构建时间双减
        // （有 native 依赖的项目效果尤其显著）
        ndk {
            abiFilters += "arm64-v8a"
        }
    }

    buildTypes {
        release {
            // 模板项目无代码可混淆；正式项目按需开启（开启会明显增加构建时间）
            isMinifyEnabled = false

            // 正式项目签名参考（keystore 绝不能提交进仓库，用 GitHub Secrets）：
            // 1. 本地: base64 keystore.jks > keystore.b64，内容存入 Secrets: ANDROID_KEYSTORE
            // 2. workflow 里: echo "${{ secrets.ANDROID_KEYSTORE }}" | base64 -d > keystore.jks
            // 3. signingConfig 里引用 storeFile/secrets 中解出的密码字段
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        // 关闭 release 包构建前的强制 lint 检查：防模板被 lint 卡住，同时提速
        checkReleaseBuilds = false
    }
}
