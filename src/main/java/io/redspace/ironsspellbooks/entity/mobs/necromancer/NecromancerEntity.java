package io.redspace.ironsspellbooks.entity.mobs.necromancer;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class NecromancerEntity extends AbstractSpellCastingMob implements Enemy {
   public NecromancerEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 15;
   }

   protected void m_8099_() {
      this.f_21345_
         .m_25352_(
            4,
            new WizardAttackGoal(this, 1.25, 35, 80)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.FANG_STRIKE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ICICLE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.FANG_WARD_SPELL.get()),
                  List.of(),
                  List.of((AbstractSpell)SpellRegistry.BLIGHT_SPELL.get(), (AbstractSpell)SpellRegistry.ROOT_SPELL.get())
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.RAISE_DEAD_SPELL.get(), 80, 350, 4, 5)
               .setDrinksPotions()
         );
      this.f_21345_.m_25352_(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.f_21345_.m_25352_(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
      this.f_21345_.m_25352_(10, new WizardRecoverGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.f_30122_));
   }

   protected SoundEvent m_7515_() {
      return SoundEvents.f_12423_;
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_12381_;
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_12424_;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.f_12383_;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistry.TARNISHED_CROWN.get()));
      this.m_21409_(EquipmentSlot.HEAD, 0.15F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22282_, 0.0)
         .m_22268_(Attributes.f_22276_, 25.0)
         .m_22268_(Attributes.f_22277_, 25.0)
         .m_22268_((Attribute)AttributeRegistry.SPELL_POWER.get(), 0.75)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   protected boolean m_8028_() {
      return true;
   }
}
