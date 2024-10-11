package com.arcade.galactic.racer.presentation.navigation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.Savage.Trail.presentation.navigatio.Screen
import com.Savage.Trail.presentation.navigatio.navigatePopUpInclusive
import com.Savage.Trail.presentation.navigatio.navigateSingleTop
import com.arcade.galactic.racer.data.Prefs
import com.arcade.galactic.racer.data.SoundManager
import com.arcade.galactic.racer.domain.Level
import com.arcade.galactic.racer.presentation.view.ExitScreen
import com.arcade.galactic.racer.presentation.view.GameScreen
import com.arcade.galactic.racer.presentation.view.LevelScreen
import com.arcade.galactic.racer.presentation.view.MainMenuScreen
import com.arcade.galactic.racer.presentation.view.OptionsScreen
import com.arcade.galactic.racer.presentation.view.SplashScreen
import com.arcade.galactic.racer.ui.theme.GalacticRacerTheme
import com.arcade.galactic.racer.ui.theme.myfont

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        Prefs.init(applicationContext)
        SoundManager.init(applicationContext)
        SoundManager.playMusic()
        setContent {
            GalacticRacerTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.SplashScreen.route
                ) {
                    composable(Screen.SplashScreen.route) {
                        SplashScreen(navController::navigatePopUpInclusive)
                    }
                    composable(Screen.MainMenuScreen.route) {
                        MainMenuScreen(navController::navigateSingleTop)
                    }
                    composable(Screen.LevelScreen.route) {
                        LevelScreen(
                            onBack = navController::popBackStack,
                            onChooseLevel = { level ->
                                navController.navigateSingleTop(Screen.GameScreen, level)
                            }

                        )
                    }
                    composable(Screen.OptionScreen.route) {
                        OptionsScreen(onBack = navController::popBackStack)
                    }
                    composable(Screen.ExitScreen.route) {
                        ExitScreen (
                            onBack = navController::popBackStack
                        )
                    }
                    composable(Screen.GameScreen.route) { backStackEntry ->
                        val level = Screen.GameScreen.getLevel(backStackEntry)
                        val levels = Level.entries.toTypedArray()

                        GameScreen(
                            level,
                            onBack = navController::popBackStack,
                            onNextLevel = {
                                navController.navigate("game_screen/${levels[level.ordinal+ 1].name}") {
                                    // Pop the back stack to clear the previous instance of the game screen
                                    popUpTo("game_screen/${levels[level.ordinal+ 1].name}") { inclusive = true }
                                }
                            },
                            restartGame = {
                                navController.navigate("game_screen/${level.name}") {
                                    // Pop the back stack to clear the previous instance of the game screen
                                    popUpTo("game_screen/${level.name}") { inclusive = true }
                                }
                            },
                            onNext =  navController::navigateSingleTop

                        )
                    }


                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SoundManager.resumeSound()
    }

    override fun onPause() {
        super.onPause()
        SoundManager.pauseSound()
        SoundManager.pauseMusic()
        SoundManager.pauseGameMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.onDestroy()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.Unspecified,
    outlineColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    outlineDrawStyle: Stroke = Stroke(
        width = 20f,
    ),
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier.semantics { invisibleToUser() },
            color = outlineColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = myfont,
            letterSpacing = letterSpacing,
            textDecoration = null,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style.copy(
                shadow = null,
                drawStyle = outlineDrawStyle,
            ),
        )

        Text(
            text = text,
            color = fillColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = myfont,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
}