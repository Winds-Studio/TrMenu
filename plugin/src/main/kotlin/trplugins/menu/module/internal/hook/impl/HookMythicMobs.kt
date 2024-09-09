package trplugins.menu.module.internal.hook.impl

import ink.ptms.um.Mythic
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract
import trplugins.menu.module.internal.script.Bindings
import trplugins.menu.module.internal.script.jexl.JexlAgent
import trplugins.menu.module.internal.script.js.JavaScriptAgent

/**
 * @author lilingfengdev
 * @date 2024/7/5 15:08
 */
// TODO - Dreeam: 为什么呢
class HookMythicMobs : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }
    override fun bindingScript() {
        JavaScriptAgent.putBinding(namespace, Mythic.API)
        JexlAgent.putBinding(namespace, Mythic.API)
    }

    override val isHooked by lazy {
        val loaded=Mythic.isLoaded()
        if (loaded and Bindings.exportHook) bindingScript()
        loaded
    }

    fun getItem(material: String): ItemStack {
        return Mythic.API.getItemStack(material) ?: empty
    }

    fun getID(id: ItemStack): String {
        if (checkHooked()) {
            return Mythic.API.getItemId(id) ?: "UNKNOWN"
        }
        return "UNHOOKED"
    }

}