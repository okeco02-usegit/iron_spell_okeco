package io.redspace.ironsspellbooks.entity.mobs.wizards.archevoker;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.GustDefenseGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class ArchevokerEntity extends AbstractSpellCastingMob implements Enemy {
   public ArchevokerEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 25;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.SUMMON_VEX_SPELL.get(), 1, 3, 100, 260, 1));
      this.f_21345_.m_25352_(1, new GustDefenseGoal(this));
      this.f_21345_
         .m_25352_(
            2,
            new WizardAttackGoal(this, 1.5, 30, 80)
               .setSpells(
                  List.of((AbstractSpell)SpellRegistry.FANG_STRIKE_SPELL.get(), (AbstractSpell)SpellRegistry.FIRECRACKER_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.FANG_WARD_SPELL.get(), (AbstractSpell)SpellRegistry.SHIELD_SPELL.get()),
                  List.of(),
                  List.of()
               )
               .setSpellQuality(0.4F, 0.6F)
               .setSingleUseSpell((AbstractSpell)SpellRegistry.INVISIBILITY_SPELL.get(), 40, 80, 5, 5)
               .setDrinksPotions()
         );
      this.f_21345_.m_25352_(3, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(10, new WizardRecoverGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
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
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistry.ARCHEVOKER_HELMET.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.ARCHEVOKER_CHESTPLATE.get()));
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22282_, 0.0)
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22277_, 24.0)
         .m_22268_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get(), 1.5)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   protected SoundEvent m_7515_() {
      return SoundEvents.f_11861_;
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_11864_;
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_11866_;
   }

   protected boolean m_8028_() {
      return true;
   }
}
