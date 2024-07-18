package trplugins.menu.util.bukkit

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Banner
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.BannerMeta
import taboolib.common.platform.function.warning
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.ItemTag
import taboolib.platform.util.ItemBuilder
import trplugins.menu.module.display.MenuSettings
import trplugins.menu.module.internal.hook.HookPlugin
import trplugins.menu.util.parseJson
import kotlin.math.min


/**
 * @author Arasple
 * @date 2021/2/4 9:56
 */
object ItemHelper {

    fun serializeColor(color: String): Color {
        val rgb = color.split(",")
        if (rgb.size == 3) {
            val r = min(rgb[0].toIntOrNull() ?: 0, 255)
            val g = min(rgb[1].toIntOrNull() ?: 0, 255)
            val b = min(rgb[2].toIntOrNull() ?: 0, 255)
            return Color.fromRGB(r, g, b)
        }
        return Color.BLACK
    }

    fun deserializeColor(color: Color): String {
        return "${color.red},${color.green},${color.blue}"
    }

    fun deserializePattern(builder: ItemBuilder, string: String) {
        builder.patterns.clear()
        builder.patterns.addAll(string.split(",").let {
            val patterns = mutableListOf<Pattern>()
            it.forEach {
                val type = it.split(" ")
                if (type.size == 1) {
                    builder.finishing = {
                        try {
                            (it.itemMeta as? Banner)?.baseColor = DyeColor.valueOf(type[0].uppercase())
                        } catch (e: Exception) {
                            (it.itemMeta as? Banner)?.baseColor = DyeColor.BLACK
                        }
                    }
                } else if (type.size == 2) {
                    try {
                        patterns.add(
                            Pattern(
                                DyeColor.valueOf(type[0].uppercase()), PatternType.valueOf(
                                    type[1].uppercase()
                                )
                            )
                        )
                    } catch (e: Exception) {
                        patterns.add(Pattern(DyeColor.BLACK, PatternType.BASE))
                    }
                }
            }
            patterns
        })
    }

    fun defColorize(string: String, isLore: Boolean = false): String {
        return if (string.isNotBlank() && !string.startsWith(ChatColor.COLOR_CHAR) && !string.startsWith('&')) {
            val defColor = if (isLore) MenuSettings.DEFAULT_LORE_COLOR else MenuSettings.DEFAULT_NAME_COLOR
            defColor + string
        } else string
    }

    fun isJson(json: String): Boolean {
        return try {
            json.parseJson()
            true
        } catch (e: Throwable) {
            false
        }
    }

    fun fromJson(json: String): ItemStack? {
        try {
            // 自动判别老式/新式 NBT 标签
            if (HookPlugin.getNBTAPI().isHooked && json.startsWith("{\"item\":")) {
                return HookPlugin.getNBTAPI().fromJson(json)
            }
            val parse = JsonParser().parse(json)
            if (parse is JsonObject) {
                val itemStack = parse["type"].let {
                    it ?: return XMaterial.STONE.parseItem()
                    try {
                        XMaterial.valueOf(it.asString).parseItem()
                    } catch (e: IllegalArgumentException) {
                        ItemStack(Material.valueOf(it.asString))
                    }
                }
                parse["data"].let {
                    it ?: return@let
                    itemStack?.durability = it.asShort
                }
                parse["amount"].let {
                    it ?: return@let
                    itemStack?.amount = it.asInt
                }
                val meta = parse["meta"]
                return meta?.let { itemStack?.also { ItemTag.fromLegacyJson(it.toString()).saveTo(it) } } ?: itemStack
            }
            return null
        } catch (t: Throwable) {
            return null
        }
    }

    fun fromPlayerInv(inv: PlayerInventory, item: String): Any? {
        return when (item.lowercase()) {
            "all", "inv" -> inv.contents
            "armor" -> inv.armorContents
            "hand", "mainhand" -> inv.itemInHand
            "offhand" -> inv.itemInOffHand
            "helmet" -> inv.armorContents[3]
            "chestplate" -> inv.armorContents[2]
            "leggings" -> inv.armorContents[1]
            "boots" -> inv.armorContents[0]
            else -> try {
                inv.getItem(Integer.parseInt(item))
            } catch (ignored: NumberFormatException) {
                null
            }
        }
    }

}