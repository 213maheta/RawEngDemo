
data class GameSchedule(
    val id: String,
    val date: String,
    val time: String,
    val arena: String,
    val arenaCity: String,
    val homeTeamID: String,
    val visitorTeamID: String,
    val status: Int // 1 - Future, 2 - Live, 3 - Past
) {
    companion object {
        fun fromJson(json: Map<String, Any?>): GameSchedule {
            return GameSchedule(
                id = json["gid"] as String,
                date = (json["gametime"] as String).substring(0, 10),
                time = (json["gametime"] as String).substring(11, 16),
                arena = json["arena_name"] as String,
                arenaCity = json["arena_city"] as String,
                homeTeamID = (json["h"] as Map<*, *>)["tid"] as String,
                visitorTeamID = (json["v"] as Map<*, *>)["tid"] as String,
                status = (json["st"] as Double).toInt()
            )
        }
    }
}


data class Team(
    val tid: String,
    val name: String,
    val logo: String,
    val primaryColor: String
) {
    companion object {
        fun fromJson(json: Map<String, Any?>): Team {
            return Team(
                tid = json["tid"] as String,
                name = json["tn"] as String,
                logo = json["logo"] as String,
                primaryColor = json["color"] as String
            )
        }
    }
}

