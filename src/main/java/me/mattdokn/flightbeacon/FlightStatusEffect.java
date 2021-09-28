package me.mattdokn.flightbeacon;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class FlightStatusEffect extends StatusEffect {
    public FlightStatusEffect() {
        super( StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            // Set ability flag
            player.getAbilities().allowFlying = true;
            // Update ability flags that way the client can register the changes
            player.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            // Turn off the player flag and update it on the client
            player.getAbilities().allowFlying = false;
            player.sendAbilitiesUpdate();

            // Give the player the NoFall status effect for 8 seconds (20 * 8 = 160)
            player.addStatusEffect(new StatusEffectInstance(FlightBeacon.NOFALL, 160, 0, true, true));
        }
    }
}
