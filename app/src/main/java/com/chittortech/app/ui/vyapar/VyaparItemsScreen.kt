package com.chittortech.app.ui.vyapar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.Project
import com.chittortech.app.theme.*

data class TechServiceItem(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val status: String
)

@Composable
fun VyaparItemsScreen(
    projects: List<Project>,
    onAddNewItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Default ChittorTech Catalog Services
    val defaultServices = remember {
        listOf(
            TechServiceItem("1", "Enterprise AI Agent System", "AI & Automation", "₹ 75,000", "Ready to Deploy"),
            TechServiceItem("2", "Fullstack Native Android App", "Mobile Engineering", "₹ 60,000", "Ready to Deploy"),
            TechServiceItem("3", "Next.js 15 High-Speed Portal", "Web Architecture", "₹ 45,000", "Ready to Deploy"),
            TechServiceItem("4", "Managed Cloud VPC & Docker DevOps", "Cloud Hosting", "₹ 25,000/yr", "Active")
        )
    }

    var showCatalog by remember { mutableStateOf(projects.isNotEmpty()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg)
    ) {
        if (!showCatalog && projects.isEmpty()) {
            // ── Empty State matching WA0031 ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 3D Box Illustration Vector
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw isometric 3D open box
                                val boxPath = Path().apply {
                                    moveTo(w * 0.5f, h * 0.25f)
                                    lineTo(w * 0.85f, h * 0.42f)
                                    lineTo(w * 0.5f, h * 0.6f)
                                    lineTo(w * 0.15f, h * 0.42f)
                                    close()
                                }
                                drawPath(boxPath, color = Color(0xFFC7DFF8))

                                // Left panel
                                val leftPanel = Path().apply {
                                    moveTo(w * 0.15f, h * 0.42f)
                                    lineTo(w * 0.5f, h * 0.6f)
                                    lineTo(w * 0.5f, h * 0.9f)
                                    lineTo(w * 0.15f, h * 0.72f)
                                    close()
                                }
                                drawPath(leftPanel, color = Color(0xFFA5C8F2))

                                // Right panel
                                val rightPanel = Path().apply {
                                    moveTo(w * 0.5f, h * 0.6f)
                                    lineTo(w * 0.85f, h * 0.42f)
                                    lineTo(w * 0.85f, h * 0.72f)
                                    lineTo(w * 0.5f, h * 0.9f)
                                    close()
                                }
                                drawPath(rightPanel, color = Color(0xFF8BB5EA))

                                // Open flaps
                                drawLine(Color(0xFF6F9FE0), Offset(w * 0.15f, h * 0.42f), Offset(w * 0.05f, h * 0.25f), 4f)
                                drawLine(Color(0xFF6F9FE0), Offset(w * 0.85f, h * 0.42f), Offset(w * 0.95f, h * 0.25f), 4f)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Hey! You have not added any items yet.",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VyaparDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add your first item now.",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Red Pill Button: Add New Item
                        Button(
                            onClick = {
                                onAddNewItem()
                                showCatalog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VyaparRed),
                            shape = RoundedCornerShape(26.dp),
                            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 13.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add New Item",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Tutorial Video Banner (WA0031)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chittortech.in"))
                            context.startActivity(intent)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp, 40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "How to add items on ChittorTech?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VyaparDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Watch Video >",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VyaparRed
                            )
                        }
                    }
                }
            }
        } else {
            // ── Active Catalog Items List ──────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Services & Deployments",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparDark
                        )
                        TextButton(onClick = { onAddNewItem() }) {
                            Text("+ Add Item", color = VyaparRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(defaultServices) { service ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                        border = BorderStroke(1.dp, VyaparCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(service.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(service.category, fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(service.price, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = VyaparGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(service.status, fontSize = 11.sp, color = VyaparBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}
