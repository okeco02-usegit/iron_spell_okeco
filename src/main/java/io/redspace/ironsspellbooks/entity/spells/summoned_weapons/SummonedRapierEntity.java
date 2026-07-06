package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;

public class SummonedRapierEntity extends SummonedWeaponEntity {
   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22282_, 1.0)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22276_, 15.0)
         .m_22268_(Attributes.f_22277_, 40.0)
         .m_22268_(Attributes.f_22280_, 2.2)
         .m_22268_((Attribute)ForgeMod.ENTITY_REACH.get(), 4.0)
         .m_22268_(Attributes.f_22279_, 0.2);
   }

   @Override
   public GenericAnimatedWarlockAttackGoal<? extends SummonedWeaponEntity> makeAttackGoal() {
      return new GenericAnimatedWarlockAttackGoal<>(this, 1.5, 0, 20).setMoveset(List.of(new AttackAnimationData(40, "summoned_sword_multistab", 20, 26, 32)));
   }

   public SummonedRapierEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SummonedRapierEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends PathfinderMob>)EntityRegistry.SUMMONED_RAPIER.get(), level);
      this.setSummoner(owner);
   }

   @Override
   public boolean m_6469_(DamageSource pSource, float pAmount) {
      if (!this.f_19853_.f_46443_ && pSource.m_7639_() != null && !pSource.m_269533_(DamageTypeTags.f_268738_)) {
         if (this.shouldIgnoreDamage(pSource)) {
            return false;
         }

         if (this.f_19796_.m_188501_() < 0.4F) {
            this.performSidestep(pSource.m_7639_());
            return false;
         }
      }

      return super.m_6469_(pSource, pAmount);
   }

   public void performSidestep(Entity damager) {
      boolean direction = this.f_19796_.m_188499_();
      Vec3 delta = this.m_20182_().m_82546_(damager.m_20182_());
      Vec3 targetPos;
      if (delta.m_82556_() < 49.0) {
         targetPos = damager.m_20182_().m_82549_(delta.m_82524_(direction ? (float) (-Math.PI / 2) : (float) (Math.PI / 2)));
      } else {
         targetPos = this.m_20182_().m_82549_(new Vec3(direction ? -2.0 : 2.0, 0.0, -0.25).m_82524_(this.m_146908_()));
      }

      EntityDimensions dimensions = this.m_6972_(this.m_20089_());
      Vec3 vec3 = targetPos.m_82520_(0.0, dimensions.f_20378_ / 2.0, 0.0);
      VoxelShape voxelshape = Shapes.m_83064_(AABB.m_165882_(vec3, dimensions.f_20377_ + 0.2F, dimensions.f_20378_ + 0.2F, dimensions.f_20377_ + 0.2F));
      Optional<Vec3> optional = this.f_19853_.m_151418_(null, voxelshape, vec3, dimensions.f_20377_, dimensions.f_20378_, dimensions.f_20377_);
      if (optional.isPresent()) {
         targetPos = optional.get().m_82520_(0.0, -dimensions.f_20378_ / 2.0F + 1.0E-6, 0.0);
      }

      if (this.f_19853_
         .m_186437_(
            null, AABB.m_165882_(targetPos.m_82520_(0.0, dimensions.f_20378_ / 2.0F, 0.0), dimensions.f_20377_, dimensions.f_20378_, dimensions.f_20377_)
         )) {
         targetPos = this.m_20182_();
      }

      if (this.m_20159_()) {
         this.m_8127_();
      }

      MagicManager.spawnParticles(
         this.f_19853_, ParticleHelper.UNSTABLE_ENDER, this.m_20185_(), this.m_20186_(), this.m_20189_(), 25, 0.1, 0.1, 0.1, 0.1, false
      );
      this.m_6021_(targetPos.f_82479_, targetPos.f_82480_, targetPos.f_82481_);
      MagicManager.spawnParticles(
         this.f_19853_, ParticleHelper.UNSTABLE_ENDER, this.m_20185_(), this.m_20186_(), this.m_20189_(), 25, 0.1, 0.1, 0.1, 0.1, false
      );
      this.f_19853_
         .m_6263_(
            null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_12052_, SoundSource.AMBIENT, 1.0F, 0.9F + this.f_19796_.m_188501_() * 0.2F
         );
   }
}
