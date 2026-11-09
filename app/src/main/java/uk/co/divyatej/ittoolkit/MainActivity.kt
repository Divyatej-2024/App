package uk.co.divyatej.ittoolkit

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ToolkitApp() }
    }
}

private enum class Section(val label: String) {
    HOME("Overview"), NETWORK("Network"), CHECKLISTS("Checklists"), CASES("Guided case"), NOTES("Work notes")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolkitApp() {
    var section by remember { mutableStateOf(Section.HOME) }
    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Column {
                    Text("IT Professional Toolkit", fontWeight = FontWeight.Bold)
                    Text("Offline-first field reference", style = MaterialTheme.typography.labelSmall)
                } })
            },
            bottomBar = {
                NavigationBar {
                    Section.entries.forEach { item ->
                        NavigationBarItem(
                            selected = section == item,
                            onClick = { section = item },
                            icon = { Text(when(item) { Section.HOME -> "⌂"; Section.NETWORK -> "⌘"; Section.CHECKLISTS -> "✓"; Section.CASES -> "◎"; Section.NOTES -> "▤" }) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { padding ->
            when (section) {
                Section.HOME -> Overview(Modifier.padding(padding)) { section = it }
                Section.NETWORK -> NetworkTools(Modifier.padding(padding))
                Section.CHECKLISTS -> Checklists(Modifier.padding(padding))
                Section.CASES -> GuidedCase(Modifier.padding(padding))
                Section.NOTES -> WorkNotes(Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun Overview(modifier: Modifier = Modifier, navigate: (Section) -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Your field companion", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Practical references for everyday IT support and junior security work.")
        }
        item { SummaryCard("Network utilities", "Calculate IPv4 subnet details and review common service ports.", "Open network tools") { navigate(Section.NETWORK) } }
        item { SummaryCard("Support checklists", "Work through structured first-line troubleshooting steps.", "Open checklists") { navigate(Section.CHECKLISTS) } }
        item { SummaryCard("Guided support case", "Turn a vague problem into clear checks, evidence and a useful handover.", "Start a case") { navigate(Section.CASES) } }
        item { SummaryCard("Work notes", "Keep local notes for tasks, observations and handovers.", "Open notes") { navigate(Section.NOTES) } }
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Privacy by design", fontWeight = FontWeight.Bold)
                    Text("This first version stores notes on this device. It has no account, analytics, advertising or network permission.")
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, body: String, action: String, onClick: () -> Unit) {
    Card {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(body)
            TextButton(onClick = onClick) { Text(action) }
        }
    }
}

@Composable
private fun NetworkTools(modifier: Modifier = Modifier) {
    var address by remember { mutableStateOf("192.168.1.10") }
    var prefix by remember { mutableStateOf("24") }
    val result = remember(address, prefix) { calculateSubnet(address, prefix) }
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("IPv4 subnet calculator", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OutlinedTextField(address, { address = it }, label = { Text("IPv4 address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(prefix, { prefix = it }, label = { Text("Prefix length (0–32)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            if (result == null) Text("Enter a valid IPv4 address and prefix length.", color = MaterialTheme.colorScheme.error)
            else Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Calculation", fontWeight = FontWeight.Bold)
                    ResultRow("Network", result.network)
                    ResultRow("Subnet mask", result.mask)
                    ResultRow("Broadcast", result.broadcast)
                    ResultRow("First host", result.firstHost)
                    ResultRow("Last host", result.lastHost)
                    ResultRow("Usable hosts", result.hosts.toString())
                }
            }
        }
        item { Text("Common service ports", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(commonPorts) { (port, service, purpose) ->
            ListItem(headlineContent = { Text("$port · $service", fontWeight = FontWeight.SemiBold) }, supportingContent = { Text(purpose) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

private data class SubnetResult(val network: String, val mask: String, val broadcast: String, val firstHost: String, val lastHost: String, val hosts: Long)

private fun calculateSubnet(ip: String, prefixText: String): SubnetResult? {
    val prefix = prefixText.toIntOrNull()?.takeIf { it in 0..32 } ?: return null
    val parts = ip.split(".")
    if (parts.size != 4 || parts.any { it.isEmpty() || (it.length > 1 && it.startsWith("0")) }) return null
    val octets = parts.map { it.toIntOrNull() ?: return null }
    if (octets.any { it !in 0..255 }) return null
    val address = octets.fold(0L) { acc, part -> (acc shl 8) or part.toLong() }
    val mask = if (prefix == 0) 0L else (0xFFFF_FFFFL shl (32 - prefix)) and 0xFFFF_FFFFL
    val network = address and mask
    val broadcast = network or (mask xor 0xFFFF_FFFFL)
    fun format(value: Long) = (24 downTo 0 step 8).joinToString(".") { ((value shr it) and 255).toString() }
    val hostCount = when (prefix) { 32 -> 1L; 31 -> 2L; else -> (1L shl (32 - prefix)) - 2L }
    val first = if (prefix >= 31) network else network + 1
    val last = if (prefix >= 31) broadcast else broadcast - 1
    return SubnetResult(format(network), format(mask), format(broadcast), format(first), format(last), hostCount)
}

private val commonPorts = listOf(
    Triple("20/21", "FTP", "File Transfer Protocol; legacy, unencrypted by default."),
    Triple("22", "SSH", "Secure remote shell and file transfer."),
    Triple("25", "SMTP", "Mail transfer between servers."),
    Triple("53", "DNS", "Domain name resolution over UDP or TCP."),
    Triple("80", "HTTP", "Unencrypted web traffic."),
    Triple("123", "NTP", "Network time synchronisation."),
    Triple("143", "IMAP", "Email access protocol."),
    Triple("443", "HTTPS", "Encrypted web traffic using TLS."),
    Triple("445", "SMB", "Windows file and printer sharing."),
    Triple("3389", "RDP", "Remote Desktop Protocol.")
)

private data class Checklist(val title: String, val steps: List<String>)
private val supportChecklists = listOf(
    Checklist("No internet connection", listOf("Confirm whether one user or multiple users are affected.", "Check Wi-Fi or Ethernet connection and link status.", "Check IP address, subnet mask, gateway and DNS configuration.", "Test the default gateway, then a known external IP.", "Test DNS resolution and review VPN or proxy settings.", "Record findings, actions and escalation details.")),
    Checklist("Computer running slowly", listOf("Confirm symptoms, start time and business impact.", "Check CPU, memory and disk usage in Task Manager.", "Review available disk space and startup applications.", "Check for pending updates and approved security alerts.", "Restart only after saving work and confirming user approval.", "Document results and escalate recurring or unexplained issues.")),
    Checklist("Account sign-in issue", listOf("Verify the user and the exact error message.", "Check service status and whether other users are affected.", "Confirm keyboard layout, network and account status.", "Follow the organisation's approved identity verification process.", "Never ask for or record the user's password.", "Record the resolution or escalate to the identity team."))
)

@Composable
private fun Checklists(modifier: Modifier = Modifier) {
    var selected by remember { mutableIntStateOf(0) }
    val checked = remember(selected) { mutableStateListOf<Boolean>().apply { repeat(supportChecklists[selected].steps.size) { add(false) } } }
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Support checklists", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Use these as prompts, and follow your organisation's policies and change controls.")
        }
        items(supportChecklists.size) { index ->
            FilterChip(selected == index, { selected = index }, label = { Text(supportChecklists[index].title) })
        }
        item {
            Text(supportChecklists[selected].title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("${checked.count { it }} of ${checked.size} steps completed")
            LinearProgressIndicator(progress = { checked.count { it }.toFloat() / checked.size }, modifier = Modifier.fillMaxWidth())
        }
        items(supportChecklists[selected].steps.size) { index ->
            Card {
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(checked[index], { checked[index] = it })
                    Text(supportChecklists[selected].steps[index], modifier = Modifier.weight(1f).padding(top = 10.dp))
                }
            }
        }
        item { TextButton(onClick = { checked.indices.forEach { checked[it] = false } }) { Text("Reset checklist") } }
    }
}

@Composable
private fun GuidedCase(modifier: Modifier = Modifier) {
    var issue by remember { mutableStateOf("Connectivity problem") }
    var impact by remember { mutableStateOf("") }
    var report by remember { mutableStateOf("") }
    var checks by remember { mutableStateOf("") }
    var nextAction by remember { mutableStateOf("") }
    val context = LocalContext.current
    val handover = """
        SUPPORT HANDOVER
        Issue: $issue
        Impact: ${impact.ifBlank { "Not recorded" }}
        User report: ${report.ifBlank { "Not recorded" }}
        Checks and evidence: ${checks.ifBlank { "Not recorded" }}
        Outcome / next action: ${nextAction.ifBlank { "Further investigation required" }}
    """.trimIndent()

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Guided support case", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Work through the facts before deciding what the problem is. A clear handover saves the next person from starting again.")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(issue, { issue = it }, label = { Text("What are you investigating?") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(impact, { impact = it }, label = { Text("Who or what is affected?") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(report, { report = it }, label = { Text("What did the person actually report?") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(checks, { checks = it }, label = { Text("What did you check, and what did you observe?") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(nextAction, { nextAction = it }, label = { Text("What happened next, or what should happen next?") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Text("Handover preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) { Text(handover, modifier = Modifier.padding(12.dp)) }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Support handover", handover))
        }, modifier = Modifier.fillMaxWidth()) { Text("Copy handover summary") }
        Text("Review the summary before sharing. Do not include passwords, verification codes or confidential details.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun WorkNotes(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(loadNotes(context)) }
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Work notes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Notes stay on this device. Do not store passwords, personal data or confidential incident details here.")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(title, { title = it }, label = { Text("Note title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(body, { body = it }, label = { Text("Details") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && body.isNotBlank()) {
                    notes = listOf(Note(title.trim(), body.trim(), System.currentTimeMillis())) + notes
                    saveNotes(context, notes)
                    title = ""; body = ""
                }
            },
            enabled = title.isNotBlank() && body.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save note on device") }
        Spacer(Modifier.height(12.dp))
        if (notes.isEmpty()) Text("No notes yet. Add a work note above.")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes) { note ->
                Card {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.body)
                        TextButton(onClick = {
                            notes = notes.filterNot { it.id == note.id }
                            saveNotes(context, notes)
                        }) { Text("Delete") }
                    }
                }
            }
        }
    }
}

private data class Note(val title: String, val body: String, val id: Long)

private fun loadNotes(context: Context): List<Note> = runCatching {
    val raw = context.getSharedPreferences("toolkit_notes", Context.MODE_PRIVATE).getString("notes", "[]") ?: "[]"
    val array = JSONArray(raw)
    (0 until array.length()).map { i ->
        val item = array.getJSONObject(i)
        Note(item.getString("title"), item.getString("body"), item.getLong("id"))
    }
}.getOrDefault(emptyList())

private fun saveNotes(context: Context, notes: List<Note>) {
    val array = JSONArray()
    notes.forEach { note ->
        array.put(JSONObject().put("title", note.title).put("body", note.body).put("id", note.id))
    }
    context.getSharedPreferences("toolkit_notes", Context.MODE_PRIVATE).edit().putString("notes", array.toString()).apply()
}
