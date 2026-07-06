package io.redspace.ironsspellbooks.entity.mobs.wizards.alchemist;

import com.google.common.collect.Sets;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.AlchemistAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.FocusOnTradingPlayerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.loot.SpellFilter;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ApothecaristEntity extends NeutralWizard implements IMerchantWizard {
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final List<MerchantOffer> fillerOffers = List.of(
      new MerchantOffer(new ItemStack(Items.f_42616_, 4), ItemStack.f_41583_, new ItemStack(Items.f_42542_, 1), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 6), ItemStack.f_41583_, new ItemStack(Items.f_42787_, 2), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 10), ItemStack.f_41583_, new ItemStack(Items.f_42588_, 5), 0, 5, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 3), ItemStack.f_41583_, new ItemStack(Items.f_42525_), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 3), ItemStack.f_41583_, new ItemStack(Items.f_42451_), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 2), ItemStack.f_41583_, new ItemStack(Items.f_151056_), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 4), ItemStack.f_41583_, new ItemStack(Items.f_42784_), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 7), ItemStack.f_41583_, new ItemStack(Items.f_42592_, 2), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 12), ItemStack.f_41583_, new ItemStack(Items.f_42648_, 1), 0, 3, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 9), ItemStack.f_41583_, new ItemStack(Items.f_42546_, 2), 0, 4, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 12), ItemStack.f_41583_, new ItemStack(Items.f_41954_, 4), 0, 4, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42616_, 12), ItemStack.f_41583_, new ItemStack(Items.f_41955_, 4), 0, 4, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42410_, 12), ItemStack.f_41583_, new ItemStack(Items.f_42616_, 6), 0, 6, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42732_, 10), ItemStack.f_41583_, new ItemStack(Items.f_42616_, 8), 0, 6, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42619_, 6), ItemStack.f_41583_, new ItemStack(Items.f_42616_, 4), 0, 6, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42485_, 6), ItemStack.f_41583_, new ItemStack(Items.f_42616_, 6), 0, 6, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_42735_, 1), ItemStack.f_41583_, new ItemStack((ItemLike)ItemRegistry.ARCANE_ESSENCE.get(), 8), 0, 8, 5, 0.01F),
      new MerchantOffer(new ItemStack(Items.f_151057_, 1), ItemStack.f_41583_, new ItemStack(Items.f_42616_, 16), 0, 1, 5, 0.01F)
   );

   public ApothecaristEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_21364_ = 25;
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FocusOnTradingPlayerGoal<>(this));
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_
         .m_25352_(
            2,
            new AlchemistAttackGoal(this, 1.25, 30, 70, 12.0F, 0.5F)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.FANG_STRIKE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.FANG_STRIKE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ACID_ORB_SPELL.get(),
                     (AbstractSpell)SpellRegistry.POISON_BREATH_SPELL.get(),
                     (AbstractSpell)SpellRegistry.STOMP_SPELL.get(),
                     (AbstractSpell)SpellRegistry.POISON_ARROW_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.ROOT_SPELL.get()),
                  List.of(),
                  List.of((AbstractSpell)SpellRegistry.OAKSKIN_SPELL.get(), (AbstractSpell)SpellRegistry.STOMP_SPELL.get())
               )
               .setDrinksPotions()
               .setSingleUseSpell((AbstractSpell)SpellRegistry.FIREFLY_SWARM_SPELL.get(), 80, 200, 4, 6)
               .setSpellQuality(0.25F, 0.6F)
         );
      this.f_21345_.m_25352_(3, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.f_21345_.m_25352_(10, new WizardRecoverGoal(this));
      this.f_21346_.m_25352_(1, new HurtByTargetGoal(this, new Class[0]));
      this.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(this, AbstractPiglin.class, true));
      this.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isHostileTowards));
      this.f_21346_.m_25352_(5, new ResetUniversalAngerTargetGoal(this, false));
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19853_.f_46443_ && this.f_20913_ > 0) {
         this.f_20913_--;
      }
   }

   public void m_6674_(InteractionHand pHand) {
      this.f_20913_ = 10;
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
      this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.PLAGUED_CHESTPLATE.get()));
      this.m_21409_(EquipmentSlot.CHEST, 0.0F);
   }

   @Override
   public boolean m_6469_(DamageSource pSource, float pAmount) {
      return pSource.m_276093_(DamageTypes.f_268530_) && pSource.m_7639_() == this ? false : super.m_6469_(pSource, pAmount);
   }

   public boolean m_7301_(MobEffectInstance pEffectInstance) {
      return !AlchemistAttackGoal.ATTACK_POTIONS.contains(pEffectInstance.m_19544_());
   }

   public static Builder prepareAttributes() {
      return LivingEntity.m_21183_()
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_(Attributes.f_22282_, 0.0)
         .m_22268_(Attributes.f_22276_, 60.0)
         .m_22268_(Attributes.f_22277_, 24.0)
         .m_22268_(Attributes.f_22279_, 0.25);
   }

   @Override
   protected void m_8024_() {
      super.m_8024_();
      if (this.f_19797_ % 60 == 0) {
         this.f_19853_.m_45976_(AbstractPiglin.class, this.m_20191_().m_82400_(this.m_21133_(Attributes.f_22277_))).forEach(piggy -> {
            if (PiglinAi.m_34975_(piggy).isEmpty() && TargetingConditions.m_148352_().m_26885_(piggy, this)) {
               PiglinAi.m_34924_(piggy, this);
            }
         });
      }
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
         this.offers.addAll(this.createRandomOffers(3, 4));
         if (this.f_19796_.m_188501_() < 0.25F) {
            this.offers.add(new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()).m_213663_(this, this.f_19796_));
         }

         if (this.f_19796_.m_188501_() < 0.25F) {
            this.offers.add(new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_RARE.get()).m_213663_(this, this.f_19796_));
         }

         if (this.f_19796_.m_188501_() < 0.25F) {
            this.offers.add(new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_EPIC.get()).m_213663_(this, this.f_19796_));
         }

         if (this.f_19796_.m_188501_() < 0.5F) {
            this.offers.add(new AdditionalWanderingTrades.ExilirBuyTrade(true, false).m_213663_(this, this.f_19796_));
         }

         int j = this.f_19796_.m_216332_(1, 3);

         for (int i = 0; i < j; i++) {
            this.offers
               .add(
                  this.f_19796_.m_188499_()
                     ? new AdditionalWanderingTrades.PotionSellTrade(null).m_213663_(this, this.f_19796_)
                     : new AdditionalWanderingTrades.ExilirSellTrade(true, false).m_213663_(this, this.f_19796_)
               );
         }

         this.offers
            .add(
               new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.NATURE.get()), 0.0F, 0.4F)
                  .m_213663_(this, this.f_19796_)
            );
         if (this.f_19796_.m_188501_() < 0.65F) {
            this.offers
               .add(
                  new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.NATURE.get()), 0.5F, 0.9F)
                     .m_213663_(this, this.f_19796_)
               );
         }

         this.offers
            .add(
               new MerchantOffer(
                  new ItemStack(Items.f_42616_, 16), ItemStack.f_41583_, new ItemStack((ItemLike)ItemRegistry.NETHERWARD_TINCTURE.get(), 1), 0, 8, 5, 0.01F
               )
            );
         Item greaterElixir = (Item)List.of(
               ItemRegistry.GREATER_EVASION_ELIXIR,
               ItemRegistry.GREATER_OAKSKIN_ELIXIR,
               ItemRegistry.GREATER_INVISIBILITY_ELIXIR,
               ItemRegistry.GREATER_HEALING_POTION
            )
            .get(this.f_19796_.m_188503_(4))
            .get();
         this.offers
            .add(new MerchantOffer(new ItemStack(greaterElixir, 4), ItemStack.f_41583_, ((Item)ItemRegistry.NATURE_RUNE.get()).m_7968_(), 0, 1, 5, 0.1F));
         this.offers.removeIf(Objects::isNull);
         this.setLastRestockGameTime(this.f_19853_.m_46467_());
      }

      return this.offers;
   }

   private Collection<MerchantOffer> createRandomOffers(int min, int max) {
      Set<Integer> set = Sets.newHashSet();
      int fillerTrades = this.f_19796_.m_216332_(min, max);

      for (int i = 0; i < 10 && set.size() < fillerTrades; i++) {
         set.add(this.f_19796_.m_188503_(fillerOffers.size()));
      }

      Collection<MerchantOffer> offers = new ArrayList<>();

      for (Integer integer : set) {
         offers.add(fillerOffers.get(integer));
      }

      return offers;
   }

   public void m_6255_(MerchantOffers pOffers) {
   }

   public int m_8100_() {
      return 200;
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
      return pIsYesSound ? SoundEvents.f_12238_ : SoundEvents.f_12243_;
   }

   public SoundEvent m_7596_() {
      return SoundEvents.f_12238_;
   }

   @Override
   public Optional<SoundEvent> getAngerSound() {
      return Optional.of(SoundEvents.f_12302_);
   }

   protected SoundEvent m_7515_() {
      return SoundEvents.f_12239_;
   }

   protected SoundEvent m_7975_(DamageSource pDamageSource) {
      return SoundEvents.f_12244_;
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_12242_;
   }

   protected void m_7355_(BlockPos pPos, BlockState pBlock) {
      this.m_5496_(SoundEvents.f_12299_, 0.15F, 1.0F);
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
