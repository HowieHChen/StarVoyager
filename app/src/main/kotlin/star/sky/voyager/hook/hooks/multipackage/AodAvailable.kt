package star.sky.voyager.hook.hooks.multipackage

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.hostPackageName
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import star.sky.voyager.utils.init.HookRegister

object AodAvailable : HookRegister() {
    override fun init() {
        when (hostPackageName) {
            "android" -> {
                loadClass("miui.util.FeatureParser").methodFinder()
                    .filterByName("getBoolean")
                    .toList().createHooks {
                        before {
                            if (it.args[0] == "support_aod") {
                                it.result = true
                            }
                        }
                    }
            }

            "com.android.settings" -> {
                val aodUtilsCls =
                    loadClass("com.android.settings.utils.AodUtils")

                aodUtilsCls.methodFinder().filter {
                    name in setOf("isAodAvailable", "actionAvailable", "isAodEnabled")
                }.toList().createHooks {
                    returnConstant(true)
                }

                loadClass("miui.util.FeatureParser").methodFinder()
                    .filterByName("getBoolean")
                    .toList().createHooks {
                        before {
                            if (it.args[0] == "support_aod") {
                                it.result = true
                            }
                        }
                    }
            }

            "com.xiaomi.misettings" -> {
                loadClass("miui.util.FeatureParser").methodFinder()
                    .filterByName("getBoolean")
                    .toList().createHooks {
                        before {
                            if (it.args[0] == "support_aod") {
                                it.result = true
                            }
                        }
                    }
            }

            "com.android.systemui" -> {
                val deviceConfigCls =
                    loadClass("com.miui.systemui.DeviceConfig")

                deviceConfigCls.declaredFields.first { field ->
                    field.name == "SUPPORT_AOD"
                }.apply { isAccessible = true }.set(null, true)

//                deviceConfigCls.declaredFields.toList().forEach {
//                    Log.i("${it.name}: ${it.get(null)}")
//                }
            }

            "com.miui.aod" -> {
                val utilsCls =
                    loadClass("com.miui.aod.Utils")

                utilsCls.declaredFields.first { field ->
                    field.name == "SUPPORT_AOD"
                }.apply { isAccessible = true }.set(null, true)

                utilsCls.methodFinder()
                    .filterByName("isSupportAodAnimateDevice")
                    .first().createHook {
                        returnConstant(true)
                    }
            }
        }
    }
}