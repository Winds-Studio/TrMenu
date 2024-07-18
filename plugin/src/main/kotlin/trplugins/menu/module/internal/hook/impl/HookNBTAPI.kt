package trplugins.menu.module.internal.hook.impl

import de.tr7zw.nbtapi.NBT
import org.bukkit.inventory.ItemStack
import trplugins.menu.module.internal.hook.HookAbstract

class HookNBTAPI:HookAbstract(){

    fun toJson(item: ItemStack): String {
        return "{\"item\":${NBT.itemStackToNBT(item)}}"
    }

    fun fromJson(json: String): ItemStack {
        return NBT.itemStackFromNBT(NBT.parseNBT(json.removePrefix("{\"item\":").removeSuffix("}")))
            ?: error("Failed to parse item from json: $json")
    }

}