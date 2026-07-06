package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.PoiTypeRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CinderousSoulcallerItem extends Item {
   public CinderousSoulcallerItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.m_21120_(hand);
      if (level instanceof ServerLevel serverlevel && player instanceof ServerPlayer serverPlayer) {
         player.m_36335_().m_41524_((Item)ItemRegistry.CINDEROUS_SOULCALLER.get(), 80);
         BlockPos playerBlockPos = player.m_20183_();
         PoiManager poimanager = serverlevel.m_8904_();
         Optional<BlockPos> keystone = poimanager.m_27192_(
            poi -> Objects.equals(poi.m_203543_().get(), PoiTypeRegistry.CINDEROUS_KEYSTONE_POI.getKey()), playerBlockPos, 22, Occupancy.ANY
         );
         if (keystone.isPresent() && playerBlockPos.m_123342_() + 2 >= keystone.get().m_123342_()) {
            BlockPos keystonePos = keystone.get();
            AABB exclusiveRange = AABB.m_165882_(keystonePos.m_252807_(), 80.0, 80.0, 80.0);
            if (level.m_45976_(FireBossEntity.class, exclusiveRange).isEmpty()) {
               if (!player.m_150110_().f_35937_) {
                  Vec3 particlePos = player.m_146892_().m_82549_(player.m_20156_().m_82490_(0.6)).m_82492_(0.0, 0.3, 0.0);
                  MagicManager.spawnParticles(
                     serverlevel,
                     new ItemParticleOption(ParticleTypes.f_123752_, itemStack),
                     particlePos.f_82479_,
                     particlePos.f_82480_,
                     particlePos.f_82481_,
                     9,
                     0.15,
                     0.15,
                     0.15,
                     0.08,
                     false
                  );
                  itemStack.m_41774_(1);
                  player.m_21008_(hand, itemStack);
               }

               Vec3 center = keystonePos.m_252807_().m_82520_(0.0, 0.6, 0.0);
               float yRot = Utils.getAngle(center.f_82479_, center.f_82481_, player.m_20185_(), player.m_20189_()) * (180.0F / (float)Math.PI);
               FireBossEntity fireBoss = (FireBossEntity)((EntityType)EntityRegistry.FIRE_BOSS.get()).m_20615_(serverlevel);
               fireBoss.m_20219_(center);
               fireBoss.m_146922_(yRot + 90.0F);
               fireBoss.triggerSpawnAnim();
               fireBoss.m_6518_(serverlevel, level.m_6436_(player.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
               level.m_7967_(fireBoss);
               this.tollEffects(serverlevel, player.m_20182_(), true);
            } else {
               serverPlayer.f_8906_
                  .m_9829_(
                     new ClientboundSetActionBarTextPacket(
                        Component.m_237115_("item.irons_spellbooks.cinderous_soulcaller.failure.in_progress").m_130940_(ChatFormatting.GOLD)
                     )
                  );
               this.tollEffects(serverlevel, player.m_20182_(), false);
            }
         } else {
            serverPlayer.f_8906_
               .m_9829_(
                  new ClientboundSetActionBarTextPacket(
                     Component.m_237115_("item.irons_spellbooks.cinderous_soulcaller.failure.no_soul").m_130940_(ChatFormatting.GOLD)
                  )
               );
            this.tollEffects(serverlevel, player.m_20182_(), false);
         }
      }

      player.m_6674_(hand);
      return InteractionResultHolder.m_19096_(itemStack);
   }

   public void tollEffects(ServerLevel serverLevel, Vec3 usePosition, boolean success) {
      serverLevel.m_6263_(
         null,
         usePosition.f_82479_,
         usePosition.f_82480_,
         usePosition.f_82481_,
         success ? (SoundEvent)SoundRegistry.SOULCALLER_TOLL_SUCCESS.get() : (SoundEvent)SoundRegistry.SOULCALLER_TOLL_FAILURE.get(),
         SoundSource.PLAYERS,
         6.0F,
         1.0F
      );
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(1.0F, 0.6F, 0.3F, 16.0F),
         usePosition.f_82479_,
         usePosition.f_82480_,
         usePosition.f_82481_,
         0,
         0.0,
         0.0,
         0.0,
         0.0,
         false
      );
   }
}
