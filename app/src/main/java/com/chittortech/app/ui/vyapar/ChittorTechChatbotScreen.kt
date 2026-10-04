package com.chittortech.app.ui.vyapar

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.app.R
import com.chittortech.app.data.ChatMessage
import com.chittortech.app.data.GroqChatService
import com.chittortech.app.data.MessageSender
import com.chittortech.app.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChittorTechChatbotScreen(
    modifier: Modifier = Modifier,
    currentScreen: String = "ChittorTech Mobile App"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val groqService = remember { GroqChatService(context) }

    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "Hello! I'm ChittorTech GPT, your AI Assistant. How can I assist your business growth or engineering today? Feel free to ask about our website development, mobile apps, Google Play publishing, enterprise AI, or custom software."
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    val quickSuggestions = listOf(
        "Website & SaaS Development",
        "Mobile Apps (Android & iOS)",
        "Google Play 12-Tester Publishing",
        "Enterprise AI & RAG",
        "B2B Lead Generation Engine",
        "Custom ERP & CRM Pricing",
        "What is ChittorTech?",
        "Contact Kush & Lav Sharma"
    )

    fun sendUserMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || isTyping) return

        val userMsg = ChatMessage(sender = MessageSender.USER, text = trimmed)
        messages = messages + userMsg
        inputText = ""
        isTyping = true

        scope.launch {
            listState.animateScrollToItem(messages.size)

            val userName = context.getSharedPreferences("chittortech_user_prefs", Context.MODE_PRIVATE)
                .getString("user_name", "Guest").orEmpty().ifBlank { "Guest" }
            val botResponseText = groqService.sendMessage(
                userMessage = trimmed,
                history = messages,
                currentScreen = currentScreen,
                userName = userName
            )
            val botMsg = ChatMessage(sender = MessageSender.BOT, text = botResponseText)
            messages = messages + botMsg
            isTyping = false

            listState.animateScrollToItem(messages.size)
        }
    }

    // Auto-scroll when keyboard opens, new messages arrive, or bot starts typing
    val imeBottom = WindowInsets.ime.getBottom(androidx.compose.ui.platform.LocalDensity.current)
    LaunchedEffect(imeBottom, messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            kotlinx.coroutines.delay(100)
            listState.animateScrollToItem(messages.size)
        }
    }

    // WhatsApp Direct Sync — attaches latest user query so founders have immediate context
    fun openWhatsApp(specificQuery: String = "") {
        val lastUserMsg = messages.filter { it.sender == MessageSender.USER }.lastOrNull()?.text?.trim().orEmpty()
        val queryToInclude = if (specificQuery.isNotBlank()) specificQuery else lastUserMsg

        val userName = context.getSharedPreferences("chittortech_user_prefs", Context.MODE_PRIVATE)
            .getString("user_name", "").orEmpty().trim()
        val userGreeting = if (userName.isNotBlank()) "I am $userName, chatting" else "I am chatting"

        val messageToSend = if (queryToInclude.isNotBlank()) {
            "Namaste Lav Sir! $userGreeting with ChittorTech GPT on the mobile app.\n\nQuery: \"$queryToInclude\"\n\nCan we discuss this further?"
        } else {
            "Namaste Lav Sir! $userGreeting with ChittorTech GPT on the mobile app. I would like to explore ChittorTech services."
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/917597451057?text=${Uri.encode(messageToSend)}")
        }
        context.startActivity(intent)
    }

    fun callTeam() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:+917597451057")
        }
        context.startActivity(intent)
    }

    fun openEstimator() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://chittortech.in/project-estimator")
        }
        context.startActivity(intent)
    }

    fun openDemoRequest() {
        val lastUserMsg = messages.filter { it.sender == MessageSender.USER }.lastOrNull()?.text?.trim().orEmpty()
        val queryPart = if (lastUserMsg.isNotBlank()) "\n\nRegarding: \"$lastUserMsg\"" else ""
        val messageToSend = "Namaste Lav Sir! I would like to request a live demo of ChittorTech solutions.$queryPart\n\nPlease let me know the available time slots."
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/917597451057?text=${Uri.encode(messageToSend)}")
        }
        context.startActivity(intent)
    }

    fun openScheduleCall() {
        val lastUserMsg = messages.filter { it.sender == MessageSender.USER }.lastOrNull()?.text?.trim().orEmpty()
        val queryPart = if (lastUserMsg.isNotBlank()) "\n\nRegarding: \"$lastUserMsg\"" else ""
        val messageToSend = "Namaste Lav Sir! I would like to schedule a consultation call with the ChittorTech team.$queryPart\n\nPlease share available slots."
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/917597451057?text=${Uri.encode(messageToSend)}")
        }
        context.startActivity(intent)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .imePadding()
    ) {
        // ── 1. Top ChittorTech GPT Header ─────────────────────────────────────
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // ChittorTech GPT Avatar with green pulsing live indicator
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.chatbot_kaira),
                                contentDescription = "ChittorTech GPT Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "ChittorTech GPT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Online · Groq LPU Powered",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // Reset Chat Icon ONLY (no ugly WhatsApp icon in header)
                IconButton(
                    onClick = {
                        messages = listOf(
                            ChatMessage(
                                sender = MessageSender.BOT,
                                text = "Hello! I'm ChittorTech GPT, your AI Assistant. How can I assist your business growth or engineering today? Feel free to ask about our website development, mobile apps, Google Play publishing, enterprise AI, or custom software."
                            )
                        )
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RestartAlt,
                        contentDescription = "Restart Chat",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── 2. Message History (LazyColumn) ───────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick suggestions chips at the top
            item {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text(
                        text = "Suggested Topics",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickSuggestions) { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { sendUserMessage(suggestion) }
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Chat Messages List
            items(messages) { message ->
                if (message.sender == MessageSender.USER) {
                    UserMessageBubble(text = message.text)
                } else {
                    val msgIndex = messages.indexOf(message)
                    val precedingUserMsg = messages
                        .take(msgIndex)
                        .filter { it.sender == MessageSender.USER }
                        .lastOrNull()?.text ?: ""

                    BotMessageBubble(
                        text = message.text,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(message.text))
                        },
                        onWhatsAppClick = { openWhatsApp(precedingUserMsg) },
                        onCallClick = { callTeam() },
                        onEstimatorClick = { openEstimator() },
                        onDemoClick = { openDemoRequest() },
                        onScheduleClick = { openScheduleCall() }
                    )
                }
            }

            // Typing indicator
            if (isTyping) {
                item {
                    BotTypingBubble()
                }
            }
        }

        // ── 3. Bottom Input Row ───────────────────────────────────────────────
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Input TextField
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask ChittorTech GPT...",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { sendUserMessage(inputText) }
                    ),
                    maxLines = 3,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                // Send Button
                IconButton(
                    onClick = { sendUserMessage(inputText) },
                    enabled = inputText.isNotBlank() && !isTyping,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isTyping)
                                Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                            else
                                Brush.linearGradient(listOf(Color(0xFFCBD5E1), Color(0xFFCBD5E1)))
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Message Block Sealed Structure for Markdown & Tables ──────────────────────
private sealed interface MessageBlock {
    data class Text(val text: String) : MessageBlock
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MessageBlock
}

