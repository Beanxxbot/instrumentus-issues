package com.beanbot.instrumentus.common.items;

import com.beanbot.instrumentus.common.items.interfaces.IEnergyItem;
import com.beanbot.instrumentus.common.items.interfaces.IItemLightningChargeable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class EnergyShearsItem extends ModShearsItem implements IItemLightningChargeable, IEnergyItem {


    public EnergyShearsItem(Tier material) {
        super(material, new Item.Properties().stacksTo(1).durability(0).fireResistant());
    }

    //TODO: Fix 1.20.5
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) return InteractionResult.PASS;
        if (entity instanceof IShearable) {
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(!(energyStorage == null)){
                if(!(energyStorage.getEnergyStored() > 0)) return InteractionResult.PASS;
            IShearable target = (IShearable) entity;
            BlockPos pos = BlockPos.containing(entity.position());
            if (target.isShearable(stack, entity.level(), pos)) {
                java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level(), pos,
                        net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack));
                java.util.Random rand = new java.util.Random();
                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add((double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double) (rand.nextFloat() * 0.05F), (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });
                if (!(energyStorage == null)) {
                    energyStorage.extractEnergy(getMaxTransferRate() - 24, false);
                }
            }
            return InteractionResult.SUCCESS;
        }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker){
        return energyDamageEnemy(stack, target, attacker);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state){
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null) return 0.0F;
        if(!(energyStorage.getEnergyStored() > 0)) return 0.0F;
        return super.getDestroySpeed(stack, state);
    }

    //TODO: Fix 1.20.5
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
        addTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getBarWidth(ItemStack stack){
        return getEnergyBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack){
        return getEnergyBarColor(stack);
    }

    @Override
    public boolean isDamaged(ItemStack stack){
        return isEnergyBelowZero(stack);
    }
    @Override
    public boolean isBarVisible(ItemStack stack){
        return isEnergyBarVisible(stack);
    }
}
