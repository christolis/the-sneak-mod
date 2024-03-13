package com.christolis.mixin.client;

import com.christolis.SneakSprintClientMod;
import com.christolis.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    private static final int SPRITE_WIDTH = 18;
    private static final int SPRITE_HEIGHT = 18;

    @Final
    @Shadow
    private MinecraftClient client;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @Inject(method = "renderHotbar", at = @At(value = "TAIL"))
    private void afterRenderHotbar(float partialTicks, DrawContext drawContext, CallbackInfo info) {
        final PlayerEntity player = getCameraPlayer();
        Config config = SneakSprintClientMod.CONFIG_MANAGER.getConfig();

        if (!config.isEnabled()) {
            return;
        }

        if (!(player instanceof ClientPlayerEntity)) {
            return;
        }

        if (!shouldRenderHotbar(player)) {
            return;
        }
        final List<Sprite> sprites = new ArrayList<>();
        final Arm arm = player.getMainArm();
        final Vec3i iconsPos = getIconsCoords(arm, player);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        StatusEffectSpriteManager spriteManager =
                this.client.getStatusEffectSpriteManager();

        if (player.isSprinting() && config.isSprintEnabled()) {
            sprites.add(spriteManager.getSprite(StatusEffects.SPEED));
        }

        if (player.isSneaking() && config.isSneakEnabled()) {
            sprites.add(spriteManager.getSprite(StatusEffects.SLOWNESS));
        }

        renderIndicators(sprites, drawContext, arm, iconsPos.getX(), iconsPos.getY());
    }

    private void renderIndicators(List<Sprite> sprites, DrawContext drawContext, Arm arm, int x, int y) {
        for (int spriteIdx = 0; spriteIdx < sprites.size(); spriteIdx++) {
            final Sprite sprite = sprites.get(spriteIdx);
            final int dir = arm == Arm.RIGHT ? 1 : -1;
            final int offsetX = x + (SPRITE_WIDTH * spriteIdx) * dir;

            RenderSystem.setShaderTexture(0, sprite.getAtlasId());
            drawContext.drawSprite(offsetX, y, 0, SPRITE_WIDTH, SPRITE_HEIGHT, sprite);
        }
    }

    private Vec3i getIconsCoords(Arm arm, PlayerEntity player) {
        final int y = this.scaledHeight - 20;
        int x;

        if (arm == Arm.LEFT) {
            x = (this.scaledWidth - 182) / 2 - SPRITE_WIDTH - 6;
        } else {
            x = (this.scaledWidth + 182) / 2 + 6;
        }

        return new Vec3i(x, y, 0);
    }

    private boolean shouldRenderHotbar(PlayerEntity player) {
        return player.isSneaking() || player.isSprinting();
    }
}