private fun splitTableRow(row: String): List<String> {
    val trimmed = row.trim()
    val clean = if (trimmed.startsWith("|")) trimmed.substring(1) else trimmed
    val cleanEnd = if (clean.endsWith("|")) clean.substring(0, clean.length - 1) else clean
    return cleanEnd.split("|").map { it.trim() }
}

private fun isTableSeparator(line: String): Boolean {
    val trimmed = line.trim()
    if (!trimmed.contains("-") || !trimmed.contains("|")) return false
    val cells = splitTableRow(trimmed)
    return cells.isNotEmpty() && cells.all { cell ->
        val c = cell.trim()
        c.isNotEmpty() && c.all { it == '-' || it == ':' || it == ' ' }
    }
}

private fun parseMessageBlocks(rawText: String): List<MessageBlock> {
    val blocks = mutableListOf<MessageBlock>()
    val lines = rawText.lines()
    var i = 0
    val currentTextLines = mutableListOf<String>()

    fun flushText() {
        if (currentTextLines.isNotEmpty()) {
            val combined = currentTextLines.joinToString("\n").trim()
            if (combined.isNotBlank()) {
                blocks.add(MessageBlock.Text(combined))
            }
            currentTextLines.clear()
        }
    }

    while (i < lines.size) {
        val line = lines[i].trim()
        if (line.contains("|") && i + 1 < lines.size && isTableSeparator(lines[i + 1])) {
            flushText()
            val headers = splitTableRow(line)
            val rows = mutableListOf<List<String>>()
            i += 2 // skip header and separator
            while (i < lines.size) {
                val rowLine = lines[i].trim()
                if (rowLine.contains("|") && !isTableSeparator(rowLine)) {
                    val cells = splitTableRow(rowLine)
                    rows.add(cells)
                    i++
                } else {
                    break
                }
            }
            if (headers.isNotEmpty() && rows.isNotEmpty()) {
                blocks.add(MessageBlock.Table(headers, rows))
            }
            continue
        }
        currentTextLines.add(lines[i])
        i++
    }
    flushText()
    return blocks
}

