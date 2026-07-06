package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.EnderSlashParticleOptions;
import io.redspace.ironsspellbooks.particle.TraceParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@AutoSpellConfig
public class ShadowSlashSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "shadow_slash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public ShadowSlashSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 30;
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   @Override
   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      super.onClientCast(level, spellLevel, entity, castData);
      entity.m_5618_(entity.m_146908_());
   }

   @Override
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.SHADOW_SLASH.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float distance = 12.0F;
      Vec3 forward = entity.m_20156_();
      Vec3 end = Utils.raycastForBlock(level, entity.m_146892_(), entity.m_146892_().m_82549_(forward.m_82490_(distance)), Fluid.NONE).m_82450_();
      AABB hitbox = entity.m_20191_().m_82369_(end.m_82546_(entity.m_146892_())).m_82400_(2.0);
      List<Entity> targetableEntities = level.m_6249_(
         entity,
         hitbox,
         e -> !e.m_5833_()
            && (e instanceof LivingEntity || e instanceof Projectile)
            && e.m_20191_().m_82399_().m_82546_(entity.m_20191_().m_82399_()).m_82541_().m_82526_(entity.m_20156_()) >= 0.85
      );
      targetableEntities.sort(Comparator.comparingDouble(e -> e.m_20280_(entity)));
      if (!targetableEntities.isEmpty() && targetableEntities.get(0).m_20280_(entity) < distance * distance) {
         Entity closestEntity = targetableEntities.get(0);
         float radius = 2.5F;
         AABB damageBox = AABB.m_165882_(closestEntity.m_20191_().m_82399_(), radius, radius + 1.0F, radius).m_82383_(forward.m_82490_(radius / 2.0F));
         end = damageBox.m_82399_().m_82549_(end).m_82490_(0.5);
         List<Entity> damageEntities = level.m_45933_(entity, damageBox);
         SpellDamageSource damageSource = this.getDamageSource(entity);
         boolean projectileEffects = false;

         for (Entity targetEntity : damageEntities) {
            if (targetEntity instanceof Projectile projectile && !projectile.f_19794_ && !projectile.m_6095_().m_204039_(ModTags.CANT_PARRY)) {
               projectileEffects = true;
               projectile.m_5602_(entity);
               projectile.m_6686_(forward.f_82479_, forward.f_82480_, forward.f_82481_, (float)projectile.m_20184_().m_82553_(), 0.0F);
            } else if (targetEntity.m_6084_()
               && entity.m_6087_()
               && Utils.hasLineOfSight(level, entity.m_146892_(), targetEntity.m_20191_().m_82399_(), true)
               && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), damageSource)) {
               MagicManager.spawnParticles(
                  level,
                  ParticleHelper.ENDER_SPARKS,
                  targetEntity.m_20185_(),
                  targetEntity.m_20186_() + targetEntity.m_20206_() * 0.5F,
                  targetEntity.m_20189_(),
                  15,
                  targetEntity.m_20205_() * 0.5F,
                  targetEntity.m_20206_() * 0.5F,
                  targetEntity.m_20205_() * 0.5F,
                  0.25,
                  false
               );
               EnchantmentHelper.m_44896_(entity, targetEntity);
               Vec3 knockback = targetEntity.m_20182_().m_82546_(entity.m_20182_()).m_82541_().m_82520_(0.0, 0.5, 0.0).m_82541_();
               knockback.m_82490_(Utils.random.m_216332_(70, 100) / 100.0F * Utils.clampedKnockbackResistanceFactor(targetEntity, 0.2F, 1.0F) * 0.1F);
               targetEntity.m_20256_(targetEntity.m_20184_().m_82549_(knockback));
               targetEntity.f_19864_ = true;
            }
         }

         if (projectileEffects) {
            level.m_6263_(
               null,
               closestEntity.m_20185_(),
               closestEntity.m_20186_(),
               closestEntity.m_20189_(),
               (SoundEvent)SoundRegistry.FIRE_DAGGER_PARRY.get(),
               entity.m_5720_(),
               1.0F,
               1.0F
            );
            MagicManager.spawnParticles(
               level,
               ParticleHelper.ENDER_SPARKS,
               closestEntity.m_20185_(),
               closestEntity.m_20186_() + closestEntity.m_20206_() * 0.5F,
               closestEntity.m_20189_(),
               25,
               0.0,
               0.0,
               0.0,
               0.4,
               false
            );
         }
      }

      Vec3 rayVector = end.m_82546_(entity.m_146892_());
      Vec3 impulse = rayVector.m_82490_(0.16666667F).m_82520_(0.0, 0.1, 0.0);
      entity.m_20256_(entity.m_20184_().m_82490_(0.2).m_82549_(impulse));
      entity.f_19864_ = true;
      entity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.FALL_DAMAGE_IMMUNITY.get(), 20, 0, false, false, true));
      forward = impulse.m_82541_();
      Vec3 up = new Vec3(0.0, 1.0, 0.0);
      if (forward.m_82526_(up) > 0.999) {
         up = new Vec3(1.0, 0.0, 0.0);
      }

      Vec3 right = up.m_82537_(forward);
      Vec3 particlePos = end.m_82546_(forward.m_82490_(3.0)).m_82549_(right.m_82490_(-0.3));
      MagicManager.spawnParticles(
         level,
         new EnderSlashParticleOptions(
            (float)forward.f_82479_,
            (float)forward.f_82480_,
            (float)forward.f_82481_,
            (float)right.f_82479_,
            (float)right.f_82480_,
            (float)right.f_82481_,
            1.0F
         ),
         particlePos.f_82479_,
         particlePos.f_82480_ + 0.3,
         particlePos.f_82481_,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      int trailParticles = 15;
      double speed = rayVector.m_82553_() / 12.0 * 0.75;

      for (int i = 0; i < trailParticles; i++) {
         Vec3 particleStart = entity.m_20191_().m_82399_().m_82549_(Utils.getRandomVec3(1.0F + entity.m_20205_()));
         Vec3 particleEnd = particleStart.m_82549_(rayVector);
         MagicManager.spawnParticles(
            level,
            new TraceParticleOptions(Utils.v3f(particleEnd), new Vector3f(1.0F, 0.333F, 1.0F)),
            particleStart.f_82479_,
            particleStart.f_82480_,
            particleStart.f_82481_,
            1,
            0.0,
            0.0,
            0.0,
            speed,
            false
         );
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity);
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = Utils.getWeaponDamage(entity);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, entity), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, entity) + "";
      }
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_VERTICAL_UPSWING_ANIMATION;
   }
}
