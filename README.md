# FabricHG

## Creating a kit

Start with calling the `kit` function

Pass the name the kit will be having as the first parameter and open the `KitBuilder`
```kt
val blinkKit = kit("Blink") {

}
```

First, let´s change the Item which will be displayed in the kitselector, so change the `kitSelectorItem` property:

```kt
val blinkKit = kit("Blink") {
    kitSelectorItem = Items.NETHER_STAR.defaultStack
}
```

After that we will want to have a simple clickable KitItem. 

To open a `KitItemBuilder` use the `kitItem` function

```kt
val blinkKit = kit("Blink") {
    kitSelectorItem = Items.NETHER_STAR.defaultStack

    kitItem {       
    
    }
}
```

In this case we want to have the `kitSelectorItem` as the `kitItem` ItemStack, but you can use any ItemStack you want

```kt
val blinkKit = kit("Blink") {
    kitSelectorItem = Items.NETHER_STAR.defaultStack

    kitItem {
        itemStack = kitSelectorItem
    }
}
```

As we only want to blink if the player clicks the kitItem, use the `onClick` method

Inside the method you have acess to the `HGPlayer` which used the kitItem and the kit which kitItem was used

```kt
val blinkKit = kit("Blink") {
    kitSelectorItem = Items.NETHER_STAR.defaultStack

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayerEntity ?: return@onClick
        }
    }
}
```
You can easily get the `ServerPlayerEntity` by using the `serverPlayerEntity` value on the `hgPlayer`

Now you can implement the kit logic:

```kt
val blinkKit = kit("Blink") {
val maxUses = 5
kitSelectorItem = Items.NETHER_STAR.defaultStack
    cooldown = 15.0

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayerEntity ?: return@onClick
            hgPlayer.checkUsesForCooldown(kit, maxUses)
        }
    }
}
```

By setting the `cooldown` property inside the `KitBuilder` you can either check for the remaining uses if you can use the kit more than once by using `HGPlayer.checkUsesForCooldown`
or apply the cooldown yourself with `HGPlayer.activateCooldown`

After you checked the cooldown you implement the remaining logic

```kt
val blinkKit = kit("Blink") {
    val maxUses = 5
    val blinkDistance = 4.0
    kitSelectorItem = Items.NETHER_STAR.defaultStack
    cooldown = 15.0

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayerEntity ?: return@onClick
            hgPlayer.checkUsesForCooldown(kit, maxUses)
            val newPos = player.pos.add(player.direction.normalize().multiply(blinkDistance))
            player.teleport(
                newPos.x, newPos.y, newPos.z
            )
            player.world.setBlockState(BlockPos(player.pos.subtract(0.0, 1.0, 0.0)), Blocks.OAK_LEAVES.defaultState)
            player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 100F, 100F)
        }
    }
}
```
If you want to execute something when the player does something without his kititem, use the `events` function

```kt
val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)

    usableInInvincibility = false

    events {
        onHitEntity { _, _, entity ->
            if (Random.nextInt(4) == 3)
                entity.fireTicks += 40
        }
    }
}
```
If you need a event which is not available, feel free to add it
