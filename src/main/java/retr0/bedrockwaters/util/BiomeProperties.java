package retr0.bedrockwaters.util;

import static retr0.bedrockwaters.util.WaterPropertiesUtil.DEFAULT_WATER_FOG_DISTANCE;

/**
 * A record containing the water and water fog color for a biome. It can be initialized with Integer values or
 * String-based hexadecimal/octal values.
 *
 * @param waterColor The biome's water color.
 * @param waterFogColor The biome's water fog color.
 * @param waterFogDistance The biome's underwater fog distance.
 */
public record BiomeProperties(int waterColor, int waterFogColor, int waterFogDistance, float waterAlpha) {
    public BiomeProperties(String waterColor, String waterFogColor, int waterFogDistance, float waterAlpha) {
        this(Integer.decode(waterColor), Integer.decode(waterFogColor), waterFogDistance, waterAlpha);
    }

    public BiomeProperties(String waterColor, String waterFogColor, int waterFogDistance) {
        this(waterColor, waterFogColor, waterFogDistance, 0.65f);
    }

    public BiomeProperties(String waterColor, int waterFogDistance) { this(waterColor, waterColor, waterFogDistance); }

    public BiomeProperties(String waterColor) { this(waterColor, DEFAULT_WATER_FOG_DISTANCE); }

    // Practically, the distance the fog ends is ~1 block ahead of where it should be--we need to correct for that.
    public int waterFogDistance() { return waterFogDistance + 1; }
}
