package io.redspace.ironsspellbooks.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.attribute.IMagicAttribute;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.effect.IMobEffectEndCallback;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.item.armor.IArmorCapeProvider;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements MagicData.IExtendedEntity {
   @Unique
   MagicData irons_spellbooks$magicData = null;
   @Unique
   IArmorCapeProvider.CapeData irons_spellbooks$capeData = null;
   @Unique
   private static final List<EquipmentSlot> handSlots = List.of(EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND);
   @Unique
   private static final Predicate<Attribute> allNonBaseAttackAttributes = attribute -> attribute != ForgeMod.ENTITY_REACH.get()
      && attribute != Attributes.f_22281_
      && attribute != Attributes.f_22283_
      && attribute != Attributes.f_22282_;
   @Unique
   private static final Predicate<Attribute> onlyIronAttributes = attribute -> attribute instanceof IMagicAttribute;

   @Override
   public IArmorCapeProvider.CapeData irons_spellbooks$getCapData() {
      if (this.irons_spellbooks$capeData == null) {
         this.irons_spellbooks$capeData = new IArmorCapeProvider.CapeData();
      }

      return this.irons_spellbooks$capeData;
   }

   @Override
   public MagicData irons_spellbooks$getMagicData() {
      if (this.irons_spellbooks$magicData == null) {
         if (this instanceof Player player) {
            if (player instanceof ServerPlayer serverplayer) {
               this.irons_spellbooks$magicData = new MagicData(serverplayer);
            } else {
               this.irons_spellbooks$magicData = new MagicData();
            }
         } else {
            this.irons_spellbooks$magicData = new MagicData(true);
         }
      }

      assert this.irons_spellbooks$magicData != null;
      return this.irons_spellbooks$magicData;
   }

   @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
   public void irons_spellbooks$saveDataAttachment(CompoundTag pCompound, CallbackInfo ci) {
      if (this.irons_spellbooks$magicData != null) {
         CompoundTag tag = new CompoundTag();
         this.irons_spellbooks$magicData.saveNBTData(tag, ((Entity)this).f_19853_.m_9598_());
         pCompound.m_128365_("irons_spellbooks:magic_data", tag);
      }
   }

   @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
   public void irons_spellbooks$readDataAttachment(CompoundTag pCompound, CallbackInfo ci) {
      if (pCompound.m_128441_("irons_spellbooks:magic_data")) {
         this.irons_spellbooks$getMagicData();
         CompoundTag tag = pCompound.m_128469_("irons_spellbooks:magic_data");
         this.irons_spellbooks$magicData.loadNBTData(tag, ((Entity)this).f_19853_.m_9598_());
      }
   }

   @Inject(method = "onEffectRemoved", at = @At("HEAD"))
   public void irons_spellbooks$onEffectRemoved(MobEffectInstance effectInstance, CallbackInfo ci) {
      LivingEntity self = (LivingEntity)this;
      if (!self.f_19853_.f_46443_) {
         if (effectInstance.m_19544_() instanceof IMobEffectEndCallback mobEffect) {
            mobEffect.onEffectRemoved(self, effectInstance.m_19564_());
         }

         if (effectInstance.m_19544_() instanceof ISyncedMobEffect && self.f_19853_.m_7726_() instanceof ServerChunkCache serverChunk) {
            serverChunk.m_8445_(self, new ClientboundRemoveMobEffectPacket(self.m_19879_(), effectInstance.m_19544_()));
         }
      }
   }

   @Inject(method = "onEffectUpdated", at = @At("HEAD"))
   public void irons_spellbooks$onEffectUpdated(MobEffectInstance effectInstance, boolean forced, Entity entity, CallbackInfo ci) {
      LivingEntity self = (LivingEntity)this;
      if (!self.f_19853_.f_46443_ && effectInstance.m_19544_() instanceof ISyncedMobEffect && self.f_19853_.m_7726_() instanceof ServerChunkCache serverChunk) {
         serverChunk.m_8445_(self, new ClientboundUpdateMobEffectPacket(self.m_19879_(), effectInstance));
      }
   }

   @Inject(method = "onEffectAdded", at = @At("HEAD"))
   public void irons_spellbooks$onEffectAdded(MobEffectInstance effectInstance, Entity entity, CallbackInfo ci) {
      LivingEntity self = (LivingEntity)this;
      if (!self.f_19853_.f_46443_ && effectInstance.m_19544_() instanceof ISyncedMobEffect && self.f_19853_.m_7726_() instanceof ServerChunkCache serverChunk) {
         serverChunk.m_8445_(self, new ClientboundUpdateMobEffectPacket(self.m_19879_(), effectInstance));
      }
   }

   @Inject(method = "updateInvisibilityStatus", at = @At("TAIL"))
   public void irons_spellbooks$updateInvisibilityStatus(CallbackInfo ci) {
      LivingEntity self = (LivingEntity)this;
      if (self.m_21023_((MobEffect)MobEffectRegistry.TRUE_INVISIBILITY.get())) {
         self.m_6842_(true);
      }
   }

   @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
   public void irons_spellbooks$isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
      LivingEntity self = (LivingEntity)this;
      if (!self.f_19853_.m_5776_() && self.m_21023_((MobEffect)MobEffectRegistry.GUIDING_BOLT.get())) {
         cir.setReturnValue(true);
      }
   }

   @Inject(method = "hurt", at = @At("RETURN"))
   public void irons_spellbooks$changeSummonHurtCredit(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
      if ((damageSource.m_7640_() instanceof IMagicSummon summon ? summon : (damageSource.m_7639_() instanceof IMagicSummon summon ? summon : null)) instanceof LivingEntity livingSummon
         )
       {
         ((LivingEntity)this).m_6703_(livingSummon);
      }
   }

   @Shadow
   abstract ItemStack m_21244_(EquipmentSlot var1);

   @Unique
   private static Multimap<Attribute, AttributeModifier> filterApplicableAttributes(Multimap<Attribute, AttributeModifier> attributeModifierMap) {
      Multimap<Attribute, AttributeModifier> map = HashMultimap.create();

      for (Attribute attribute : attributeModifierMap.keySet()) {
         Predicate<Attribute> predicate = ServerConfigs.APPLY_ALL_MULTIHAND_ATTRIBUTES.get() ? allNonBaseAttackAttributes : onlyIronAttributes;
         if (predicate.test(attribute)) {
            map.putAll(attribute, attributeModifierMap.get(attribute));
         }
      }

      return map;
   }
}
