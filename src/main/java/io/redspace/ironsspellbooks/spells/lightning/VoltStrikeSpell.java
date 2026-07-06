package io.redspace.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VoltStrikeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "volt_strike");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(10.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.damage", new Object[]{this.getDamage(spellLevel, caster)}));
   }

   public VoltStrikeSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 1;
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
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.f_19812_ = true;
      float multiplier = (15.0F + this.getSpellPower(spellLevel, entity)) / 20.0F;
      Vec3 forward = entity.m_20154_();
      double upwardness = forward.m_82526_(new Vec3(0.0, 1.0, 0.0));
      double remap = 1.0 - Math.max(0.0, upwardness) * 0.6F;
      Vec3 impulse = forward.m_82490_(3.0F * multiplier).m_82542_(1.0, remap, 1.0);
      if (entity.m_20096_()) {
         if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9774_(serverPlayer.m_20185_(), serverPlayer.m_20186_() + 1.0, serverPlayer.m_20189_(), serverPlayer.m_146908_(), serverPlayer.m_146909_());
         } else {
            entity.m_6478_(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
         }

         impulse.m_82520_(0.0, 0.5, 0.0);
      } else {
         impulse.m_82520_(0.0, 0.25, 0.0);
      }

      entity.m_20256_(
         new Vec3(
            Mth.m_14139_(0.75, entity.m_20184_().f_82479_, impulse.f_82479_),
            Mth.m_14139_(0.75, entity.m_20184_().f_82480_, impulse.f_82480_),
            Mth.m_14139_(0.75, entity.m_20184_().f_82481_, impulse.f_82481_)
         )
      );
      entity.f_19864_ = true;
      entity.m_7292_(new MobEffectInstance((MobEffect)MobEffectRegistry.VOLT_STRIKE.get(), 10, this.getDamage(spellLevel, entity), false, false, false));
      entity.f_19802_ = 20;
      playerMagicData.getSyncedData().setSpinAttackType(SpinAttackType.LIGHTNING);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private int getDamage(int spellLevel, LivingEntity caster) {
      return (int)(5.0F + this.getSpellPower(spellLevel, caster));
   }
}
