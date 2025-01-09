package trplugins.menu.util.hybrid

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

object ModItemSource {
    private val notFound = buildItem(XMaterial.BARRIER) { name = "NOT_FOUND" }
    fun getItem(name: String): ItemStack {
        return Material.getMaterial(name.uppercase().replace("(:|\\s)".toRegex(), "_").replace("\\W".toRegex(), ""))?.let { buildItem(it) } ?: notFound
    }
}