package com.mrp_v2.biomeborderviewer.config;

import org.apache.commons.lang3.tuple.Pair;

import com.mrp_v2.biomeborderviewer.util.Color;
import com.mrp_v2.biomeborderviewer.visualize.VisualizeBorders;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class BiomeBorderViewerConfig {

	public static class Client {

		public final IntValue borderA_R;
		public final IntValue borderA_G;
		public final IntValue borderA_B;
		public final IntValue borderA_A;

		public final IntValue borderB_R;
		public final IntValue borderB_G;
		public final IntValue borderB_B;
		public final IntValue borderB_A;

		public final IntValue horizontalViewRange;
		public final IntValue verticalViewRange;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("biome border viewer client settings").push("client");

			final String bAR = join(BORDER_A_KEY, RED_KEY);
			borderA_R = builder
					.comment("The red value of the line's color when the 2 biomes have similar temperatures.")
					.translation(TRANSLATION_KEY + bAR).defineInRange(bAR, 0, 0, 255);

			final String bAG = join(BORDER_A_KEY, GREEN_KEY);
			borderA_G = builder
					.comment("The green value of the line's color when the 2 biomes have similar temperatures.")
					.translation(TRANSLATION_KEY + bAG).defineInRange(bAG, 255, 0, 255);

			final String bAB = join(BORDER_A_KEY, BLUE_KEY);
			borderA_B = builder
					.comment("The blue value of the line's color when the 2 biomes have similar temperatures.")
					.translation(TRANSLATION_KEY + bAB).defineInRange(bAB, 0, 0, 255);

			final String bAA = join(BORDER_A_KEY, ALPHA_KEY);
			borderA_A = builder.comment(
					"The alpha (transparency) value of the line's color when the 2 biomes have similar temperatures.")
					.translation(TRANSLATION_KEY + bAA).defineInRange(bAA, 64, 0, 255);

			final String bBR = join(BORDER_B_KEY, RED_KEY);
			borderB_R = builder
					.comment("The red value of the line's color when the 2 biomes have unsimilar temperatures.")
					.translation(TRANSLATION_KEY + bBR).defineInRange(bBR, 255, 0, 255);

			final String bBG = join(BORDER_B_KEY, GREEN_KEY);
			borderB_G = builder
					.comment("The green value of the line's color when the 2 biomes have unsimilar temperatures.")
					.translation(TRANSLATION_KEY + bBG).defineInRange(bBG, 0, 0, 255);

			final String bBB = join(BORDER_B_KEY, BLUE_KEY);
			borderB_B = builder
					.comment("The blue value of the line's color when the 2 biomes have unsimilar temperatures.")
					.translation(TRANSLATION_KEY + bBB).defineInRange(bBB, 0, 0, 255);

			final String bBA = join(BORDER_B_KEY, ALPHA_KEY);
			borderB_A = builder.comment(
					"The alpha (transparency) value of the line's color when the 2 biomes have unsimilar temperatures.")
					.translation(TRANSLATION_KEY + bBA).defineInRange(bBA, 64, 0, 255);

			final String hVR = "horizontalViewRange";
			horizontalViewRange = builder
					.comment("The horizontal distance to show biome borders around the player.\n"
							+ "Like render distance, but for the biome border.\nHigh values may impact performance.")
					.translation(TRANSLATION_KEY + hVR).defineInRange(hVR, 2, 1, 32);

			final String vVR = "verticalViewRange";
			verticalViewRange = builder
					.comment("The vertical distance to show biome borders above and below the player.\n"
							+ "High values may impact performance.")
					.translation(TRANSLATION_KEY + vVR).defineInRange(vVR, 2, 1, 16);

			builder.pop();
		}
	}

	private static final String TRANSLATION_KEY = "biomeborderviewer.configgui.";
	private static final String BORDER_A_KEY = "border.a";
	private static final String BORDER_B_KEY = "border.b";
	private static final String RED_KEY = "red";
	private static final String GREEN_KEY = "green";
	private static final String BLUE_KEY = "blue";
	private static final String ALPHA_KEY = "alpha";

	private static String join(String a, String b) {
		return a + "." + b;
	}

	public static final ForgeConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static Color getColorA() {
		return new Color(CLIENT.borderA_R.get(), CLIENT.borderA_G.get(), CLIENT.borderA_B.get(),
				CLIENT.borderA_A.get());
	}

	public static Color getColorB() {
		return new Color(CLIENT.borderB_R.get(), CLIENT.borderB_G.get(), CLIENT.borderB_B.get(),
				CLIENT.borderB_A.get());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfig.Reloading configEvent) {
		VisualizeBorders.loadConfigSettings();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		VisualizeBorders.loadConfigSettings();
	}
}