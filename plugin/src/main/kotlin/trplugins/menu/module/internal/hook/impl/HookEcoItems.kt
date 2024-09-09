package trplugins.menu.module.internal.hook.impl

import com.willfp.ecoitems.items.EcoItems
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract

/**
 * @author lilingfengdev
 * @date 2024/7/3 10:30
 */
// TODO - Dreeam: 为什么呢
class HookEcoItems : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }

    fun getItem(id: String): ItemStack {
        if (checkHooked()) {
            return EcoItems.getByID(id)?.itemStack ?:empty
        }
        return empty
    }

    fun getId(itemStack: ItemStack): String {
        if (checkHooked()) {
            EcoItems.values().firstOrNull {it.itemStack == itemStack }?.id?.key ?: "UNKNOWN"
        }
        return "UNHOOKED"
    }

}