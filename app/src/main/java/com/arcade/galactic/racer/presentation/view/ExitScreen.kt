package com.arcade.galactic.racer.presentation.view

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.Savage.Trail.presentation.navigatio.Screen
import com.arcade.galactic.racer.R
import com.arcade.galactic.racer.data.SoundManager
import com.arcade.galactic.racer.presentation.navigation.OutlinedText


@Composable
fun ExitScreen(
    onBack: () -> Unit
) {
    val activity = LocalContext.current as? Activity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Back",
            modifier = Modifier
                .size(100.dp)
                .clickable(
                    onClick = {
                        SoundManager.playSound()
                        onBack()
                    }
                )
                .align(Alignment.TopEnd) // Align the image to the top-end (top-right corner)
                .padding(16.dp) // Optional: Add padding to prevent the image from touching the edge
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "EXIT",
                fontSize = 48.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 2.dp)
            )

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
                            activity?.finish()
                        }
                )
                OutlinedText(
                    text = "YES",
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
                    text = "NO",
                    outlineColor = Color.Black,
                    fillColor = Color.White,
                    fontSize = 50.sp
                )
            }
        }
    }
}
