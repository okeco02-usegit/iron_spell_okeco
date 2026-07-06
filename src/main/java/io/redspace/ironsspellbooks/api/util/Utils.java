package io.redspace.ironsspellbooks.api.util;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.attribute.IMagicAttribute;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.entity.spells.shield.ShieldEntity;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UniqueItem;
import io.redspace.ironsspellbooks.network.casting.CancelCastPacket;
import io.redspace.ironsspellbooks.network.casting.SyncTargetingDataPacket;
import io.redspace.ironsspellbooks.particle.FallingBlockParticleOption;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

public class Utils {
   public static final RandomSource random = RandomSource.m_216337_();
   public static final Predicate<Holder<Attribute>> ONLY_MAGIC_ATTRIBUTES = attribute -> attribute.m_203334_() instanceof IMagicAttribute;
   public static final Predicate<Holder<Attribute>> NON_BASE_ATTRIBUTES = attribute -> attribute != ForgeMod.ENTITY_REACH.get()
      && attribute != Attributes.f_22281_
      && attribute != Attributes.f_22283_
      && attribute != Attributes.f_22282_;

   public static long getServerTick() {
      return IronsSpellbooks.OVERWORLD.m_46467_();
   }

   public static String getStackTraceAsString() {
      Stream<StackTraceElement> trace = Arrays.stream(Thread.currentThread().getStackTrace());
      StringBuffer sb = new StringBuffer();
      trace.forEach(item -> {
         sb.append(item.toString());
         sb.append("\n");
      });
      return sb.toString();
   }

   public static void spawnInWorld(Level level, BlockPos pos, ItemStack remaining) {
      if (!remaining.m_41619_()) {
         ItemEntity itemEntity = new ItemEntity(level, pos.m_123341_() + 0.5, pos.m_123342_() + 0.5, pos.m_123343_() + 0.5, remaining);
         itemEntity.m_32010_(40);
         itemEntity.m_20256_(itemEntity.m_20184_().m_82542_(0.0, 1.0, 0.0));
         level.m_7967_(itemEntity);
      }
   }

   public static boolean canBeUpgraded(ItemStack stack) {
      Item item = stack.m_41720_();
      boolean isUpgradeable = stack.m_204117_(ModTags.CAN_BE_UPGRADED);
      return !ServerConfigs.UPGRADE_BLACKLIST_ITEMS.contains(item)
         && (
            stack.m_41720_() instanceof SpellBook
               || stack.m_41720_() instanceof ArmorItem
               || stack.m_41720_() instanceof CastingItem
               || ServerConfigs.UPGRADE_WHITELIST_ITEMS.contains(item)
               || isUpgradeable
         );
   }

   public static String timeFromTicks(float ticks, int decimalPlaces) {
      float ticks_to_seconds = 20.0F;
      float seconds_to_minutes = 60.0F;
      String affix = "s";
      float time = ticks / ticks_to_seconds;
      if (time > seconds_to_minutes) {
         time /= seconds_to_minutes;
         affix = "m";
      }

      return stringTruncation(time, decimalPlaces) + affix;
   }

   public static boolean handleSpellTeleport(AbstractSpell spell, Entity entity, Vec3 destination) {
      SpellTeleportEvent event = new SpellTeleportEvent(spell, entity, destination.f_82479_, destination.f_82480_, destination.f_82481_);
      MinecraftForge.EVENT_BUS.post(event);
      boolean canceled = event.isCanceled();
      if (!canceled) {
         entity.m_6021_(event.getTargetX(), event.getTargetY(), event.getTargetZ());
      }

      return canceled;
   }

   public static double softCapFormula(double x) {
      return x <= 1.5 ? x : -0.25 * (1.0 / (x - 1.0)) + 2.0;
   }

   @Nullable
   public static ItemStack getPlayerSpellbookStack(@NotNull Player player) {
      return CuriosApi.getCuriosHelper().findCurio(player, Curios.SPELLBOOK_SLOT, 0).<ItemStack>map(SlotResult::stack).orElse(null);
   }

   public static void setPlayerSpellbookStack(@NotNull Player player, ItemStack itemStack) {
      CuriosApi.getCuriosInventory(player).ifPresent(curios -> curios.setEquippedCurio(Curios.SPELLBOOK_SLOT, 0, itemStack));
   }

   public static String stringTruncation(double f, int decimalPlaces) {
      if (f == Math.floor(f)) {
         return Integer.toString((int)f);
      }

      double multiplier = Math.pow(10.0, decimalPlaces);
      double truncatedValue = Math.floor(f * multiplier) / multiplier;
      String result = Double.toString(truncatedValue);
      result = result.replaceAll("0*$", "");
      return result.endsWith(".") ? result.substring(0, result.length() - 1) : result;
   }

