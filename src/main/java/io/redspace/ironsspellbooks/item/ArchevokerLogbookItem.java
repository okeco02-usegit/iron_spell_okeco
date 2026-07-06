package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.backwards_compat.IBackwardsCompatDefaultNbtItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ArchevokerLogbookItem extends ReadableLoreItem implements IBackwardsCompatDefaultNbtItem {
   public static List<Component> TRANSLATED_CONTENTS = List.of(
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130946_("2:\n\n")
         .m_7220_(Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_1.1")),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_1.2"),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130946_("14:\n\n")
         .m_7220_(Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_2.1")),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_2.2"),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130946_("31:\n\n")
         .m_7220_(Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_3.1")),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_3.2"),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130946_("73:\n\n")
         .m_7220_(Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_4.1")),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_4.2")
   );
   public static List<Component> UNTRANSLATED_CONTENTS = List.of(
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         .m_7220_(Component.m_237113_("2:\n\n").m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("default"))))
         .m_7220_(
            Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_1.1")
               .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         ),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_1.2")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt"))),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         .m_7220_(Component.m_237113_("14:\n\n").m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("default"))))
         .m_7220_(
            Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_2.1")
               .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         ),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_2.2")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt"))),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         .m_7220_(Component.m_237113_("31:\n\n").m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("default"))))
         .m_7220_(
            Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_3.1")
               .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         ),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_3.2")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt"))),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.header")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         .m_7220_(Component.m_237113_("73:\n\n").m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("default"))))
         .m_7220_(
            Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_4.1")
               .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
         ),
      Component.m_237115_("item.irons_spellbooks.archevoker_log.entry_4.2")
         .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("illageralt")))
   );
   private final boolean translated;

   public ArchevokerLogbookItem(boolean translated, Properties pProperties) {
      super(IronsSpellbooks.id("textures/entity/lectern/archevoker_logbook.png"), pProperties);
      this.translated = translated;
   }

   public void m_7373_(ItemStack pStack, Level pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
      super.m_7373_(pStack, pContext, pTooltipComponents, pTooltipFlag);
      if (this.translated) {
         pTooltipComponents.add(Component.m_237115_("tooltip.irons_spellbooks.translated").m_130940_(ChatFormatting.YELLOW));
      } else {
         pTooltipComponents.add(Component.m_237115_("tooltip.irons_spellbooks.untranslated").m_130940_(ChatFormatting.RED));
      }
   }

   @Override
   public void setupItem(ItemStack stack) {
      ListTag listtag = new ListTag();
      List<Component> pages = this.translated ? TRANSLATED_CONTENTS : UNTRANSLATED_CONTENTS;
      pages.stream().map(component -> StringTag.m_129297_(Serializer.m_130703_(component))).forEach(listtag::add);
      stack.m_41700_("pages", listtag);
      stack.m_41700_("author", StringTag.m_129297_("Archevoker"));
      stack.m_41700_("title", StringTag.m_129297_("Archevoker Logbook"));
   }
}
