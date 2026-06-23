package com.example.attendease.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendease.ui.theme.Spacing

@Composable
fun CornerBracket(alignment: Alignment) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.sm)
    ) {
        val bracketSize = 24.dp
        val strokeWidth = 3.dp
        val color = MaterialTheme.colorScheme.primary
        
        when (alignment) {
            Alignment.TopStart -> {
                Box(modifier = Modifier.size(bracketSize).align(Alignment.TopStart)) {
                    Box(modifier = Modifier.fillMaxWidth().height(strokeWidth).background(color))
                    Box(modifier = Modifier.fillMaxHeight().width(strokeWidth).background(color))
                }
            }
            Alignment.TopEnd -> {
                Box(modifier = Modifier.size(bracketSize).align(Alignment.TopEnd)) {
                    Box(modifier = Modifier.fillMaxWidth().height(strokeWidth).background(color))
                    Box(modifier = Modifier.fillMaxHeight().width(strokeWidth).background(color).align(Alignment.TopEnd))
                }
            }
            Alignment.BottomStart -> {
                Box(modifier = Modifier.size(bracketSize).align(Alignment.BottomStart)) {
                    Box(modifier = Modifier.fillMaxWidth().height(strokeWidth).background(color).align(Alignment.BottomStart))
                    Box(modifier = Modifier.fillMaxHeight().width(strokeWidth).background(color))
                }
            }
            Alignment.BottomEnd -> {
                Box(modifier = Modifier.size(bracketSize).align(Alignment.BottomEnd)) {
                    Box(modifier = Modifier.fillMaxWidth().height(strokeWidth).background(color).align(Alignment.BottomEnd))
                    Box(modifier = Modifier.fillMaxHeight().width(strokeWidth).background(color).align(Alignment.BottomEnd))
                }
            }
        }
    }
}
