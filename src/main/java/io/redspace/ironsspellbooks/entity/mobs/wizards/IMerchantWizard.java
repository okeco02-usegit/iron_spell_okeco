package io.redspace.ironsspellbooks.entity.mobs.wizards;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IMerchantWizard extends Merchant {
   default void serializeMerchant(CompoundTag pCompound, @Nullable MerchantOffers offers, long lastRestockGameTime, int numberOfRestocksToday) {
      if (offers != null && !offers.isEmpty()) {
         pCompound.m_128365_("Offers", offers.m_45388_());
      }

      pCompound.m_128356_("LastRestock", lastRestockGameTime);
      pCompound.m_128405_("RestocksToday", numberOfRestocksToday);
   }

   default void deserializeMerchant(CompoundTag pCompound, Consumer<MerchantOffers> setOffers) {
      if (pCompound.m_128425_("Offers", 10)) {
         setOffers.accept(new MerchantOffers(pCompound.m_128469_("Offers")));
      }

      this.setLastRestockGameTime(pCompound.m_128454_("LastRestock"));
      this.setRestocksToday(pCompound.m_128451_("RestocksToday"));
   }

   default boolean isTrading() {
      return this.m_7962_() != null;
   }

   default boolean needsToRestock() {
      for (MerchantOffer merchantoffer : this.m_6616_()) {
         if (merchantoffer.m_45382_()) {
            return true;
         }
      }

      return false;
   }

   default boolean allowedToRestock() {
      return this.getRestocksToday() == 0 && this.m_9236_().m_46467_() > this.getLastRestockGameTime() + 2400L;
   }

   default void stopTrading() {
      this.m_7189_(null);
   }

   default boolean shouldRestock() {
      long timeToNextRestock = this.getLastRestockGameTime() + 12000L;
      long currentGameTime = this.m_9236_().m_46467_();
      boolean hasDayElapsed = currentGameTime > timeToNextRestock;
      long currentDayTime = this.m_9236_().m_46468_();
      if (this.getLastRestockCheckDayTime() > 0L) {
         long lastRestockDay = this.getLastRestockCheckDayTime() / 24000L;
         long currentDay = currentDayTime / 24000L;
         hasDayElapsed |= currentDay > lastRestockDay;
      } else {
         this.setLastRestockCheckDayTime(currentDayTime);
      }

      if (hasDayElapsed) {
         this.setLastRestockCheckDayTime(currentDayTime);
         this.setRestocksToday(0);
      }

      return this.needsToRestock() && this.allowedToRestock();
   }

   default void restock() {
      for (MerchantOffer offer : this.m_6616_()) {
         offer.m_45369_();
         offer.m_45372_();
      }

      this.setLastRestockGameTime(this.m_9236_().m_46467_());
      this.setRestocksToday(this.getRestocksToday() + 1);
   }

   int getRestocksToday();

   void setRestocksToday(int var1);

   long getLastRestockGameTime();

   void setLastRestockGameTime(long var1);

   long getLastRestockCheckDayTime();

   void setLastRestockCheckDayTime(long var1);

   Level m_9236_();

   default int m_7809_() {
      return 0;
   }

   default void m_6621_(int pXp) {
   }

   default boolean m_7826_() {
      return false;
   }

   default boolean m_183595_() {
      return this.m_9236_().m_5776_();
   }
}
