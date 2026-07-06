package io.redspace.ironsspellbooks.item.weapons.pyrium_staff;

import io.redspace.ironsspellbooks.render.ClientStaffItemExtensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public class PyriumStaffClientExtensions extends ClientStaffItemExtensions {
   public BlockEntityWithoutLevelRenderer getCustomRenderer() {
      return new PyriumStaffRenderer(Minecraft.m_91087_().m_91291_(), Minecraft.m_91087_().m_167973_());
   }
}
