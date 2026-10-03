package com.chittortech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.theme.*

// ─── Status Badge ─────────────────────────────────────────────────────────────

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when (status.uppercase()) {
        "PAID", "LIVE & ACTIVE", "ACTIVE", "OPEN" -> Triple(CtGreenLight, CtGreen, status)
        "UNPAID", "IN_PROGRESS", "IN PROGRESS"    -> Triple(CtAmberLight, CtAmber, status.replace("_", " "))
        "OVERDUE", "RESOLVED", "CLOSED"           -> Triple(CtRedLight, CtRed, status)
        "FLASHING" -> Triple(CtRedLight, CtRed, "URGENT")
        else -> Triple(CtSurfaceVariant, TextSecondary, status)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

// ─── Priority Badge ───────────────────────────────────────────────────────────

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (priority.uppercase()) {
        "LOW"    -> Pair(CtGreenLight, CtGreen)
        "MEDIUM" -> Pair(CtAmberLight, CtAmber)
        "HIGH"   -> Pair(CtOrangeLight, CtOrange)
        "URGENT" -> Pair(CtRedLight, CtRed)
        else     -> Pair(CtSurfaceVariant, TextSecondary)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(priority, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

// ─── KPI Card ─────────────────────────────────────────────────────────────────

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: ImageVector,
    iconBg: Color = CtPrimaryLight,
    iconTint: Color = CtPrimaryBlue,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CtCardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(subtitle, fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String = "",
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (actionLabel.isNotBlank()) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                Text(actionLabel, fontSize = 12.sp, color = CtPrimaryBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Gradient Top Bar ─────────────────────────────────────────────────────────

@Composable
fun CtTopBar(
    title: String,
    subtitle: String = "",
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(GradStart, GradEnd))
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            if (subtitle.isNotBlank()) {
                Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.80f))
            }
        }
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
        }
    }
}

// ─── Renewal Alert Banner ──────────────────────────────────────────────────────

@Composable
fun RenewalAlertBanner(
    label: String,
    daysLeft: Int,
    modifier: Modifier = Modifier
) {
    val (bg, fg, icon) = when {
        daysLeft <= 7  -> Triple(CtRedLight, CtRed, Icons.Default.Warning)
        daysLeft <= 30 -> Triple(CtAmberLight, CtAmber, Icons.Default.Info)
        else           -> Triple(CtGreenLight, CtGreen, Icons.Default.CheckCircle)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = fg)
                Text("$daysLeft days remaining", fontSize = 11.sp, color = fg.copy(alpha = 0.8f))
            }
        }
    }
}

// ─── Empty State ─────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.Inbox,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, fontSize = 13.sp, color = TextMuted)
    }
}

// ─── Loading Overlay ──────────────────────────────────────────────────────────

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = CtPrimaryBlue)
    }
}
