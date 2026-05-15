package com.example.grama_wastetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.grama_wastetracker.ui.theme.GramawasteTrackerTheme
import com.google.firebase.database.*

object AppStrings {
    val liveMap = mapOf("en" to "Live Map", "kn" to "ನೇರ ನಕ್ಷೆ")
    val report = mapOf("en" to "Report", "kn" to "ವರದಿ")
    val wasteGuide = mapOf("en" to "Waste Guide", "kn" to "ತ್ಯಾಜ್ಯ ಮಾರ್ಗದರ್ಶಿ")
    val tractorNearby = mapOf("en" to "🚛 Tractor is 2 streets away!", "kn" to "🚛 ಟ್ರ್ಯಾಕ್ಟರ್ 2 ಬೀದಿ ದೂರದಲ್ಲಿದೆ!")
    val fetching = mapOf("en" to "Fetching tractor location...", "kn" to "ಟ್ರ್ಯಾಕ್ಟರ್ ಸ್ಥಳ ತರುತ್ತಿದೆ...")
    val reportBlackspot = mapOf("en" to "Report Blackspot", "kn" to "ಕಪ್ಪು ಚುಕ್ಕೆ ವರದಿ ಮಾಡಿ")
    val takePhoto = mapOf("en" to "Take Photo", "kn" to "ಫೋಟೋ ತೆಗೆಯಿರಿ")
    val describeIssue = mapOf("en" to "Describe the issue", "kn" to "ಸಮಸ್ಯೆ ವಿವರಿಸಿ")
    val gpsAttached = mapOf("en" to "GPS location will be attached automatically", "kn" to "GPS ಸ್ವಯಂಚಾಲಿತವಾಗಿ ಲಗತ್ತಿಸಲಾಗುತ್ತದೆ")
    val submit = mapOf("en" to "Submit Report to Panchayat", "kn" to "ಪಂಚಾಯತಿಗೆ ಸಲ್ಲಿಸಿ")
    val submitted = mapOf("en" to "✅ Report submitted!", "kn" to "✅ ಪಂಚಾಯತಿಗೆ ವರದಿ ಸಲ್ಲಿಸಲಾಗಿದೆ!")
    val wasteTitle = mapOf("en" to "Waste Separation Guide", "kn" to "ತ್ಯಾಜ್ಯ ವಿಂಗಡಣೆ ಮಾರ್ಗದರ್ಶಿ")
    val dryWaste = mapOf("en" to "🟡 DRY WASTE", "kn" to "🟡 ಒಣ ತ್ಯಾಜ್ಯ")
    val dryItems = mapOf("en" to "• Paper\n• Plastic bottles\n• Glass & Metal\n• Cloth",
        "kn" to "• ಕಾಗದ\n• ಪ್ಲಾಸ್ಟಿಕ್ ಬಾಟಲಿ\n• ಗಾಜು ಮತ್ತು ಲೋಹ\n• ಬಟ್ಟೆ")
    val wetWaste = mapOf("en" to "🟢 WET WASTE", "kn" to "🟢 ಹಸಿ ತ್ಯಾಜ್ಯ")
    val wetItems = mapOf("en" to "• Food scraps\n• Vegetable peels\n• Leaves\n• Cooked food",
        "kn" to "• ಆಹಾರ ತ್ಯಾಜ್ಯ\n• ತರಕಾರಿ ಸಿಪ್ಪೆ\n• ಎಲೆಗಳು\n• ಬೇಯಿಸಿದ ಆಹಾರ")
    val tip = mapOf("en" to "💡 TIP: Keep TWO bins at home!", "kn" to "💡 ಸಲಹೆ: ಮನೆಯಲ್ಲಿ ಎರಡು ಡಬ್ಬಿ ಇಡಿ!")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GramawasteTrackerTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var language by remember { mutableStateOf("en") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (language == "en") "Grama Waste Tracker"
                        else "ಗ್ರಾಮ ತ್ಯಾಜ್ಯ ಟ್ರ್ಯಾಕರ್"
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("EN", fontSize = 12.sp,
                            color = if (language == "en") Color.Blue else Color.Gray)
                        Switch(
                            checked = language == "kn",
                            onCheckedChange = { language = if (it) "kn" else "en" },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Text("ಕನ್ನಡ", fontSize = 12.sp,
                            color = if (language == "kn") Color.Blue else Color.Gray)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Map") },
                    label = { Text(AppStrings.liveMap[language]!!) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Report") },
                    label = { Text(AppStrings.report[language]!!) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Guide") },
                    label = { Text(AppStrings.wasteGuide[language]!!) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Admin") },
                    label = { Text(if (language == "en") "Admin" else "ಆಡಳಿತ") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> MapScreen(language)
                1 -> ReportScreen(language)
                2 -> WasteGuideScreen(language)
                3 -> AdminScreen(language)
            }
        }
    }
}

@Composable
fun MapScreen(language: String) {
    var tractorLat by remember { mutableDoubleStateOf(12.9716) }
    var tractorLng by remember { mutableDoubleStateOf(77.5946) }
    var statusText by remember { mutableStateOf("") }

    statusText = AppStrings.fetching[language]!!

    LaunchedEffect(Unit) {
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("tractor")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("lat").getValue(Double::class.java)
                val lng = snapshot.child("lng").getValue(Double::class.java)
                val status = snapshot.child("status").getValue(String::class.java)
                if (lat != null && lng != null) {
                    tractorLat = lat
                    tractorLng = lng
                }
                statusText = if (status == "active")
                    AppStrings.tractorNearby[language]!!
                else
                    AppStrings.fetching[language]!!
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    val tractorPosition = LatLng(tractorLat, tractorLng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tractorPosition, 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = tractorPosition),
                title = "Kachara Gaadi",
                snippet = "Waste Collection Tractor"
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(statusText, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ReportScreen(language: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var description by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = android.provider.MediaStore.Images.Media.insertImage(
                context.contentResolver, bitmap,
                "Blackspot_${System.currentTimeMillis()}", null
            )
            imageUri = android.net.Uri.parse(path)
        }
    }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(AppStrings.reportBlackspot[language]!!, fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp))

        if (imageUri != null) {
            Card(modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 16.dp)) {
                coil.compose.AsyncImage(
                    model = imageUri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            OutlinedButton(
                onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(if (language == "en") "Retake Photo" else "ಮತ್ತೆ ಫೋಟೋ ತೆಗೆಯಿರಿ")
            }
        } else {
            Button(
                onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Add, contentDescription = null,
                        tint = Color.Gray, modifier = Modifier.size(40.dp))
                    Text(AppStrings.takePhoto[language]!!, color = Color.Gray)
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(AppStrings.describeIssue[language]!!) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            minLines = 3
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2196F3))
                Spacer(modifier = Modifier.width(8.dp))
                Text(AppStrings.gpsAttached[language]!!)
            }
        }

        Button(
            onClick = {
                isUploading = true
                val db = FirebaseDatabase.getInstance()
                val ref = db.getReference("blackspots")
                val key = ref.push().key ?: return@Button
                if (imageUri != null) {
                    val storageRef = com.google.firebase.storage.FirebaseStorage
                        .getInstance().reference.child("blackspots/$key.jpg")
                    storageRef.putFile(imageUri!!).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            val report = mapOf(
                                "description" to description,
                                "lat" to 12.9716, "lng" to 77.5946,
                                "imageUrl" to downloadUrl.toString(),
                                "timestamp" to System.currentTimeMillis()
                            )
                            ref.child(key).setValue(report)
                            isUploading = false
                            submitted = true
                        }
                    }
                } else {
                    val report = mapOf(
                        "description" to description,
                        "lat" to 12.9716, "lng" to 77.5946,
                        "timestamp" to System.currentTimeMillis()
                    )
                    ref.child(key).setValue(report)
                    isUploading = false
                    submitted = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(AppStrings.submit[language]!!, fontSize = 16.sp)
            }
        }

        if (submitted) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(AppStrings.submitted[language]!!, color = Color(0xFF4CAF50), fontSize = 16.sp)
        }
    }
}

@Composable
fun WasteGuideScreen(language: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(AppStrings.wasteTitle[language]!!, fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(AppStrings.dryWaste[language]!!, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(AppStrings.dryItems[language]!!)
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(AppStrings.wetWaste[language]!!, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(AppStrings.wetItems[language]!!)
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(AppStrings.tip[language]!!, fontSize = 16.sp)
            }
        }
    }
}