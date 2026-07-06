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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class FangWardSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fang_ward");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(15.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.ring_count", new Object[]{this.getRings(spellLevel, caster)}),
         Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)})
      );
   }

   public FangWardSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 15;
      this.baseManaCost = 45;
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
      int rings = this.getRings(spellLevel, entity);
      int count = 5;
      Vec3 center = entity.m_146892_();

      for (int r = 0; r < rings; r++) {
         float fangs = count + r * r;

         for (int i = 0; i < fangs; i++) {
            Vec3 spawn = center.m_82549_(new Vec3(0.0, 0.0, 1.5 * (r + 1)).m_82524_(entity.m_146908_() * (float) (Math.PI / 180.0) + 6.281F / fangs * i));
            spawn = Utils.moveToRelativeGroundLevel(world, spawn, 5);
            if (!world.m_8055_(BlockPos.m_274446_(spawn).m_7495_()).m_60795_()) {
               ExtendedEvokerFang fang = new ExtendedEvokerFang(
                  world, spawn.f_82479_, spawn.f_82480_, spawn.f_82481_, this.get2DAngle(center, spawn), r, entity, this.getDamage(spellLevel, entity)
               );
               world.m_7967_(fang);
            }
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float get2DAngle(Vec3 a, Vec3 b) {
      return Utils.getAngle(new Vec2((float)a.f_82479_, (float)a.f_82481_), new Vec2((float)b.f_82479_, (float)b.f_82481_));
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }

   private int getRings(int spellLevel, LivingEntity entity) {
      return 2 + (spellLevel - 1) / 3;
   }

   @Override
   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      float d = 1.5F * (this.getRings(spellLevel, mob) + 1);
      return mob.m_20280_(target) > d * d * 1.2F;
   }
}
