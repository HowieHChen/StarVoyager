package star.sky.voyager.hook.hooks.securitycenter

import android.view.View
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import io.luckypray.dexkit.enums.MatchType
import star.sky.voyager.utils.init.HookRegister
import star.sky.voyager.utils.key.hasEnable
import star.sky.voyager.utils.yife.DexKit.dexKitBridge
import star.sky.voyager.utils.yife.DexKit.loadDexKit

object LockOneHundred : HookRegister() {
    override fun init() = hasEnable("lock_one_hundred") {
        loadClass("com.miui.securityscan.ui.main.MainContentFrame").methodFinder().first {
            name == "onClick" && parameterTypes[0] == View::class.java
        }.createHook {
            before {
                it.result = null
            }
        }

        loadDexKit()
        val minusScoreMethod = dexKitBridge.findMethodUsingString {
            usingString = "getMinusPredictScore"
            matchType = MatchType.CONTAINS
            methodDeclareClass = "com.miui.securityscan.scanner.ScoreManager"
        }.single()

        XposedBridge.hookMethod(
            minusScoreMethod.getMethodInstance(EzXHelper.classLoader),
            XC_MethodReplacement.returnConstant(0)
        )
    }
}