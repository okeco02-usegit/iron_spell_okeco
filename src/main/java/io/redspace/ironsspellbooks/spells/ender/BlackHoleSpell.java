package io.redspace.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlackHoleSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "black_hole");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(120.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)})
      );
   }

   public BlackHoleSpell() {
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 0;
      this.castTime = 100;
      this.baseManaCost = 300;
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
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
      return Optional.of((SoundEvent)SoundRegistry.BLACK_HOLE_CHARGE.get());
   }

   @Override
   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.BLACK_HOLE_CAST.get());
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      HitResult raycast = Utils.raycastForEntity(level, entity, 16.0F + radius * 1.5F, true);
      Vec3 center = raycast.m_82450_();
      if (raycast instanceof BlockHitResult blockHitResult) {
         if (blockHitResult.m_82434_().m_122434_().m_122479_()) {
            center = center.m_82492_(0.0, radius, 0.0);
         } else if (blockHitResult.m_82434_() == Direction.DOWN) {
            center = center.m_82492_(0.0, radius * 2.0F - 1.0F, 0.0);
         } else {
            center = center.m_82492_(0.0, 1.0, 0.0);
         }
      }

      level.m_6263_(null, center.f_82479_, center.f_82480_, center.f_82481_, (SoundEvent)SoundRegistry.BLACK_HOLE_CAST.get(), SoundSource.AMBIENT, 4.0F, 1.0F);
      BlackHole blackHole = new BlackHole(level, entity);
      blackHole.setRadius(radius);
      blackHole.setDamage(this.getDamage(spellLevel, entity));
      blackHole.m_20219_(center);
      level.m_7967_(blackHole);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 2.0F;
   }

   private float getRadius(int spellLevel, LivingEntity entity) {
      return 2 * spellLevel + 4 + 0.125F * this.getSpellPower(spellLevel, entity);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   @Override
   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.FINISH_ANIMATION;
   }

   @Override
   public boolean stopSoundOnCancel() {
      return true;
   }
}
