package star.sky.voyager.hook.apps

import de.robv.android.xposed.callbacks.XC_LoadPackage
import star.sky.voyager.hook.hooks.multipackage.AospShareSheet
import star.sky.voyager.hook.hooks.multipackage.SuperClipboard
import star.sky.voyager.utils.init.AppRegister

object Browser : AppRegister() {
    override val packageName: String = "com.android.browser"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        autoInitHooks(
            lpparam,
            SuperClipboard, // 超级剪切板
            AospShareSheet,
        )
    }
}