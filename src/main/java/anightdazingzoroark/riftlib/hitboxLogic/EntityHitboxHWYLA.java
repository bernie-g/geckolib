package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.RiftLib;
import com.google.common.base.Strings;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nonnull;
import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.getRenderString;

@WailaPlugin(RiftLib.ModID)
public class EntityHitboxHWYLA implements IWailaPlugin {
    @Override
    public void register(IWailaRegistrar registrar) {
        EntityHitboxProvider hitboxProvider = new EntityHitboxProvider();
        registrar.registerHeadProvider(hitboxProvider, EntityHitbox.class);
        registrar.registerBodyProvider(hitboxProvider, EntityHitbox.class);
        registrar.registerTailProvider(hitboxProvider, EntityHitbox.class);
    }

    class EntityHitboxProvider implements IWailaEntityProvider {
        private int nhearts = 20;
        private final float maxhpfortext = 40f;

        @Nonnull
        @Override
        public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
            EntityHitbox hitbox = (EntityHitbox) entity;
            if (hitbox.getParent().hitboxUseHWYLA()) {
                currenttip.clear();
                EntityLiving parent = hitbox.getParentAsEntityLiving();
                if (parent != null) {
                    String entityName = TextFormatting.WHITE + parent.getName();
                    currenttip.add(entityName);
                }
            }
            return currenttip;
        }

        @Nonnull
        @Override
        public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
            EntityHitbox hitbox = (EntityHitbox) entity;
            if (hitbox.getParent().hitboxUseHWYLA()) {
                currenttip.clear();
                EntityLiving parent = hitbox.getParentAsEntityLiving();
                if (parent != null && config.getConfig("general.showhp")) {
                    this.nhearts = this.nhearts <= 0 ? 20 : this.nhearts;
                    float health = parent.getHealth() / 2.0f;
                    float maxhp = parent.getMaxHealth() / 2.0f;

                    if (parent.getMaxHealth() > this.maxhpfortext)
                        currenttip.add(String.format(I18n.translateToLocal("hud.msg.health") + ": %.0f / %.0f", parent.getHealth(), parent.getMaxHealth()));
                    else
                        currenttip.add(getRenderString("waila.health", String.valueOf(nhearts), String.valueOf(health), String.valueOf(maxhp)));
                }
            }
            return currenttip;
        }

        @Nonnull
        @Override
        public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
            EntityHitbox hitbox = (EntityHitbox) entity;
            if (hitbox.getParent().hitboxUseHWYLA()) {
                currenttip.clear();
                EntityLiving parent = hitbox.getParentAsEntityLiving();
                if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat) && !Strings.isNullOrEmpty(getEntityMod(parent)))
                    currenttip.add(String.format(FormattingConfig.modNameFormat, getEntityMod(parent)));
            }

            return currenttip;
        }

        private String getEntityMod(Entity entity) {
            EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
            if (entityEntry == null)
                return "Unknown";

            ModContainer container = ModIdentification.findModContainer(entityEntry.getRegistryName().getNamespace());
            return container.getName();
        }
    }
}
