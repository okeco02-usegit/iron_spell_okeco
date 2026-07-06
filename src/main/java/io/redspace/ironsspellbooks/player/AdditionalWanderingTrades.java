package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.loot.SpellFilter;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class AdditionalWanderingTrades {
   public static final int INK_SALE_PRICE_PER_RARITY = 8;
   public static final int INK_BUY_PRICE_PER_RARITY = 5;

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void addWanderingTrades(WandererTradesEvent event) {
      if (!ServerConfigs.SPEC.isLoaded() || (Boolean)ServerConfigs.ADDITIONAL_WANDERING_TRADER_TRADES.get()) {
         List<ItemListing> additionalGenericTrades = List.of(
            new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter()),
            new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter()),
            new AdditionalWanderingTrades.RandomScrollTrade(new SpellFilter()),
            new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_COMMON.get()),
            new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()),
            new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_RARE.get()),
            new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_EPIC.get()),
            new AdditionalWanderingTrades.InkBuyTrade((InkItem)ItemRegistry.INK_LEGENDARY.get()),
            new AdditionalWanderingTrades.InkSellTrade((InkItem)ItemRegistry.INK_COMMON.get()),
            new AdditionalWanderingTrades.InkSellTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()),
            new AdditionalWanderingTrades.InkSellTrade((InkItem)ItemRegistry.INK_RARE.get()),
            new AdditionalWanderingTrades.InkSellTrade((InkItem)ItemRegistry.INK_EPIC.get()),
            new AdditionalWanderingTrades.InkSellTrade((InkItem)ItemRegistry.INK_LEGENDARY.get()),
            new AdditionalWanderingTrades.RandomCurioTrade()
         );
         List<ItemListing> additionalRareTrades = List.of(
            AdditionalWanderingTrades.SimpleTrade.of(
               (trader, random) -> new MerchantOffer(
                  new ItemStack(Items.f_42616_, 64 - random.m_216332_(1, 8)),
                  new ItemStack(Items.f_220224_, random.m_216332_(1, 3)),
                  new ItemStack((ItemLike)ItemRegistry.LOST_KNOWLEDGE_FRAGMENT.get()),
                  8,
                  0,
                  0.05F
               )
            ),
            AdditionalWanderingTrades.SimpleTrade.of(
               (trader, random) -> new MerchantOffer(
                  new ItemStack(Items.f_42616_, 64),
                  new ItemStack(Items.f_42616_, random.m_216332_(48, 64)),
                  new ItemStack((ItemLike)ItemRegistry.HITHER_THITHER_WAND.get()),
                  1,
                  0,
                  0.05F
               )
            ),
            new AdditionalWanderingTrades.RandomCurioTrade(),
            new AdditionalWanderingTrades.RandomCurioTrade(),
            new AdditionalWanderingTrades.RandomCurioTrade(),
            new AdditionalWanderingTrades.ScrollPouchTrade(),
            new AdditionalWanderingTrades.ScrollPouchTrade()
         );
         event.getGenericTrades().addAll(additionalGenericTrades.stream().filter(Objects::nonNull).toList());
         event.getRareTrades().addAll(additionalRareTrades.stream().filter(Objects::nonNull).toList());
      }
   }

   public static class ExilirBuyTrade extends AdditionalWanderingTrades.SimpleTrade {
      public ExilirBuyTrade(boolean onlyLesser, boolean onlyGreater) {
         super(
            (trader, random) -> {
               List<Item> lesser = List.of(
                  (Item)ItemRegistry.EVASION_ELIXIR.get(), (Item)ItemRegistry.OAKSKIN_ELIXIR.get(), (Item)ItemRegistry.INVISIBILITY_ELIXIR.get()
               );
               List<Item> greater = List.of(
                  (Item)ItemRegistry.GREATER_EVASION_ELIXIR.get(),
                  (Item)ItemRegistry.GREATER_OAKSKIN_ELIXIR.get(),
                  (Item)ItemRegistry.GREATER_INVISIBILITY_ELIXIR.get(),
                  (Item)ItemRegistry.GREATER_HEALING_POTION.get()
               );
               boolean isGreater;
               if (onlyLesser) {
                  isGreater = false;
               } else if (onlyGreater) {
                  isGreater = true;
               } else {
                  isGreater = random.m_188499_();
               }

               Item item = isGreater ? greater.get(random.m_188503_(greater.size())) : lesser.get(random.m_188503_(lesser.size()));
               return new MerchantOffer(new ItemStack(item), new ItemStack(Items.f_42616_, 6 + random.m_216332_(3, 6) * (isGreater ? 2 : 1)), 6, 1, 0.05F);
            }
         );
      }
   }

   public static class ExilirSellTrade extends AdditionalWanderingTrades.SimpleTrade {
      public ExilirSellTrade(boolean onlyLesser, boolean onlyGreater) {
         super(
            (trader, random) -> {
               List<Item> lesser = List.of(
                  (Item)ItemRegistry.EVASION_ELIXIR.get(), (Item)ItemRegistry.OAKSKIN_ELIXIR.get(), (Item)ItemRegistry.INVISIBILITY_ELIXIR.get()
               );
               List<Item> greater = List.of(
                  (Item)ItemRegistry.GREATER_EVASION_ELIXIR.get(),
                  (Item)ItemRegistry.GREATER_OAKSKIN_ELIXIR.get(),
                  (Item)ItemRegistry.GREATER_INVISIBILITY_ELIXIR.get(),
                  (Item)ItemRegistry.GREATER_HEALING_POTION.get()
               );
               boolean isGreater;
               if (onlyLesser) {
                  isGreater = false;
               } else if (onlyGreater) {
                  isGreater = true;
               } else {
                  isGreater = random.m_188499_();
               }

               Item item = isGreater ? greater.get(random.m_188503_(greater.size())) : lesser.get(random.m_188503_(lesser.size()));
               return new MerchantOffer(new ItemStack(Items.f_42616_, 10 + random.m_216332_(4, 8) * (isGreater ? 2 : 1)), new ItemStack(item), 3, 1, 0.05F);
            }
         );
      }
   }

   public static class InkBuyTrade extends AdditionalWanderingTrades.SimpleTrade {
      public InkBuyTrade(InkItem item) {
         super(
            (trader, random) -> {
               boolean emeralds = random.m_188499_();
               return new MerchantOffer(
                  new ItemStack(item),
                  new ItemStack(
                     (ItemLike)(emeralds ? Items.f_42616_ : (ItemLike)ItemRegistry.ARCANE_ESSENCE.get()),
                     5 * item.getRarity().getValue() / (emeralds ? 1 : 2) + random.m_216332_(2, 3)
                  ),
                  8,
                  1,
                  0.05F
               );
            }
         );
      }
   }

   public static class InkSellTrade extends AdditionalWanderingTrades.SimpleTrade {
      public InkSellTrade(InkItem item) {
         super(
            (trader, random) -> new MerchantOffer(
               new ItemStack(Items.f_42616_, 8 * item.getRarity().getValue() + random.m_216332_(2, 3)), new ItemStack(item), 4, 1, 0.05F
            )
         );
      }
   }

   public static class PotionSellTrade extends AdditionalWanderingTrades.SimpleTrade {
      public PotionSellTrade(@Nullable Potion potion) {
         super(
            (trader, random) -> {
               Potion potion1 = potion;
               if (potion1 == null) {
                  List<Potion> potions = ForgeRegistries.POTIONS.getValues().stream().filter(p -> p.m_43488_().size() > 0).toList();
                  potion1 = potions.get(random.m_188503_(potions.size()));
               }

               if (potion1 == null) {
                  potion1 = Potions.f_43602_;
               }

               int amplifier = 0;
               int duration = 0;
               List<MobEffectInstance> effects = potion1.m_43488_();
               if (effects.size() > 0) {
                  MobEffectInstance effect = effects.get(0);
                  amplifier = effect.m_19564_();
                  duration = effect.m_19557_() / 1200;
               }

               return new MerchantOffer(
                  new ItemStack(Items.f_42616_, random.m_216332_(12, 16) + random.m_216332_(4, 6) * amplifier + duration),
                  PotionUtils.m_43549_(new ItemStack(Items.f_42589_), potion1),
                  3,
                  1,
                  0.05F
               );
            }
         );
      }
   }

   static class RandomCurioTrade extends AdditionalWanderingTrades.SimpleTrade {
      private RandomCurioTrade() {
         super((trader, random) -> {
            if (!trader.f_19853_.f_46443_) {
               LootTable loottable = trader.f_19853_.m_7654_().m_278653_().m_278676_(IronsSpellbooks.id("magic_items/basic_curios"));
               LootParams context = new Builder((ServerLevel)trader.f_19853_).m_287235_(LootContextParamSets.f_81410_);
               ObjectArrayList<ItemStack> items = loottable.m_287195_(context);
               if (!items.isEmpty()) {
                  ItemStack forSale = (ItemStack)items.get(0);
                  ItemStack cost = new ItemStack(Items.f_42616_, random.m_216332_(14, 25));
                  return new MerchantOffer(cost, forSale, 1, 5, 0.5F);
               }
            }

            return null;
         });
      }
   }

   public static class RandomScrollTrade implements ItemListing {
      protected final ItemStack price;
      protected final ItemStack price2;
      protected final ItemStack forSale;
      protected final int maxTrades;
      protected final int xp;
      protected final float priceMult;
      protected final SpellFilter spellFilter;
      protected float minQuality;
      protected float maxQuality;

      public RandomScrollTrade(SpellFilter spellFilter) {
         this.spellFilter = spellFilter;
         this.price = new ItemStack(Items.f_42616_);
         this.price2 = ItemStack.f_41583_;
         this.forSale = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
         this.maxTrades = 1;
         this.xp = 5;
         this.priceMult = 0.05F;
         this.minQuality = 0.0F;
         this.maxQuality = 1.0F;
      }

      public RandomScrollTrade(SpellFilter filter, float minQuality, float maxQuality) {
         this(filter);
         this.minQuality = minQuality;
         this.maxQuality = maxQuality;
      }

      @Nullable
      public MerchantOffer m_213663_(Entity pTrader, RandomSource random) {
         AbstractSpell spell = this.spellFilter.getRandomSpell(random);
         if (spell == SpellRegistry.none()) {
            return null;
         }

         int level = random.m_216332_(1 + (int)(spell.getMaxLevel() * this.minQuality), (int)((spell.getMaxLevel() - 1) * this.maxQuality) + 1);
         ISpellContainer.createScrollContainer(spell, level, this.forSale);
         this.price.m_41764_(spell.getRarity(level).getValue() * 5 + random.m_216332_(4, 7) + level);
         return new MerchantOffer(this.price, this.price2, this.forSale, this.maxTrades, this.xp, this.priceMult);
      }
   }

   static class ScrollPouchTrade extends AdditionalWanderingTrades.SimpleTrade {
      private ScrollPouchTrade() {
         super((trader, random) -> {
            if (!trader.f_19853_.f_46443_) {
               LootTable loottable = trader.f_19853_.m_7654_().m_278653_().m_278676_(IronsSpellbooks.id("magic_items/scroll_pouch"));
               LootParams context = new Builder((ServerLevel)trader.f_19853_).m_287235_(LootContextParamSets.f_81410_);
               ObjectArrayList<ItemStack> items = loottable.m_287195_(context);
               if (!items.isEmpty()) {
                  int quality = 0;
                  ItemStack forSale = new ItemStack(Items.f_151058_).m_41714_(Component.m_237115_("item.irons_spellbooks.scroll_pouch"));
                  ListTag itemsTag = new ListTag();
                  ObjectListIterator cost = items.iterator();

                  while (cost.hasNext()) {
                     ItemStack scroll = (ItemStack)cost.next();
                     itemsTag.add(scroll.m_41739_(new CompoundTag()));
                     if (scroll.m_41720_() instanceof Scroll) {
                        quality += ISpellContainer.get(scroll).getSpellAtIndex(0).getRarity().getValue() + 1;
                     }
                  }

                  forSale.m_41784_().m_128365_("Items", itemsTag);
                  ItemStack costx = new ItemStack(Items.f_42616_, quality * 4 + random.m_216332_(8, 16));
                  return new MerchantOffer(costx, forSale, 1, 5, 0.5F);
               }
            }

            return null;
         });
      }
   }

   public static class SimpleBuy extends AdditionalWanderingTrades.SimpleTrade {
      public SimpleBuy(int tradeCount, ItemStack buy, int minEmeralds, int maxEmeralds) {
         super((trader, random) -> new MerchantOffer(buy, new ItemStack(Items.f_42616_, random.m_216332_(minEmeralds, maxEmeralds)), tradeCount, 0, 0.05F));
      }
   }

   public static class SimpleSell extends AdditionalWanderingTrades.SimpleTrade {
      public SimpleSell(int tradeCount, ItemStack sell, int minEmeralds, int maxEmeralds) {
         super((trader, random) -> new MerchantOffer(new ItemStack(Items.f_42616_, random.m_216332_(minEmeralds, maxEmeralds)), sell, tradeCount, 0, 0.05F));
      }
   }

   public static class SimpleTrade implements ItemListing {
      final BiFunction<Entity, RandomSource, MerchantOffer> getOffer;

      protected SimpleTrade(BiFunction<Entity, RandomSource, MerchantOffer> getOffer) {
         this.getOffer = getOffer;
      }

      public static AdditionalWanderingTrades.SimpleTrade of(BiFunction<Entity, RandomSource, MerchantOffer> getOffer) {
         return new AdditionalWanderingTrades.SimpleTrade(getOffer);
      }

      public MerchantOffer m_213663_(Entity pTrader, RandomSource pRandom) {
         return this.getOffer.apply(pTrader, pRandom);
      }
   }
}
