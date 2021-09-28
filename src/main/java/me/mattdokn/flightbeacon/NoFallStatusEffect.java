package me.mattdokn.flightbeacon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class NoFallStatusEffect extends StatusEffect {
    public NoFallStatusEffect() {
        super( StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            // Checks incase the player wouldnt normally take fall damage anyways
            if (player.fallDistance <= (player.isFallFlying() ? 1f : 2f)) return;
            if (player.isFallFlying() && player.isSneaking() && !(player.getVelocity().y < -.5f)) return;

            // Modify the players fallDistance tracker
            player.fallDistance = 0f;
        }
    }


    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            // Execute this on the next world tick, as if the player still has the flight status effect
            // at that time then we dont want to turn it off, causing them to fall to the ground
            FlightBeacon.scheduler.add(new ScheduledTask((int) (FlightBeacon.server.getOverworld().getTime() + 1), () -> {
                // Serves as a check to see if the status effect is active
                if (!player.getAbilities().allowFlying) {
                    // Set flying to false and update on client, this will cause them to fall on the ground
                    // If this isn't done then the player will still be in the flight state, causing them to be
                    // kicked for flying
                    player.getAbilities().flying = false;
                    player.sendAbilitiesUpdate();
                }
            }));
        }
    }
}
