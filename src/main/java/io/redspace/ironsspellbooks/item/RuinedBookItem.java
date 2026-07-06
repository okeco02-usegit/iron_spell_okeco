package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RuinedBookItem extends Item implements ILecternPlaceable {
   public static Component PAGE = Component.m_237113_(
         String.valueOf(
            new char[]{
               'I',
               'n',
               ' ',
               't',
               'h',
               'e',
               ' ',
               'b',
               'e',
               'g',
               'i',
               'n',
               'n',
               'i',
               'n',
               'g',
               ',',
               ' ',
               'o',
               'n',
               'l',
               'y',
               ' ',
               'd',
               'a',
               'r',
               'k',
               'n',
               'e',
               's',
               's',
               '.',
               '\n',
               'W',
               'h',
               'y',
               ' ',
               'd',
               'i',
               'd',
               ' ',
               's',
               'h',
               'e',
               ' ',
               'l',
               'e',
               'a',
               'v',
               'e',
               '?',
               '\n',
               'D',
               'a',
               'u',
               'g',
               'h',
               't',
               'e',
               'r',
               ' ',
               'o',
               'f',
               ' ',
               'D',
               'a',
               'r',
               'k',
               'n',
               'e',
               's',
               's',
               ',',
               ' ',
               'B',
               'r',
               'i',
               'n',
               'g',
               'e',
               'r',
               ' ',
               'o',
               'f',
               ' ',
               'L',
               'i',
               'g',
               'h',
               't',
               '.',
               '\n',
               'O',
               'h',
               ' ',
               'h',
               'o',
               'w',
               ' ',
               't',
               'h',
               'e',
               ' ',
               'V',
               'o',
               'i',
               'd',
               ' ',
               'm',
               'u',
               's',
               't',
               ' ',
               'h',
               'a',
               't',
               'e',
               ' ',
               'y',
               'o',
               'u',
               ',',
               '\n',
               'A',
               'r',
               'a',
               't',
               'h',
               'y',
               'l',
               'l',
               ',',
               ' ',
               'A',
               'r',
               'a',
               't',
               'h',
               'y',
               'l',
               'l',
               ',',
               ' ',
               'A',
               'r',
               'a',
               't',
               'h',
               'y',
               'l',
               'l',
               '.',
               '.',
               '.'
            }
         )
      )
      .m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("alt")));
   public static Component PAGE2 = PAGE.m_6881_().m_130940_(ChatFormatting.OBFUSCATED);
   public static Component DARKNESS = Component.m_237113_("Darkness").m_130948_(Style.f_131099_.m_131150_(ResourceLocation.withDefaultNamespace("alt")));
   public static Component DARKNESS2 = DARKNESS.m_6881_().m_130940_(ChatFormatting.OBFUSCATED);
   public static ResourceLocation LECTERN_LOCATION = IronsSpellbooks.id("textures/entity/lectern/ruined_book.png");

   public RuinedBookItem(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public List<Component> getPages(ItemStack stack) {
      Player player = MinecraftInstanceHelper.getPlayer();
      return player != null && player.m_21023_((MobEffect)MobEffectRegistry.PLANAR_SIGHT.get())
         ? List.of(PAGE, DARKNESS, DARKNESS, DARKNESS, DARKNESS, DARKNESS, DARKNESS, DARKNESS, DARKNESS, DARKNESS)
         : List.of(PAGE2, DARKNESS2, DARKNESS2, DARKNESS2, DARKNESS2, DARKNESS2, DARKNESS2, DARKNESS2, DARKNESS2, DARKNESS2);
   }

   public InteractionResult m_6225_(UseOnContext pContext) {
      Level level = pContext.m_43725_();
      BlockPos blockpos = pContext.m_8083_();
      BlockState blockstate = level.m_8055_(blockpos);
      if (blockstate.m_60713_(Blocks.f_50624_)) {
         return LecternBlock.m_269125_(pContext.m_43723_(), level, blockpos, blockstate, pContext.m_43722_())
            ? InteractionResult.m_19078_(level.f_46443_)
            : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public Optional<ResourceLocation> simpleTextureOverride(ItemStack stack) {
      return Optional.of(LECTERN_LOCATION);
   }
}
