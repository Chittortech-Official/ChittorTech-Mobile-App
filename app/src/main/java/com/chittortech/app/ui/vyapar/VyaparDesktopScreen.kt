package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.theme.*

@Composable
fun VyaparDesktopScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VyaparWhite),
            border = BorderStroke(1.dp, VyaparCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Windows Desktop Icon Representation
                Row(modifier = Modifier.size(56.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).background(Color(0xFFF25022)))
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).background(Color(0xFF00A4EF)))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).background(Color(0xFF7FBA00)))
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).background(Color(0xFFFFB900)))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ChittorTech Enterprise Desktop",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Access your full engineering dashboard, client projects, cloud infrastructure, and invoice builder from any desktop browser.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VyaparBlue),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Outlined.OpenInBrowser, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch ChittorTech Web Portal", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Features Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = VyaparWhite),
            border = BorderStroke(1.dp, VyaparCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Desktop Features", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VyaparDark)

                DesktopFeatureRow(icon = Icons.Outlined.Sync, title = "Live Real-Time Sync", subtitle = "Instant sync with your mobile app through Firebase")
                DesktopFeatureRow(icon = Icons.Outlined.PictureAsPdf, title = "GST & Invoice Reports", subtitle = "Generate and export official PDF invoices with single click")
                DesktopFeatureRow(icon = Icons.Outlined.Security, title = "Encrypted Vault", subtitle = "Enterprise-grade credential and infrastructure protection")
            }
        }
    }
}

@Composable
private fun DesktopFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VyaparBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = VyaparBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark)
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}
