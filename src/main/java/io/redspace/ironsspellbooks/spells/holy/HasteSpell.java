package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetedTargetAreaCastData;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class HasteSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "haste");
   private static final int MAX_TARGETS = 5;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(80.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_(
            "ui.irons_spellbooks.hastened", new Object[]{Utils.stringTruncation((1 + this.getAmplifier(spellLevel, caster)) * 0.1F * 100.0F, 1)}
         ),
         Component.m_237110_("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}),
         Component.m_237110_("ui.irons_spellbooks.max_victims", new Object[]{5})
      );
   }

   public HasteSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 5;
      this.castTime = 30;
      this.baseManaCost = 50;
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
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (!Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F, false)) {
         playerMagicData.setAdditionalCastData(new TargetEntityCastData(entity));
         if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9829_(
                  new ClientboundSetActionBarTextPacket(
                     Component.m_237110_("ui.irons_spellbooks.spell_target_success_self", new Object[]{this.getDisplayName(serverPlayer)})
                        .m_130940_(ChatFormatting.GREEN)
                  )
               );
         }
      }

      float radius = 3.0F;
      LivingEntity target = ((TargetEntityCastData)playerMagicData.getAdditionalCastData()).getTarget((ServerLevel)level);
      TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, target.m_20182_(), radius, Utils.packRGB(this.getTargetingColor()), target);
      playerMagicData.setAdditionalCastData(new TargetedTargetAreaCastData(target, area));
      return true;
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetedTargetAreaCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)world);
         if (targetEntity != null) {
            float radius = 3.0F;
            AtomicInteger targets = new AtomicInteger(0);
            targetEntity.f_19853_
               .m_45976_(LivingEntity.class, targetEntity.m_20191_().m_82400_(radius))
               .forEach(
                  victim -> {
                     if (targets.get() < 5 && victim.m_20280_(targetEntity) < radius * radius && Utils.shouldHealEntity(entity, victim)) {
                        victim.m_7292_(
                           new MobEffectInstance(
                              (MobEffect)MobEffectRegistry.HASTENED.get(), this.getDuration(spellLevel, entity), this.getAmplifier(spellLevel, entity)
                           )
                        );
                        targets.incrementAndGet();
                     }
                  }
               );
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public int getAmplifier(int spellLevel, LivingEntity caster) {
      return spellLevel - 1;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }
}
