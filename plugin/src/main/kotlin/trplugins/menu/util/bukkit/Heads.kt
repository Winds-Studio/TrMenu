package trplugins.menu.util.bukkit

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.profiles.builder.XSkull
import com.cryptomorin.xseries.profiles.objects.Profileable
import com.mojang.authlib.GameProfile
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
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
            XSkull.of(this).profile(Profileable.detect(id)).apply()
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