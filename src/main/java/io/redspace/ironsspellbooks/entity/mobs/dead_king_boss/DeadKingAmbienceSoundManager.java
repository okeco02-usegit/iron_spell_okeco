package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DeadKingAmbienceSoundManager {
   private final Vec3 vec3;
   @OnlyIn(Dist.CLIENT)
   private DeadKingAmbienceSoundInstance soundInstance;

   protected DeadKingAmbienceSoundManager(Vec3 origin) {
      this.vec3 = origin;
   }

   public void trigger() {
      if ((Boolean)ClientConfigs.ENABLE_BOSS_MUSIC.get() && (this.soundInstance == null || this.soundInstance.m_7801_())) {
         this.soundInstance = new DeadKingAmbienceSoundInstance(this.vec3);
         Minecraft.m_91087_().m_91106_().m_120367_(this.soundInstance);
      }
   }

   public void triggerStop() {
      if (this.soundInstance != null) {
         this.soundInstance.triggerStop();
      }
   }
}
