package io.redspace.ironsspellbooks.entity.mobs.wizards.cryomancer;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.FocusOnTradingPlayerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
import io.redspace.ironsspellbooks.item.FurledMapItem;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.loot.SpellFilter;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class CryomancerEntity extends NeutralWizard implements IMerchantWizard {
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final List<ItemListing> fillerOffers = List.of();

   public CryomancerEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 25;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FocusOnTradingPlayerGoal<>(this));
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.ICE_BLOCK_SPELL.get(), 3, 6, 100, 250, 1));
      this.f_21345_
         .m_25352_(
            3,
            new WizardAttackGoal(this, 1.25, 50, 75)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.ICICLE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ICICLE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ICICLE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CONE_OF_COLD_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.FROST_STEP_SPELL.get()),
                  List.of()
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(), 80, 400, 3, 6)
               .setDrinksPotions()
         );
      this.f_21345_.m_25352_(4, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(10, new WizardRecoverGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.f_21346_.m_25352_(5, new ResetUniversalAngerTargetGoal(this, false));
   }

   @org.jetbrains.annotations.Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor pLevel,
      DifficultyInstance pDifficulty,
      MobSpawnType pReason,
      @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData,
      @org.jetbrains.annotations.Nullable CompoundTag pDataTag
   ) {
      RandomSource randomsource = Utils.random;
      this.m_213945_(randomsource, pDifficulty);
      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   protected void m_213945_(RandomSource pRandom, DifficultyInstance pDifficulty) {
      this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistry.CRYOMANCER_HELMET.get()));
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.CRYOMANCER_CHESTPLATE.get()));
      this.m_21409_(EquipmentSlot.HEAD, 0.0F);
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
   }

   public boolean m_142079_() {
      return false;
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22282_, 0.0)
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22277_, 24.0)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   protected InteractionResult m_6071_(Player pPlayer, InteractionHand pHand) {
      boolean preventTrade = this.m_5912_() || !this.f_19853_.f_46443_ && this.m_6616_().isEmpty();
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

   private void startTrading(Player pPlayer) {
      this.m_7189_(pPlayer);
      this.m_45301_(pPlayer, this.m_5446_(), 0);
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

   public void m_7189_(@org.jetbrains.annotations.Nullable Player pTradingPlayer) {
      this.tradingPlayer = pTradingPlayer;
   }

   public Player m_7962_() {
      return this.tradingPlayer;
   }

   public MerchantOffers m_6616_() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.offers.addAll(this.createRandomOffers(2, 3));
         if (this.f_19796_.m_188501_() < 0.25F) {
            this.offers.add(new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_COMMON.get()).m_213663_(this, this.f_19796_));
         }

         if (this.f_19796_.m_188501_() < 0.25F) {
            this.offers.add(new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()).m_213663_(this, this.f_19796_));
         }

         if (this.f_19796_.m_188501_() < 0.25F) {
            this.offers.add(new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_RARE.get()).m_213663_(this, this.f_19796_));
         }

         this.offers
            .add(
               new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.ICE.get()), 0.0F, 0.25F)
                  .m_213663_(this, this.f_19796_)
            );
         if (this.f_19796_.m_188501_() < 0.8F) {
            this.offers
               .add(
                  new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.ICE.get()), 0.3F, 0.7F)
                     .m_213663_(this, this.f_19796_)
               );
         }

         if (this.f_19796_.m_188501_() < 0.8F) {
            this.offers
               .add(
                  new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.ICE.get()), 0.8F, 1.0F)
                     .m_213663_(this, this.f_19796_)
               );
         }

         this.offers
            .add(
               new MerchantOffer(
                  new ItemStack(Items.f_42616_, 32),
                  ItemStack.f_41583_,
                  this.f_19796_.m_188499_()
                     ? FurledMapItem.of(IronsSpellbooks.id("impaled_icebreaker"), Component.m_237115_("item.irons_spellbooks.failed_arctic_voyage_map"))
                     : FurledMapItem.of(IronsSpellbooks.id("ice_spider_den"), Component.m_237115_("item.irons_spellbooks.ice_spider_den_map")),
                  0,
                  1,
                  5,
                  10.0F
               )
            );
         this.offers
            .add(
               new MerchantOffer(
                  new ItemStack((ItemLike)ItemRegistry.FIRE_ALE.get(), 4),
                  ItemStack.f_41583_,
                  ((Item)ItemRegistry.MUSIC_DISC_WHISPERS_OF_ICE.get()).m_7968_(),
                  0,
                  1,
                  5,
                  0.1F
               )
            );
         this.offers
            .add(
               new MerchantOffer(
                  new ItemStack((ItemLike)ItemRegistry.ICY_FANG.get(), 2), ItemStack.f_41583_, ((Item)ItemRegistry.ICE_RUNE.get()).m_7968_(), 0, 1, 5, 0.1F
               )
            );
         this.offers.removeIf(Objects::isNull);
         this.setLastRestockGameTime(this.f_19853_.m_46467_());
      }

      return this.offers;
   }

   private Collection<MerchantOffer> createRandomOffers(int min, int max) {
      return new ArrayList<>();
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

   protected SoundEvent getTradeUpdatedSound(boolean pIsYesSound) {
      return pIsYesSound ? (SoundEvent)SoundRegistry.TRADER_YES.get() : (SoundEvent)SoundRegistry.TRADER_NO.get();
   }

   public SoundEvent m_7596_() {
      return (SoundEvent)SoundRegistry.TRADER_YES.get();
   }

   @Override
   public void m_7380_(CompoundTag pCompound) {
      super.m_7380_(pCompound);
      this.serializeMerchant(pCompound, this.offers, this.lastRestockGameTime, this.numberOfRestocksToday);
   }

   @Override
   public void m_7378_(CompoundTag pCompound) {
      super.m_7378_(pCompound);
      this.deserializeMerchant(pCompound, c -> this.offers = c);
   }
}
