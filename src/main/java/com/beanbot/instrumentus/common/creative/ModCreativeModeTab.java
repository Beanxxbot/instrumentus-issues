package com.beanbot.instrumentus.common.creative;

import com.beanbot.instrumentus.common.Instrumentus;
import com.beanbot.instrumentus.common.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Instrumentus.MODID);
    public static Supplier<CreativeModeTab> MOD_ITEM_GROUP = CREATIVE_MOD_TABS.register("instrumentus_tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.DIAMOND_PAXEL.get()))
                    .title(Component.translatable("instrumentus.creativetab")).build());

    public static void register(IEventBus bus) {
        CREATIVE_MOD_TABS.register(bus);
    }
}
