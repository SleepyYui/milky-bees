package com.sleepyyui

import net.fabricmc.api.ModInitializer
import net.minecraft.util.ActionResult
import org.slf4j.LoggerFactory

import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.passive.BeeEntity
import net.minecraft.text.Text


object MilkyBees : ModInitializer {
    private val logger = LoggerFactory.getLogger("milky-bees")


    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

        UseEntityCallback.EVENT.register { player, world, hand, entity, _ ->
            logger.debug("UseEntityCallback")
            logger.debug("UseEntityCallback: Entity: {}", entity)
            if (entity is BeeEntity) {
                logger.debug("UseEntityCallback: Bee")
                if (player != null && entity.hasNectar() && !entity.hasStung()) {
                    logger.debug("UseEntityCallback: Bee: Sneaking")
                    // get item that player is holding
                    val itemStack = player.getStackInHand(hand)

                    // if player is holding a bucket milk the bee
                    if (itemStack.item == net.minecraft.item.Items.BUCKET) {
                        logger.debug("UseEntityCallback: Bee: Bucket")
                        // remove bucket from player's inventory
                        itemStack.decrement(1)

                        // add milk bucket to player's inventory
                        val milk = net.minecraft.item.ItemStack(net.minecraft.item.Items.MILK_BUCKET)
                        val milk_name = Text.literal("\"Milk\" Bucket")
                        milk.setCustomName(milk_name)

                        player.giveItemStack(milk)

                        // play sound
                        entity.playSound(net.minecraft.sound.SoundEvents.ENTITY_COW_MILK, 1.0f, 1.0f)

                        // remove nectar from bee
                        entity.onHoneyDelivered()

                        // send message to global chat
                        val player_name_string = player.name.string
                        val message = Text.of("$player_name_string milked an unsuspecting \"bee\"")
                        world.server?.playerManager?.broadcast(message, false)

                        // return success
                        return@register ActionResult.SUCCESS
                    }
                    return@register ActionResult.PASS
                }
            }
            return@register ActionResult.PASS
        }
    }
}

