package trplugins.menu.module.internal.hook.impl

import net.Indyuce.mmoitems.MMOItems
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract

/**
 * @author Rubenicos
 * @date 2024/5/7 08:30
 */
// TODO - Dreeam: 为什么呢
class HookMMOItems : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }

    fun getItem(material: String): ItemStack {
        val index = material.indexOf(':')
        if (index < 1 || index + 1 >= material.length) {
            return empty
        }
        return getItem(material.substring(0, index), material.substring(index + 1))
    }

    fun getItem(type: String, id: String): ItemStack {
        if (checkHooked()) {
            return MMOItems.plugin.getItem(type, id) ?: empty;
        }
        return empty
    }

}