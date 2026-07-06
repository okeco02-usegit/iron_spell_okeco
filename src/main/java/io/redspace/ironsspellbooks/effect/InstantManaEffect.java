package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.Nullable;

public class InstantManaEffect extends CustomDescriptionMobEffect {
   public static final int manaPerAmplifier = 25;
   public static final float manaPerAmplifierPercent = 0.05F;

   public InstantManaEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   @Override
   public Component getDescriptionLine(MobEffectInstance instance) {
      int amp = instance.m_19564_() + 1;
      int addition = amp * 25;
      int percent = (int)(amp * 0.05F * 100.0F);
      return Component.m_237110_("tooltip.irons_spellbooks.instant_mana_description", new Object[]{addition, percent}).m_130940_(ChatFormatting.BLUE);
   }

   public boolean m_8093_() {
      return true;
   }

   public void m_19461_(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity livingEntity, int pAmplifier, double pHealth) {
      int i = pAmplifier + 1;
      int maxMana = (int)livingEntity.m_21133_((Attribute)AttributeRegistry.MAX_MANA.get());
      int manaAdd = (int)(i * 25 + maxMana * (i * 0.05F));
      MagicData pmg = MagicData.getPlayerMagicData(livingEntity);
      pmg.setMana(pmg.getMana() + manaAdd);
      if (livingEntity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(pmg));
      }
   }

   public boolean m_6584_(int pDuration, int pAmplifier) {
      return true;
   }

   public void m_6742_(LivingEntity livingEntity, int pAmplifier) {
      this.m_19461_(null, null, livingEntity, pAmplifier, livingEntity.m_21223_());
   }
}
