package io.redspace.ironsspellbooks.entity.spells.electrocute;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ElectrocuteProjectile extends AbstractConeProjectile {
   private List<Vec3> beamVectors;

   public ElectrocuteProjectile(EntityType<? extends AbstractConeProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public ElectrocuteProjectile(Level level, LivingEntity entity) {
      super((EntityType<? extends AbstractConeProjectile>)EntityRegistry.ELECTROCUTE_PROJECTILE.get(), level, entity);
   }

   public boolean m_6783_(double pDistance) {
      return super.m_6783_(pDistance);
   }

   public boolean m_6000_(double pX, double pY, double pZ) {
      return super.m_6000_(pX, pY, pZ);
   }

   public void generateLightningBeams() {
      Random random = new Random();
      this.beamVectors = new ArrayList<>();
      Vec3 coreStart = new Vec3(0.0, 0.0, 0.0);
      int coreLength = random.nextInt(3) + 7;

      for (int core = 0; core < coreLength; core++) {
         float width = Mth.m_14179_((float)core / coreLength, 2.0F, 4.0F);
         Vec3 coreEnd = coreStart.m_82520_(0.0, 0.0, 1.0).m_82549_(randomVector(0.3F).m_82542_(width, 1.0, width));
         this.beamVectors.add(coreStart);
         this.beamVectors.add(coreEnd);
         coreStart = coreEnd;
         int branchSegments = random.nextInt(3) + 1;
         this.beamVectors.addAll(generateBranch(coreEnd, branchSegments, 0.5F, 1));
      }
   }

   public static List<Vec3> generateBranch(Vec3 origin, int maxLength, float splitChance, int recursionCount) {
      List<Vec3> branchSegements = new ArrayList<>();
      Random random = new Random();
      int branches = random.nextInt(maxLength + 1);
      Vec3 branchStart = origin;
      int dir = random.nextBoolean() ? 1 : -1;
      float branchLength = 1.75F / (recursionCount + 1);

      for (int i = 0; i < branches; i++) {
         Vec3 branchEnd = branchStart.m_82520_(dir * branchLength, 0.0, branchLength).m_82549_(randomVector(0.4F));
         branchSegements.add(branchStart);
         branchSegements.add(branchEnd);
         if (random.nextFloat() <= splitChance) {
            branchSegements.addAll(generateBranch(branchEnd, maxLength - 1, splitChance * 1.2F, recursionCount + 1));
         }

         branchStart = branchEnd;
      }

      return branchSegements;
   }

   public int getAge() {
      return this.age;
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.f_19853_.f_46443_) {
         this.generateLightningBeams();
      }
   }

   public static Vec3 randomVector(float radius) {
      double x = Math.random() * 2.0 * radius - radius;
      double y = Math.random() * 2.0 * radius - radius;
      double z = Math.random() * 2.0 * radius - radius;
      return new Vec3(x, y, z);
   }

   public List<Vec3> getBeamCache() {
      if (this.beamVectors == null) {
         this.generateLightningBeams();
      }

      return this.beamVectors;
   }

   @Override
   public void spawnParticles() {
   }

   @Override
   protected void m_5790_(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.m_82443_();
      DamageSources.applyDamage(entity, this.damage, ((AbstractSpell)SpellRegistry.ELECTROCUTE_SPELL.get()).getDamageSource(this, this.m_19749_()));
      MagicManager.spawnParticles(
         this.m_9236_(),
         ParticleHelper.ELECTRICITY,
         entity.m_20185_(),
         entity.m_20186_() + entity.m_20206_() / 2.0F,
         entity.m_20189_(),
         10,
         entity.m_20205_() / 3.0F,
         entity.m_20206_() / 3.0F,
         entity.m_20205_() / 3.0F,
         0.1,
         false
      );
   }
}
