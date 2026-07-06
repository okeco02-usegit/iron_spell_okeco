package io.redspace.ironsspellbooks.entity;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(forRemoval = true)
public class VisualFallingBlockEntity extends FallingBlockEntity {
   int maxAge = 200;
   private double originalX;
   private double originalY;
   private double originalZ;
   private double ticks;
   private boolean particlesOnImpact;

   public VisualFallingBlockEntity(EntityType<? extends VisualFallingBlockEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public void m_6853_(boolean pOnGround) {
   }

   public boolean m_20096_() {
      return this.f_19797_ > 1 && this.m_20184_().m_82556_() < 0.001F;
   }

   public VisualFallingBlockEntity(Level pLevel, double pX, double pY, double pZ, BlockState pState) {
      this((EntityType<? extends VisualFallingBlockEntity>)EntityRegistry.FALLING_BLOCK.get(), pLevel);
      this.originalX = pX;
      this.originalY = pY;
      this.originalZ = pZ;
      this.ticks = 0.0;
      this.f_19850_ = false;
      this.f_31946_ = pState;
      this.m_6034_(pX + 0.5, pY, pZ + 0.5);
      this.f_19854_ = pX;
      this.f_19855_ = pY;
      this.f_19856_ = pZ;
      this.m_31959_(this.m_20183_());
      this.f_31943_ = false;
      this.f_31947_ = true;
   }

   public VisualFallingBlockEntity(Level pLevel, double pX, double pY, double pZ, BlockState pState, int maxAge) {
      this(pLevel, pX, pY, pZ, pState);
      this.maxAge = maxAge;
   }

   public VisualFallingBlockEntity(Level pLevel, double pX, double pY, double pZ, BlockState pState, int maxAge, boolean particlesOnImpact) {
      this(pLevel, pX, pY, pZ, pState, maxAge);
      this.particlesOnImpact = particlesOnImpact;
   }

   public void m_8119_() {
      boolean onGround = this.m_20096_();
      if (!this.f_31946_.m_60795_() && !onGround && this.f_19797_ <= this.maxAge) {
         this.m_6478_(MoverType.SELF, this.m_20184_());
         if (!this.m_20068_() && !this.m_20096_()) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.08, 0.0));
         }
      } else {
         if (onGround) {
            this.m_149650_(this.f_19853_.m_8055_(this.m_20183_().m_7495_()).m_60734_(), this.m_20183_());
         }

         this.m_146870_();
      }
   }

   protected void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.f_31943_ = false;
      this.f_31947_ = true;
   }

   public boolean m_6087_() {
      return false;
   }

   public void m_149650_(Block pBlock, BlockPos pPos) {
      if (!this.f_19853_.f_46443_ && this.particlesOnImpact) {
         MagicManager.spawnParticles(
            this.f_19853_,
            new BlockParticleOption(ParticleTypes.f_123794_, this.f_31946_),
            this.m_20185_(),
            this.m_20186_(),
            this.m_20189_(),
            25,
            0.25,
            0.25,
            0.25,
            0.04,
            false
         );
      }
   }

   public boolean m_142535_(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }
}
