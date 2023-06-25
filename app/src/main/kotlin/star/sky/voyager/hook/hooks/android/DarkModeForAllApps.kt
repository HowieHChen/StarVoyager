package star.sky.voyager.hook.hooks.android

import android.content.pm.ApplicationInfo
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.ObjectUtils.invokeMethodBestMatch
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import star.sky.voyager.utils.init.HookRegister
import star.sky.voyager.utils.key.hasEnable
import star.sky.voyager.utils.yife.Build.IS_INTERNATIONAL_BUILD

object DarkModeForAllApps : HookRegister() {
    override fun init() = hasEnable("dark_mode_for_all_apps") {
        if (IS_INTERNATIONAL_BUILD) return@hasEnable
        val forceDarkAppListManager =
            loadClass("com.android.server.ForceDarkAppListManager")

        forceDarkAppListManager.methodFinder()
            .filterByName("getDarkModeAppList")
            .toList().createHooks {
                before {
                    setStaticObject(
                        loadClass("miui.os.Build"),
                        "IS_INTERNATIONAL_BUILD",
                        true
                    )
                }

                after {
                    setStaticObject(
                        loadClass("miui.os.Build"),
                        "IS_INTERNATIONAL_BUILD",
                        IS_INTERNATIONAL_BUILD
                    )
                }
            }

        forceDarkAppListManager.methodFinder()
            .filterByName("shouldShowInSettings")
            .filterByParamTypes(ApplicationInfo::class.java)
            .toList().createHooks {
                before { param ->
                    val info = param.args[0] as ApplicationInfo?
                    param.result = !(info == null || (invokeMethodBestMatch(
                        info,
                        "isSystemApp"
                    ) as Boolean) || (info.uid <= 10000))
                }
            }
    }
}