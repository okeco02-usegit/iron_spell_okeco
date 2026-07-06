package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.wall_of_fire.WallOfFireEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WallOfFireSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "wall_of_fire");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(30.0)
      .build();

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.m_237110_("ui.irons_spellbooks.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.m_237110_("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getWallLength(spellLevel, caster), 1)})
      );
   }

   public WallOfFireSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 30;
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
   public ICastDataSerializable getEmptyCastData() {
      return new WallOfFireSpell.FireWallData(0.0F);
   }

   @Override
   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 3;
   }

   @Override
   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getPlayerRecasts().hasRecastForSpell(this)) {
         RecastInstance recast = playerMagicData.getPlayerRecasts().getRecastInstance(this.getSpellId());
         WallOfFireSpell.FireWallData fireWallData = (WallOfFireSpell.FireWallData)recast.getCastData();
         this.addAnchor(fireWallData, world, entity, recast);
      } else {
         WallOfFireSpell.FireWallData fireWallData = new WallOfFireSpell.FireWallData(this.getWallLength(spellLevel, entity));
         RecastInstance recast = new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 40, castSource, fireWallData);
         this.addAnchor(fireWallData, world, entity, recast);
         playerMagicData.getPlayerRecasts().addRecast(recast, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public void onRecastFinished(ServerPlayer entity, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (!recastResult.isFailure()) {
         Level level = entity.f_19853_;
         WallOfFireSpell.FireWallData fireWallData = (WallOfFireSpell.FireWallData)recastInstance.getCastData();
         if (fireWallData.anchorPoints.size() == 1) {
            this.addAnchor(fireWallData, level, entity, recastInstance);
         }

         if (fireWallData.anchorPoints.size() > 0) {
            WallOfFireEntity fireWall = new WallOfFireEntity(level, entity, fireWallData.anchorPoints, this.getDamage(recastInstance.getSpellLevel(), entity));
            Vec3 origin = fireWallData.anchorPoints.get(0);

            for (int i = 1; i < fireWallData.anchorPoints.size(); i++) {
               origin.m_82549_(fireWallData.anchorPoints.get(i));
            }

            origin.m_82490_(1.0F / fireWallData.anchorPoints.size());
            fireWall.m_146884_(origin);
            level.m_7967_(fireWall);
         }
      }

      super.onRecastFinished(entity, recastInstance, recastResult, castDataSerializable);
   }

   @Override
   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFireTicks(80);
   }

   private float getWallLength(int spellLevel, LivingEntity entity) {
      return 10.0F + spellLevel * 3 * this.getEntityPowerMultiplier(entity);
   }

   private float getDamage(int spellLevel, LivingEntity sourceEntity) {
      return this.getSpellPower(spellLevel, sourceEntity);
   }

   public void addAnchor(WallOfFireSpell.FireWallData fireWallData, Level level, LivingEntity entity, RecastInstance recastInstance) {
      Vec3 anchor = Utils.getTargetBlock(level, entity, Fluid.ANY, 20.0).m_82450_();
      anchor = this.setOnGround(anchor, level);
      List<Vec3> anchorPoints = fireWallData.anchorPoints;
      if (anchorPoints.size() == 0) {
         anchorPoints.add(anchor);
      } else {
         int i = anchorPoints.size();
         float distance = (float)anchorPoints.get(i - 1).m_82554_(anchor);
         float maxDistance = fireWallData.maxTotalDistance - fireWallData.accumulatedDistance;
         if (distance <= maxDistance) {
            fireWallData.accumulatedDistance += distance;
            anchorPoints.add(anchor);
         } else {
            Vec3 var12 = anchorPoints.get(i - 1).m_82549_(anchor.m_82546_(anchorPoints.get(i - 1)).m_82541_().m_82490_(maxDistance));
            anchor = this.setOnGround(var12, level);
            anchorPoints.add(anchor);
            if (entity instanceof ServerPlayer serverPlayer && recastInstance.getRemainingRecasts() > 0) {
               MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts().removeRecast(recastInstance, RecastResult.USED_ALL_RECASTS);
            }
         }
      }

      MagicManager.spawnParticles(level, ParticleTypes.f_123744_, anchor.f_82479_, anchor.f_82480_ + 1.5, anchor.f_82481_, 5, 0.05, 0.25, 0.05, 0.0, true);
   }

   private Vec3 setOnGround(Vec3 in, Level level) {
      if (level.m_8055_(BlockPos.m_274561_(in.f_82479_, in.f_82480_ + 0.5, in.f_82481_)).m_60795_()) {
         for (int i = 0; i < 15; i++) {
            if (!level.m_8055_(BlockPos.m_274561_(in.f_82479_, in.f_82480_ - i, in.f_82481_)).m_60795_()) {
               return new Vec3(in.f_82479_, in.f_82480_ - i + 1.0, in.f_82481_);
            }
         }

         return new Vec3(in.f_82479_, in.f_82480_ - 15.0, in.f_82481_);
      } else {
         double y = level.m_6924_(Types.MOTION_BLOCKING, (int)in.f_82479_, (int)in.f_82481_);
         return new Vec3(in.f_82479_, y, in.f_82481_);
      }
   }

   public class FireWallData implements ICastDataSerializable {
      private Entity castingEntity;
      public List<Vec3> anchorPoints = new ArrayList<>();
      public float maxTotalDistance;
      public float accumulatedDistance;
      public int ticks;

      FireWallData(float maxTotalDistance) {
         this.maxTotalDistance = maxTotalDistance;
      }

      @Override
      public void reset() {
      }

      @Override
      public void writeToBuffer(FriendlyByteBuf buffer) {
         buffer.writeInt(this.anchorPoints.size());

         for (Vec3 vec : this.anchorPoints) {
            buffer.writeFloat((float)vec.f_82479_);
            buffer.writeFloat((float)vec.f_82480_);
            buffer.writeFloat((float)vec.f_82481_);
         }
      }

      @Override
      public void readFromBuffer(FriendlyByteBuf buffer) {
         this.anchorPoints = new ArrayList<>();
         int length = buffer.readInt();

         for (int i = 0; i < length; i++) {
            this.anchorPoints.add(new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
         }
      }

      public CompoundTag serializeNBT() {
         CompoundTag compoundTag = new CompoundTag();
         ListTag anchors = new ListTag();

         for (Vec3 vec : this.anchorPoints) {
            CompoundTag anchor = new CompoundTag();
            anchor.m_128350_("x", (float)vec.f_82479_);
            anchor.m_128350_("y", (float)vec.f_82480_);
            anchor.m_128350_("z", (float)vec.f_82481_);
            anchors.add(anchor);
         }

         compoundTag.m_128365_("Anchors", anchors);
         return compoundTag;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.anchorPoints = new ArrayList<>();
         if (nbt.m_128425_("Anchors", 9)) {
            for (Tag tag : (ListTag)nbt.m_128423_("Anchors")) {
               if (tag instanceof CompoundTag anchor) {
                  this.anchorPoints.add(new Vec3(anchor.m_128459_("x"), anchor.m_128459_("y"), anchor.m_128459_("z")));
               }
            }
         }
      }
   }
}
