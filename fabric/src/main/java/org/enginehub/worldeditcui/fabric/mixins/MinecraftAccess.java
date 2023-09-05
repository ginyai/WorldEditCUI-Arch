package org.enginehub.worldeditcui.fabric.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Mark Vainomaa
 */
@Mixin(value = Minecraft.class)
public interface MinecraftAccess {
    @Accessor
    Timer getTimer();
}
