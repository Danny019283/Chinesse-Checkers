package Model.Entities;

public class GameStats {
    private int gameId;
    private String winner;
    private String winnerColor;
    private String[] namePlayers;

    public GameStats(int gameId, String winner, String winnerColor, String[] namePlayers) {
        this.setGameId(gameId);
        this.setWinner(winner);
        this.setWinnerColor(winnerColor);
        this.setNamePlayers(namePlayers);
    }

    public GameStats() {
        gameId = 0;
        winner = "";
        winnerColor = "";
        namePlayers = new String[0];
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinnerColor() {
        return winnerColor;
    }

    public void setWinnerColor(String winnerColor) {
        this.winnerColor = winnerColor;
    }

    public String[] getNamePlayers() {
        return namePlayers;
    }

    public void setNamePlayers(String[] namePlayers) {
        this.namePlayers = namePlayers;
    }

    @Override
    public String toString() {
        StringBuilder players = new StringBuilder();
        for (String name : namePlayers) {
            players.append(name).append(" ");
        }
        return "Game ID:" + gameId +
                "Winner: '" + winner + '\n' +
                "Winner Color: " + winnerColor + '\n' +
                "Players: " + players.toString().trim() + '\n';
    }
}
