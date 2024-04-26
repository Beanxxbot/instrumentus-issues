package com.beanbot.instrumentus.common.items;

import com.beanbot.instrumentus.common.items.interfaces.IEnergyItem;
import com.beanbot.instrumentus.common.items.interfaces.IItemLightningChargeable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EnergyHammerItem extends HammerItem implements IItemLightningChargeable, IEnergyItem {

    protected Tier material;

    public EnergyHammerItem(Tier material, float attackDamageIn, float attackSpeedIn){
        super(material, attackSpeedIn, attackDamageIn, new Item.Properties().stacksTo(1).fireResistant());
        this.material = material;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity){
        if(state.getBlock() == null || world.getBlockState(pos).getBlock() == Blocks.AIR)
            return false;

        boolean isStone = state.is(BlockTags.MINEABLE_WITH_PICKAXE);

        int r = isStone ? 0 : 2;

        if(material == Tiers.WOOD || material == Tiers.STONE || material == Tiers.IRON || material == Tiers.GOLD || material == Tiers.DIAMOND || material == Tiers.NETHERITE){
            r = 1;
        }

        //TODO: Fix 1.20.5
        stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        int numberTrimmed = 0;

        if(isStone && !entity.isCrouching())
        {
            numberTrimmed += trim(stack, entity, world, pos, r, HammerItem.TrimType.TRIM_ROCK, false, 100);
        }
        return numberTrimmed > 0;
    }

    public int trim(ItemStack stack, LivingEntity entity, Level world, BlockPos blockPos, int r, HammerItem.TrimType trimType, boolean cutCorners, int damagePercentChance){
        int numberTrimmed = 0;
        int fortune = 0;
        Vec3 look = entity.getLookAngle();

        if(look.x >= -1 && look.x <= -0.75 || look.x <= 1 && look.x >= 0.75) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (dy == 0 && dz == 0)
                        continue;
                    if (trimType.trimAtPos(world, blockPos.offset(0, dy, dz), entity, stack)) {
                        numberTrimmed++;
                        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                            if(!(energyStorage == null)){
                                if(world.getBlockState(blockPos).getDestroySpeed(world, blockPos) != 0.0F){
                                    energyStorage.extractEnergy(getMaxTransferRate() - 24, false);
                                }
                            }
                    }
                }
            }
        } else if(look.z >= -1 && look.z <= -0.75 || look.z <= 1 && look.z >= 0.75) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (dy == 0 && dx == 0)
                        continue;
                    if (trimType.trimAtPos(world, blockPos.offset(dx, dy, 0), entity, stack)) {
                        numberTrimmed++;
                        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                        if(!(energyStorage == null)){
                            if(world.getBlockState(blockPos).getDestroySpeed(world, blockPos) != 0.0F){
                                energyStorage.extractEnergy(getMaxTransferRate() - 24, false);
                            }
                        }
                    }
                }
            }
        } else if (look.y >= -1 && look.y <= -0.75 || look.y <= 1 && look.y >= 0.75) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dz == 0 && dx == 0)
                        continue;
                    if (trimType.trimAtPos(world, blockPos.offset(dx, 0, dz), entity, stack)) {
                        numberTrimmed++;
                        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                        if(!(energyStorage == null)){
                            if(world.getBlockState(blockPos).getDestroySpeed(world, blockPos) != 0.0F){
                                energyStorage.extractEnergy(getMaxTransferRate() - 24, false);
                            }
                        }
                    }
                }
            }
        }
        return numberTrimmed;
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

    public enum TrimType{
        TRIM_ROCK;

        public boolean trimAtPos(Level world, BlockPos pos, LivingEntity entity, ItemStack item)
        {
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, (Player) entity);
            NeoForge.EVENT_BUS.post(event);

            switch (this){
                case TRIM_ROCK:default:
                    if(state.is(BlockTags.MINEABLE_WITH_PICKAXE) && state.canHarvestBlock(world, pos, (Player)entity)){
                        state.getBlock().playerDestroy(world, (Player) entity, pos, state,  blockEntity, item);
                        state.getBlock().popExperience((ServerLevel) world, pos, event.getExpToDrop());
                        world.removeBlock(pos, false);
                        return true;
                    }
                    return false;
            }
        }
    }
}
