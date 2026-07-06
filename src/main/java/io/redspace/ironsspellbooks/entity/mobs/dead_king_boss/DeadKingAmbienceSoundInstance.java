package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DeadKingAmbienceSoundInstance extends AbstractTickableSoundInstance {
   public static final int SOUND_RANGE_SQR = 400;
   public static final int MAX_VOLUME_RANGE_SQR = 144;
   private static final float END_TRANSITION_TIME = 0.01F;
   final Vec3 vec3;
   boolean ending = false;
   boolean triggerEnd = false;

   protected DeadKingAmbienceSoundInstance(Vec3 vec3) {
      super((SoundEvent)SoundRegistry.DEAD_KING_AMBIENCE.get(), SoundSource.AMBIENT, SoundInstance.m_235150_());
      this.f_119580_ = Attenuation.NONE;
      this.f_119578_ = true;
      this.f_119579_ = 0;
      this.f_119573_ = 0.0F;
      this.vec3 = vec3;
   }

   public void m_7788_() {
      if (this.triggerEnd) {
         if (!this.ending) {
            this.ending = true;
         }

         this.f_119573_ -= 0.01F;
      } else {
         MinecraftInstanceHelper.ifPlayerPresent(player -> {
            double d = player.m_20238_(this.vec3);
            this.f_119573_ = 1.0F - (float)Mth.m_14008_((d - 144.0) / 400.0, 0.0, 1.0);
         });
      }

      if (this.f_119573_ <= 0.0F) {
         this.m_119609_();
      }
   }

   public boolean m_7784_() {
      return true;
   }

   public void triggerStop() {
      this.triggerEnd = true;
   }
}
