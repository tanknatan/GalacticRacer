package com.arcade.galactic.racer.presentation.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Savage.Trail.presentation.navigatio.Screen
import com.arcade.galactic.racer.R
import com.arcade.galactic.racer.data.SoundManager
import com.arcade.galactic.racer.domain.Level
import com.arcade.galactic.racer.presentation.navigation.OutlinedText
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(
    level: Level,
    onBack: () -> Unit,
    restartGame: () -> Unit,
    onNextLevel: () -> Unit,
    onNext: (Screen) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("LevelPreferences", Context.MODE_PRIVATE)
    val levelManager = LevelManager(sharedPreferences)
    var score by remember { mutableIntStateOf(3) }
    var timeLeft by remember { mutableIntStateOf(level.time) }
    var rocketPosition by remember { mutableFloatStateOf(0f) }
    val rocketSpeed = 4f

    var bombs by remember { mutableStateOf(listOf<Bomb>()) }  // List of bombs
    var gameOver by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }
    var isMovingLeft by remember { mutableStateOf(false) }
    var isMovingRight by remember { mutableStateOf(false) }
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    var timeFormatted = String.format("%02d:%02d", minutes, seconds)  // Format as MM:SS

    // Countdown timer
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        } else {
            gameOver = true
        }
        timeFormatted = String.format("%02d:%02d", minutes, seconds)

    }
    LaunchedEffect(Unit) {
        SoundManager.pauseMusic()
        SoundManager.playGameMusic()
    }

    if (!gameOver && !levelCompleted) {
        // Game UI
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(id = R.drawable.background),
                    contentScale = ContentScale.FillBounds
                ),
            contentAlignment = Alignment.Center
        ) {
            val maxHeight = constraints.maxHeight
            val screenWidth = constraints.maxWidth * 0.3f

            val leftLimit =
                -screenWidth / 2 + 40f // Set left limit, taking into account rocket size
            val rightLimit = screenWidth / 2 - 40f // Set right limit

            LaunchedEffect(isMovingLeft, isMovingRight) {
                while (isMovingLeft || isMovingRight) {
                    delay(16L) // Frame delay for roughly 60 FPS

                    if (isMovingLeft) {
                        if (rocketPosition > leftLimit) {
                            rocketPosition -= rocketSpeed
                        } else {
                            rocketPosition =
                                leftLimit // Prevent rocket from going past the left limit
                            isMovingLeft = false
                        }
                    }

                    if (isMovingRight) {
                        if (rocketPosition < rightLimit) {
                            rocketPosition += rocketSpeed
                        } else {
                            rocketPosition =
                                rightLimit // Prevent rocket from going past the right limit
                            isMovingRight = false
                        }
                    }
                }
            }
            // Display coins and bombs
            bombs.forEach { bomb -> BombItem(bomb.x, bomb.y) }

            // Display score
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top
//            ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
                    .clickable(

                        onClick = {
                            SoundManager.playSound()
                            onBack()
                        }
                    )
            )
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center  // Align contents to the top center
            ) {
                Column(
                    modifier = Modifier

                ) {
                    OutlinedText(
                        text = "   LEVEL ${level.number}",
                        outlineColor = Color.Black,
                        fillColor = Color.White,
                        fontSize = 35.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier.size(140.dp, 60.dp),
                        contentAlignment = Alignment.Center // This aligns the background image centrally
                    ) {
                        // Background image
                        Image(
                            painter = painterResource(id = R.drawable.time),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Time left text positioned bottom-right
                        Text(
                            text = timeFormatted,  // Use the formatted string
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)  // Align text to the bottom-right corner
                                .padding(
                                    bottom = 13.dp,
                                    end = 36.dp
                                )  // Add padding to adjust the position
                        )
                    }

                }
            }



            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Back",
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clickable(

                        onClick = {
                            SoundManager.playSound()
                            onNext(Screen.OptionScreen)
                        }
                    )
            )


            // Display rocket
            Box(
                modifier = Modifier
                    .offset(x = rocketPosition.dp, y = (maxHeight * 0.1f).dp)
                    .size(70.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.racket),
                    contentDescription = "Rocket",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Movement buttons (left and right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { rocketPosition -= rocketSpeed },
                    modifier = Modifier
                        .pointerInteropFilter {
                            when (it.action) {
                                android.view.MotionEvent.ACTION_DOWN -> isMovingLeft = true
                                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> isMovingLeft =
                                    false
                            }
                            true
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(onClick = { rocketPosition += rocketSpeed },
                    modifier = Modifier
                        .pointerInteropFilter {
                            when (it.action) {
                                android.view.MotionEvent.ACTION_DOWN -> isMovingRight = true
                                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> isMovingRight =
                                    false
                            }
                            true
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Generate coins and bombs
            LaunchedEffect(Unit) {
                val (newCoins, newBombs) = generateCoinsAndBombs(
                    level,
                    leftLimit,
                    rightLimit,
                    screenWidth,
                    maxHeight
                )

                bombs += newBombs
                while (timeLeft > 0) {
                    delay(10000L)
                    val (newCoins, newBombs) = generateCoinsAndBombs(
                        level,
                        leftLimit,
                        rightLimit,
                        screenWidth,
                        maxHeight
                    )

                    bombs += newBombs
                }
            }

            // Smooth movement and collision detection
            LaunchedEffect(Unit) {
                while (timeLeft > 0) {
                    delay(16L)

                    // Move coins and bombs down smoothly


                    // Update bomb positions and reset bombs that go off-screen
                    bombs = bombs.map { bomb ->
                        val newBomb = bomb.copy(y = bomb.y + 4)
                        if (newBomb.y > maxHeight) {
                            // Generate a new bomb if it goes off the bottom of the screen
                            Bomb(
                                x = (-screenWidth.roundToInt()..screenWidth.roundToInt()).random()
                                    .toFloat(),
                                y = Random.nextFloat() * -maxHeight - 50f
                            )
                        } else {
                            newBomb
                        }
                    }

// Check for bomb collisions and filter out the bombs that hit the rocket
                    bombs = bombs.filter { bomb ->
                        val bombHit =
                            bomb.y > (maxHeight * 0.1f) && bomb.y < (maxHeight * 0.1f + 80) &&
                                    abs(bomb.x - rocketPosition) < 50

                        if (bombHit) {
                            score--
                            if (score <= 0) {
                                gameOver = true // End the game if player hits a bomb
                            }
                            false // Remove the bomb by not including it in the filtered list
                        } else {
                            true // Keep the bomb in the list if it didn't hit the rocket
                        }
                    }


                    // Check if player won the level
                    if (timeLeft <= 0) {
                        levelCompleted = true
                        levelManager.unlockNextLevel(level.ordinal + 1)
                    }
                }
            }
        }
    } else if (levelCompleted) {
        WinScreen(score, onNextLevel, level, onBack)
    } else {
        LossScreen(restartGame, level, onBack)
    }
}

// Coin data class to store position
data class Coin(val x: Float, val y: Float)

// Bomb data class to store position
data class Bomb(val x: Float, val y: Float)

// Coin and Bomb composables for drawing

@Composable
fun BombItem(x: Float, y: Float) {
    // List of 6 drawable resources
    val bombImages = listOf(
        R.drawable.bomb1, // Replace with your actual drawable names
        R.drawable.bomb2,
        R.drawable.bomb3,
        R.drawable.bomb4,
        R.drawable.bomb5,
        R.drawable.bomb6
    )

    // Remember the randomly selected image, so it doesn't change on recomposition
    val randomBombImage = remember {
        bombImages[Random.nextInt(bombImages.size)]
    }

    Image(
        modifier = Modifier
            .offset(x = x.dp, y = y.dp)
            .size(40.dp),
        painter = painterResource(id = randomBombImage),  // Use the remembered random bomb image
        contentDescription = "Bomb",
        contentScale = ContentScale.Fit
    )
}


// Function to generate initial set of coins and bombs based on the level
fun generateCoinsAndBombs(
    level: Level,
    left: Float,
    right: Float,
    screenWidth: Float,
    height: Int
): Pair<List<Coin>, List<Bomb>> {
    val numberOfCoins = 0 // Increase the number of coins per level
    val numberOfBombs = 1 + level.number // Increase the number of bombs per level
    val coins = List(numberOfCoins) {
        // Spawn coins at the top, with a y-coordinate above the screen, and x-position randomly
        Coin(
            (left.roundToInt()..right.roundToInt()).random().toFloat(),
            Random.nextFloat() * -height - 50f
        ) // Coins spawn off-screen above the visible area
    }
    val bombs = List(numberOfBombs) {
        // Spawn bombs similarly to coins
        Bomb(
            (-screenWidth.roundToInt()..screenWidth.roundToInt()).random().toFloat(),
            Random.nextFloat() * -height - 50f
        )
    }
    return Pair(coins, bombs)
}


@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun LossScreen(next: () -> Unit, level: Level, onBack: () -> Unit) {
    LaunchedEffect(Unit) {
        SoundManager.pauseGameMusic()
        SoundManager.resumeMusic()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background), // Background image
                contentScale = ContentScale.FillBounds
            ),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(id = R.drawable.lava), // Background image
                    contentScale = ContentScale.FillBounds
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            // Score Display Box
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .paint(
                        painter = painterResource(id = R.drawable.racket), // Button background
                        contentScale = ContentScale.FillBounds
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nostar), // Замените на ваш ресурс для кнопки
                    contentDescription = null,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier

                        .fillMaxWidth(0.8f)

                )

            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedText(
                text = "YOU CRASHED INTO",
                outlineColor = Color(0xFFBD4242),
                fillColor = Color(0xFFF7C927),
                fontSize = 30.sp
            )
            OutlinedText(
                text = "A PLANET",
                outlineColor = Color(0xFFBD4242),
                fillColor = Color(0xFFF7C927),
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Menu Button
            Box(
                modifier = Modifier

                    .padding(vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bbutton), // Замените на ваш ресурс для кнопки
                    contentDescription = null,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier

                        .fillMaxWidth(0.7f)
                        .clickable {
                            SoundManager.playSound()
                            next()
                        }
                )
                OutlinedText(
                    text = "TRY AGAIN",
                    outlineColor = Color.Black,
                    fillColor = Color.White,
                    fontSize = 50.sp
                )
            }

            Box(
                modifier = Modifier

                    .padding(vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.button), // Замените на ваш ресурс для кнопки
                    contentDescription = null,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier

                        .fillMaxWidth(0.7f)
                        .clickable {
                            SoundManager.playSound()
                            onBack()
                        }
                )
                OutlinedText(
                    text = "HOME",
                    outlineColor = Color.Black,
                    fillColor = Color.White,
                    fontSize = 50.sp
                )
            }
        }

    }
}

