package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class DebugWizardSpellName extends GeoRenderLayer<AbstractSpellCastingMob> {
   Font font = Minecraft.m_91087_().f_91062_;

   public DebugWizardSpellName(GeoEntityRenderer entityRendererIn) {
      super(entityRendererIn);
   }
}
