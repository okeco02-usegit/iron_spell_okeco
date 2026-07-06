package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.ExtendedEvokerFang;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;

public class FangStrikeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fang_strike");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(5.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.fang_count", new Object[]{this.getCount(spellLevel, caster)}),
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)})
      );
   }

   public FangStrikeSpell() {
      this.manaCostPerLevel = 3;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 15;
      this.baseManaCost = 30;
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
      return Optional.of(SoundEvents.f_11867_);
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 forward = entity.m_20156_().m_82542_(1.0, 0.0, 1.0).m_82541_();
      Vec3 start = entity.m_146892_().m_82549_(forward.m_82490_(1.5));

      for (int i = 0; i < this.getCount(spellLevel, entity); i++) {
         Vec3 spawn = start.m_82549_(forward.m_82490_(i));
         spawn = new Vec3(spawn.f_82479_, this.getGroundLevel(world, spawn, 8), spawn.f_82481_);
         if (!world.m_8055_(BlockPos.m_274446_(spawn).m_7495_()).m_60795_()) {
            int delay = i / 3;
            ExtendedEvokerFang fang = new ExtendedEvokerFang(
               world,
               spawn.f_82479_,
               spawn.f_82480_,
               spawn.f_82481_,
               (entity.m_146908_() - 90.0F) * (float) (Math.PI / 180.0),
               delay,
               entity,
               this.getDamage(spellLevel, entity)
            );
            world.m_7967_(fang);
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private int getGroundLevel(Level level, Vec3 start, int maxSteps) {
      if (!level.m_8055_(BlockPos.m_274446_(start)).m_60795_()) {
         for (int i = 0; i < maxSteps; i++) {
            start = start.m_82520_(0.0, 1.0, 0.0);
            if (level.m_8055_(BlockPos.m_274446_(start)).m_60795_()) {
               break;
            }
         }
      }

      Vec3 lower = level.m_45547_(new ClipContext(start, start.m_82520_(0.0, maxSteps * -2, 0.0), Block.COLLIDER, Fluid.NONE, null)).m_82450_();
      return (int)lower.f_82480_;
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      float f = this.getCount(spellLevel, mob) * 1.2F;
      return mob.m_20280_(target) > f * f;
   }

   private int getCount(int spellLevel, LivingEntity entity) {
      return 7 + spellLevel;
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }
}
