package trplugins.menu.module.internal.hook.impl

import io.rokuko.azureflow.api.AzureFlowAPI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract

class HookAzureFlow : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }

    fun getItem(material: String, player: Player): ItemStack {

        val factory = AzureFlowAPI.getFactory(material)
        val stack = factory?.build()?.virtualItemStack(player) ?: empty
        return stack
    }
}