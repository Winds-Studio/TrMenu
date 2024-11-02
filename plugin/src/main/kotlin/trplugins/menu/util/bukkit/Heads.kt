package trplugins.menu.util.bukkit

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.BukkitSkull
import trplugins.menu.module.internal.hook.HookPlugin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*

/**
 * @author Arasple
 * @date 2021/1/27 14:05
 */
object Heads {

    private const val USER_API = "https://api.mojang.com/users/profiles/minecraft/"
    private const val SESSION_API = "https://sessionserver.mojang.com/session/minecraft/profile/"

    private val JSON_PARSER = JsonParser()
    private val DEFAULT_HEAD = XMaterial.PLAYER_HEAD.parseItem()!!.apply {
        if (runCatching { Material.PLAYER_HEAD }.isFailure) {
            durability = 3
        }
    }
    private val CACHED_SKULLS = mutableMapOf<String, ItemStack>()
    private val VALUE = if (MinecraftVersion.major >= 1.20) "value" else "getValue"
    private val NAME = if (MinecraftVersion.major >= 1.20) "name" else "getName"
    private val USE_PROFILE = runCatching { OfflinePlayer::class.java.getDeclaredMethod("getPlayerProfile") }.isSuccess

    fun cacheSize(): Int {
        return CACHED_SKULLS.size
    }

    fun getHead(id: String): ItemStack {
        return if (id.length <= 20) {
            getPlayerHead(id)
        } else if (id.length == 32) {
            getPlayerHead(UUID.fromString(StringBuilder(id)
                .insert(20, '-').insert(16, '-').insert(12, '-').insert(8, '-')
                .toString()))
        } else if (id.length == 36) {
            getPlayerHead(UUID.fromString(id))
        } else {
            getCustomHead(id)
        }
    }

    private fun getCustomHead(id: String): ItemStack = CACHED_SKULLS.computeIfAbsent(id) {
        if (id.startsWith("http://textures.minecraft.net/texture/")) {
            BukkitSkull.applySkull(id.substring(38))
        } else {
            BukkitSkull.applySkull(id)
        }
    }.clone()

    private fun getPlayerHead(uniqueId: UUID): ItemStack {
        val player = Bukkit.getPlayer(uniqueId)
        if (player != null) {
            return getPlayerHead(player)
        }
        val name = Bukkit.getOfflinePlayer(uniqueId).name
        return if (name == null) DEFAULT_HEAD else getPlayerHead(name)
    }

    private fun getPlayerHead(name: String): ItemStack {
        if (HookPlugin.getSkinsRestorer().isHooked) {
            val texture: String? = HookPlugin.getSkinsRestorer().getPlayerSkinTexture(name)
            return texture?.let { getCustomHead(it) } ?: DEFAULT_HEAD
        }
        val player = Bukkit.getPlayer(name)
        if (player != null) {
            return getPlayerHead(player)
        }
        val texture = seekTexture(name)
        return if (texture == null) DEFAULT_HEAD else getCustomHead(texture)
    }

    private fun getPlayerHead(player: Player): ItemStack {
        if (USE_PROFILE) {
            return getCustomHead(player.playerProfile.textures.skin.toString())
        } else {
            val profile = player.invokeMethod<GameProfile>("getProfile")
            profile?.properties?.get("textures")?.forEach { texture ->
                if (texture != null) {
                    return getCustomHead(texture.getProperty<String>(VALUE)!!)
                }
            }
            val texture = seekTexture(player.name)
            return if (texture == null) DEFAULT_HEAD else getCustomHead(texture)
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

    fun seekTexture(name: String): String? {
        val user = urlJson(USER_API + name)
        if (user != null && user.has("id")) {
            val uuid = user["id"].asString
            val session = urlJson(SESSION_API + uuid)
            if (session != null) {
                for (element in session.getAsJsonArray("properties")) {
                    val property = element.asJsonObject
                    if (property["name"].asString == "textures") {
                        val value = property["value"].asString
                        val texture = JSON_PARSER.parse(String(Base64.getDecoder().decode(value))).asJsonObject
                        if (texture != null) {
                            return texture["textures"].asJsonObject["SKIN"].asJsonObject["url"].asString
                        }
                    }
                }
            }
        }
        return null
    }

    private fun urlJson(url: String): JsonObject? {
        val text = urlText(url)
        return if (text.trim { it <= ' ' }.isEmpty()) {
            null
        } else {
            JSON_PARSER.parse(text).asJsonObject
        }
    }

    private fun urlText(url: String): String {
        try {
            val con = URL(url).openConnection()
            // Java 8 require user agent
            con.addRequestProperty("User-Agent", "Mozilla/5.0")
            con.getInputStream().use { `in` ->
                BufferedReader(InputStreamReader(`in`)).use { reader ->
                    val out = java.lang.StringBuilder()
                    var line: String?
                    while ((reader.readLine().also { line = it }) != null) {
                        out.append(line)
                    }
                    return out.toString()
                }
            }
        } catch (e: Exception) {
            return ""
        }
    }
}