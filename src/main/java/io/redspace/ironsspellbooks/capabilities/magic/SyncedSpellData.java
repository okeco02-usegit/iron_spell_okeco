package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.LearnedSpellData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.gui.overlays.SpellSelection;
import io.redspace.ironsspellbooks.network.casting.SyncEntityDataPacket;
import io.redspace.ironsspellbooks.network.casting.SyncPlayerDataPacket;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SyncedSpellData {
   private final int serverPlayerId;
   @Nullable
   private LivingEntity livingEntity = null;
   private boolean isCasting;
   private String castingSpellId;
   private int castingSpellLevel;
   private float heartStopAccumulatedDamage;
   private int evasionHitsRemaining;
   private SpinAttackType spinAttackType;
   private LearnedSpellData learnedSpellData;
   private SpellSelection spellSelection;
   private String castingEquipmentSlot;

   public SyncedSpellData(int serverPlayerId) {
      this.serverPlayerId = serverPlayerId;
      this.isCasting = false;
      this.castingSpellId = "";
      this.castingEquipmentSlot = "";
      this.castingSpellLevel = 0;
      this.heartStopAccumulatedDamage = 0.0F;
      this.evasionHitsRemaining = 0;
      this.spinAttackType = SpinAttackType.RIPTIDE;
      this.learnedSpellData = new LearnedSpellData();
      this.spellSelection = new SpellSelection();
   }

   public static void write(FriendlyByteBuf buffer, SyncedSpellData data) {
      buffer.writeInt(data.serverPlayerId);
      buffer.writeBoolean(data.isCasting);
      buffer.m_130070_(data.castingSpellId);
      buffer.writeInt(data.castingSpellLevel);
      buffer.writeFloat(data.heartStopAccumulatedDamage);
      buffer.writeInt(data.evasionHitsRemaining);
      buffer.m_130085_(data.spinAttackType.textureId());
      buffer.writeBoolean(data.spinAttackType.fullbright());
      buffer.m_130070_(data.castingEquipmentSlot);
      data.learnedSpellData.writeToBuffer(buffer);
      data.spellSelection.writeToBuffer(buffer);
   }

   public static SyncedSpellData read(FriendlyByteBuf buffer) {
      SyncedSpellData data = new SyncedSpellData(buffer.readInt());
      data.isCasting = buffer.readBoolean();
      data.castingSpellId = buffer.m_130277_();
      data.castingSpellLevel = buffer.readInt();
      data.heartStopAccumulatedDamage = buffer.readFloat();
      data.evasionHitsRemaining = buffer.readInt();
      data.spinAttackType = new SpinAttackType(buffer.m_130281_(), buffer.readBoolean());
      data.castingEquipmentSlot = buffer.m_130277_();
      data.learnedSpellData.readFromBuffer(buffer);
      data.spellSelection.readFromBuffer(buffer);
      return data;
   }

   public SyncedSpellData(LivingEntity livingEntity) {
      this(livingEntity == null ? -1 : livingEntity.m_19879_());
      this.livingEntity = livingEntity;
   }

   public void saveNBTData(CompoundTag compound, Provider provider) {
      compound.m_128379_("isCasting", this.isCasting);
      compound.m_128359_("castingSpellId", this.castingSpellId);
      compound.m_128359_("castingEquipmentSlot", this.castingEquipmentSlot);
      compound.m_128405_("castingSpellLevel", this.castingSpellLevel);
      compound.m_128350_("heartStopAccumulatedDamage", this.heartStopAccumulatedDamage);
      compound.m_128350_("evasionHitsRemaining", this.evasionHitsRemaining);
      this.learnedSpellData.saveToNBT(compound);
      compound.m_128365_("spellSelection", this.spellSelection.serializeNBT());
   }

   public void loadNBTData(CompoundTag compound, Provider provider) {
      this.isCasting = compound.m_128471_("isCasting");
      this.castingSpellId = compound.m_128461_("castingSpellId");
      this.castingEquipmentSlot = compound.m_128461_("castingEquipmentSlot");
      this.castingSpellLevel = compound.m_128451_("castingSpellLevel");
      this.heartStopAccumulatedDamage = compound.m_128457_("heartStopAccumulatedDamage");
      this.evasionHitsRemaining = compound.m_128451_("evasionHitsRemaining");
      this.learnedSpellData.loadFromNBT(compound);
      this.spellSelection.deserializeNBT(compound.m_128469_("spellSelection"));
   }

   public int getServerPlayerId() {
      return this.serverPlayerId;
   }

   public String getCastingEquipmentSlot() {
      return this.castingEquipmentSlot;
   }

   public float getHeartstopAccumulatedDamage() {
      return this.heartStopAccumulatedDamage;
   }

   public void setHeartstopAccumulatedDamage(float damage) {
      this.heartStopAccumulatedDamage = damage;
      this.doSync();
   }

   public SpellSelection getSpellSelection() {
      return this.spellSelection;
   }

   public void setSpellSelection(SpellSelection spellSelection) {
      this.spellSelection = spellSelection;
      this.doSync();
   }

   public void learnSpell(AbstractSpell spell) {
      this.learnSpell(spell, true);
   }

   public void learnSpell(AbstractSpell spell, boolean sync) {
      this.learnedSpellData.learnedSpells.add(spell.getSpellResource());
      if (sync) {
         this.doSync();
      }
   }

   public void forgetAllSpells() {
      this.learnedSpellData.learnedSpells.clear();
      this.doSync();
   }

   public boolean isSpellLearned(AbstractSpell spell) {
      return !spell.requiresLearning() || this.learnedSpellData.learnedSpells.contains(spell.getSpellResource());
   }

   public SpinAttackType getSpinAttackType() {
      return this.spinAttackType;
   }

   public void setSpinAttackType(SpinAttackType spinAttackType) {
      this.spinAttackType = spinAttackType;
      this.doSync();
   }

   public int getEvasionHitsRemaining() {
      return this.evasionHitsRemaining;
   }

   public void subtractEvasionHit() {
      this.evasionHitsRemaining--;
      this.doSync();
   }

   public void setEvasionHitsRemaining(int hitsRemaining) {
      this.evasionHitsRemaining = hitsRemaining;
      this.doSync();
   }

   public void addHeartstopDamage(float damage) {
      this.heartStopAccumulatedDamage += damage;
      this.doSync();
   }

   public void doSync() {
      if (this.livingEntity instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncPlayerDataPacket(this));
      } else if (this.livingEntity instanceof IMagicEntity abstractSpellCastingMob) {
         PacketDistributor.sendToPlayersTrackingEntity(this.livingEntity, new SyncEntityDataPacket(this, abstractSpellCastingMob));
      }
   }

   public void syncToPlayer(ServerPlayer serverPlayer) {
      PacketDistributor.sendToPlayer(serverPlayer, new SyncPlayerDataPacket(this));
   }

   public void setIsCasting(boolean isCasting, String castingSpellId, int castingSpellLevel, String castingEquipmentSlot) {
      this.isCasting = isCasting;
      this.castingSpellId = castingSpellId;
      this.castingSpellLevel = castingSpellLevel;
      this.castingEquipmentSlot = castingEquipmentSlot;
      this.doSync();
   }

   public boolean isCasting() {
      return this.isCasting;
   }

   public String getCastingSpellId() {
      return this.castingSpellId;
   }

   public int getCastingSpellLevel() {
      return this.castingSpellLevel;
   }

   protected SyncedSpellData clone() {
      return new SyncedSpellData(this.livingEntity);
   }

   @Override
   public String toString() {
      return String.format("isCasting:%s, spellID:%s, spellLevel:%d", this.isCasting, this.castingSpellId, this.castingSpellLevel);
   }

   public SyncedSpellData getPersistentData(ServerPlayer serverPlayer) {
      SyncedSpellData persistentData = new SyncedSpellData(this.livingEntity);
      persistentData.livingEntity = serverPlayer;
      persistentData.learnedSpellData.learnedSpells.addAll(this.learnedSpellData.learnedSpells);
      persistentData.spellSelection = this.spellSelection;
      return persistentData;
   }
}
