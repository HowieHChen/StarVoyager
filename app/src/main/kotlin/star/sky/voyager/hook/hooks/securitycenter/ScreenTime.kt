package star.sky.voyager.hook.hooks.securitycenter

import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import org.luckypray.dexkit.query.enums.StringMatchType
import star.sky.voyager.utils.init.HookRegister
import star.sky.voyager.utils.key.hasEnable
import star.sky.voyager.utils.yife.DexKit.dexKitBridge

object ScreenTime : HookRegister() {
    private val cls by lazy {
//        dexKitBridge.batchFindClassesUsingStrings {
//            addQuery("qwq1", setOf("not support screenPowerSplit", "PowerRankHelperHolder"))
//            matchType = MatchType.FULL
//        }.values
//            .flatten()
//            .map { it.getClassInstance(classLoader) }
//            .firstOrNull()!!
        dexKitBridge.findClass {
            matcher {
                usingStrings = listOf("not support screenPowerSplit", "PowerRankHelperHolder")
                StringMatchType.Equals
            }
        }.first().getInstance(classLoader)
    }
    private val method1 by lazy {
//        dexKitBridge.batchFindMethodsUsingStrings {
//            addQuery("qwq2", setOf("ishtar", "nuwa", "fuxi"))
//            matchType = MatchType.FULL
//        }.values
//            .flatten()
//            .map { it.getMethodInstance(classLoader) }
//            .firstOrNull()!!
        dexKitBridge.findMethod {
            matcher {
                usingStrings = listOf("ishtar", "nuwa", "fuxi")
                StringMatchType.Equals
            }
        }.first().getMethodInstance(classLoader)
    }
    private val method2 by lazy {
        dexKitBridge.findMethod {
            matcher {
                declaredClass = cls.name
                returnType = "boolean"
                parameterTypes = listOf()
            }
//            methodDeclareClass = cls.name
//            methodReturnType = "boolean"
//            methodParamTypes = arrayOf()
        }.map { it.getMethodInstance(classLoader) }.toList()
    }

    override fun init() = hasEnable("screen_time") {
        method2.forEach {
            it.createHook {
                returnConstant(
                    when (it) {
                        method1 -> true
                        else -> false
                    }
                )
            }
        }
    }
}