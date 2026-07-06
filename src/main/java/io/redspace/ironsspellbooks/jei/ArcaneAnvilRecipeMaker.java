package io.redspace.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.UpgradeOrbTypeData;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public final class ArcaneAnvilRecipeMaker {
   private ArcaneAnvilRecipeMaker() {
   }

   static List<ArcaneAnvilJeiRecipe> getRecipes(IVanillaRecipeFactory vanillaRecipeFactory, JeiPlugin.ItemFinder itemFinder) {
      return Stream.of(getScrollRecipes(itemFinder), getImbueRecipes(itemFinder), getUpgradeRecipes(itemFinder), getAffinityAttuneRecipes(itemFinder))
         .flatMap(x -> (Stream<? extends ArcaneAnvilJeiRecipe>)x)
         .toList();
   }

   private static Stream<ArcaneAnvilJeiRecipe> getScrollRecipes(JeiPlugin.ItemFinder itemFinder) {
      return ServerConfigs.SPEC.isLoaded() && !ServerConfigs.SCROLL_MERGING.get()
         ? Stream.empty()
         : SpellRegistry.getEnabledSpells()
            .stream()
            .sorted(Comparator.comparing(AbstractSpell::getSpellId))
            .flatMap(spell -> IntStream.rangeClosed(spell.getMinLevel(), spell.getMaxLevel() - 1).mapToObj(i -> new ArcaneAnvilJeiRecipe(spell, i)));
   }

   private static Stream<ArcaneAnvilJeiRecipe> getImbueRecipes(JeiPlugin.ItemFinder itemFinder) {
      return itemFinder.imbueable.stream().map(item -> new ArcaneAnvilJeiRecipe(item, (AbstractSpell)null));
   }

   private static Stream<ArcaneAnvilJeiRecipe> getUpgradeRecipes(JeiPlugin.ItemFinder itemFinder) {
      return BuiltInRegistries.f_257033_
         .m_123024_()
         .filter(item -> UpgradeOrbTypeData.has(item.m_7968_()))
         .flatMap(upgradeOrb -> itemFinder.upgradeable.stream().map(item -> new ArcaneAnvilJeiRecipe(item, upgradeOrb)));
   }

   private static Stream<ArcaneAnvilJeiRecipe> getAffinityAttuneRecipes(JeiPlugin.ItemFinder itemFinder) {
      return SpellRegistry.getEnabledSpells().stream().sorted(Comparator.comparing(AbstractSpell::getSpellId)).map(ArcaneAnvilJeiRecipe::new);
   }

   private static ItemStack getScrollStack(ItemStack stack, AbstractSpell spell, int spellLevel) {
      ItemStack scrollStack = stack.m_41777_();
      ISpellContainer.createScrollContainer(spell, spellLevel, scrollStack);
      return scrollStack;
   }
}
