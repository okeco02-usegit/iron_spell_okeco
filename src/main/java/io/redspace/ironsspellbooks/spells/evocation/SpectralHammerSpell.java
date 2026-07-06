package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.spectral_hammer.SpectralHammer;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SpectralHammerSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "spectral_hammer");
   private static final int distance = 16;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(2.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_(
            "ui.irons_spellbooks.dimensions",
            new Object[]{1 + this.getRadius(spellLevel, caster) * 2, 1 + this.getRadius(spellLevel, caster) * 2, this.getDepth(spellLevel, caster) + 1}
         ),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{16})
      );
   }

   public SpectralHammerSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 15;
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
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof ServerPlayer serverPlayer && serverPlayer.f_8941_.m_9290_() == GameType.ADVENTURE) {
         serverPlayer.f_8906_
            .m_9829_(new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_adventure").m_130940_(ChatFormatting.RED)));
         return false;
      } else {
         BlockHitResult blockHitResult = Utils.getTargetBlock(level, entity, Fluid.NONE, 16.0);
         boolean success = blockHitResult.m_6662_() == Type.BLOCK && level.m_8055_(blockHitResult.m_82425_()).m_204336_(ModTags.SPECTRAL_HAMMER_MINEABLE);
         if (!success && entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9829_(
                  new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_spectral_hammer").m_130940_(ChatFormatting.RED))
               );
         }

         return success;
      }
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      BlockHitResult blockPosition = Utils.getTargetBlock(world, entity, Fluid.NONE, 16.0);
      Direction face = blockPosition.m_82434_();
      int radius = this.getRadius(spellLevel, entity);
      int depth = this.getDepth(spellLevel, entity);
      SpectralHammer spectralHammer = new SpectralHammer(world, entity, blockPosition, depth, radius);
      Vec3 position = Vec3.m_82512_(blockPosition.m_82425_());
      if (!face.m_122434_().m_122478_()) {
         position = position.m_82492_(0.0, 2.0, 0.0).m_82546_(entity.m_20156_().m_82541_().m_82490_(1.5));
      } else if (face == Direction.DOWN) {
         position = position.m_82492_(0.0, 3.0, 0.0);
      }

      spectralHammer.m_6034_(position.f_82479_, position.f_82480_, position.f_82481_);
      world.m_7967_(spectralHammer);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private int getDepth(int spellLevel, LivingEntity caster) {
      return (int)this.getSpellPower(spellLevel, caster);
   }

   private int getRadius(int spellLevel, LivingEntity caster) {
      return (int)Math.max(this.getSpellPower(spellLevel, caster) * 0.5F, 1.0F);
   }
}
