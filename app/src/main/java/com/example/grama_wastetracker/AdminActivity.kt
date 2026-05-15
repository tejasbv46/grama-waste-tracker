package com.example.grama_wastetracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.firebase.database.*

data class Blackspot(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val description: String = ""
)

@Composable
fun AdminScreen(language: String) {
    var blackspots by remember { mutableStateOf(listOf<Blackspot>()) }
    var tractorLat by remember { mutableStateOf(12.9716) }
    var tractorLng by remember { mutableStateOf(77.5946) }

    // Fetch blackspots from Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseDatabase.getInstance()

        // Listen to blackspots
        db.getReference("blackspots")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Blackspot>()
                    for (child in snapshot.children) {
                        val lat = child.child("lat").getValue(Double::class.java) ?: 0.0
                        val lng = child.child("lng").getValue(Double::class.java) ?: 0.0
                        val desc = child.child("description").getValue(String::class.java) ?: ""
                        list.add(Blackspot(lat, lng, desc))
                    }
                    blackspots = list
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // Listen to tractor
        db.getReference("tractor")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tractorLat = snapshot.child("lat")
                        .getValue(Double::class.java) ?: 12.9716
                    tractorLng = snapshot.child("lng")
                        .getValue(Double::class.java) ?: 77.5946
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(12.9716, 77.5946), 13f
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Tractor marker (green)
            Marker(
                state = MarkerState(LatLng(tractorLat, tractorLng)),
                title = "Kachara Gaadi",
                snippet = "Live Tractor Location",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN
                )
            )

            // Blackspot markers (red)
            blackspots.forEach { spot ->
                Marker(
                    state = MarkerState(LatLng(spot.lat, spot.lng)),
                    title = if (language == "en") "Blackspot" else "ಕಪ್ಪು ಚುಕ್ಕೆ",
                    snippet = spot.description,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
            }
        }

        // Stats card at top
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1565C0)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (language == "en") "Admin Panel" else "ಆಡಳಿತ ಫಲಕ",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    "${blackspots.size} " +
                            if (language == "en") "Reports" else "ವರದಿಗಳು",
                    color = Color.Yellow,
                    fontSize = 16.sp
                )
            }
        }

        // Legend at bottom
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("🟢 " + if (language == "en") "Tractor" else "ಟ್ರ್ಯಾಕ್ಟರ್")
                Text("🔴 " + if (language == "en") "Blackspot" else "ಕಪ್ಪು ಚುಕ್ಕೆ")
            }
        }
    }
}