   public static float intPow(float f, int exponent) {
      if (exponent == 0) {
         return 1.0F;
      }

      float b = f;

      for (int i = 1; i < Math.abs(exponent); i++) {
         b *= f;
      }

      return exponent < 0 ? 1.0F / b : b;
   }

   public static double intPow(double d, int exponent) {
      if (exponent == 0) {
         return 1.0;
      }

      double b = d;

      for (int i = 1; i < Math.abs(exponent); i++) {
         b *= d;
      }

      return exponent < 0 ? 1.0 / b : b;
   }

   public static float getAngle(Vec2 a, Vec2 b) {
      return getAngle(a.f_82470_, a.f_82471_, b.f_82470_, b.f_82471_);
   }

   public static float getAngle(double ax, double ay, double bx, double by) {
      return (float)Math.atan2(by - ay, bx - ax) + 3.141F;
   }

   public static BlockHitResult getTargetOld(Level level, Player player, Fluid clipContext, double reach) {
      float f = player.m_146909_();
      float f1 = player.m_146908_();
      Vec3 vec3 = player.m_146892_();
      float f2 = Mth.m_14089_(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = Mth.m_14031_(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -Mth.m_14089_(-f * (float) (Math.PI / 180.0));
      float f5 = Mth.m_14031_(-f * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      Vec3 vec31 = vec3.m_82520_(f6 * reach, f5 * reach, f7 * reach);
      return level.m_45547_(new ClipContext(vec3, vec31, Block.OUTLINE, clipContext, player));
   }

   public static BlockHitResult getTargetBlock(Level level, LivingEntity entity, Fluid clipContext, double reach) {
      Vec3 rotation = entity.m_20154_().m_82541_().m_82490_(reach);
      Vec3 pos = entity.m_146892_();
      Vec3 dest = rotation.m_82549_(pos);
      return level.m_45547_(new ClipContext(pos, dest, Block.COLLIDER, clipContext, entity));
   }

   public static boolean hasLineOfSight(Level level, Vec3 start, Vec3 end, boolean checkForShields) {
      if (checkForShields) {
         List<ShieldEntity> shieldEntities = level.m_45976_(ShieldEntity.class, new AABB(start, end));
         if (shieldEntities.size() > 0) {
            HitResult shieldImpact = checkEntityIntersecting(shieldEntities.get(0), start, end, 0.0F);
            if (shieldImpact.m_6662_() != Type.MISS) {
               end = shieldImpact.m_82450_();
            }
         }
      }

      return level.m_45547_(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, null)).m_6662_() == Type.MISS;
   }

   public static boolean hasLineOfSight(Level level, Entity entity1, Entity entity2, boolean checkForShields) {
      return hasLineOfSight(level, entity1.m_146892_(), entity2.m_20191_().m_82399_(), checkForShields);
   }

   public static BlockHitResult raycastForBlock(Level level, Vec3 start, Vec3 end, Fluid clipContext) {
      return level.m_45547_(new ClipContext(start, end, Block.COLLIDER, clipContext, null));
   }

   public static HitResult checkEntityIntersecting(Entity entity, Vec3 start, Vec3 end, float bbInflation) {
      Vec3 hitPos = null;
      if (entity.isMultipartEntity()) {
         for (PartEntity p : entity.getParts()) {
            Vec3 hit = (Vec3)p.m_20191_().m_82400_(bbInflation).m_82371_(start, end).orElse(null);
            if (hit != null) {
               hitPos = hit;
               break;
            }
         }
      } else {
         hitPos = (Vec3)entity.m_20191_().m_82400_(bbInflation).m_82371_(start, end).orElse(null);
      }

      return (HitResult)(hitPos != null ? new EntityHitResult(entity, hitPos) : BlockHitResult.m_82426_(end, Direction.UP, BlockPos.m_274446_(end)));
   }

   public static Vec3 getPositionFromEntityLookDirection(Entity originEntity, float distance) {
      Vec3 start = originEntity.m_146892_();
      return originEntity.m_20154_().m_82541_().m_82490_(distance).m_82549_(start);
   }

   public static HitResult raycastForEntity(Level level, Entity originEntity, float distance, boolean checkForBlocks) {
      Vec3 start = originEntity.m_146892_();
      Vec3 end = originEntity.m_20154_().m_82541_().m_82490_(distance).m_82549_(start);
      return raycastForEntity(level, originEntity, start, end, checkForBlocks);
   }

   public static HitResult raycastForEntity(Level level, Entity originEntity, float distance, boolean checkForBlocks, float bbInflation) {
      Vec3 start = originEntity.m_146892_();
      Vec3 end = originEntity.m_20154_().m_82541_().m_82490_(distance).m_82549_(start);
      return internalRaycastForEntity(level, originEntity, start, end, checkForBlocks, bbInflation, Utils::canHitWithRaycast);
   }

   public static HitResult raycastForEntity(Level level, Entity originEntity, Vec3 start, Vec3 end, boolean checkForBlocks) {
      return internalRaycastForEntity(level, originEntity, start, end, checkForBlocks, 0.0F, Utils::canHitWithRaycast);
   }

   public static HitResult raycastForEntity(
      Level level, Entity originEntity, Vec3 start, Vec3 end, boolean checkForBlocks, float bbInflation, Predicate<? super Entity> filter
   ) {
      return internalRaycastForEntity(level, originEntity, start, end, checkForBlocks, bbInflation, filter);
   }

   public static HitResult raycastForEntityOfClass(Level level, Entity originEntity, Vec3 start, Vec3 end, boolean checkForBlocks, Class<? extends Entity> c) {
      return internalRaycastForEntity(level, originEntity, start, end, checkForBlocks, 0.0F, entity -> entity.getClass() == c);
   }

   public static void releaseUsingHelper(LivingEntity entity, ItemStack itemStack, int ticksUsed) {
      if (entity instanceof ServerPlayer serverPlayer) {
         MagicData pmd = MagicData.getPlayerMagicData(serverPlayer);
         if (pmd.isCasting()) {
            serverSideCancelCast(serverPlayer);
            serverPlayer.m_5810_();
         }
      }
   }

   public static boolean serverSideInitiateCast(ServerPlayer serverPlayer) {
      SpellSelectionManager ssm = new SpellSelectionManager(serverPlayer);
      SpellSelectionManager.SelectionOption spellItem = ssm.getSelection();
      if (spellItem != null) {
         SpellData spellData = ssm.getSelectedSpellData();
         if (spellData != SpellData.EMPTY) {
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            if (playerMagicData.isCasting() && !playerMagicData.getCastingSpellId().equals(spellData.getSpell().getSpellId())) {
               CancelCastPacket.cancelCast(serverPlayer, playerMagicData.getCastType() != CastType.LONG);
            }

            return spellData.getSpell()
               .attemptInitiateCast(
                  ItemStack.f_41583_,
                  spellData.getSpell().getLevelFor(spellData.getLevel(), serverPlayer),
                  serverPlayer.f_19853_,
                  serverPlayer,
                  spellItem.getCastSource(),
                  true,
                  spellItem.slot
               );
         }
      } else if (getPlayerSpellbookStack(serverPlayer) == null) {
         ItemStack heldSpellbookStack = serverPlayer.m_21205_();
         if (!(heldSpellbookStack.m_41720_() instanceof SpellBook)) {
            heldSpellbookStack = serverPlayer.m_21206_();
         }

         if (heldSpellbookStack.m_41720_() instanceof SpellBook spellBook) {
            spellBook.onEquipFromUse(new SlotContext(Curios.SPELLBOOK_SLOT, serverPlayer, 0, false, true), heldSpellbookStack);
            setPlayerSpellbookStack(serverPlayer, heldSpellbookStack.m_41620_(1));
         }
      }

      return false;
   }

   public static double signedMin(double a, double b) {
      return (a < 0.0 ? -1 : 1) * Math.min(Math.abs(a), Math.abs(b));
   }

   public static boolean serverSideInitiateQuickCast(ServerPlayer serverPlayer, int slot) {
      SpellSelectionManager.SelectionOption spellSelection = new SpellSelectionManager(serverPlayer).getSpellSlot(slot);
      if (spellSelection != null) {
         SpellData spellData = spellSelection.spellData;
         if (spellData != SpellData.EMPTY) {
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            if (playerMagicData.isCasting() && !playerMagicData.getCastingSpellId().equals(spellData.getSpell().getSpellId())) {
               CancelCastPacket.cancelCast(serverPlayer, playerMagicData.getCastType() != CastType.LONG);
            }

            return spellData.getSpell()
               .attemptInitiateCast(
                  ItemStack.f_41583_,
                  spellData.getSpell().getLevelFor(spellData.getLevel(), serverPlayer),
                  serverPlayer.f_19853_,
                  serverPlayer,
                  spellSelection.getCastSource(),
                  true,
                  spellSelection.slot
               );
         }
      }

      return false;
   }

   private static HitResult internalRaycastForEntity(
      Level level, Entity originEntity, Vec3 start, Vec3 end, boolean checkForBlocks, float bbInflation, Predicate<? super Entity> filter
   ) {
      BlockHitResult blockHitResult = null;
      if (checkForBlocks) {
         blockHitResult = level.m_45547_(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, originEntity));
         end = blockHitResult.m_82450_();
      }

      AABB range = originEntity.m_20191_().m_82369_(end.m_82546_(start));
      List<HitResult> hits = new ArrayList<>();

      for (Entity target : level.m_6249_(originEntity, range, filter)) {
         HitResult hit = checkEntityIntersecting(target, start, end, bbInflation);
         if (hit.m_6662_() != Type.MISS) {
            hits.add(hit);
         }
      }

      if (!hits.isEmpty()) {
         hits.sort(Comparator.comparingDouble(o -> o.m_82450_().m_82557_(start)));
         return hits.get(0);
      } else {
         return checkForBlocks ? blockHitResult : BlockHitResult.m_82426_(end, Direction.UP, BlockPos.m_274446_(end));
      }
   }

   public static void serverSideCancelCast(ServerPlayer serverPlayer) {
      CancelCastPacket.cancelCast(serverPlayer, MagicData.getPlayerMagicData(serverPlayer).getCastingSpell().getSpell().getCastType() == CastType.CONTINUOUS);
   }

   public static void serverSideCancelCast(ServerPlayer serverPlayer, boolean triggerCooldown) {
      CancelCastPacket.cancelCast(serverPlayer, triggerCooldown);
   }

   public static float smoothstep(float a, float b, float x) {
      x = 6.0F * (x * x * x * x * x) - 15.0F * (x * x * x * x) + 10.0F * (x * x * x);
      return a + (b - a) * x;
   }

   public static boolean canHitWithRaycast(Entity entity) {
      return entity.m_6087_() && entity.m_6084_() && !entity.m_5833_();
   }

   public static int applyCooldownReduction(int baseTicks, @org.jetbrains.annotations.Nullable LivingEntity livingEntity) {
      double modifier = livingEntity == null ? 1.0 : livingEntity.m_21133_((Attribute)AttributeRegistry.COOLDOWN_REDUCTION.get());
      return (int)(baseTicks * (2.0 - softCapFormula(modifier)));
   }

   public static Vec2 rotationFromDirection(Vec3 vector) {
      float pitch = (float)Math.asin(vector.f_82480_);
      float yaw = (float)Math.atan2(vector.f_82479_, vector.f_82481_);
      return new Vec2(pitch, yaw);
   }

   public static boolean doMeleeAttack(Mob attacker, Entity target, DamageSource damageSource) {
      if (attacker.f_19853_.f_46443_) {
         return false;
      }

      float f = (float)attacker.m_21133_(Attributes.f_22281_);
      float f1 = (float)attacker.m_21133_(Attributes.f_22282_);
      if (target instanceof LivingEntity) {
         f += EnchantmentHelper.m_44833_(attacker.m_21205_(), ((LivingEntity)target).m_6336_());
         f1 += EnchantmentHelper.m_44894_(attacker);
      }

      boolean flag = DamageSources.applyDamage(target, f, damageSource);
      if (flag) {
         if (f1 > 0.0F && target instanceof LivingEntity livingTarget) {
            ((LivingEntity)target)
               .m_147240_(
                  f1 * 0.5F, Mth.m_14031_(attacker.m_146908_() * (float) (Math.PI / 180.0)), -Mth.m_14089_(attacker.m_146908_() * (float) (Math.PI / 180.0))
               );
            attacker.m_20256_(attacker.m_20184_().m_82542_(0.6, 1.0, 0.6));
            livingTarget.m_6703_(attacker);
         }

         EnchantmentHelper.m_44896_(attacker, target);
         attacker.m_21335_(target);
      }

      return flag;
   }

   public static double getRandomScaled(double scale) {
      return (2.0 * Math.random() - 1.0) * scale;
   }

   public static Vec3 getRandomVec3(double scale) {
      return new Vec3(getRandomScaled(scale), getRandomScaled(scale), getRandomScaled(scale));
   }

   public static Vector3f getRandomVec3f(double scale) {
      return new Vector3f((float)getRandomScaled(scale), (float)getRandomScaled(scale), (float)getRandomScaled(scale));
   }

   public static Vector3f v3f(Vec3 vec3) {
      return new Vector3f((float)vec3.f_82479_, (float)vec3.f_82480_, (float)vec3.f_82481_);
   }

   public static Vec3 v3d(Vector3f vec3) {
      return new Vec3(vec3.x, vec3.y, vec3.z);
   }

   public static Vec3 lerp(float f, Vec3 a, Vec3 b) {
      return a.m_82549_(b.m_82546_(a).m_82490_(f));
   }

   public static boolean shouldHealEntity(Entity healer, Entity target) {
      if (healer instanceof NeutralMob neutralMob && target instanceof LivingEntity livingEntity && neutralMob.m_21674_(livingEntity)) {
         return false;
      } else if (healer == target) {
         return true;
      } else if (target.m_6095_().m_204039_(ModTags.ALWAYS_HEAL) && !(healer instanceof Enemy)) {
         return true;
      } else if (target.m_7307_(healer) || healer.m_7307_(target)) {
         return true;
      } else if (healer.m_5647_() != null) {
         return target.m_20031_(healer.m_5647_());
      } else {
         return healer instanceof Player
            ? target instanceof Player
            : healer.m_6095_().m_20674_() == target.m_6095_().m_20674_() && healer instanceof Enemy ^ target instanceof Enemy;
      }
   }

   public static boolean canImbue(ItemStack itemStack) {
      if (itemStack.m_41720_() instanceof UniqueItem) {
         return false;
      } else {
         Item item = itemStack.m_41720_();
         if (ServerConfigs.IMBUE_BLACKLIST_ITEMS.contains(item)) {
            return false;
         } else if (ServerConfigs.IMBUE_WHITELIST_ITEMS.contains(item)) {
            return true;
         } else if (itemStack.m_41720_() instanceof SwordItem) {
            return true;
         } else {
            return ISpellContainer.isSpellContainer(itemStack) && !(itemStack.m_41720_() instanceof Scroll) && !(itemStack.m_41720_() instanceof SpellBook)
               ? true
               : itemStack.m_204117_(ModTags.CAN_BE_IMBUED);
         }
      }
   }

   public static ItemStack handleShriving(ItemStack baseStack) {
      ItemStack result = baseStack.m_41777_();
      if (result.m_150930_((Item)ItemRegistry.SCROLL.get())) {
         return ItemStack.f_41583_;
      }

      boolean hasResult = false;
      if (ISpellContainer.isSpellContainer(result) && !(result.m_41720_() instanceof SpellBook) && !(result.m_41720_() instanceof UniqueItem)) {
         if (result.m_41720_() instanceof IPresetSpellContainer) {
            ISpellContainerMutable spellContainer = ISpellContainer.get(result).mutableCopy();
            spellContainer.getActiveSpells().forEach(spellData -> spellContainer.removeSpell(spellData.getSpell()));
            ISpellContainer.set(result, spellContainer.toImmutable());
         } else {
            ISpellContainer.remove(result);
         }

         hasResult = true;
      }

      if (UpgradeData.hasUpgradeData(result)) {
         UpgradeData.removeUpgradeData(result);
         hasResult = true;
      }

      return hasResult ? result : ItemStack.f_41583_;
   }

   public static boolean validAntiMagicTarget(Entity entity) {
      return !entity.m_5833_() && (entity instanceof AntiMagicSusceptible || entity instanceof Player || entity instanceof IMagicEntity);
   }

   public static float findRelativeGroundLevel(Level level, Vec3 start, int maxSteps) {
      if (level.m_8055_(BlockPos.m_274446_(start)).m_60828_(level, BlockPos.m_274446_(start))) {
         for (int i = 0; i < maxSteps; i++) {
            start = start.m_82520_(0.0, 1.0, 0.0);
            BlockPos pos = BlockPos.m_274446_(start);
            if (!level.m_8055_(pos).m_60828_(level, pos)) {
               return pos.m_123342_();
            }
         }
      }

      return (float)level.m_45547_(new ClipContext(start, start.m_82520_(0.0, -maxSteps, 0.0), Block.COLLIDER, Fluid.NONE, null)).m_82450_().f_82480_;
   }

   public static Vec3 moveToRelativeGroundLevel(Level level, Vec3 start, int maxSteps) {
      return moveToRelativeGroundLevel(level, start, maxSteps, maxSteps);
   }

   public static Vec3 moveToRelativeGroundLevel(Level level, Vec3 start, int maxStepsUp, int maxStepsDown) {
      BlockCollisions<VoxelShape> blockcollisions = new BlockCollisions(
         level, null, new AABB(0.0, 0.0, 0.0, 0.5, 0.5, 0.5).m_82383_(start), true, (p_286215_, p_286216_) -> p_286216_
      );
      if (blockcollisions.hasNext()) {
         for (int i = 1; i < maxStepsUp; i++) {
            blockcollisions = new BlockCollisions(
               level, null, new AABB(0.0, 0.0, 0.0, 0.5, 0.5, 0.5).m_82383_(start.m_82520_(0.0, i, 0.0)), true, (p_286215_, p_286216_) -> p_286216_
            );
            if (!blockcollisions.hasNext()) {
               start = start.m_82520_(0.0, i, 0.0);
               break;
            }
         }
      }

      return level.m_45547_(new ClipContext(start, start.m_82520_(0.0, -maxStepsDown, 0.0), Block.COLLIDER, Fluid.NONE, null)).m_82450_();
   }

   public static boolean checkMonsterSpawnRules(ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
      return !pLevel.m_204166_(pPos).m_203565_(Biomes.f_220594_)
         && !pLevel.m_204166_(pPos).m_203656_(net.minecraftforge.common.Tags.Biomes.IS_MUSHROOM)
         && pLevel.m_46791_() != Difficulty.PEACEFUL
         && Monster.m_219009_(pLevel, pPos, pRandom)
         && Monster.m_217057_((EntityType)EntityRegistry.NECROMANCER.get(), pLevel, pSpawnType, pPos, pRandom);
   }

   public static void sendTargetedNotification(ServerPlayer target, LivingEntity caster, AbstractSpell spell) {
      target.f_8906_
         .m_9829_(
            new ClientboundSetActionBarTextPacket(
               Component.m_237110_("ui.irons_spellbooks.spell_target_warning", new Object[]{caster.m_5446_().getString(), spell.getDisplayName(target)})
                  .m_130940_(ChatFormatting.LIGHT_PURPLE)
            )
         );
   }

   public static boolean preCastTargetHelper(Level level, LivingEntity caster, MagicData playerMagicData, AbstractSpell spell, int range, float aimAssist) {
      return preCastTargetHelper(level, caster, playerMagicData, spell, range, aimAssist, true);
   }

   public static boolean preCastTargetHelper(
      Level level, LivingEntity caster, MagicData playerMagicData, AbstractSpell spell, int range, float aimAssist, boolean sendFailureMessage
   ) {
      return preCastTargetHelper(level, caster, playerMagicData, spell, range, aimAssist, sendFailureMessage, x -> true);
   }

   public static boolean preCastTargetHelper(
      Level level,
      LivingEntity caster,
      MagicData playerMagicData,
      AbstractSpell spell,
      int range,
      float aimAssist,
      boolean sendFailureMessage,
      Predicate<LivingEntity> filter
   ) {
      HitResult target = raycastForEntity(caster.f_19853_, caster, range, true, aimAssist);
      LivingEntity livingTarget = null;
      if (target instanceof EntityHitResult entityHit) {
         if (entityHit.m_82443_() instanceof LivingEntity livingEntity && filter.test(livingEntity)) {
            livingTarget = livingEntity;
         } else if (entityHit.m_82443_() instanceof PartEntity<?> partEntity
            && partEntity.getParent() instanceof LivingEntity livingParent
            && !caster.equals(livingParent)
            && filter.test(livingParent)) {
            livingTarget = livingParent;
         } else if (entityHit.m_82443_() instanceof PreventDismount && entityHit.m_82443_().m_146895_() instanceof LivingEntity livingRooted) {
            livingTarget = livingRooted;
         }
      }

      if (livingTarget != null) {
         playerMagicData.setAdditionalCastData(new TargetEntityCastData(livingTarget));
         if (caster instanceof ServerPlayer serverPlayer) {
            if (spell.getCastType() != CastType.INSTANT) {
               PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(livingTarget, spell));
            }

            serverPlayer.f_8906_
               .m_9829_(
                  new ClientboundSetActionBarTextPacket(
                     Component.m_237110_(
                           "ui.irons_spellbooks.spell_target_success", new Object[]{livingTarget.m_5446_().getString(), spell.getDisplayName(serverPlayer)}
                        )
                        .m_130940_(ChatFormatting.GREEN)
                  )
               );
         }

         if (livingTarget instanceof ServerPlayer serverPlayer) {
            sendTargetedNotification(serverPlayer, caster, spell);
         }

         return true;
      } else {
         if (sendFailureMessage && caster instanceof ServerPlayer serverPlayer) {
            serverPlayer.f_8906_
               .m_9829_(new ClientboundSetActionBarTextPacket(Component.m_237115_("ui.irons_spellbooks.cast_error_target").m_130940_(ChatFormatting.RED)));
         }

         return false;
      }
   }

