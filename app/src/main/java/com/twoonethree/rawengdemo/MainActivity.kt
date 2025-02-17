package com.twoonethree.rawengdemo

import GameSchedule
import Team
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = koinViewModel()) {
    val schedule by viewModel.schedule.collectAsState()
    val teams by viewModel.teams.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredSchedule = schedule.filter {
        it.arena.contains(searchQuery, ignoreCase = true) ||
                teams.find { team -> team.tid == it.homeTeamID }?.name?.contains(
                    searchQuery,
                    ignoreCase = true
                ) == true ||
                teams.find { team -> team.tid == it.visitorTeamID }?.name?.contains(
                    searchQuery,
                    ignoreCase = true
                ) == true
    }

    val lazyListState = rememberLazyListState()

    val nextUpcomingGameIndex = remember {
        schedule.indexOfFirst { game ->
            val gameDate = LocalDate.parse(game.date)
            gameDate.isAfter(LocalDate.now())
        }
    }

    LaunchedEffect(nextUpcomingGameIndex) {
        if (nextUpcomingGameIndex != -1) {
            lazyListState.scrollToItem(nextUpcomingGameIndex)
        }
    }

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Search by arena, team or city") }
        )

        LazyColumn(state = lazyListState) {
            filteredSchedule.groupBy { it.date.substring(0, 7) }
                .forEach { (month, games) ->
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = Util.formatMonth(month + "-01", "MMMM yyyy"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    items(games) { game ->
                        GameRow(game, teams)
                    }
                }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GameRow(game: GameSchedule, teams: List<Team>) {
    val homeTeam = teams.find { it.tid == game.homeTeamID }
    val visitorTeam = teams.find { it.tid == game.visitorTeamID }
    val isAppTeamHome = game.homeTeamID == "1610612748"
    val str = if (isAppTeamHome) "Home" else "Away"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.DarkGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    )
    {

        Text(
            text = "$str | ${Util.formatMonth(game.date, "EEE MMM dd")} | ${game.time}",
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
            ),
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center

        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Visitor Team Column
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = visitorTeam?.logo,
                    contentDescription = visitorTeam?.name,
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_placeholder),
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TeamNameText(visitorTeam?.name)
            }

            // Central text (vs or @)
            Text(
                text = if (isAppTeamHome) "vs" else "@",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace,
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            )

            // Home Team Column
            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamNameText(homeTeam?.name)
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = homeTeam?.logo,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_placeholder),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        val isFutureGame = game.status == 1
        val context = LocalContext.current
        if (isFutureGame) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ticketmaster.com"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text(
                    text = "Buy Ticket on Ticketmaster",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
        }
    }
}

@Composable
fun TeamNameText(name: String?) {
    Text(
        text = name ?: "Unknown",
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Yellow,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.Serif
        )
    )
}

