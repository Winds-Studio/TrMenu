package trplugins.menu.module.display.item

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.NBTType
import de.tr7zw.nbtapi.iface.ReadWriteNBT
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.isAir
import trplugins.menu.module.display.MenuSession
import trplugins.menu.module.internal.script.evalScript
import trplugins.menu.util.Regexs

/**
 * @author Arasple
 * @date 2021/1/24 18:50
 * 显示物品的非动画, 支持动态的属性
 */
class Meta(
    val amount: String,
    val shiny: String,
    val flags: Array<ItemFlag>,
    val nbt:  ReadWriteNBT?,
) {

    private val isAmountDynamic = amount.toIntOrNull() == null
    private val isShinyDynamic = !shiny.matches(Regexs.BOOLEAN)
    private val isNBTDynamic = nbt != null && Regexs.containsPlaceholder(nbt.toString())
    val isDynamic = isAmountDynamic || isNBTDynamic || isShinyDynamic

    fun amount(session: MenuSession): Int {
        return (if (isAmountDynamic) session.parse(amount) else amount).toDoubleOrNull()?.toInt() ?: 1
    }

    fun shiny(session: MenuSession, builder: ItemBuilder) {
        if ((shiny.toBoolean()) || (isShinyDynamic && session.placeholderPlayer.evalScript(shiny).asBoolean())) {
            builder.shiny()
        }
        return
    }

    fun flags(builder: ItemBuilder) {
        if (flags.isNotEmpty()) {
            builder.flags.addAll(flags)
        }
    }

    fun nbt(session: MenuSession, itemStack: ItemStack): ItemMeta? {
        if (nbt.toString().isNotEmpty()) {
            val nbt = if (isNBTDynamic) NBT.parseNBT(session.parse(nbt.toString())) else nbt
            if (nbt != null && !itemStack.isAir) {
                NBT.modify(itemStack) { itemNBT ->
                    nbt.keys.forEach { key ->
                        val type = nbt.getType(key)
                        when (type) {
                            NBTType.NBTTagDouble -> itemNBT.setDouble(key, nbt.getDouble(key))
                            NBTType.NBTTagInt -> itemNBT.setInteger(key, nbt.getInteger(key))
                            NBTType.NBTTagString -> itemNBT.setString(key, nbt.getString(key))
                            else -> {}
                        }
                    }
                }
            }
        }
        return null
    }

    fun hasAmount(): Boolean {
        return amount.isNotEmpty() || amount.toIntOrNull() != null
    }

}