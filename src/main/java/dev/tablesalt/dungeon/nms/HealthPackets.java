package dev.tablesalt.dungeon.nms;


import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HealthPackets {


    public static void sendRegenPacket(Player playerToSend, double healthAfterRegen) {
        ServerPlayerConnection connection = ((CraftPlayer) playerToSend).getHandle().connection;

        connection.send(new ClientboundSetHealthPacket((float) healthAfterRegen, 20, 20));
    }


}
