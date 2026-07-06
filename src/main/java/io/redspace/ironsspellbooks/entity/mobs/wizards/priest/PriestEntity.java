package io.redspace.ironsspellbooks.entity.mobs.wizards.priest;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.SupportMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.FindSupportableTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.FocusOnTradingPlayerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericDefendVillageTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GustDefenseGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.HomeOwner;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.ReturnToHomeAtNightGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.RoamVillageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardSupportGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
import io.redspace.ironsspellbooks.item.FurledMapItem;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PriestEntity extends NeutralWizard implements VillagerDataHolder, SupportMob, HomeOwner, IMerchantWizard {
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.m_135353_(PriestEntity.class, EntityDataSerializers.f_135043_);
   private static final EntityDataAccessor<Boolean> DATA_VILLAGER_UNHAPPY = SynchedEntityData.m_135353_(PriestEntity.class, EntityDataSerializers.f_135035_);
   public GoalSelector supportTargetSelector;
   private int unhappyTimer;
   boolean shouldLookForPoi;
   LivingEntity supportTarget;
   BlockPos homePos;
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;

   public PriestEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 15;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FocusOnTradingPlayerGoal<>(this));
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(0, new OpenDoorGoal(this, true));
      this.f_21345_.m_25352_(1, new GustDefenseGoal(this));
      this.f_21345_
         .m_25352_(
            2,
            new WizardSupportGoal<>(this, 1.25, 100, 180)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.BLESSING_OF_LIFE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLESSING_OF_LIFE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEALING_CIRCLE_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.FORTIFY_SPELL.get())
               )
         );
      this.f_21345_
         .m_25352_(
            3,
            new WizardAttackGoal(this, 1.25, 35, 70)
               .setSpells(
                  List.of((AbstractSpell)SpellRegistry.WISP_SPELL.get(), (AbstractSpell)SpellRegistry.GUIDING_BOLT_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.GUST_SPELL.get()),
                  List.of(),
                  List.of((AbstractSpell)SpellRegistry.HEAL_SPELL.get())
               )
               .setSpellQuality(0.3F, 0.5F)
               .setDrinksPotions()
         );
      this.f_21345_.m_25352_(5, new RoamVillageGoal(this, 30.0F, 1.0));
      this.f_21345_.m_25352_(6, new ReturnToHomeAtNightGoal<PriestEntity>(this, 1.0));
      this.f_21345_.m_25352_(7, new PatrolNearLocationGoal(this, 30.0F, 1.0));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(10, new WizardRecoverGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new GenericDefendVillageTargetGoal(this));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isHostileTowards));
      this.f_21346_.m_25352_(4, new NearestAttackableTargetGoal(this, Mob.class, 5, false, false, mob -> mob instanceof Enemy && !(mob instanceof Creeper)));
      this.f_21346_.m_25352_(5, new ResetUniversalAngerTargetGoal(this, false));
      this.supportTargetSelector = new GoalSelector(this.f_19853_.m_46658_());
      this.supportTargetSelector
         .m_25352_(
            0,
            new FindSupportableTargetGoal(
               this,
               LivingEntity.class,
               true,
               mob -> !this.m_21674_(mob)
                  && mob.m_21223_() * 1.25F < mob.m_21233_()
                  && (mob.m_6095_().m_204039_(ModTags.VILLAGE_ALLIES) || mob instanceof Player)
            )
         );
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      if (pReason == MobSpawnType.STRUCTURE) {
         this.shouldLookForPoi = true;
      }

      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistry.PRIEST_HELMET.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.PRIEST_CHESTPLATE.get()));
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22282_, 0.0)
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22277_, 24.0)
         .m_22268_((Attribute)AttributeRegistry.CAST_TIME_REDUCTION.get(), 1.5)
         .m_22268_(Attributes.f_22279_, 0.23);
   }

   protected PathNavigation m_6037_(Level pLevel) {
      return new GroundPathNavigation(this, pLevel) {
         protected PathFinder m_5532_(int pMaxVisitedNodes) {
            this.f_26508_ = new WalkNodeEvaluator();
            this.f_26508_.m_77351_(true);
            this.f_26508_.m_77355_(true);
            return new PathFinder(this.f_26508_, pMaxVisitedNodes);
         }
      };
   }

   @Override
   public boolean guardsBlocks() {
      return false;
   }

   @javax.annotation.Nullable
   protected SoundEvent m_7515_() {
      if (this.m_5803_()) {
         return null;
      } else {
         return this.isTrading() ? SoundEvents.f_12508_ : SoundEvents.f_12503_;
      }
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_12505_;
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_12506_;
   }

   @Override
   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_VILLAGER_DATA, new VillagerData(VillagerType.f_35821_, VillagerProfession.f_35585_, 1));
      this.f_19804_.m_135372_(DATA_VILLAGER_UNHAPPY, false);
   }

   public void m_34375_(VillagerData villagerdata) {
      villagerdata.m_35565_(VillagerProfession.f_35585_);
      this.f_19804_.m_135381_(DATA_VILLAGER_DATA, villagerdata);
   }

   public boolean isUnhappy() {
      return (Boolean)this.f_19804_.m_135370_(DATA_VILLAGER_UNHAPPY);
   }

   @NotNull
   public VillagerData m_7141_() {
      return (VillagerData)this.f_19804_.m_135370_(DATA_VILLAGER_DATA);
   }

   @Nullable
   @Override
   public LivingEntity getSupportTarget() {
      return this.supportTarget;
   }

   @Override
   public void setSupportTarget(LivingEntity target) {
      this.supportTarget = target;
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      if (this.shouldLookForPoi) {
         if (this.f_19853_ instanceof ServerLevel serverLevel) {
            Optional<BlockPos> optional1 = serverLevel.m_8904_()
               .m_27186_(poiTypeHolder -> poiTypeHolder.m_203565_(PoiTypes.f_218061_), blockPos -> true, this.m_20183_(), 100, Occupancy.ANY);
            optional1.ifPresent(this::setHome);
         }

         this.shouldLookForPoi = false;
      }

      if (this.f_19797_ % 4 == 0 && this.f_19797_ > 1) {
         this.supportTargetSelector.m_25373_();
      }

      if (this.f_19797_ % 60 == 0) {
         this.f_19853_
            .m_6249_(
               this,
               this.m_20191_().m_82400_(this.m_21133_(Attributes.f_22277_)),
               entity -> entity instanceof Enemy && !(entity instanceof Creeper) && !(entity instanceof IMagicSummon) && !(entity instanceof TamableAnimal)
            )
            .forEach(enemy -> {
               if (enemy instanceof Mob mob && mob.m_5448_() == null && TargetingConditions.m_148352_().m_26885_(mob, this)) {
                  mob.m_6710_(this);
               }
            });
      }

      if (this.unhappyTimer > 0 && --this.unhappyTimer == 0) {
         this.f_19804_.m_135381_(DATA_VILLAGER_UNHAPPY, false);
      }
   }

   protected InteractionResult m_6071_(Player pPlayer, InteractionHand pHand) {
      boolean preventTrade = this.m_5912_() || !this.f_19853_.f_46443_ && this.m_6616_().isEmpty();
      if (pHand == InteractionHand.MAIN_HAND && preventTrade && !this.f_19853_.f_46443_) {
         this.setUnhappy();
      }

      if (!preventTrade) {
         if (!this.f_19853_.f_46443_ && !this.m_6616_().isEmpty()) {
            if (this.shouldRestock()) {
               this.restock();
            }

            this.startTrading(pPlayer);
         }

         return InteractionResult.m_19078_(this.f_19853_.f_46443_);
      } else {
         return super.m_6071_(pPlayer, pHand);
      }
   }

   public void setUnhappy() {
      if (!this.f_19853_.f_46443_) {
         this.m_5496_(SoundEvents.f_12507_, this.m_6121_(), this.m_6100_());
         this.unhappyTimer = 20;
         this.f_19804_.m_135381_(DATA_VILLAGER_UNHAPPY, true);
      }
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      this.serializeHome(this, pCompound);
      this.serializeMerchant(pCompound, this.offers, this.lastRestockGameTime, this.numberOfRestocksToday);
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.deserializeHome(this, pCompound);
      this.deserializeMerchant(pCompound, c -> this.offers = c);
   }

   @Override
   public Optional<SoundEvent> getAngerSound() {
      return Optional.of(SoundEvents.f_12507_);
   }

   @Nullable
   @Override
   public BlockPos getHome() {
      return this.homePos;
   }

   @Override
   public void setHome(BlockPos homePos) {
      this.homePos = homePos;
   }

   public void m_7189_(@Nullable Player pTradingPlayer) {
      this.tradingPlayer = pTradingPlayer;
   }

   @Nullable
   public Player m_7962_() {
      return this.tradingPlayer;
   }

   public MerchantOffers m_6616_() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.offers
            .add(
               new MerchantOffer(
                  new ItemStack(Items.f_42616_, 24),
                  ItemStack.f_41583_,
                  FurledMapItem.of(
                     IronsSpellbooks.id("evoker_fort"), FurledMapItem.OVERWORLD, Component.m_237115_("item.irons_spellbooks.evoker_fort_battle_plans")
                  ),
                  0,
                  1,
                  5,
                  10.0F
               )
            );
         this.offers.add(new MerchantOffer(new ItemStack((ItemLike)ItemRegistry.GREATER_HEALING_POTION.get()), new ItemStack(Items.f_42616_, 18), 3, 0, 0.2F));
         this.offers
            .add(new MerchantOffer(new ItemStack(Items.f_42616_, 6), PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43623_), 2, 0, 0.2F));
         this.offers.add(new PriestEntity.BibleTrade().m_213663_(this, this.f_19796_));
         this.offers.removeIf(Objects::isNull);
         this.setLastRestockGameTime(this.f_19853_.m_46467_());
      }

      return this.offers;
   }

   public void m_6255_(MerchantOffers pOffers) {
   }

   public void m_6996_(MerchantOffer pOffer) {
      pOffer.m_45374_();
      this.f_21363_ = -this.m_8100_();
   }

   public void m_7713_(ItemStack pStack) {
      if (!this.f_19853_.f_46443_ && this.f_21363_ > -this.m_8100_() + 20) {
         this.f_21363_ = -this.m_8100_();
         this.m_5496_(this.getTradeUpdatedSound(!pStack.m_41619_()), this.m_6121_(), this.m_6100_());
      }
   }

   public SoundEvent m_7596_() {
      return SoundEvents.f_12509_;
   }

   protected SoundEvent getTradeUpdatedSound(boolean pIsYesSound) {
      return pIsYesSound ? SoundEvents.f_12509_ : SoundEvents.f_12507_;
   }

   private void startTrading(Player pPlayer) {
      this.m_7189_(pPlayer);
      this.m_45301_(pPlayer, this.m_5446_(), this.m_7141_().m_35576_());
   }

   @Override
   public int getRestocksToday() {
      return this.numberOfRestocksToday;
   }

   @Override
   public void setRestocksToday(int restocks) {
      this.numberOfRestocksToday = restocks;
   }

   @Override
   public long getLastRestockGameTime() {
      return this.lastRestockGameTime;
   }

   @Override
   public void setLastRestockGameTime(long time) {
      this.lastRestockGameTime = time;
   }

   @Override
   public long getLastRestockCheckDayTime() {
      return this.lastRestockCheckDayTime;
   }

   @Override
   public void setLastRestockCheckDayTime(long time) {
      this.lastRestockCheckDayTime = time;
   }

   @Override
   public Level m_9236_() {
      return this.f_19853_;
   }

   static class BibleTrade extends AdditionalWanderingTrades.SimpleTrade {
      private BibleTrade() {
         super((trader, random) -> {
            if (!trader.f_19853_.f_46443_) {
               ItemStack cost = new ItemStack((ItemLike)ItemRegistry.TRANSLATED_ARCHEVOKER_LOGBOOK.get());
               ItemStack forSale = new ItemStack((ItemLike)ItemRegistry.VILLAGER_SPELL_BOOK.get());
               return new MerchantOffer(cost, forSale, 1, 5, 0.5F);
            } else {
               return new MerchantOffer(ItemStack.f_41583_, ItemStack.f_41583_, 0, 0, 0.0F);
            }
         });
      }
   }
}
