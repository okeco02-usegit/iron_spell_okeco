package io.redspace.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.network.particles.HealParticlesPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BlessingOfLifeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "blessing_of_life");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(10.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.m_237110_("ui.irons_spellbooks.healing", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public BlessingOfLifeSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
      this.baseManaCost = 10;
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
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 64, 0.35F);
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData healTargetingData) {
         LivingEntity targetEntity = healTargetingData.getTarget((ServerLevel)world);
         if (targetEntity != null) {
            float healAmount = this.getSpellPower(spellLevel, entity);
            MinecraftForge.EVENT_BUS.post(new SpellHealEvent(entity, targetEntity, healAmount, this.getSchoolType()));
            targetEntity.m_5634_(healAmount);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(targetEntity, new HealParticlesPacket(targetEntity.m_20182_()));
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   @Nullable
   private LivingEntity findTarget(LivingEntity caster) {
      return Utils.raycastForEntity(caster.m_9236_(), caster, 32.0F, true, 0.35F) instanceof EntityHitResult entityHit
            && entityHit.m_82443_() instanceof LivingEntity livingTarget
         ? livingTarget
         : null;
   }

   @Override
   public Vector3f getTargetingColor() {
      return new Vector3f(0.85F, 0.0F, 0.0F);
   }
}
