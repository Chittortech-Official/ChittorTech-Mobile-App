package com.chittortech.app.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R

enum class OnboardingFlowStep {
    PERMISSIONS,
    CAROUSEL,
    THREE_STEPS
}

private val BrandNavy = Color(0xFF0F3E8F)
private val PrimaryBtnBlue = Color(0xFF0D3B85)
private val TextDark = Color(0xFF1E293B)
private val TextSubtle = Color(0xFF64748B)

/**
 * Streamlined ChittorTech First-Time Launch Experience:
 * 1. Permissions Needed (Notification / Camera / Security)
 * 2. Feature Carousel (AI Intelligence / Custom Enterprise Software / 24/7 Security Vault)
 * 3. 3-Step Registration Guide (Enter Email ID -> Enter the OTP -> Access the Dashboard)
 */
@Composable
fun ChittorTechOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(OnboardingFlowStep.PERMISSIONS) }

    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut()
            }
        },
        label = "OnboardingStepTransition",
        modifier = modifier.fillMaxSize()
    ) { step ->
        when (step) {
            OnboardingFlowStep.PERMISSIONS -> {
                PermissionsNeededStep(
                    onContinue = { currentStep = OnboardingFlowStep.CAROUSEL },
                    onSkip = onComplete
                )
            }
            OnboardingFlowStep.CAROUSEL -> {
                FeatureCarouselStep(
                    onContinue = { currentStep = OnboardingFlowStep.THREE_STEPS },
                    onSkip = onComplete
                )
            }
            OnboardingFlowStep.THREE_STEPS -> {
                ThreeStepsGuideStep(
                    onBack = { currentStep = OnboardingFlowStep.CAROUSEL },
                    onFinish = onComplete
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. PERMISSIONS NEEDED STEP (Reference Image 2)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PermissionsNeededStep(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Skip Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                }
            }

            // Main Permission Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // ChittorTech Mascot Header in Permissions Card
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(2.dp, Color(0xFF38BDF8)),
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.chittortech_ai_mascot),
                                contentDescription = "ChittorTech Mascot",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Permissions Needed",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "ChittorTech Secure Workspace",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "These permissions help the ChittorTech app work safely and smoothly.",
                        fontSize = 13.sp,
                        color = TextSubtle,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Permission 1: Camera
                    PermissionItem(
                        icon = Icons.Default.CameraAlt,
                        title = "Camera & Scanner",
                        description = "Used for document scanning, project attachments, and profile verification."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Permission 2: Notifications
                    PermissionItem(
                        icon = Icons.Default.NotificationsActive,
                        title = "Notifications & Alerts",
                        description = "Used to send instant project updates, milestone alerts, and verification OTPs."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Permission 3: Security & Session Binding
                    PermissionItem(
                        icon = Icons.Default.PhoneAndroid,
                        title = "Device & Security Binding",
                        description = "Used to check device security availability for encrypted session login."
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Continue Button
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBtnBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEFF6FF),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandNavy,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSubtle,
                lineHeight = 16.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. FEATURE CAROUSEL STEP (Reference Images 4, 5, 6)
// ─────────────────────────────────────────────────────────────────────────────
private data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val badge: String,
    val badgeIcon: ImageVector
)

@Composable
private fun FeatureCarouselStep(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val slides = remember {
        listOf(
            OnboardingSlide(
                title = "AI-Powered Business Intelligence",
                subtitle = "Empower your business with autonomous AI workflows, real-time analytics, and smart client bots.",
                badge = "Enterprise AI Core",
                badgeIcon = Icons.Default.AutoAwesome
            ),
            OnboardingSlide(
                title = "Custom Enterprise Software & Cloud",
                subtitle = "Scalable web & mobile platforms, high-performance APIs, and custom software tailored for your business.",
                badge = "Enterprise Solutions",
                badgeIcon = Icons.Default.Terminal
            ),
            OnboardingSlide(
                title = "Bank-Grade Cloud Security",
                subtitle = "Military-grade encryption, automated cloud backups, and 24/7 dedicated enterprise reliability.",
                badge = "24/7 Secure Vault",
                badgeIcon = Icons.Default.Shield
            )
        )
    }

    var currentSlideIndex by remember { mutableIntStateOf(0) }

    fun nextSlide() {
        if (currentSlideIndex < slides.size - 1) {
            currentSlideIndex++
        } else {
            onContinue()
        }
    }

    fun prevSlide() {
        if (currentSlideIndex > 0) {
            currentSlideIndex--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -30) {
                        nextSlide()
                    } else if (dragAmount > 30) {
                        prevSlide()
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar (Logo & Skip)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.chittortech_logo),
                        contentDescription = "ChittorTech Logo",
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CHITTORTECH",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        letterSpacing = 0.5.sp
                    )
                }

                TextButton(onClick = onSkip) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy
                    )
                }
            }

            // Center Interactive Carousel View
            val slide = slides[currentSlideIndex]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Carousel Navigation Arrows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Arrow
                    IconButton(
                        onClick = { prevSlide() },
                        enabled = currentSlideIndex > 0,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = if (currentSlideIndex > 0) Color(0xFFF1F5F9) else Color.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            tint = if (currentSlideIndex > 0) BrandNavy else Color.Transparent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // ChittorTech 3D Mascot Hero Card
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Card(
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.5.dp, Color(0xFFBAE6FD)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier.size(205.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.chittortech_ai_mascot),
                                        contentDescription = "ChittorTech Mascot",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            // Dynamic Slide Category Chip
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0F172A).copy(alpha = 0.92f),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                shadowElevation = 4.dp,
                                modifier = Modifier.offset(y = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = slide.badgeIcon,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = slide.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        // Dynamic Pagination Dots (● ○ ○)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            slides.indices.forEach { index ->
                                val isActive = index == currentSlideIndex
                                Box(
                                    modifier = Modifier
                                        .height(7.dp)
                                        .width(if (isActive) 22.dp else 7.dp)
                                        .clip(RoundedCornerShape(3.5.dp))
                                        .background(if (isActive) BrandNavy else Color(0xFFCBD5E1))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = slide.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = slide.subtitle,
                            fontSize = 13.sp,
                            color = TextSubtle,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Right Arrow
                    IconButton(
                        onClick = { nextSlide() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F5F9), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = BrandNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bottom "Continue to Register" Action Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (currentSlideIndex == slides.size - 1) {
                            onContinue()
                        } else {
                            nextSlide()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBtnBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (currentSlideIndex == slides.size - 1) "Continue to Register" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. THREE STEPS REGISTRATION GUIDE (Reference Image 7)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ThreeStepsGuideStep(
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Back Arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Register with just 3 Steps",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            // 3 Steps Stack: Enter Email -> Enter OTP -> Access Dashboard
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Step 1: Enter your Email ID
                StepGuidanceMascotCard(
                    stepNumber = "1",
                    title = "Enter your Email ID",
                    mockupLabel = "EMAIL AUTH",
                    mockupDetail = "user@chittortech.in",
                    description = "Input your official business email address to initiate your secure session."
                )

                // Step 2: Enter the OTP
                StepGuidanceMascotCard(
                    stepNumber = "2",
                    title = "Enter the OTP",
                    mockupLabel = "VERIFY OTP",
                    mockupDetail = "• • • • • •",
                    description = "Input the 6-digit one-time passcode sent directly to your email inbox."
                )

                // Step 3: Access the Dashboard
                StepGuidanceMascotCard(
                    stepNumber = "3",
                    title = "Access the Dashboard",
                    mockupLabel = "DASHBOARD",
                    mockupDetail = "Projects · Cloud · AI",
                    description = "Instantly unlock your personalized ChittorTech client portal, projects, and AI copilot."
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom Action Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onFinish,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBtnBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Enter your Email / Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Step Card highlighting ChittorTech 3D Mascot assisting the user,
 * perfectly reproducing Reference Photo 7 layout.
 */
@Composable
private fun StepGuidanceMascotCard(
    stepNumber: String,
    title: String,
    mockupLabel: String,
    mockupDetail: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Step Badge + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stepNumber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Stage: Phone Mockup Container + ChittorTech 3D Mascot!
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left side: Mini Mockup Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = mockupLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = mockupDetail,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right side: ChittorTech 3D Mascot Photo!
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(70.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chittortech_ai_mascot),
                            contentDescription = "ChittorTech Mascot",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSubtle,
                lineHeight = 15.sp
            )
        }
    }
}
