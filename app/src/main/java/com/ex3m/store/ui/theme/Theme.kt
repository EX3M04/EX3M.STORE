package com.ex3m.store.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ex3m.store.data.prefs.SettingsPreferences
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Composable
fun Theme(
    darkTheme: Boolean = androidx.compose.material3.isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val activity = view.findViewTreeActivity() ?: return

    // Get settings
    val settingsPreferences = EntryPointAccessors.fromApplication(
        context.applicationContext,
        SettingsPreferencesEntryPoint::class.java
    ).settingsPreferences()

    val useDynamicColors = remember { mutableStateOf(true) }
    val customPrimaryColor = remember { mutableStateOf(0xFF6750A4L) }
    val customSecondaryColor = remember { mutableStateOf(0xFF03DAC5L) }
    val customTertiaryColor = remember { mutableStateOf(0xFF018782L) }

    // Observe settings
    androidx.compose.runtime.LaunchedEffect(Unit) {
        settingsPreferences.useDynamicColors().subscribe { useDynamicColors.value = it }
        settingsPreferences.customPrimaryColor().subscribe { customPrimaryColor.value = it }
        settingsPreferences.customSecondaryColor().subscribe { customSecondaryColor.value = it }
        settingsPreferences.customTertiaryColor().subscribe { customTertiaryColor.value = it }
    }

    // Generate color schemes
    val lightColorScheme = if (useDynamicColors.value && !darkTheme) {
        dynamicLightColorScheme(activity)?.let { scheme ->
            scheme.copy(
                primary = if (customPrimaryColor.value != 0L) Color(customPrimaryColor.value.toInt()) else scheme.primary,
                secondary = if (customSecondaryColor.value != 0L) Color(customSecondaryColor.value.toInt()) else scheme.secondary,
                tertiary = if (customTertiaryColor.value != 0L) Color(customTertiaryColor.value.toInt()) else scheme.tertiary
            )
        } ?: LightColorScheme
    } else {
        LightColorScheme.copy(
            primary = if (customPrimaryColor.value != 0L) Color(customPrimaryColor.value.toInt()) else LightColorScheme.primary,
            secondary = if (customSecondaryColor.value != 0L) Color(customSecondaryColor.value.toInt()) else LightColorScheme.secondary,
            tertiary = if (customTertiaryColor.value != 0L) Color(customTertiaryColor.value.toInt()) else LightColorScheme.tertiary
        )
    }

    val darkColorScheme = if (useDynamicColors.value && darkTheme) {
        dynamicDarkColorScheme(activity)?.let { scheme ->
            scheme.copy(
                primary = if (customPrimaryColor.value != 0L) Color(customPrimaryColor.value.toInt()) else scheme.primary,
                secondary = if (customSecondaryColor.value != 0L) Color(customSecondaryColor.value.toInt()) else scheme.secondary,
                tertiary = if (customTertiaryColor.value != 0L) Color(customTertiaryColor.value.toInt()) else scheme.tertiary
            )
        } ?: DarkColorScheme
    } else {
        DarkColorScheme.copy(
            primary = if (customPrimaryColor.value != 0L) Color(customPrimaryColor.value.toInt()) else DarkColorScheme.primary,
            secondary = if (customSecondaryColor.value != 0L) Color(customSecondaryColor.value.toInt()) else DarkColorScheme.secondary,
            tertiary = if (customTertiaryColor.value != 0L) Color(customTertiaryColor.value.toInt()) else DarkColorScheme.tertiary
        )
    }

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    // Apply edge-to-edge
    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Entry point for Hilt
@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface SettingsPreferencesEntryPoint {
    fun settingsPreferences(): SettingsPreferences
}

// Light Color Scheme (Fallback)
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Color(0xFF6750A4),
    primaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFF625B71),
    secondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF7D5260),
    tertiaryContainer = Color(0xFFFFD8E4),
    surface = Color(0xFFFFFBFE),
    surfaceContainerLow = Color(0xFFF7F2FA),
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0E9),
    background = Color(0xFFFFFBFE),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF21005D),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF1D192B),
    onTertiary = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xFF31111D),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFD0BCFF)
)

// Dark Color Scheme (Fallback)
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFFD0BCFF),
    primaryContainer = Color(0xFF4F378B),
    secondary = Color(0xFFCCC2DC),
    secondaryContainer = Color(0xFF4A4458),
    tertiary = Color(0xFFEFB8C8),
    tertiaryContainer = Color(0xFF633B48),
    surface = Color(0xFF1C1B1F),
    surfaceContainerLow = Color(0xFF1C1B1F),
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerHighest = Color(0xFF36343B),
    background = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF381E72),
    onPrimaryContainer = Color(0xFFEADDFF),
    onSecondary = Color(0xFF332D41),
    onSecondaryContainer = Color(0xFFE8DEF8),
    onTertiary = Color(0xFF492532),
    onTertiaryContainer = Color(0xFFFFD8E4),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF6750A4)
)

// Typography
val Typography = androidx.compose.material3.Typography(
    displayLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp
    ),
    displayMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Helper extension to find Activity from View
private fun android.view.View.findViewTreeActivity(): Activity? {
    var view: android.view.View? = this
    while (view != null) {
        val context = view.context
        if (context is Activity) {
            return context
        }
        view = (view.parent as? android.view.View)
    }
    return null
}