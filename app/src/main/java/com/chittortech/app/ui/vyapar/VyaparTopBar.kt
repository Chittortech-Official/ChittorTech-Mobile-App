package com.chittortech.app.ui.vyapar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.theme.*

@Composable
fun VyaparTopBar(
    businessName: String = "ChittorTech",
    notificationCount: Int = 2,
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        color = VyaparWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Circular store/tech icon + Business Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, VyaparCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.chittortech.app.R.drawable.chittortech_logo),
                        contentDescription = "ChittorTech Logo",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = businessName,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark,
                    maxLines = 1
                )
            }

            // Right: Responsive Notification Bell with Non-Clipped Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(24.dp)
                )

                if (notificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-3).dp, y = 3.dp)
                            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                            .clip(CircleShape)
                            .background(VyaparRed)
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (notificationCount > 99) "99+" else "$notificationCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
