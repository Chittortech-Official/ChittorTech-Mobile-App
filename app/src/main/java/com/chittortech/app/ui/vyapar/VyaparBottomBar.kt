package com.chittortech.app.ui.vyapar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.theme.*

enum class VyaparTab(val label: String) {
    HOME("HOME"),
    DASHBOARD("DASHBOARD"),
    ITEMS("ITEMS"),
    MENU("MENU"),
    GET_DESKTOP("GET DESKTOP")
}

@Composable
fun VyaparBottomBar(
    currentTab: VyaparTab,
    onTabSelected: (VyaparTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = VyaparWhite,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VyaparNavItem(
                label = "HOME",
                icon = Icons.Outlined.Home,
                selected = currentTab == VyaparTab.HOME,
                onClick = { onTabSelected(VyaparTab.HOME) }
            )
            VyaparNavItem(
                label = "DASHBOARD",
                icon = Icons.Outlined.BarChart,
                selected = currentTab == VyaparTab.DASHBOARD,
                onClick = { onTabSelected(VyaparTab.DASHBOARD) }
            )
            VyaparNavItem(
                label = "ITEMS",
                icon = Icons.Outlined.Inventory2,
                selected = currentTab == VyaparTab.ITEMS,
                onClick = { onTabSelected(VyaparTab.ITEMS) }
            )
            VyaparNavItem(
                label = "MENU",
                icon = Icons.Outlined.Menu,
                selected = currentTab == VyaparTab.MENU,
                onClick = { onTabSelected(VyaparTab.MENU) }
            )
            VyaparDesktopNavItem(
                selected = currentTab == VyaparTab.GET_DESKTOP,
                onClick = { onTabSelected(VyaparTab.GET_DESKTOP) }
            )
        }
    }
}

@Composable
private fun VyaparNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) VyaparBlue else Color(0xFF64748B)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = tint
        )
    }
}

@Composable
private fun VyaparDesktopNavItem(
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) VyaparBlue else Color(0xFF64748B)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        // Windows 4-color grid icon representation
        Row(modifier = Modifier.size(22.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(1.dp).background(Color(0xFFF25022)))
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(1.dp).background(Color(0xFF00A4EF)))
            }
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(1.dp).background(Color(0xFF7FBA00)))
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(1.dp).background(Color(0xFFFFB900)))
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "GET DESKTOP",
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = tint
        )
    }
}
