package trplugins.menu.util.bukkit

import com.mojang.authlib.GameProfile
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.profiles.builder.XSkull
import taboolib.module.nms.MinecraftVersion
import trplugins.menu.module.internal.hook.HookPlugin

/**
 * @author Arasple
 * @date 2021/1/27 14:05
 */
object Heads {

    private val DEFAULT_HEAD = XMaterial.PLAYER_HEAD.parseItem()!!
    private val CACHED_SKULLS = mutableMapOf<String, ItemStack>()
    private val VALUE = if (MinecraftVersion.major >= 1.20) "value" else "getValue"
    private val NAME = if (MinecraftVersion.major >= 1.20) "name" else "getName"

    fun cacheSize(): Int {
        return CACHED_SKULLS.size
    }

    fun getHead(id: String): ItemStack {
        return if (id.length > 20) getCustomHead(id) else getPlayerHead(id)
    }

    private fun getCustomHead(id: String): ItemStack = CACHED_SKULLS.computeIfAbsent(id) {
        DEFAULT_HEAD.clone().apply {
            itemMeta = itemMeta?.let { m -> XSkull.of(m).apply() }
        }
    }.clone()

    private fun getPlayerHead(name: String): ItemStack {
        if (HookPlugin.getSkinsRestorer().isHooked) {
            val texture: String? = HookPlugin.getSkinsRestorer().getPlayerSkinTexture(name)
            return texture?.let { getCustomHead(it) } ?: DEFAULT_HEAD
        }
        return getCustomHead(name)
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