package trplugins.menu.module.internal.command.impl

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.io.newFile
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XSound
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.platform.util.isAir
import taboolib.platform.util.sendLang
import taboolib.module.nms.getName
import taboolib.type.BukkitEquipment
import trplugins.menu.module.internal.command.CommandExpression
import trplugins.menu.module.internal.hook.HookPlugin
import trplugins.menu.module.internal.item.ItemRepository
import trplugins.menu.util.bukkit.ItemHelper

/**
 * @author Arasple
 * @date 2021/1/31 10:41
 */
object CommandItem : CommandExpression {

    // toJson -NoValueNeeded-
    // fromJson [value]
    // save [id]
    // get [id]
    // del [id]

    // trm item [Method] <Value>
    override val command = subCommand {
        // Method
        dynamic {
            suggestion<CommandSender> { _, _ ->
                listOf("toJson", "fromJson", "save", "get", "del", "delete")
            }
            // Method
            execute<Player> { player, _, argument ->
                if (!argument.equals("toJson", ignoreCase = true)) {
                    return@execute
                }
                XSound.ITEM_BOTTLE_FILL.play(player, 1f, 0f)

                val item = BukkitEquipment.getItems(player)[BukkitEquipment.HAND]

                item ?: kotlin.run {
                    player.sendLang("Command-Item-No-Item")
                    return@execute
                }
                toJson(player, item)
            }
            // Value
            dynamic(optional = true) {
                execute<Player> { player, context, argument ->
                    XSound.ITEM_BOTTLE_FILL.play(player, 1f, 0f)

                    val item = BukkitEquipment.getItems(player)[BukkitEquipment.HAND]

                    when (context.argument(-1).lowercase()) {
                        "fromjson" -> fromJson(player, argument)
                        "get" -> ItemRepository.itemStacks[argument]?.let {
                            player.inventory.addItem(it).values.forEach { e ->
                                player.world.dropItem(
                                    player.location,
                                    e
                                )
                            }
                        }
                        "save" -> item?.let {
                            ItemRepository.itemStacks[argument] = item
                            submit(async = true) { ItemRepository.saveTask() }
                            player.sendLang("Command-Item-Saved", argument)
                        }
                        "delete", "del" -> ItemRepository.removeItem(argument)?.let {
                            player.sendLang("Command-Item-Deleted", argument)
                        }
                    }
                }
            }
        }
    }

    private val saveFile by lazy {
        Configuration.loadFromFile(newFile(getDataFolder(), "itemsJson.yml", create = true), Type.YAML)
    }

    private fun toJson(player: Player, item: ItemStack) {
        if (item.isAir) {
            player.sendLang("Command-Item-No-Item")
            return
        }
        val name = item.getName()
        val stringJson: String = HookPlugin.getNBTAPI().toJson(item)

        if (stringJson.length < 200) {
            player.sendLang("Command-Item-To-Json", stringJson)
        } else {
//            Paster.paste(adaptPlayer(player), stringJson, "json")
            saveFile[name] = stringJson
            saveFile.saveToFile()
            player.sendMessage("§7[§3TrMenu§7] §8[§7Item§8] §7Item §3$name §7has been saved to §3${saveFile.file?.name}§7.")
        }
    }

    private fun fromJson(player: Player, json: String) {
        ItemHelper.fromJson(json)?.let {
            player.inventory.addItem(it).values.forEach { e -> player.world.dropItem(player.location, e) }
        }
    }

}