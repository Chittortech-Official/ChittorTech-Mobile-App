package com.chittortech.app.ui.screens.client

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.CtUser
import com.chittortech.app.theme.*

@Composable
fun ClientProfileScreen(
    user: CtUser,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CtBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Gradient Header with Avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(CtPrimaryDark, CtPrimaryMid)))
                .statusBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.companyName.take(2).uppercase().ifBlank { "CT" },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(user.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(user.companyName, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("CLIENT PORTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CtCardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Account Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(14.dp))
                ProfileRow(icon = Icons.Default.Person, label = "Full Name", value = user.displayName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Business, label = "Company", value = user.companyName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Email, label = "Email", value = user.email)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Phone, label = "Phone", value = user.phone.ifBlank { "Not set" })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ChittorTech Contact Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CtCardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your ChittorTech Contact", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(14.dp))
                ProfileRow(icon = Icons.Default.Person, label = "Lead Engineer", value = "Kush (Founder)")
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Email, label = "Email", value = "kush@chittortech.in")
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CtBorder)
                ProfileRow(icon = Icons.Default.Language, label = "Website", value = "chittortech.in")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CtRedLight),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = CtRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CtRed)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "ChittorTech v1.0.0 • chittortech.in",
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CtPrimaryBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