   public static void doMobBreakSuffocatingBlocks(LivingEntity entity) {
      doMobBreakSuffocatingBlocks(entity, Vec3.f_82478_);
   }

   public static void doMobBreakSuffocatingBlocks(LivingEntity entity, Vec3 offset) {
      if (ForgeEventFactory.getMobGriefingEvent(entity.f_19853_, entity)) {
         int l = Mth.m_14143_(entity.m_20205_() / 2.0F + 1.0F);
         int i1 = Mth.m_14167_(entity.m_20206_());
         Vec3i o = new Vec3i(Math.round((float)offset.f_82479_), Math.round((float)offset.f_82480_), Math.round((float)offset.f_82481_));

         for (BlockPos blockpos : BlockPos.m_121976_(
            entity.m_146903_() - l + o.m_123341_(),
            entity.m_146904_() + o.m_123342_(),
            entity.m_146907_() - l + o.m_123343_(),
            entity.m_146903_() + l + o.m_123341_(),
            entity.m_146904_() + i1 + o.m_123342_(),
            entity.m_146907_() + l + o.m_123343_()
         )) {
            BlockState blockstate = entity.f_19853_.m_8055_(blockpos);
            if (blockstate.canEntityDestroy(entity.m_9236_(), blockpos, entity)
               && ForgeEventFactory.onEntityDestroyBlock(entity, blockpos, blockstate)
               && entity.f_19853_.m_46953_(blockpos, true, entity)) {
               entity.f_19853_.m_5898_(null, 1022, entity.m_20183_(), 0);
            }
         }
      }
   }

