package trplugins.menu.util.bukkit

import com.mojang.authlib.GameProfile
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.XSkull
import taboolib.module.nms.MinecraftVersion

/**
 * @author Arasple
 * @date 2021/1/27 14:05
 */
object Heads {

    private val DEFAULT_HEAD = XMaterial.PLAYER_HEAD.parseItem()!!
    private val CACHED_PLAYER_TEXTURE = mutableMapOf<String, String?>()
    private val CACHED_SKULLS = mutableMapOf<String, ItemStack>()
    private val VALUE = if (MinecraftVersion.major >= 1.20) "value" else "getValue"
    private val NAME = if (MinecraftVersion.major >= 1.20) "name" else "getName"

    fun cacheSize(): Pair<Int, Int> {
        return CACHED_SKULLS.size to CACHED_PLAYER_TEXTURE.size
    }

    fun getPlayerHead(name: String): ItemStack = CACHED_SKULLS.computeIfAbsent(name) {
        (CACHED_SKULLS[it] ?: DEFAULT_HEAD).apply {
            itemMeta = itemMeta?.let { m -> XSkull.applySkin(m, name) }
        }
    }

    fun seekTexture(itemStack: ItemStack): String? {
        val meta = itemStack.itemMeta ?: return null

        if (meta is SkullMeta) {
            meta.owningPlayer?.name?.let { return it }
        }

        meta.getProperty<GameProfile>("profile")?.properties?.values()?.forEach {
            if (it.getProperty<String>(NAME) == "textures") return it.getProperty<String>(VALUE)
        }
        return null
    }
}
