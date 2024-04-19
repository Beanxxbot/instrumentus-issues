package com.beanbot.instrumentus.client;

import com.beanbot.instrumentus.common.ISidedReference;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
//import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class ClientReference implements ISidedReference {
    public static final KeyMapping warpedTeleportBinding = new KeyMapping("instrumentus.key.warpedTeleport", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KeyMapping.CATEGORY_GAMEPLAY);

    @Override
    public void setup(final IEventBus mod, final IEventBus forge) {
        mod.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
//        ClientRegistry.registerKeyBinding(warpedTeleportBinding);
    }
}
