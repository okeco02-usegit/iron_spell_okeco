package io.redspace.ironsspellbooks.entity.mobs.wizards.cultist;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class CultistRenderer extends AbstractSpellCastingMobRenderer {
   public CultistRenderer(Context context) {
      super(context, new CultistModel());
   }
}
