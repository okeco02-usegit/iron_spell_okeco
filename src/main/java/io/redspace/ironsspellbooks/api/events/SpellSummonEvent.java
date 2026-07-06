package io.redspace.ironsspellbooks.api.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

@Deprecated
public class SpellSummonEvent<K extends LivingEntity> extends LivingEvent {
   private LivingEntity caster = null;
   private K creature = (K)null;
   private final ResourceLocation spellId;
   private int spellLevel = 0;

   public SpellSummonEvent(LivingEntity caster, K creature, ResourceLocation spellId, int spellLevel) {
      super(caster);
      this.caster = caster;
      this.creature = creature;
      this.spellId = spellId;
      this.spellLevel = spellLevel;
   }

   public K getCreature() {
      return this.creature;
   }

   public void setCreature(K creature) {
      this.creature = creature;
   }

   public LivingEntity getCaster() {
      return this.caster;
   }

   public ResourceLocation getSpellId() {
      return this.spellId;
   }

   public int getSpellLevel() {
      return this.spellLevel;
   }
}
