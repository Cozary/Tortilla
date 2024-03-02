/*
 *
 *  * Copyright (c) 2024 Cozary
 *  *
 *  * This file is part of Tortilla, a mod made for Minecraft.
 *  *
 *  * Tortilla is free software: you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published
 *  * by the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * Tortilla is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * License along with Tortilla.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.cozary.tortilla;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TortillaBlock extends Block {

    public static final int MAX_BITES = 6;
    public static final IntegerProperty BITES = BlockStateProperties.BITES;
    public static final int FULL_CAKE_SIGNAL = getOutputSignal(0);
    protected static final float AABB_OFFSET = 1.0F;
    protected static final float AABB_SIZE_PER_BITE = 2.0F;
    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.box(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};

    public TortillaBlock() {
        super(Properties.of(Material.CAKE)
                .strength(0.5F)
                .sound(SoundType.WOOL)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, Integer.valueOf(0)));
    }

    protected static InteractionResult eat(LevelAccessor p_51186_, BlockPos p_51187_, BlockState p_51188_, Player p_51189_) {
        if (!p_51189_.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            p_51189_.awardStat(Stats.EAT_CAKE_SLICE);
            p_51189_.getFoodData().eat(4, 0.2F);
            int i = p_51188_.getValue(BITES);
            p_51186_.gameEvent(p_51189_, GameEvent.EAT, p_51187_);
            if (i < 6) {
                p_51186_.setBlock(p_51187_, p_51188_.setValue(BITES, Integer.valueOf(i + 1)), 3);
            } else {
                p_51186_.removeBlock(p_51187_, false);
                p_51186_.gameEvent(p_51189_, GameEvent.BLOCK_DESTROY, p_51187_);
            }

            return InteractionResult.SUCCESS;
        }
    }

    public static int getOutputSignal(int p_152747_) {
        return (7 - p_152747_) * 2;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState p_51222_, @NotNull BlockGetter p_51223_, @NotNull BlockPos p_51224_, @NotNull CollisionContext p_51225_) {
        return SHAPE_BY_BITE[p_51222_.getValue(BITES)];
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState p_51202_, @NotNull Level p_51203_, @NotNull BlockPos p_51204_, Player p_51205_, @NotNull InteractionHand p_51206_, @NotNull BlockHitResult p_51207_) {
        ItemStack itemstack = p_51205_.getItemInHand(p_51206_);
        Item item = itemstack.getItem();
        if (itemstack.is(ItemTags.CANDLES) && p_51202_.getValue(BITES) == 0) {
            Block block = Block.byItem(item);
            if (block instanceof CandleBlock) {
                if (!p_51205_.isCreative()) {
                    itemstack.shrink(1);
                }

                p_51203_.playSound(null, p_51204_, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
                p_51203_.setBlockAndUpdate(p_51204_, CandleCakeBlock.byCandle(block));
                p_51203_.gameEvent(p_51205_, GameEvent.BLOCK_CHANGE, p_51204_);
                p_51205_.awardStat(Stats.ITEM_USED.get(item));
                return InteractionResult.SUCCESS;
            }
        }

        if (p_51203_.isClientSide) {
            if (eat(p_51203_, p_51204_, p_51202_, p_51205_).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (itemstack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(p_51203_, p_51204_, p_51202_, p_51205_);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState p_51213_, @NotNull Direction p_51214_, @NotNull BlockState p_51215_, @NotNull LevelAccessor p_51216_, @NotNull BlockPos p_51217_, @NotNull BlockPos p_51218_) {
        return p_51214_ == Direction.DOWN && !p_51213_.canSurvive(p_51216_, p_51217_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_51213_, p_51214_, p_51215_, p_51216_, p_51217_, p_51218_);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState p_51209_, LevelReader p_51210_, BlockPos p_51211_) {
        return p_51210_.getBlockState(p_51211_.below()).getMaterial().isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51220_) {
        p_51220_.add(BITES);
    }

    @Override
    public int getAnalogOutputSignal(BlockState p_51198_, @NotNull Level p_51199_, @NotNull BlockPos p_51200_) {
        return getOutputSignal(p_51198_.getValue(BITES));
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState p_51191_) {
        return true;
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState p_51193_, @NotNull BlockGetter p_51194_, @NotNull BlockPos p_51195_, @NotNull PathComputationType p_51196_) {
        return false;
    }

}
