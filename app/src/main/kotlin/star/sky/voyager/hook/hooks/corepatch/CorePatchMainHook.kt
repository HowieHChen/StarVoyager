package star.sky.voyager.hook.hooks.corepatch

import android.os.Build
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage


open class CorePatchMainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if ("android" == lpparam.packageName && lpparam.processName == "android") {
            Log.d(TAG, "Current sdk version " + Build.VERSION.SDK_INT)
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> CorePatchForU().handleLoadPackage(lpparam)
                Build.VERSION_CODES.TIRAMISU -> CorePatchForT().handleLoadPackage(lpparam)
                Build.VERSION_CODES.S -> CorePatchForS().handleLoadPackage(lpparam)
                Build.VERSION_CODES.R -> CorePatchForR().handleLoadPackage(lpparam)
                Build.VERSION_CODES.Q -> CorePatchForQ().handleLoadPackage(lpparam)
                else -> XposedBridge.log(TAG + ": Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }

    @Throws(Throwable::class)
    override fun initZygote(startupParam: StartupParam) {
        if (startupParam.startsSystemServer) {
            Log.d(TAG, "Current sdk version " + Build.VERSION.SDK_INT)
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> CorePatchForU().initZygote(startupParam)
                Build.VERSION_CODES.TIRAMISU -> CorePatchForT().initZygote(startupParam)
                Build.VERSION_CODES.S -> CorePatchForS().initZygote(startupParam)
                Build.VERSION_CODES.R -> CorePatchForR().initZygote(startupParam)
                Build.VERSION_CODES.Q -> CorePatchForQ().initZygote(startupParam)
                else -> XposedBridge.log(TAG + ": Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }

    companion object {
        const val TAG = "Voyager: CorePatch"
    }
}

