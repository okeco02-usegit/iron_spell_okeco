package io.redspace.ironsspellbooks.setup;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand.CursedArmorStandModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.loading.FMLLoader;

@EventBusSubscriber(modid = "irons_spellbooks", bus = Bus.FORGE, value = Dist.CLIENT)
public class PlayerAnimationTrigger {
   @SubscribeEvent
   public static void onChatReceived(ClientChatReceivedEvent event) {
      if (!FMLLoader.isProduction()) {
         String str = event.getMessage().getString();
         if (str.contains("armorstand")) {
            int id = 0;
            int i = str.indexOf(91);
            double[] ad = new double[3];

            for (int c = 0; c < 100; c++) {
               int j = str.indexOf(44, i + 1);
               if (j < 0) {
                  ad[id] = Double.parseDouble(str.substring(i + 1, str.indexOf(93)));
                  break;
               }

               ad[id++] = Double.parseDouble(str.substring(i + 1, j));
               i = j;
            }

            CursedArmorStandModel.rightArmPos = ad;
         }
      }

      if (event.getMessage().m_240452_(Component.m_237113_("waving"))) {
         Player player = Minecraft.m_91087_().f_91073_.m_46003_(event.getSender());
         if (player == null) {
            return;
         }

         ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>)PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayer)player)
            .get(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animation"));
         if (animation != null) {
            animation.setAnimation(
               new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "waving")))
            );
         }
      }
   }
}
