package io.redspace.ironsspellbooks.damage;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortalDamageSource extends DamageSource {
   public PortalDamageSource(Holder<DamageType> pType, @Nullable Entity pEntity) {
      super(pType, pEntity);
   }

   @NotNull
   public Component m_6157_(@NotNull LivingEntity pLivingEntity) {
      return Component.m_237110_("death.attack.unstable_portal_owner", new Object[]{pLivingEntity.m_5446_()});
   }
}
