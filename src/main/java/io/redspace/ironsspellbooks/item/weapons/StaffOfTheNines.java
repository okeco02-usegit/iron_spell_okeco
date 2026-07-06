package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class StaffOfTheNines extends Item {
   public StaffOfTheNines(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand pUsedHand) {
      if (!level.f_46443_) {
         Vec3 pos = this.getPosition(player.m_146892_(), 0.425F, 0.275F, 0.1775F, player.m_20155_());
         MagicManager.spawnParticles(level, ParticleTypes.f_123777_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 5, 0.1, 0.1, 0.1, 0.01, false);
         level.m_5594_(null, player.m_20183_(), SoundEvents.f_11928_, SoundSource.PLAYERS, 4.0F, 1.5F);
         level.m_5594_(null, player.m_20183_(), SoundEvents.f_11929_, SoundSource.PLAYERS, 6.0F, 1.5F);
         HitResult hit = Utils.raycastForEntity(level, player, 64.0F, true, 0.1F);
         if (hit instanceof BlockHitResult blockHitResult) {
            Vec3 loc = blockHitResult.m_82450_();
            MagicManager.spawnParticles(
               level,
               new BlockParticleOption(ParticleTypes.f_123794_, level.m_8055_(blockHitResult.m_82425_())),
               loc.f_82479_,
               loc.f_82480_,
               loc.f_82481_,
               25,
               0.1,
               0.1,
               0.1,
               0.25,
               true
            );
         } else if (hit instanceof EntityHitResult entityHitResult) {
            entityHitResult.m_82443_().m_6469_(level.m_269111_().m_269425_(), (float)(10.0 * player.m_21133_((Attribute)AttributeRegistry.SPELL_POWER.get())));
            Vec3 loc = entityHitResult.m_82450_();
            MagicManager.spawnParticles(level, ParticleHelper.BLOOD, loc.f_82479_, loc.f_82480_, loc.f_82481_, 25, 0.1, 0.1, 0.1, 0.25, true);
         }

         CameraShakeManager.addCameraShake(new CameraShakeData(level, 10, player.m_20182_(), 5.0F));
         ((ServerPlayer)player)
            .m_8999_(
               (ServerLevel)level,
               player.m_20185_(),
               player.m_20186_(),
               player.m_20189_(),
               player.m_146908_(),
               player.m_146909_() - Utils.random.m_216332_(6, 9)
            );
      }

      return super.m_7203_(level, player, pUsedHand);
   }

   public Vec3 getPosition(Vec3 vec3, float forwards, float up, float left, Vec2 vec2) {
      float f = Mth.m_14089_((vec2.f_82471_ + 90.0F) * (float) (Math.PI / 180.0));
      float f1 = Mth.m_14031_((vec2.f_82471_ + 90.0F) * (float) (Math.PI / 180.0));
      float f2 = Mth.m_14089_(-vec2.f_82470_ * (float) (Math.PI / 180.0));
      float f3 = Mth.m_14031_(-vec2.f_82470_ * (float) (Math.PI / 180.0));
      float f4 = Mth.m_14089_((-vec2.f_82470_ + 90.0F) * (float) (Math.PI / 180.0));
      float f5 = Mth.m_14031_((-vec2.f_82470_ + 90.0F) * (float) (Math.PI / 180.0));
      Vec3 vec31 = new Vec3(f * f2, f3, f1 * f2);
      Vec3 vec32 = new Vec3(f * f4, f5, f1 * f4);
      Vec3 vec33 = vec31.m_82537_(vec32).m_82490_(-1.0);
      double d0 = vec31.f_82479_ * forwards + vec32.f_82479_ * up + vec33.f_82479_ * left;
      double d1 = vec31.f_82480_ * forwards + vec32.f_82480_ * up + vec33.f_82480_ * left;
      double d2 = vec31.f_82481_ * forwards + vec32.f_82481_ * up + vec33.f_82481_ * left;
      return new Vec3(vec3.f_82479_ + d0, vec3.f_82480_ + d1, vec3.f_82481_ + d2);
   }
}
