package DataAccess;

import DataAccess.Exceptions.GlobalException;
import DataAccess.Exceptions.NoDataException;
import Model.Entities.GameStats;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GameStatsDAO extends OracleConnection {

    private static GameStatsDAO instance;
    public static GameStatsDAO getInstance () {
        if (instance == null) {
            instance = new GameStatsDAO();
        }
        return instance;
    }

    private static final String INSERT_GAME_STAT = "{call insert_game_stat(?, ?, ?, ?, ?)}";
    private static final String UPDATE_GAME_STAT = "{call update_game_stat(?, ?, ?, ?, ?)}";
    private static final String DELETE_GAME_STAT = "{call delete_game_stat(?)}";
    private static final String FIND_GAME_STAT = "{?=call find_game_stat(?)}";
    private static final String FIND_ALL_GAME_STATS = "{?=call find_all_game_stats()}";
    private static final String COUNT_GAME_STATS = "{?=call count_game_stats()}";

    public void insertGameStat(GameStats gameStat) throws GlobalException, NoDataException {
        connect();
        CallableStatement cst = null;
        try {
            cst = connection.prepareCall(INSERT_GAME_STAT);
            cst.setInt(1, gameStat.getGameId());
            cst.setString(2, gameStat.getWinner());
            cst.setString(3, gameStat.getWinnerColor());
            ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("STRING_ARRAY", connection);
            ARRAY array = new ARRAY(descriptor, connection, gameStat.getNamePlayers());
            cst.setArray(4, array);
            cst.registerOutParameter(5, java.sql.Types.VARCHAR);
            boolean result = cst.execute();
            if (result) {
                throw new NoDataException("Insertion did not complete");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GlobalException("Duplicate key");
        } finally {
            try {
                if (cst != null) {
                    cst.close();
                }
                disconnect();
            } catch (SQLException e) {
                throw new GlobalException("Invalid or null statements");
            }
        }
    }

    public void updateGameStat(GameStats gameStat) throws GlobalException, NoDataException {
        connect();
        CallableStatement cst = null;
        try {
            cst = connection.prepareCall(UPDATE_GAME_STAT);
            cst.setInt(1, gameStat.getGameId());
            cst.setString(2, gameStat.getWinner());
            cst.setString(3, gameStat.getWinnerColor());
            ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("STRING_ARRAY", connection);
            ARRAY array = new ARRAY(descriptor, connection, gameStat.getNamePlayers());
            cst.setArray(4, array);
            cst.registerOutParameter(5, java.sql.Types.VARCHAR);
            int result = cst.executeUpdate();
            if (result == 0) {
                throw new NoDataException("Update did not complete");
            }
        } catch (SQLException e) {
            throw new GlobalException("Invalid statement");
        } finally {
            try {
                if (cst != null) {
                    cst.close();
                }
                disconnect();
            } catch (SQLException e) {
                throw new GlobalException("Invalid or null statements");
            }
        }
    }

    public void deleteGameStat(int id) throws GlobalException, NoDataException {
        connect();
        CallableStatement cst = null;
        try {
            cst = connection.prepareCall(DELETE_GAME_STAT);
            cst.setInt(1, id);
            int result = cst.executeUpdate();
            if (result == 0) {
                throw new NoDataException("Deletion did not complete");
            }
        } catch (SQLException e) {
            throw new GlobalException("Invalid statement");
        } finally {
            try {
                if (cst != null) {
                    cst.close();
                }
                disconnect();
            } catch (SQLException e) {
                throw new GlobalException("Invalid or null statements");
            }
        }
    }

    public GameStats findGameStat(int id) throws GlobalException, NoDataException {
        connect();
        ResultSet rs = null;
        GameStats gameStat = null;
        CallableStatement cst = null;
        try {
            cst = connection.prepareCall(FIND_GAME_STAT);
            cst.registerOutParameter(1, OracleTypes.CURSOR);
            cst.setInt(2, id);
            cst.execute();
            rs = (ResultSet) cst.getObject(1);

            if (rs.next()) {
                gameStat = new GameStats();
                gameStat.setGameId(rs.getInt("id"));
                gameStat.setWinner(rs.getString("player"));
                gameStat.setWinnerColor(rs.getString("win"));
                Array sqlArray = rs.getArray("name_players");
                if (sqlArray != null) {
                    String[] players = (String[]) sqlArray.getArray();
                    gameStat.setNamePlayers(players);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GlobalException("Invalid statement");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cst != null) {
                    cst.close();
                }
                disconnect();
            } catch (SQLException e) {
                throw new GlobalException("Invalid or null statements");
            }
        }
        if (gameStat == null) {
            throw new NoDataException("Record not found");
        }
        return gameStat;
    }

    public ArrayList<GameStats> findAllGameStats() throws GlobalException, NoDataException {
        connect();
        ResultSet rs = null;
        ArrayList<GameStats> collection = new ArrayList<>();
        CallableStatement cst = null;
        try {
            cst = connection.prepareCall(FIND_ALL_GAME_STATS);
            cst.registerOutParameter(1, OracleTypes.CURSOR);
            cst.execute();
            rs = (ResultSet) cst.getObject(1);
            while (rs.next()) {
                GameStats gameStat = new GameStats();
                gameStat.setGameId(rs.getInt("id"));
                gameStat.setWinner(rs.getString("player"));
                gameStat.setWinnerColor(rs.getString("win"));
                Array sqlArray = rs.getArray("name_players");
                if (sqlArray != null) {
                    String[] players = (String[]) sqlArray.getArray();
                    gameStat.setNamePlayers(players);
                }
                collection.add(gameStat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GlobalException("Invalid statement");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cst != null) {
                    cst.close();
                }
                disconnect();
            } catch (SQLException e) {
                throw new GlobalException("Invalid or null statements");
            }
        }
        if (collection.isEmpty()) {
            throw new NoDataException("No data");
        }
        return collection;
    }

    public int countGameStats() throws GlobalException, NoDataException {
        connect();
        CallableStatement cst = null;
        int count = 0;
        try {
            cst = connection.prepareCall(COUNT_GAME_STATS);
            cst.registerOutParameter(1, OracleTypes.INTEGER);
            cst.execute();
            count = cst.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GlobalException("Invalid statement or function call");
        } finally {
            try {
                if (cst != null) {
                    cst.close();
                }
                disconnect();
            } catch (SQLException e) {
                throw new GlobalException("Invalid or null statements");
            }
        }
        if (count == 0) {
            throw new NoDataException("No records found");
        }
        return count;
    }
}