/**
 * Parses markdown bold (**text**), headers (###), and bullet points (- / *),
 * stripping all raw asterisks so the text renders cleanly without any "**".
 */
private fun parseMarkdownToAnnotatedString(text: String): AnnotatedString {
    val cleanedText = text
        .lines()
        .joinToString("\n") { line ->
            val trimmed = line.trimStart()
            when {
                trimmed.startsWith("### ") -> "**${trimmed.substring(4)}**"
                trimmed.startsWith("## ") -> "**${trimmed.substring(3)}**"
                trimmed.startsWith("# ") -> "**${trimmed.substring(2)}**"
                (trimmed.startsWith("* ") && !trimmed.startsWith("**")) || trimmed.startsWith("- ") -> {
                    val indent = line.substring(0, line.indexOf(trimmed))
                    "$indent• ${trimmed.substring(2)}"
                }
                else -> line
            }
        }

    val builder = AnnotatedString.Builder()
    val boldRegex = Regex("""\*\*(.*?)\*\*""")
    var currentIndex = 0
    val matches = boldRegex.findAll(cleanedText)

    for (match in matches) {
        val start = match.range.first
        val end = match.range.last + 1
        val boldContent = match.groupValues[1]

        if (start > currentIndex) {
            builder.append(cleanedText.substring(currentIndex, start))
        }

        val boldStart = builder.length
        builder.append(boldContent)
        val boldEnd = builder.length
        builder.addStyle(
            SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
            boldStart,
            boldEnd
        )

        currentIndex = end
    }

    if (currentIndex < cleanedText.length) {
        builder.append(cleanedText.substring(currentIndex))
    }

    return builder.toAnnotatedString()
}

// ── Responsive Centered Blue Table (Exact 1:1 Website Design) ─────────────────
@Composable
private fun CenteredMarkdownTable(table: MessageBlock.Table) {
    if (table.headers.isEmpty()) return

    val numCols = table.headers.size

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row with soft blue gradient matching website
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                table.headers.forEach { header ->
                    val cleanHeader = header.replace("**", "").trim().uppercase()
                    Text(
                        text = cleanHeader,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF),
                        lineHeight = 15.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFDBEAFE), thickness = 1.dp)

            // Table Rows with alternating background
            table.rows.forEachIndexed { rowIndex, row ->
                val rowBg = if (rowIndex % 2 == 0) Color.White else Color(0xFFF8FAFC)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (colIndex in 0 until numCols) {
                        val cellText = row.getOrElse(colIndex) { "" }
                        Text(
                            text = parseMarkdownToAnnotatedString(cellText),
                            fontSize = 11.5.sp,
                            color = Color(0xFF334155),
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
                if (rowIndex < table.rows.lastIndex) {
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                }
            }
        }
    }
}

// ── User Message Bubble ───────────────────────────────────────────────────────
@Composable
private fun UserMessageBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

