package star.sky.voyager.hook.hooks.guardprovider

import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import org.luckypray.dexkit.query.enums.StringMatchType
import star.sky.voyager.utils.init.HookRegister
import star.sky.voyager.utils.key.hasEnable
import star.sky.voyager.utils.yife.DexKit.dexKitBridge

object Anti2 : HookRegister() {
    private val region by lazy {
//        dexKitBridge.findMethodUsingString {
//            usingString = "ro.miui.customized.region"
//            matchType = MatchType.FULL
//        }.firstOrNull()?.getMethodInstance(classLoader)
        dexKitBridge.findMethod {
            matcher {
                usingStringsMatcher {
                    this.add {
                        this.value = "ro.miui.customized.region"
                        StringMatchType.Equals
                    }
                }
            }
        }.firstOrNull()?.getMethodInstance(classLoader)
    }

    private val detect by lazy {
//        dexKitBridge.findMethodUsingString {
//            usingString = "https://flash.sec.miui.com/detect/app"
//            matchType = MatchType.FULL
//        }.firstOrNull()?.getMethodInstance(classLoader)
        dexKitBridge.findMethod {
            matcher {
                usingStringsMatcher {
                    this.add {
                        this.value = "https://flash.sec.miui.com/detect/app"
                        StringMatchType.Equals
                    }
                }
            }
        }.firstOrNull()?.getMethodInstance(classLoader)
    }

    override fun init() = hasEnable("Anti_Defraud_App_Manager") {
        region?.createHook {
            returnConstant(false)
        }

        detect?.createHook {
            returnConstant(null)
        }
    }
}