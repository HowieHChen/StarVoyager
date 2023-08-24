package star.sky.voyager.hook.hooks.packageinstaller

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.ui.activity.dp2px
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.ObjectUtils.invokeMethod
import de.robv.android.xposed.XposedHelpers.findMethodsByExactParameters
import de.robv.android.xposed.XposedHelpers.getObjectField
import star.sky.voyager.R
import star.sky.voyager.utils.api.callMethod
import star.sky.voyager.utils.api.callMethodOrNull
import star.sky.voyager.utils.api.hookAfterMethod
import star.sky.voyager.utils.init.HookRegister
import star.sky.voyager.utils.key.hasEnable
import java.io.File
import java.lang.reflect.Method
import java.text.DecimalFormat
import kotlin.math.roundToInt

object ShowMoreApkInfo : HookRegister() {

    private var mApkInfo: Class<*>? = null
    private var mAppInfoViewObject: Class<*>? = null
    private var mAppInfoViewObjectViewHolder: Class<*>? = null
    private val versionName by lazy {
        moduleRes.getString(R.string.package_installer_app_version_name_title)
    }

    private val versionCode by lazy {
        moduleRes.getString(R.string.package_installer_app_version_code_title)
    }

    private val sdk by lazy {
        moduleRes.getString(R.string.package_installer_app_sdk_title)
    }

    private val size by lazy {
        moduleRes.getString(R.string.package_installer_app_size_title)
    }

