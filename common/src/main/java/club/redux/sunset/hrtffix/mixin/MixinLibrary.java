// These code is based on https://github.com/bytzo/mc-265514/blob/main/src/main/java/net/bytzo/mc_265514/mixins/LibraryMixin.java

package club.redux.sunset.hrtffix.mixin;

import com.mojang.blaze3d.audio.Library;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.openal.SOFTHRTF;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.IntBuffer;

@Mixin(value = Library.class)
public class MixinLibrary {
    @Shadow
    private long currentDevice;

    @Shadow
    private long context;

    @Unique
    private ALCCapabilities deviceCapabilities;
    @Unique
    private boolean hrtfEnabled = true;

    @ModifyArg(method = "init(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/system/MemoryStack;callocInt(I)Ljava/nio/IntBuffer;"), index = 0)
    private int modifyIntBufferSize(int originalSize) {
        return originalSize + 6;
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;put(I)Ljava/nio/IntBuffer;", ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void exportInitHrtfLocals(String string, boolean bl, CallbackInfo ci, ALCCapabilities aLCCapabilities, MemoryStack memoryStack) {
        this.deviceCapabilities = aLCCapabilities;
        this.hrtfEnabled = bl;
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;put(I)Ljava/nio/IntBuffer;", ordinal = 2))
    private IntBuffer redirectAndAddHrtfAttributes(IntBuffer intBuffer, int originalValue) {
        try {
            if (ALC10.alcGetInteger(currentDevice, SOFTHRTF.ALC_NUM_HRTF_SPECIFIERS_SOFT) > 0) {
                var setHrtf = deviceCapabilities.ALC_SOFT_HRTF && hrtfEnabled;
                intBuffer.put(SOFTHRTF.ALC_HRTF_SOFT).put(setHrtf ? ALC10.ALC_TRUE : ALC10.ALC_FALSE);
                intBuffer.put(SOFTHRTF.ALC_HRTF_ID_SOFT).put(0);
            }
            intBuffer.put(EXTEfx.ALC_MAX_AUXILIARY_SENDS).put(4);
            return intBuffer.put(originalValue);
        } finally {
            this.deviceCapabilities = null;
        }
    }
    
    @Inject(method = "setHrtf(Z)V", at = @At("HEAD"), cancellable = true)
    private void cancelSetHrtf(boolean bl, CallbackInfo ci) {
        ci.cancel();
    }
}