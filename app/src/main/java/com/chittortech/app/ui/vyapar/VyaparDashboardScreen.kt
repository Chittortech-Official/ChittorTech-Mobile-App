package com.chittortech.app.ui.vyapar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.model.AdminKpi
import com.chittortech.app.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun VyaparDashboardScreen(
    kpi: AdminKpi,
    onSeeReportsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val indianFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VyaparBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── 1. Reports Promo Card (Exact match of WA0030) ─────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ChittorTech Reports",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "View more than 50 reports and gain full control of your business!",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            onClick = onSeeReportsClick,
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFE8F1FC),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "See Reports",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VyaparBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Graphic Illustration Badge
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(VyaparBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(56.dp, 68.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(VyaparBlue))
                                Box(modifier = Modifier.fillMaxWidth(0.8f).height(4.dp).background(Color(0xFFE2E8F0)))
                                Box(modifier = Modifier.fillMaxWidth(0.6f).height(4.dp).background(Color(0xFFE2E8F0)))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFB703))
                                        .align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── 2. Side-by-Side KPI Cards: You'll Get vs You'll Give ───────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Card: You'll Get
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(VyaparGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SouthWest,
                                    contentDescription = null,
                                    tint = VyaparGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "You'll Get",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (kpi.totalOutstandingDues > 0) indianFormat.format(kpi.totalOutstandingDues) else "₹ 100.00",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparDark
                        )
                    }
                }

                // Right Card: You'll Give
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                    border = BorderStroke(1.dp, VyaparCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(VyaparRedLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NorthEast,
                                    contentDescription = null,
                                    tint = VyaparRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "You'll Give",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "₹ 0.00",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparDark
                        )
                    }
                }
            }
        }

        // ── 3. Sale Overview Card with Real Canvas Line Chart (WA0030) ────────────
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Sale Overview (Sept)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VyaparDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Sale",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (kpi.totalMonthlyInvoiced > 0) indianFormat.format(kpi.totalMonthlyInvoiced) else "₹ 2,500.00",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = VyaparGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Growth Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VyaparGreenLight)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "↑ 100%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VyaparGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "More Growth This Month",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Canvas Line Chart ──────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height - 30f

                            // Dashed baseline
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, h),
                                end = Offset(w, h),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                            )

                            // 3 Points: Jul (x=0.08w, y=h), Aug (x=0.5w, y=h), Sept (x=0.92w, y=10f)
                            val p1 = Offset(w * 0.08f, h)
                            val p2 = Offset(w * 0.50f, h)
                            val p3 = Offset(w * 0.92f, 12f)

                            val path = Path().apply {
                                moveTo(p1.x, p1.y)
                                lineTo(p2.x, p2.y)
                                lineTo(p3.x, p3.y)
                            }

                            // Draw blue line
                            drawPath(
                                path = path,
                                color = VyaparBlue,
                                style = Stroke(width = 5f)
                            )

                            // Draw circular markers
                            drawCircle(color = VyaparBlue, radius = 6f, center = p1)
                            drawCircle(color = VyaparBlue, radius = 6f, center = p2)
                            drawCircle(color = VyaparBlue, radius = 6f, center = p3)
                        }

                        // X-axis Month Labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jul", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("Aug", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("Sept", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }

        // ── 4. Expenses Card Header ───────────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VyaparWhite),
                border = BorderStroke(1.dp, VyaparCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Expenses (Sept)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VyaparDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cloud Servers & VPC", fontSize = 13.sp, color = Color(0xFF64748B))
                        Text("₹ 0.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("LLM API Inference", fontSize = 13.sp, color = Color(0xFF64748B))
                        Text("₹ 0.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VyaparDark)
                    }
                }
            }
        }
    }
}
