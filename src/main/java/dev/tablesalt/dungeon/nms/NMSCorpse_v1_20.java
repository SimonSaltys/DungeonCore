package dev.tablesalt.dungeon.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NMSCorpse_v1_20 {

    private static final List<NMSCorpse_v1_20> allCorpses = new ArrayList<>();

    private final List<ItemStack> itemsInBody;

    @Getter
    private final Player player;

    @Getter
    private ServerPlayer NMS_Corpse;

    public NMSCorpse_v1_20(Player player) {
        this.player = player;
        this.itemsInBody = new ArrayList<>();
    }


    public void makeCorpse() {
        Common.broadcast("making corpse");
        ServerPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        //making textures
        Property textures = (Property) craftPlayer.getGameProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName() + "body");
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));


        //setting up the entity
        MinecraftServer server = craftPlayer.getServer();
        ServerLevel level = craftPlayer.serverLevel().getLevel();
        ServerPlayer corpse = new ServerPlayer(server, level, gameProfile);

        //spawn the entity on the ground
        Location locationOnGround = player.getLocation().subtract(0, 0, 0).getBlock().getLocation();
        int count = 0;
        while (locationOnGround.getBlock().getType() == Material.AIR) {
            locationOnGround.subtract(0, 1, 0);

            count++;
            if (count > 20)
                return;
        }

        //make the skin overlays show
        SynchedEntityData data = corpse.getEntityData();
        byte bitmask = (byte) (0x01 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40);
        data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), bitmask);

        byte headPosition = (byte) (RandomUtils.nextBoolean() ? ((RandomUtils.nextFloat(0F, 80F) * 256f) / 360f) : ((RandomUtils.nextFloat(280F, 360F) * 256f) / 360f));
        corpse.setPos(locationOnGround.getX(), locationOnGround.getY() + 1, locationOnGround.getZ());
        corpse.setPose(Pose.SLEEPING);

        PlayerTeam team = new PlayerTeam(new Scoreboard(), corpse.getName().toString());
        team.setNameTagVisibility(Team.Visibility.NEVER);
        team.getPlayers().add(corpse.getName().getString());


        //send packets
        for (Player on : Bukkit.getOnlinePlayers()) {
            ServerPlayerConnection connection = ((CraftPlayer) on).getHandle().connection;

            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, corpse));
            connection.send(new ClientboundAddPlayerPacket(corpse));
            connection.send(new ClientboundSetEntityDataPacket(corpse.getId(), corpse.getEntityData().getNonDefaultValues()));

            connection.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));

            connection.send(new ClientboundSetEntityDataPacket(corpse.getId(), data.getNonDefaultValues()));
            connection.send(new ClientboundRotateHeadPacket(corpse, headPosition));
//
//            connection.send(new ClientboundSetEntityDataPacket(corpse.getId(), watcher.getNonDefaultValues()));
//            connection.send(movePacket);
        }

        this.NMS_Corpse = corpse;
        allCorpses.add(this);

    }

    public static void removeAllCorpses() {
        for (NMSCorpse_v1_20 corpse : allCorpses)
            removeCorpse(corpse);

    }

    private static void removeCorpse(NMSCorpse_v1_20 corpseRemove) {
        for (Player online : Bukkit.getOnlinePlayers())
            sendRemovePackets(online, corpseRemove.getNMS_Corpse());
    }


    private static void sendRemovePackets(Player player, ServerPlayer corpse) {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        ServerPlayerConnection connection = craftPlayer.getHandle().connection;

        connection.send(new ClientboundRemoveEntitiesPacket(corpse.getId()));
        connection.send(new ClientboundPlayerInfoRemovePacket(List.of(corpse.getUUID())));
    }

}
