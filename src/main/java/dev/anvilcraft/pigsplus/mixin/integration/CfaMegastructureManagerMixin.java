package dev.anvilcraft.pigsplus.mixin.integration;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.megastructure.PlanetaryReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.ReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.StarReformerHandler;
import dev.dubhe.anvilcraft.block.entity.CfaMegastructureManager;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialRefactorOption;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialRefactorRegistry;
import dev.dubhe.anvilcraft.block.entity.megastructure.IMegastructureHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CfaMegastructureManager.class)
public abstract class CfaMegastructureManagerMixin {
    @Unique
    private static final String ACTIVE_MEGASTRUCTURE_NAME_KEY = "pigsplusActiveMegastructure";

    @Shadow
    private int activeMegastructureIndex;

    // Base mod resolves active megastructures by list index, which shifts when options change.
    @Unique
    private @Nullable String anvilcraft_pigsplus$activeMegastructureName;

    @Shadow
    private void registerHandler(IMegastructureHandler handler) {
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addPlanetaryReformer(CallbackInfo ci) {
        try {
            this.registerHandler(new PlanetaryReformerHandler());
            this.registerHandler(new StarReformerHandler());
        } catch (RuntimeException | LinkageError ex) {
            AnvilCraftPigsPlus.LOGGER.warn("Failed to register planetary reformer handler", ex);
        }
    }

    @Inject(method = "buildMegastructure", at = @At("HEAD"))
    private void captureBuiltMegastructureName(
        int index,
        CelestialForgingAnvilBlockEntity be,
        CallbackInfo ci
    ) {
        if (be.getCelestialBodyData() == null) return;
        List<CelestialRefactorOption> options = be.getClientVisibleOptions();
        if (index < 0 || index >= options.size()) return;

        CelestialRefactorOption option = options.get(index);
        if ("stellar_evolution_accelerator".equals(option.megastructure())) return;
        if (this.activeMegastructureIndex >= 0) return;
        this.anvilcraft_pigsplus$activeMegastructureName = option.megastructure();
    }

    @Inject(method = "getActiveOption", at = @At("HEAD"), cancellable = true)
    private void resolveActiveOptionByStableName(
        CelestialForgingAnvilBlockEntity be,
        CallbackInfoReturnable<CelestialRefactorOption> cir
    ) {
        String name = this.anvilcraft_pigsplus$activeMegastructureName;
        if (name == null || name.isBlank()) return;
        if (this.activeMegastructureIndex < 0 || be.getCelestialBodyData() == null) {
            cir.setReturnValue(null);
            return;
        }

        List<CelestialRefactorOption> options = CelestialRefactorRegistry.getOptions(
            be.getCelestialBodyData(),
            be.isAmplify(),
            be.getPlanetaryResourceSet()
        );
        for (int i = 0; i < options.size(); i++) {
            if (name.equals(options.get(i).megastructure())) {
                this.activeMegastructureIndex = i;
                cir.setReturnValue(options.get(i));
                return;
            }
        }
        cir.setReturnValue(null);
    }

    @Inject(method = "clearMegastructure", at = @At("TAIL"))
    private void clearActiveMegastructureName(CelestialForgingAnvilBlockEntity be, CallbackInfo ci) {
        this.anvilcraft_pigsplus$activeMegastructureName = null;
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveActiveMegastructureName(
        CompoundTag tag,
        HolderLookup.Provider registries,
        CallbackInfo ci
    ) {
        this.anvilcraft_pigsplus$writeActiveMegastructureName(tag);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadActiveMegastructureName(
        CompoundTag tag,
        HolderLookup.Provider registries,
        CallbackInfo ci
    ) {
        this.anvilcraft_pigsplus$readActiveMegastructureName(tag);
    }

    @Inject(method = "writeUpdateTag", at = @At("TAIL"))
    private void writeActiveMegastructureName(
        CompoundTag tag,
        HolderLookup.Provider registries,
        CallbackInfo ci
    ) {
        this.anvilcraft_pigsplus$writeActiveMegastructureName(tag);
    }

    @Inject(method = "readUpdateTag", at = @At("TAIL"))
    private void readActiveMegastructureName(
        CompoundTag tag,
        HolderLookup.Provider registries,
        CallbackInfo ci
    ) {
        this.anvilcraft_pigsplus$readActiveMegastructureName(tag);
    }

    @Unique
    private void anvilcraft_pigsplus$writeActiveMegastructureName(CompoundTag tag) {
        String name = this.anvilcraft_pigsplus$activeMegastructureName;
        tag.putString(ACTIVE_MEGASTRUCTURE_NAME_KEY, name == null ? "" : name);
    }

    @Unique
    private void anvilcraft_pigsplus$readActiveMegastructureName(CompoundTag tag) {
        String name = tag.getString(ACTIVE_MEGASTRUCTURE_NAME_KEY);
        this.anvilcraft_pigsplus$activeMegastructureName = name.isEmpty() ? null : name;
    }

    @Inject(method = "syncLaserRequirements", at = @At("TAIL"))
    private void syncPlanetaryReformerLaserRequirements(CelestialForgingAnvilBlockEntity anvil, CallbackInfo ci) {
        IMegastructureHandler active = ((CfaMegastructureManager) (Object) this).getActiveHandler(anvil);
        if (active instanceof ReformerHandler handler) {
            handler.syncLaserRequirements(anvil);
        }
    }
}
