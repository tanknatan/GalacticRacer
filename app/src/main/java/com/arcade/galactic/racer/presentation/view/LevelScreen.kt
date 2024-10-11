package com.arcade.galactic.racer.presentation.view

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcade.galactic.racer.R
import com.arcade.galactic.racer.data.SoundManager
import com.arcade.galactic.racer.domain.Level
import com.arcade.galactic.racer.presentation.navigation.OutlinedText


@Composable
fun LevelScreen(     onBack: () -> Unit,onChooseLevel: (Level) -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("LevelPreferences", Context.MODE_PRIVATE)

    // Используем LevelManager с sharedPreferences
    val levelManager = remember { LevelManager(sharedPreferences) }

    LaunchedEffect(Unit) {
        SoundManager.pauseGameMusic()
        SoundManager.resumeMusic()
    }

    Box {
        Image(
            painter = painterResource(id = R.drawable.background), // Замените на ваше фоновое изображение
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(

                            onClick = {
                                SoundManager.playSound()
                                onBack()
                            }
                        )
                )
                Spacer(modifier = Modifier.width(70.dp))
                OutlinedText(
                    text = "Levels",
                    outlineColor = Color.Black,
                    fillColor = Color.White,
                    fontSize = 40.sp
                )


            }

            Spacer(modifier = Modifier.height(0.dp))


            for (row in 0 until 15) { // 5 rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 3) { // 9 columns per row
                        val level = row * 3 + col + 1 // Adjusting for 9 levels per row
                        val isUnlocked = levelManager.isLevelUnlocked(level)

                        Box(
                            modifier = Modifier
                                .size(80.dp,32.dp)  // Size of the level button
                                .padding(0.dp) // Slight padding to separate buttons
                                //.clip(RoundedCornerShape(50.dp)) // Rounded corner shape
                                .clickable(enabled = isUnlocked) {
                                    if (isUnlocked) {
                                        SoundManager.playSound()
                                        onChooseLevel(Level.entries[level - 1])
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val backgroundImage = painterResource(id = R.drawable.rec_act)

                            Image(
                                painter = backgroundImage,
                                contentDescription = "Level $level",
                                modifier = Modifier.fillMaxSize()
                            )

                            OutlinedText(
                                text = "$level.level",
                                outlineColor = if (isUnlocked) Color(0xFFC34E70) else Color.Black,
                                fillColor = if (isUnlocked) Color.White else Color.Gray,
                                fontSize = 18.sp, // Font size for numbers
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Preview
@Composable
fun LevelScreenPreview() {
    LevelScreen(onBack = {}, onChooseLevel = {})
}

class LevelManager(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val UNLOCKED_LEVELS_KEY = "unlockedLevels"
    }

    fun getUnlockedLevels(): Int {
        return sharedPreferences.getInt(
            UNLOCKED_LEVELS_KEY,
            1
        ) // По умолчанию разблокирован только 1 уровень
    }

    fun unlockNextLevel(levelCompleted: Int) {
        val currentUnlockedLevels = getUnlockedLevels()
        if (levelCompleted >= currentUnlockedLevels) {
            sharedPreferences.edit().putInt(UNLOCKED_LEVELS_KEY, levelCompleted + 1).apply()
        }
    }

    fun isLevelUnlocked(level: Int): Boolean {
        return level <= getUnlockedLevels()
    }
}