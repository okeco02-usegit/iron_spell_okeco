package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.network.particles.AbsorptionParticlesPacket;
import io.redspace.ironsspellbooks.network.particles.FortifyAreaParticlesPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class FortifySpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fortify");
   public static final float radius = 8.0F;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(180.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.absorption", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 0)}),
         Component.m_237110_("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(8.0, 1)})
      );
   }

   public FortifySpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 60;
      this.baseManaCost = 80;
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
      return Optional.of((SoundEvent)SoundRegistry.CLOUD_OF_REGEN_LOOP.get());
   }

   @Override
   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
      if (playerMagicData != null) {
         TargetedAreaEntity targetedAreaEntity = TargetedAreaEntity.createTargetAreaEntity(level, entity.m_20182_(), 8.0F, 16239960, entity);
         playerMagicData.setAdditionalCastData(new TargetAreaCastData(entity.m_20182_(), targetedAreaEntity));
      }
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      level.m_45976_(LivingEntity.class, new AABB(entity.m_20182_().m_82492_(8.0, 8.0, 8.0), entity.m_20182_().m_82520_(8.0, 8.0, 8.0)))
         .forEach(
            target -> {
               if (Utils.shouldHealEntity(entity, target) && entity.m_20270_(target) <= 8.0F) {
                  target.m_7292_(
                     new MobEffectInstance(
                        (MobEffect)MobEffectRegistry.FORTIFY.get(), 2400, (int)this.getSpellPower(spellLevel, entity) - 1, false, false, true
                     )
                  );
                  PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new AbsorptionParticlesPacket(target.m_20182_()));
               }
            }
         );
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new FortifyAreaParticlesPacket(entity.m_20182_()));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