@Composable
private fun WinScreen(score: Int, next: () -> Unit, level: Level, onBack: () -> Unit) {
    LaunchedEffect(Unit) {
        SoundManager.pauseGameMusic()
        SoundManager.resumeMusic()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background), // Background image
                contentScale = ContentScale.FillBounds
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(id = R.drawable.flame), // Background image
                    contentScale = ContentScale.FillBounds
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            // Score Display Box


            // Score Display Box
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .paint(
                        painter = painterResource(id = R.drawable.racket), // Button background
                        contentScale = ContentScale.FillBounds
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(0.8f) // Ensure the stars fill about 80% width
                ) {
                    // List of star resources, check score to select active or inactive stars
                    repeat(3) { index ->
                        val starImage = if (score > index) {
                            painterResource(id = R.drawable.star) // Active star
                        } else {
                            painterResource(id = R.drawable.noactstar) // Inactive star
                        }

                        Image(
                            painter = starImage,
                            contentDescription = "Star",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(60.dp) // Adjust the size as needed
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(10.dp))
            OutlinedText(
                text = "WELL DONE!",
                outlineColor = Color(0xFFBD4242),
                fillColor = Color(0xFFF7C927),
                fontSize = 50.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier

                    .padding(vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bbutton), // Замените на ваш ресурс для кнопки
                    contentDescription = null,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier

                        .fillMaxWidth(0.7f)
                        .clickable {
                            SoundManager.playSound()
                            next()
                        }
                )
                OutlinedText(
                    text = "NEXT",
                    outlineColor = Color.Black,
                    fillColor = Color.White,
                    fontSize = 50.sp
                )
            }

            Box(
                modifier = Modifier

                    .padding(vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.button), // Замените на ваш ресурс для кнопки
                    contentDescription = null,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier

                        .fillMaxWidth(0.7f)
                        .clickable {
                            SoundManager.playSound()
                            onBack()
                        }
                )
                OutlinedText(
                    text = "HOME",
                    outlineColor = Color.Black,
                    fillColor = Color.White,
                    fontSize = 50.sp
                )
            }
        }
    }
}