    @SuppressLint("SetTextI18n")
    override fun init() = hasEnable("package_installer_show_more_apk_info") {
        mApkInfo =
            loadClassOrNull("com.miui.packageInstaller.model.ApkInfo")
        mAppInfoViewObject =
            loadClassOrNull("com.miui.packageInstaller.ui.listcomponets.AppInfoViewObject")!!
        mAppInfoViewObjectViewHolder =
            loadClassOrNull("com.miui.packageInstaller.ui.listcomponets.AppInfoViewObject\$ViewHolder")!!

        val methods: Array<Method> = findMethodsByExactParameters(
            mAppInfoViewObject,
            Void.TYPE,
            mAppInfoViewObjectViewHolder
        )

        val fields = mAppInfoViewObject!!.declaredFields
        var apkInfoFieldName: String? = null
        for (field in fields) {
            if (mApkInfo!!.isAssignableFrom(field.type)) {
                apkInfoFieldName = field.name
                break
            }
        }

        if (apkInfoFieldName == null) return@hasEnable
        val finalApkInfoFieldName: String = apkInfoFieldName
        methods[0].hookAfterMethod { hookParam ->
            val viewHolder: Any = hookParam.args[0] ?: return@hookAfterMethod
            val mAppSizeTv = invokeMethod(viewHolder, "getAppSize") as TextView?
                ?: return@hookAfterMethod
            val mContext = mAppSizeTv.context
            val isDarkMode =
                mAppSizeTv.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            val apkInfo: Any =
                getObjectField(hookParam.thisObject, finalApkInfoFieldName)
            val mAppInfo =
                apkInfo.callMethodOrNull("getInstalledPackageInfo") as ApplicationInfo?
            val mPkgInfo = invokeMethod(apkInfo, "getPackageInfo") as PackageInfo
            val layout: LinearLayout = mAppSizeTv.parent as LinearLayout
            layout.removeAllViews()
            val mContainerView = layout.parent as ViewGroup
            val mRoundImageView = mContainerView.getChildAt(0) as ImageView
            val mAppNameView = mContainerView.getChildAt(1) as TextView
            mContainerView.removeAllViews()
            val linearLayout = LinearLayout(mContext)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            val appNameViewParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            appNameViewParams.setMargins(0, dp2px(mContext, 10f), 0, 0)
            mAppNameView.layoutParams = appNameViewParams
            mAppNameView.gravity = Gravity.CENTER
            val linearLayout2 = LinearLayout(mContext)
            linearLayout2.orientation = LinearLayout.VERTICAL
            linearLayout2.gravity = Gravity.CENTER
            linearLayout2.setPadding(
                dp2px(mContext, 18f),
                dp2px(mContext, 15f),
                dp2px(mContext, 18f),
                dp2px(mContext, 15f)
            )
            linearLayout2.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).also {
                it.setMargins(0, dp2px(mContext, 13f), 0, 0)
            }
            linearLayout2.background =
                moduleRes.getDrawable(if (isDarkMode) R.drawable.ic_packageinstaller_background_dark else R.drawable.ic_packageinstaller_background_light)
            val mAppPackageNameView = TextView(mContext)
            val mAppVersionNameView = TextView(mContext)
            val mAppVersionCodeView = TextView(mContext)
            val mAppSdkView = TextView(mContext)
            val mAppSizeView = TextView(mContext)
            setTextAppearance(mAppVersionNameView, mAppSizeTv)
            setTextAppearance(mAppVersionCodeView, mAppSizeTv)
            setTextAppearance(mAppSdkView, mAppSizeTv)
            setTextAppearance(mAppSizeView, mAppSizeTv)
            mAppPackageNameView.gravity = Gravity.CENTER
            mAppVersionNameView.gravity = Gravity.START
            mAppVersionCodeView.gravity = Gravity.START
            mAppSdkView.gravity = Gravity.START
            mAppSizeView.gravity = Gravity.START
            val mPackageName: String = mPkgInfo.applicationInfo.packageName
            val mAppVersionName: String
            val mAppVersionCode: String
            val mAppSdk: String
            var mOldAppSize = ""
            val newAppSize = apkInfo.callMethod("getFileSize") as Long
            val newAppSizeDistance = newAppSize.toFloat().roundToInt() / 1000000f
            val mNewAppSize = format(newAppSizeDistance)
            if (mAppInfo != null) {
                mAppVersionName =
                    apkInfo.callMethod("getInstalledVersionName") as String + " ➟ " + mPkgInfo.versionName
                mAppVersionCode = apkInfo.callMethod("getInstalledVersionCode")
                    .toString() + " ➟ " + mPkgInfo.longVersionCode
                mAppSdk =
                    mAppInfo.minSdkVersion.toString() + "-" + mAppInfo.targetSdkVersion + " ➟ " + mPkgInfo.applicationInfo.minSdkVersion + "-" + mPkgInfo.applicationInfo.targetSdkVersion
                val oldAppSize = Integer.valueOf(File(mAppInfo.sourceDir).length().toInt())
                val oldAppSizeDistance = oldAppSize.toFloat().roundToInt() / 1000000f
                mOldAppSize = format(oldAppSizeDistance) + " ➟ "
            } else {
                mAppVersionName = mPkgInfo.versionName
                mAppVersionCode = mPkgInfo.longVersionCode.toString()
                mAppSdk =
                    mPkgInfo.applicationInfo.minSdkVersion.toString() + "-" + mPkgInfo.applicationInfo.targetSdkVersion
            }
            mAppPackageNameView.text = mPackageName
            mAppVersionNameView.text =
                "$versionName: $mAppVersionName"
            mAppVersionCodeView.text =
                "$versionCode: $mAppVersionCode"
            mAppSdkView.text =
                "$sdk: $mAppSdk"
            mAppSizeView.text =
                "$size: $mOldAppSize$mNewAppSize"
            linearLayout2.addView(mAppVersionNameView, 0)
            linearLayout2.addView(mAppVersionCodeView, 1)
            linearLayout2.addView(mAppSdkView, 2)
            linearLayout2.addView(mAppSizeView, 3)
            linearLayout.addView(mRoundImageView, 0)
            linearLayout.addView(mAppNameView, 1)
            linearLayout.addView(mAppPackageNameView, 2)
            linearLayout.addView(linearLayout2, 3)
            mContainerView.addView(linearLayout)
        }
    }

    private fun setTextAppearance(textView: TextView, textView2: TextView) {
        textView.textSize = 17f
        textView.setTextColor(textView2.textColors)
        textView.ellipsize = TextUtils.TruncateAt.MARQUEE
        textView.isHorizontalFadingEdgeEnabled = true
        textView.setSingleLine()
        textView.marqueeRepeatLimit = -1
        textView.isSelected = true
        textView.setHorizontallyScrolling(true)
    }

    private fun format(appSize: Float): String {
        val decimalFormat = DecimalFormat("0.00")
        return if (appSize >= 1024f) {
            decimalFormat.format(appSize / 1024f) + "GB"
        } else if (appSize >= 1.024f) {
            decimalFormat.format(appSize) + "MB"
        } else {
            decimalFormat.format(appSize * 1024f) + "KB"
        }
    }
}