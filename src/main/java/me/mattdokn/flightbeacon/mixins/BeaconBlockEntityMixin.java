package me.mattdokn.flightbeacon.mixins;

import me.mattdokn.flightbeacon.FlightBeacon;
import me.mattdokn.flightbeacon.FlightStatusEffect;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @Inject(method = "applyPlayerEffects", at = @At("TAIL"))
    private static void applyPlayerEffects(World world, BlockPos pos, int beaconLevel, StatusEffect primaryEffect, StatusEffect secondaryEffect, CallbackInfo ci) {
        if (!world.isClient) {
            // If the block below the beacon is a netherite block
            if (world.getBlockState(pos.down()) == Blocks.NETHERITE_BLOCK.getDefaultState()) {
                // Calc range of beacon
                double d = beaconLevel * 10 + 10;
                // Create box around beacon that represents the status effect range
                Box box = (new Box(pos)).expand(d).stretch(0.0D, world.getHeight(), 0.0D);
                // Get all players in the box
                List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

                // Iterate over each player
                Iterator iter = list.iterator();
                PlayerEntity playerEnt;
                while(iter.hasNext()) {
                    playerEnt = (PlayerEntity)iter.next();
                    // Add 8 seconds (8 * 20 = 160) of the flight status effect to the player
                    playerEnt.addStatusEffect(new StatusEffectInstance(FlightBeacon.FLIGHT, 160, 0, true, true));
                }
            }
        }
    }
}