   public static Vector3f deconstructRGB(int color) {
      int red = color >> 16 & 0xFF;
      int green = color >> 8 & 0xFF;
      int blue = color & 0xFF;
      return new Vector3f(red / 255.0F, green / 255.0F, blue / 255.0F);
   }

   public static int packRGB(Vector3f color) {
      int red = (int)(color.x() * 255.0F);
      int green = (int)(color.y() * 255.0F);
      int blue = (int)(color.z() * 255.0F);
      return red << 16 | green << 8 | blue;
   }

   public static CompoundTag saveAllItems(CompoundTag pTag, NonNullList<ItemStack> pList, String location) {
      ListTag listtag = new ListTag();

      for (int i = 0; i < pList.size(); i++) {
         ItemStack itemstack = (ItemStack)pList.get(i);
         if (!itemstack.m_41619_()) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.m_128344_("Slot", (byte)i);
            itemstack.m_41739_(compoundtag);
            listtag.add(compoundtag);
         }
      }

      if (!listtag.isEmpty()) {
         pTag.m_128365_(location, listtag);
      }

      return pTag;
   }

   public static void loadAllItems(CompoundTag pTag, NonNullList<ItemStack> pList, String location) {
      ListTag listtag = pTag.m_128437_(location, 10);

      for (int i = 0; i < listtag.size(); i++) {
         CompoundTag compoundtag = listtag.m_128728_(i);
         int j = compoundtag.m_128445_("Slot") & 255;
         if (j >= 0 && j < pList.size()) {
            pList.set(j, ItemStack.m_41712_(compoundtag));
         }
      }
   }

   public static float getWeaponDamage(LivingEntity entity, MobType entityForDamageBonus) {
      if (entity != null) {
         float weapon = (float)entity.m_21133_(Attributes.f_22281_);
         float fist = (float)entity.m_21172_(Attributes.f_22281_);
         if (weapon <= fist) {
            weapon -= fist;
         }

         float enchant = EnchantmentHelper.m_44833_(entity.m_21205_(), entityForDamageBonus);
         return weapon + enchant;
      } else {
         return 0.0F;
      }
   }

   public static float getWeaponDamage(LivingEntity entity) {
      return getWeaponDamage(entity, MobType.f_21640_);
   }

   public static float clampedKnockbackResistanceFactor(Entity entity, float min, float max) {
      return entity instanceof LivingEntity living ? Mth.m_14036_(1.0F - (float)living.m_21133_(Attributes.f_22278_), min, max) : max;
   }

   public static int getEnchantmentLevel(Level level, ItemStack stack, Enchantment enchantment) {
      return stack.getEnchantmentLevel(enchantment);
   }

   @org.jetbrains.annotations.Nullable
   public static Holder<Enchantment> enchantmentFromKey(RegistryAccess registryAccess, ResourceKey<Enchantment> enchantmentkey) {
      Registry<Enchantment> reg = (Registry<Enchantment>)registryAccess.m_6632_(Registries.f_256762_).orElse(null);
      if (reg != null) {
         Enchantment enchantment = (Enchantment)reg.m_6246_(enchantmentkey);
         if (enchantment != null) {
            return reg.m_263177_(enchantment);
         }
      }

      return null;
   }

   public static void createTremorBlock(Level level, BlockPos blockPos, float impulseStrength) {
      if (!level.f_46443_) {
         if (level.m_8055_(blockPos.m_7494_()).m_60795_() || level.m_8055_(blockPos.m_7494_().m_7494_()).m_60795_()) {
            MagicManager.spawnParticles(
               level,
               new FallingBlockParticleOption(level.m_8055_(blockPos), new Vec3(0.0, impulseStrength, 0.0)),
               blockPos.m_123341_() + 0.5,
               blockPos.m_123342_(),
               blockPos.m_123343_() + 0.5,
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
            if (!level.m_8055_(blockPos.m_7494_()).m_60795_()) {
               MagicManager.spawnParticles(
                  level,
                  new FallingBlockParticleOption(level.m_8055_(blockPos.m_7494_()), new Vec3(0.0, impulseStrength, 0.0)),
                  blockPos.m_123341_() + 0.5,
                  blockPos.m_123342_() + 1,
                  blockPos.m_123343_() + 0.5,
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  true
               );
            }
         }
      }
   }

   public static void createTremorBlockWithState(Level level, BlockState state, BlockPos blockPos, float impulseStrength) {
      MagicManager.spawnParticles(
         level,
         new FallingBlockParticleOption(state, new Vec3(0.0, impulseStrength, 0.0)),
         blockPos.m_123341_() + 0.5,
         blockPos.m_123342_() + 1,
         blockPos.m_123343_() + 0.5,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
   }

   public static ItemStack setPotion(ItemStack itemStack, Holder<Potion> potion) {
      return PotionUtils.m_43549_(itemStack, (Potion)potion.get());
   }

   public static ItemStack setPotion(ItemStack itemStack, Potion potion) {
      return PotionUtils.m_43549_(itemStack, potion);
   }

   public static void performTaunt(LivingEntity newTarget, float range, Predicate<Entity> selector) {
      performTaunt(
         newTarget,
         newTarget.f_19853_
            .m_6249_(
               newTarget, newTarget.m_20191_().m_82377_(range, range, range), entity -> entity.m_20280_(newTarget) < range * range && selector.test(entity)
            )
      );
   }

   public static void performTaunt(LivingEntity newTarget, List<Entity> targets) {
      targets.forEach(
         entity -> {
            if (entity instanceof Mob tauntmob) {
               MagicManager.spawnParticles(
                  tauntmob.f_19853_,
                  ParticleTypes.f_123792_,
                  tauntmob.m_20185_(),
                  tauntmob.m_20188_() + (tauntmob.m_20191_().f_82292_ - tauntmob.m_20188_()) * 2.0,
                  tauntmob.m_20189_(),
                  5,
                  0.3,
                  0.3,
                  0.3,
                  0.0,
                  false
               );
               tauntmob.m_6710_(newTarget);
            }
         }
      );
   }

   public static void particleTrail(Level level, Vec3 a, Vec3 b, ParticleOptions particleType) {
      double d = a.m_82554_(b) * 4.0;

      for (int i = 0; i < d; i++) {
         double p = i / d;
         Vec3 vec = a.m_82549_(b.m_82546_(a).m_82490_(p));
         MagicManager.spawnParticles(level, particleType, vec.f_82479_, vec.f_82480_, vec.f_82481_, 1, 0.0, 0.0, 0.0, 0.0, true);
      }
   }

   public static Quaternionf rotationBetweenVectors(Vector3f from, Vector3f to) {
      Vector3f fromNorm = new Vector3f(from).normalize();
      Vector3f toNorm = new Vector3f(to).normalize();
      float dot = fromNorm.dot(toNorm);
      if (dot >= 0.9999F) {
         return new Quaternionf().identity();
      }

      if (dot <= -0.9999F) {
         Vector3f perpendicular = new Vector3f(1.0F, 0.0F, 0.0F);
         if (Math.abs(fromNorm.x) > 0.9F) {
            perpendicular.set(0.0F, 1.0F, 0.0F);
         }

         perpendicular.cross(fromNorm).normalize();
         return new Quaternionf().rotationAxis((float) Math.PI, perpendicular);
      } else {
         Vector3f axis = new Vector3f(fromNorm).cross(toNorm).normalize();
         float angle = (float)Math.acos(dot);
         return new Quaternionf().rotationAxis(angle, axis);
      }
   }

   public static void addFreezeTicks(LivingEntity target, int ticks) {
      addFreezeTicks(target, ticks, target.m_146891_() * 5);
   }

   public static void addFreezeTicks(LivingEntity target, int ticks, int cap) {
      target.m_146917_(Math.min(target.m_146888_() + ticks, cap < 0 ? Integer.MAX_VALUE : cap));
   }

   public static Vec3 slerp(double t, Vec3 from, Vec3 to) {
      from = from.m_82541_();
      to = to.m_82541_();
      double dot = from.m_82526_(to);
      double theta = Math.acos(dot) * t;
      Vec3 relative = to.m_82546_(from.m_82490_(dot)).m_82541_();
      return from.m_82490_(Math.cos(theta)).m_82549_(relative.m_82490_(Math.sin(theta)));
   }

   public static boolean isSameItemSameComponentsIgnoreDurability(ItemStack a, ItemStack b) {
      a = a.m_41777_();
      b = b.m_41777_();
      a.m_41749_("Damage");
      b.m_41749_("Damage");
      return ItemStack.m_150942_(a, b);
   }
}
