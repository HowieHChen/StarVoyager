package star.sky.voyager.hook.hooks.securitycenter

import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import star.sky.voyager.utils.init.HookRegister
import star.sky.voyager.utils.key.hasEnable
import star.sky.voyager.utils.yife.DexKit.dexKitBridge

object EnhanceContours : HookRegister() {
    override fun init() = hasEnable("enhance_contours") {
        dexKitBridge.findMethod {
            matcher {
                usingStrings = listOf("ro.vendor.media.video.frc.support")
            }
        }.forEach {

//        }
//        dexKitBridge.batchFindClassesUsingStrings {
//            addQuery("qwq", setOf("ro.vendor.media.video.frc.support"))
//        }.forEach { (_, classes) ->
//            classes.map {
                val qaq = it.getClassInstance(classLoader)
                var counter = 0
                dexKitBridge.findMethod {
//                    methodDeclareClass = qaq.name
//                    methodReturnType = "boolean"
//                    methodParamTypes = arrayOf("java.lang.String")
                    matcher {
                        declaredClass = qaq.name
                        returnType = "boolean"
                        parameterTypes = listOf("java.lang.String")
                    }
                }.forEach { methods ->
                    counter++
                    if (counter == 3) {
                        methods.getMethodInstance(classLoader).createHook {
                            returnConstant(true)
                        }
                    }
                }
            val tat = dexKitBridge.findMethod {
                matcher {
                    usingStrings = listOf("debug.config.media.video.ais.support")
                    declaredClass = qaq.name
                }
            }.first().getMethodInstance(classLoader)
//                    dexKitBridge.findMethodUsingString {
//                    methodDeclareClass = qaq.name
//                    usingString = "debug.config.media.video.ais.support"
//                }.single().getMethodInstance(classLoader)
                val newChar = tat.name.toCharArray()
                for (i in newChar.indices) {
                    newChar[i]++
                }
                val newName = String(newChar)
                tat.declaringClass.methodFinder()
                    .filterByName(newName)
                    .first().createHook {
                        returnConstant(true)
                    }
            }
//        }
    }
}