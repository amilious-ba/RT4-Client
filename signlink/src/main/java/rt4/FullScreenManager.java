package rt4;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

import java.awt.*;
import java.lang.reflect.Field;

@OriginalClass("signlink!e")
public final class FullScreenManager {

	public static boolean borderlessFullscreen = false;

	@OriginalMember(owner = "signlink!e", name = "b", descriptor = "Ljava/awt/DisplayMode;")
	private DisplayMode previousDisplayMode;

	@OriginalMember(owner = "signlink!e", name = "a", descriptor = "Ljava/awt/GraphicsDevice;")
	private GraphicsDevice device;

	@OriginalMember(owner = "signlink!e", name = "<init>", descriptor = "()V")
	public FullScreenManager() throws Exception {
		@Pc(3) GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.device = env.getDefaultScreenDevice();
		if (!this.device.isFullScreenSupported()) {
			@Pc(15) GraphicsDevice[] devices = env.getScreenDevices();
			for (@Pc(19) int i = 0; i < devices.length; i++) {
				@Pc(27) GraphicsDevice d = devices[i];
				if (d != null && d.isFullScreenSupported()) {
					this.device = d;
					return;
				}
			}
			throw new Exception();
		}
	}

	@OriginalMember(owner = "signlink!e", name = "a", descriptor = "(Ljava/awt/Frame;B)V")
	private void setFullScreenWindow(@OriginalArg(0) Frame frame) {
		@Pc(1) boolean wasValid = false;
		try {
			@Pc(6) Field valid = Class.forName("sun.awt.Win32GraphicsDevice").getDeclaredField("valid");
			valid.setAccessible(true);
			@Pc(16) boolean v = (Boolean) valid.get(this.device);
			if (v) {
				valid.set(this.device, Boolean.FALSE);
				wasValid = true;
			}
		} catch (@Pc(27) Throwable ex) {
		}
		try {
			this.device.setFullScreenWindow(frame);
		} finally {
			if (wasValid) {
				try {
					@Pc(66) Field valid = Class.forName("sun.awt.Win32GraphicsDevice").getDeclaredField("valid");
					valid.set(this.device, Boolean.TRUE);
				} catch (@Pc(73) Throwable ex) {
				}
			}
		}
	}

	@OriginalMember(owner = "signlink!e", name = "a", descriptor = "(IIIILjava/awt/Frame;I)V")
	public final void enter(@OriginalArg(1) int refreshRate, @OriginalArg(2) int bitDepth, @OriginalArg(3) int height, @OriginalArg(4) Frame frame, @OriginalArg(5) int width) {
		this.previousDisplayMode = this.device.getDisplayMode();
		if (this.previousDisplayMode == null) {
			throw new NullPointerException();
		}

		frame.setUndecorated(true);
		frame.enableInputMethods(false);

		String os = System.getProperty("os.name", "").toLowerCase();
		boolean useBorderless = borderlessFullscreen || os.contains("win");

		if (useBorderless) {
			Rectangle bounds = this.device.getDefaultConfiguration().getBounds();
			frame.setBounds(bounds);
			frame.setAlwaysOnTop(true);
			frame.setVisible(true);
			frame.toFront();
			return;
		}

		// Linux exclusive path (unchanged)
		try {
			this.device.setFullScreenWindow(null);
		} catch (Exception ignored) {
		}
		this.setFullScreenWindow(frame);

		DisplayMode current = this.device.getDisplayMode();
		if (current.getWidth() != width || current.getHeight() != height) {
			try {
				int rate = (refreshRate > 0) ? refreshRate : current.getRefreshRate();
				int depth = (bitDepth > 0) ? bitDepth : current.getBitDepth();
				this.device.setDisplayMode(new DisplayMode(width, height, depth, rate));
			} catch (Exception e) {
				System.err.println("Failed to set display mode " + width + "x" + height + ": " + e);
			}
		}
	}

	@OriginalMember(owner = "signlink!e", name = "a", descriptor = "(Z)[I")
	public final int[] getDisplayModes() {
		@Pc(9) DisplayMode[] displayModes = this.device.getDisplayModes();
		@Pc(15) int[] result = new int[displayModes.length << 2];
		for (@Pc(17) int i = 0; i < displayModes.length; i++) {
			result[i << 2] = displayModes[i].getWidth();
			result[(i << 2) + 1] = displayModes[i].getHeight();
			result[(i << 2) + 2] = displayModes[i].getBitDepth();
			result[(i << 2) + 3] = displayModes[i].getRefreshRate();
		}
		return result;
	}

	@OriginalMember(owner = "signlink!e", name = "a", descriptor = "(I)V")
	public final void exit() {
		try {
			this.device.setFullScreenWindow(null);
		} catch (Exception ignored) {
		}

		if (this.previousDisplayMode != null) {
			try {
				this.device.setDisplayMode(this.previousDisplayMode);
			} catch (Exception ignored) {
			}
			this.previousDisplayMode = null;
		}
	}
}
