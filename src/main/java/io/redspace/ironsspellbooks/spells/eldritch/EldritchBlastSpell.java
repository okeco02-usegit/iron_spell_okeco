package io.redspace.ironsspellbooks.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastVisualEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class EldritchBlastSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "eldritch_blast");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.blast_count", new Object[]{this.getRecastCount(spellLevel, caster)}),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(getRange(spellLevel, caster), 1)})
      );
   }

   public EldritchBlastSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 0;
      this.castTime = 0;
      this.baseManaCost = 90;
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
   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.ELDRITCH_BLAST.get());
   }

   @Override
   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2 + spellLevel;
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 80, castSource, null), playerMagicData);
      }

      HitResult hitResult = Utils.raycastForEntity(level, entity, getRange(spellLevel, entity), true, 0.15F);
      level.m_7967_(new EldritchBlastVisualEntity(level, entity.m_146892_().m_82492_(0.0, 0.75, 0.0), hitResult.m_82450_(), entity));
      if (hitResult.m_6662_() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).m_82443_();
         if (target.m_271807_()) {
            DamageSources.applyDamage(target, this.getDamage(spellLevel, entity), this.getDamageSource(entity));
         }
      }

      MagicManager.spawnParticles(
         level,
         ParticleHelper.UNSTABLE_ENDER,
         hitResult.m_82450_().f_82479_,
         hitResult.m_82450_().f_82480_,
         hitResult.m_82450_().f_82481_,
         50,
         0.0,
         0.0,
         0.0,
         0.3,
         false
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static float getRange(int level, LivingEntity caster) {
      return 30.0F;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   @Override
   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }
}
