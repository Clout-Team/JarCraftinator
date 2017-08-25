package com.cloutteam.jarcraftinator.manager;

import com.cloutteam.jarcraftinator.entity.player.OfflinePlayer;
import com.cloutteam.jarcraftinator.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private List<OfflinePlayer> playerList = new ArrayList<>();
    private List<Player> onlinePlayerList = new ArrayList<>();

    public void addOnline(Player player) {
        onlinePlayerList.add(player);
        for (OfflinePlayer op : playerList)
            if (op.getUUID().equals(player.getUUID())) return;
        player.setHasJoinedBefore(true);
        playerList.add(player);
        //TODO save to storage
    }

    public void removeOnline(Player player) {
        onlinePlayerList.remove(player);
    }

    public List<Player> getOnlinePlayers() {
        return onlinePlayerList;
    }

    public Player getPlayer(UUID uuid) {
        for (Player player : onlinePlayerList)
            if (player.getUUID().equals(uuid)) return player;
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        for (OfflinePlayer player : playerList)
            if (player.getUUID().equals(uuid)) return player;
        return null;
    }

}