// ── Bot Message Bubble ────────────────────────────────────────────────────────
@Composable
private fun BotMessageBubble(
    text: String,
    onCopy: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit,
    onEstimatorClick: () -> Unit,
    onDemoClick: () -> Unit,
    onScheduleClick: () -> Unit
) {
    // 1. Universal Action Tag Detection (matches [ACTION:WHATSAPP], [**ACTION:WHATSAPP**], [Action: WhatsApp], etc.)
    val rawUpper = text.uppercase()
    val showWhatsApp  = rawUpper.contains("WHATSAPP") || rawUpper.contains("7597451057") || rawUpper.contains("ACTION:CONTACT")
    val showCall      = rawUpper.contains("ACTION:CONTACT") || rawUpper.contains("CALL US") || rawUpper.contains("CALL ME") || rawUpper.contains("PHONE")
    val showEstimator = rawUpper.contains("ACTION:ESTIMATOR") || rawUpper.contains("ESTIMATOR")
    val showDemo      = rawUpper.contains("ACTION:DEMO") || rawUpper.contains("LIVE DEMO")
    val showSchedule  = rawUpper.contains("ACTION:SCHEDULE") || rawUpper.contains("SCHEDULE")
    val hasAnyAction  = showWhatsApp || showCall || showEstimator || showDemo || showSchedule

    // 2. Strip all action tags and clean dangling connector words ("or", "and", "/", ":") from end of lines
    val actionRegex = Regex("""(?i)\[\s*\*?\*?\s*action\s*:\s*[^\]]+\]""")
    val cleanText = text
        .replace(actionRegex, "")
        .lines()
        .map { line ->
            line.replace(Regex("""(?i)\s+(or|and|\/|,|:)\s*$"""), "").trimEnd()
        }
        .filterIndexed { index, line ->
            line.isNotBlank() || index > 0
        }
        .joinToString("\n")
        .trim()

    val blocks = remember(cleanText) { parseMessageBlocks(cleanText) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .size(32.dp)
                .padding(top = 2.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.chatbot_kaira),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Bubble column fills remaining width naturally so tables are never squished
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 350.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    // Render parsed blocks (formatted text or centered blue tables)
                    blocks.forEachIndexed { index, block ->
                        when (block) {
                            is MessageBlock.Text -> {
                                Text(
                                    text = parseMarkdownToAnnotatedString(block.text),
                                    color = Color(0xFF1E293B),
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp
                                )
                            }
                            is MessageBlock.Table -> {
                                CenteredMarkdownTable(table = block)
                            }
                        }
                        if (index < blocks.lastIndex) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy message",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Action triggers — converted to native mobile buttons with SINGLE official icons (no double emoji)
            if (hasAnyAction) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Row 1: WhatsApp + Call Us
                    if (showWhatsApp || showCall) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (showWhatsApp) {
                                Button(
                                    onClick = onWhatsAppClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_whatsapp),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("WhatsApp", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (showCall) {
                                OutlinedButton(
                                    onClick = onCallClick,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFF0284C7)),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Phone, null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Call Us", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                                }
                            }
                        }
                    }
                    // Row 2: Live Demo + Schedule Call
                    if (showDemo || showSchedule) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (showDemo) {
                                OutlinedButton(
                                    onClick = onDemoClick,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Live Demo", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                                }
                            }
                            if (showSchedule) {
                                OutlinedButton(
                                    onClick = onScheduleClick,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFF0369A1)),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF0369A1), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Schedule Call", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                                }
                            }
                        }
                    }
                    // Row 3: Estimator
                    if (showEstimator) {
                        OutlinedButton(
                            onClick = onEstimatorClick,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF6366F1)),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Calculate, null, tint = Color(0xFF6366F1), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cost Estimator", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                        }
                    }
                }
            }
        }
    }
}

// ── Bot Typing Dots Bubble ────────────────────────────────────────────────────
@Composable
private fun BotTypingBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), repeatMode = RepeatMode.Reverse), label = "d1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse), label = "d2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse), label = "d3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 40.dp, top = 2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.padding(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0284C7).copy(alpha = dot1Alpha)))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0284C7).copy(alpha = dot2Alpha)))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0284C7).copy(alpha = dot3Alpha)))
            }
        }
    }
}
