package io.redspace.ironsspellbooks.item;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Stack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ChronicleItem extends ReadableLoreItem {
   private List<Component> chronicleCache;
   private LocalDate lastCachedDate;

   public ChronicleItem(Properties pProperties) {
      super(IronsSpellbooks.id("textures/entity/lectern/archevoker_logbook.png"), pProperties);
   }

   @Override
   public Optional<ResourceLocation> simpleTextureOverride(ItemStack stack) {
      return Optional.empty();
   }

   @Override
   public List<Component> getPages(ItemStack stack) {
      if (this.chronicleCache == null || this.lastCachedDate != null && this.lastCachedDate.isBefore(LocalDate.now().minusDays(1L))) {
         this.chronicleCache = new ArrayList<>();
         List<MutableComponent> loyalSouls = new ArrayList<>();
         List<MutableComponent> faithfulSouls = new ArrayList<>();
         List<MutableComponent> lostSouls = new ArrayList<>();
         boolean success = this.resolveChronicleData(lostSouls, faithfulSouls, loyalSouls);
         if (!success) {
            this.chronicleCache.add(Component.m_237113_("Failed to fetch Patreon Data :(").m_130940_(ChatFormatting.RED));
            return this.chronicleCache;
         }

         Stack<MutableComponent> pages = new Stack<>();
         MutableComponent loyalPage = Component.m_237110_("item.irons_spellbooks.chronicle.chapter", new Object[]{1})
            .m_130948_(Style.f_131099_.m_131140_(ChatFormatting.DARK_PURPLE).m_131136_(true).m_131162_(false))
            .m_7220_(
               Component.m_237115_("item.irons_spellbooks.chronicle.chapter_1")
                  .m_130948_(Style.f_131099_.m_131140_(ChatFormatting.DARK_PURPLE).m_131136_(true).m_131162_(true))
            );
         loyalPage.m_130946_("\n\n");
         pages.push(loyalPage);
         this.createChapterPages(pages, loyalSouls);
         MutableComponent chroniclersPage = Component.m_237110_("item.irons_spellbooks.chronicle.chapter", new Object[]{2})
            .m_130948_(Style.f_131099_.m_131140_(ChatFormatting.DARK_PURPLE).m_131136_(true).m_131162_(false))
            .m_7220_(
               Component.m_237115_("item.irons_spellbooks.chronicle.chapter_2")
                  .m_130948_(Style.f_131099_.m_131140_(ChatFormatting.DARK_PURPLE).m_131136_(true).m_131162_(true))
            );
         chroniclersPage.m_130946_("\n\n");
         pages.push(chroniclersPage);
         this.createChapterPages(pages, faithfulSouls);
         MutableComponent lostPage = Component.m_237110_("item.irons_spellbooks.chronicle.chapter", new Object[]{3})
            .m_130948_(Style.f_131099_.m_131140_(ChatFormatting.DARK_PURPLE).m_131136_(true).m_131162_(false))
            .m_7220_(
               Component.m_237115_("item.irons_spellbooks.chronicle.chapter_3")
                  .m_130948_(Style.f_131099_.m_131140_(ChatFormatting.DARK_PURPLE).m_131136_(true).m_131162_(true))
            );
         lostPage.m_130946_("\n\n");
         pages.push(lostPage);
         this.createChapterPages(pages, lostSouls);
         this.chronicleCache.addAll(pages);
      }

      return this.chronicleCache;
   }

   public void clearCache() {
      this.chronicleCache = null;
   }

   private boolean resolveChronicleData(List<MutableComponent> lostSouls, List<MutableComponent> faithfulSouls, List<MutableComponent> loyalSouls) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URI("https://code.redspace.io/data/chronicle_data.json").toURL().openStream()))) {
         JsonObject json = (JsonObject)new Gson().fromJson(reader, JsonObject.class);
         int format = json.get("format").getAsInt();
         if (format != 1) {
            throw new IllegalStateException("Unsupported data format: " + format);
         }

         this.lastCachedDate = LocalDate.now();
         int entry = 0;

         for (JsonElement e : json.getAsJsonArray("values")) {
            try {
               entry++;
               JsonObject object = e.getAsJsonObject();
               int bookCategory = object.get("category").getAsInt();
               int activeTier = object.get("type").getAsInt();
               String name = object.get("name").getAsString();

               Style style = switch (activeTier) {
                  case 2 -> Style.f_131099_.m_178520_(14645504).m_131136_(true).m_131162_(false);
                  case 3 -> Style.f_131099_.m_131140_(ChatFormatting.LIGHT_PURPLE).m_131136_(true).m_131162_(false);
                  default -> Style.f_131099_.m_178520_(10376448).m_131136_(false).m_131162_(false);
               };
               MutableComponent component = Component.m_237113_(name).m_130948_(style);
               switch (bookCategory) {
                  case 0:
                     lostSouls.add(component);
                     break;
                  case 1:
                     faithfulSouls.add(component);
                     break;
                  case 2:
                     loyalSouls.add(component);
               }
            } catch (Exception exception) {
               IronsSpellbooks.LOGGER.error("Failed to handle chronicle member entry {}: {}", entry, exception.getMessage());
            }
         }

         reader.close();
      } catch (Exception ex) {
         IronsSpellbooks.LOGGER.error("Failed to handle Chronicle Data: {}", ex.toString());
         return false;
      }

      Comparator<MutableComponent> comparator = Comparator.comparing(c -> c.getString().toLowerCase(Locale.ROOT));
      lostSouls.sort(comparator);
      faithfulSouls.sort(comparator);
      loyalSouls.sort(comparator);
      return true;
   }

   private void createChapterPages(Stack<MutableComponent> pages, List<MutableComponent> entries) {
      int linecount = 3;
      int charWidth = 6;
      int bookLimit = 114;

      for (Component component : entries) {
         int estLines = component.getString().length() * charWidth / bookLimit + 1;
         linecount += estLines;
         if (linecount > 13) {
            MutableComponent nextPage = Component.m_237119_();
            pages.push(nextPage);
            linecount = estLines;
         }

         pages.peek().m_7220_(component).m_130946_("\n");
      }
   }
}
