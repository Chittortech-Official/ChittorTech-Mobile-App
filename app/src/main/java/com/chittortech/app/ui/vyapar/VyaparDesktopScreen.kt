package com.chittortech.app.ui.vyapar

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Main Coming Soon Card ─────────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(20.dp),
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
                // "COMING SOON" Status Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFF93C5FD)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COMING SOON",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0369A1),
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Windows / Mac Desktop Visual Matrix
                Row(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFF25022)))
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF00A4EF)))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF7FBA00)))
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(2.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFFFB900)))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "ChittorTech Enterprise Desktop",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyaparDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Native desktop application for Windows 11 and macOS. Designed for high-speed multi-window billing, offline-first accounting, and seamless enterprise synchronization.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Coming Soon CTA Button (no "Launch")
                Button(
                    onClick = {
                        Toast.makeText(context, "ChittorTech Enterprise Desktop is currently under active development. Coming Soon!", Toast.LENGTH_LONG).show()
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Outlined.HourglassTop, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coming Soon • Windows & Mac", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }
            }
        }

        // ── Desktop Features Card ─────────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VyaparWhite),
            border = BorderStroke(1.dp, VyaparCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Devices, contentDescription = null, tint = VyaparBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Desktop App Features", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                }

                DesktopFeatureRow(
                    icon = Icons.Outlined.Sync,
                    title = "Live Real-Time Sync",
                    subtitle = "Instant bi-directional synchronization with your mobile app & Firebase Cloud."
                )

                DesktopFeatureRow(
                    icon = Icons.Outlined.PointOfSale,
                    title = "Multi-Window Billing & POS",
                    subtitle = "High-speed billing with barcode scanner & thermal receipt printer integration."
                )

                DesktopFeatureRow(
                    icon = Icons.Outlined.PictureAsPdf,
                    title = "GST & E-Way Bill Auto-Generation",
                    subtitle = "One-click export of GST-compliant PDF invoices, E-way bills, and Excel reports."
                )

                DesktopFeatureRow(
                    icon = Icons.Outlined.SendToMobile,
                    title = "Automated WhatsApp & Email Dispatch",
                    subtitle = "Send invoice PDFs and digital payment links to clients directly from desktop."
                )

                DesktopFeatureRow(
                    icon = Icons.Outlined.Lock,
                    title = "Encrypted Founder Vault",
                    subtitle = "256-bit encrypted local storage for client SOW agreements & API keys."
                )

                DesktopFeatureRow(
                    icon = Icons.Outlined.CloudOff,
                    title = "Offline-First Reliability",
                    subtitle = "Create sales and manage inventory without internet; syncs automatically once back online."
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
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
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(VyaparBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = VyaparBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = VyaparDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF64748B), lineHeight = 16.sp)
        }
    }
}
