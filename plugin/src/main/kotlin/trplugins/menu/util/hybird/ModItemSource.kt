package trplugins.menu.util.hybird

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

object ModItemSource {
    private val notFound = buildItem(XMaterial.BEDROCK) { name = "NOT_FOUND" }
    fun getItem(name: String): ItemStack {
        return Material.getMaterial(name.uppercase().replace(':', '_'))?.let { buildItem(it) } ?: notFound
    }
}