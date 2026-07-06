package io.redspace.ironsspellbooks.entity.mobs.debug_wizard;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.DebugTargetClosestEntityGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.DebugWizardAttackGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class DebugWizard extends AbstractSpellCastingMob implements Enemy {
   private AbstractSpell spell;
   private int spellLevel;
   private boolean targetsPlayer;
   private String spellInfo = "No Spell Found";
   private int cancelCastAfterTicks;
   private static final EntityDataAccessor<String> DEBUG_SPELL_INFO = SynchedEntityData.m_135353_(DebugWizard.class, EntityDataSerializers.f_135030_);

   public DebugWizard(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public DebugWizard(
      EntityType<? extends AbstractSpellCastingMob> pEntityType,
      Level pLevel,
      AbstractSpell spell,
      int spellLevel,
      boolean targetsPlayer,
      int cancelCastAfterTicks
   ) {
      super(pEntityType, pLevel);
      this.targetsPlayer = targetsPlayer;
      this.spellLevel = spellLevel;
      this.spell = spell;
      this.cancelCastAfterTicks = cancelCastAfterTicks;
      this.initGoals();
   }

   public String getSpellInfo() {
      return this.spellInfo;
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DEBUG_SPELL_INFO, "DEFAULT");
   }

   @Override
   public void m_7350_(EntityDataAccessor<?> pKey) {
      super.m_7350_(pKey);
      if (this.m_9236_().f_46443_) {
         if (pKey.m_135015_() == DEBUG_SPELL_INFO.m_135015_()) {
            this.spellInfo = (String)this.f_19804_.m_135370_(DEBUG_SPELL_INFO);
         }
      }
   }

   private void initGoals() {
      this.f_21345_.m_25352_(1, new DebugWizardAttackGoal(this, this.spell, this.spellLevel, this.cancelCastAfterTicks));
      if (this.targetsPlayer) {
         IronsSpellbooks.LOGGER.debug("DebugWizard: Adding DebugTargetClosestEntityGoal");
         this.f_21346_.m_25352_(1, new DebugTargetClosestEntityGoal(this));
      }

      this.f_19804_.m_135381_(DEBUG_SPELL_INFO, String.format("%s (L%s)", this.spell.getSpellName(), this.spellLevel));
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      pCompound.m_128359_("spellId", this.spell.getSpellId());
      pCompound.m_128405_("spellLevel", this.spellLevel);
      pCompound.m_128379_("targetsPlayer", this.targetsPlayer);
      pCompound.m_128405_("cancelCastAfterTicks", this.cancelCastAfterTicks);
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.spell = SpellRegistry.getSpell(pCompound.m_128461_("spellId"));
      this.spellLevel = pCompound.m_128451_("spellLevel");
      this.targetsPlayer = pCompound.m_128471_("targetsPlayer");
      this.cancelCastAfterTicks = pCompound.m_128451_("cancelCastAfterTicks");
      this.initGoals();
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22276_, 30.0)
         .m_22268_(Attributes.f_22277_, 40.0)
         .m_22268_(Attributes.f_22279_, 0.4);
   }
}
