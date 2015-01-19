package com.kevintcoughlin.sightstone.models;

import org.parceler.Parcel;

/**
 * @TODO: Implement the following data models
 * assists	long	Number of assists
    champLevel	long	Champion level achieved
    combatPlayerScore	long	If game was a dominion game, player's combat score, otherwise 0
    deaths	long	Number of deaths
    doubleKills	long	Number of double kills
    firstBloodAssist	boolean	Flag indicating if participant got an assist on first blood
    firstBloodKill	boolean	Flag indicating if participant got first blood
    firstInhibitorAssist	boolean	Flag indicating if participant got an assist on the first inhibitor
    firstInhibitorKill	boolean	Flag indicating if participant destroyed the first inhibitor
    firstTowerAssist	boolean	Flag indicating if participant got an assist on the first tower
    firstTowerKill	boolean	Flag indicating if participant destroyed the first tower
    goldEarned	long	Gold earned
    goldSpent	long	Gold spent
    inhibitorKills	long	Number of inhibitor kills
    item0	long	First item ID
    item1	long	Second item ID
    item2	long	Third item ID
    item3	long	Fourth item ID
    item4	long	Fifth item ID
    item5	long	Sixth item ID
    item6	long	Seventh item ID
    killingSprees	long	Number of killing sprees
    kills	long	Number of kills
    largestCriticalStrike	long	Largest critical strike
    largestKillingSpree	long	Largest killing spree
    largestMultiKill	long	Largest multi kill
    magicDamageDealt	long	Magical damage dealt
    magicDamageDealtToChampions	long	Magical damage dealt to champions
    magicDamageTaken	long	Magic damage taken
    minionsKilled	long	Minions killed
    neutralMinionsKilled	long	Neutral minions killed
    neutralMinionsKilledEnemyJungle	long	Neutral jungle minions killed in the enemy team's jungle
    neutralMinionsKilledTeamJungle	long	Neutral jungle minions killed in your team's jungle
    nodeCapture	long	If game was a dominion game, number of node captures
    nodeCaptureAssist	long	If game was a dominion game, number of node capture assists
    nodeNeutralize	long	If game was a dominion game, number of node neutralizations
    nodeNeutralizeAssist	long	If game was a dominion game, number of node neutralization assists
    objectivePlayerScore	long	If game was a dominion game, player's objectives score, otherwise 0
    pentaKills	long	Number of penta kills
    physicalDamageDealt	long	Physical damage dealt
    physicalDamageDealtToChampions	long	Physical damage dealt to champions
    physicalDamageTaken	long	Physical damage taken
    quadraKills	long	Number of quadra kills
    sightWardsBoughtInGame	long	Sight wards purchased
    teamObjective	long	If game was a dominion game, number of completed team objectives (i.e., quests)
    totalDamageDealt	long	Total damage dealt
    totalDamageDealtToChampions	long	Total damage dealt to champions
    totalDamageTaken	long	Total damage taken
    totalHeal	long	Total heal amount
    totalPlayerScore	long	If game was a dominion game, player's total score, otherwise 0
    totalScoreRank	long	If game was a dominion game, team rank of the player's total score (e.g., 1-5)
    totalTimeCrowdControlDealt	long	Total dealt crowd control time
    totalUnitsHealed	long	Total units healed
    towerKills	long	Number of tower kills
    tripleKills	long	Number of triple kills
    trueDamageDealt	long	True damage dealt
    trueDamageDealtToChampions	long	True damage dealt to champions
    trueDamageTaken	long	True damage taken
    unrealKills	long	Number of unreal kills
    visionWardsBoughtInGame	long	Vision wards purchased
    wardsKilled	long	Number of wards killed
    wardsPlaced	long	Number of wards placed
    winner	boolean	Flag indicating whether or not the participant won
 */
@Parcel
public class ParticipantStats {
    private boolean winner;

    public ParticipantStats() {

    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}
