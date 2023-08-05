package dev.tablesalt.dungeon.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.util.EntityUtil;
import lombok.Getter;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerCorpse {

    private static final List<PlayerCorpse> allCorpses = new ArrayList<>();

    @Getter
    private final Player player;

    private ServerPlayer NMSCorpse;

    @Getter
    private ItemStack[] items;

    @Getter
    private ItemStack[] armor;

    @Getter
    private ItemStack offHand;

    @Getter
    private TextDisplay nameTag;

    public PlayerCorpse(Player player) {
        this.player = player;
    }

    /**
     * Makes a corpse of the player.
     */
    public void makeCorpse() {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        ServerPlayer corpse = new ServerPlayer(serverPlayer.getServer(), serverPlayer.serverLevel().getLevel(), makeGameProfile(craftPlayer));

        setSkinOverlay(corpse);

        Location locationOnGround = getGroundLocation().add(0, 1, 0);
        corpse.setPos(locationOnGround.getX(), locationOnGround.getY(), locationOnGround.getZ());
        corpse.setPose(Pose.SLEEPING);

        PlayerTeam team = addToTeam(corpse);
        items = player.getInventory().getStorageContents();
        armor = player.getInventory().getArmorContents();
        offHand = player.getInventory().getItemInOffHand();

        sendPackets(corpse, team);

        this.nameTag = EntityUtil.createTextDisplay(locationOnGround, Keys.DEAD_BODY_NAME + player.getName());
        nameTag.getTransformation().getScale().set(1.0);
        CompMetadata.setMetadata(nameTag, Keys.DEAD_BODY_NAME, player.getName());

        this.NMSCorpse = corpse;
        allCorpses.add(this);

    }

//    public ItemStack[] getItems() {
//        ItemStack[] items = new ItemStack[36];
//        Inventory inventory = NMSCorpse.getInventory();
//
//        for (int i = 0; i < items.length; i++)
//            items[i] = CraftItemStack.asBukkitCopy(inventory.getItem(i));
//
//        return items;
//    }

//    public ItemStack[] getArmorContents() {
//        ItemStack[] armor = new ItemStack[3];
//
//        Iterator<net.minecraft.world.item.ItemStack> itr = NMSCorpse.getArmorSlots().iterator();
//
//        int count = 0;
//        while (itr.hasNext()) {
//            armor[count] = CraftItemStack.asBukkitCopy(itr.next());
//        }
//
//        return armor;
//    }

    public static void removeAllCorpses() {

        Iterator<PlayerCorpse> itr = allCorpses.listIterator();
        while (itr.hasNext()) {
            PlayerCorpse corpse = itr.next();
            removeCorpse(corpse);
            itr.remove();
        }

    }

    public static PlayerCorpse getFromPlayerName(String name) {
        for (PlayerCorpse corpse : allCorpses)
            if (corpse.getPlayer().getName().equals(name))
                return corpse;

        return null;
    }


    /*----------------------------------------------------------------*/
    /* PRIVATE HELPER METHODS */
    /*----------------------------------------------------------------*/

    private GameProfile makeGameProfile(CraftPlayer player) {
        ServerPlayer craftPlayer = player.getHandle();

        //making textures
        Property textures = (Property) craftPlayer.getGameProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName() + "body");
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));
        return gameProfile;
    }

    private Location getGroundLocation() {
        Location location = player.getLocation().subtract(0, 0, 0).getBlock().getLocation();

        //simple brute force algorithm to find the lowest non air block
        int count = 0;
        while (location.getBlock().getType() == Material.AIR) {
            location.subtract(0, 1, 0);

            count++;
            if (count > 20)
                break;
        }

        return location;
    }

    private void setInventory(Player player, ServerPlayer corpse) {
        for (int i = 0; i < player.getInventory().getSize(); i++)
            corpse.getInventory().setItem(i, CraftItemStack.asNMSCopy(player.getInventory().getItem(i)));

        for (int i = 0; i < player.getInventory().getArmorContents().length; i++)
            corpse.getInventory().getArmorContents().add(CraftItemStack.asNMSCopy(player.getInventory().getArmorContents()[i]));
    }

    /**
     * Sets the 3d parts of the minecraft players skin
     * to the corpse
     */
    private void setSkinOverlay(ServerPlayer corpse) {
        SynchedEntityData data = corpse.getEntityData();
        byte bitmask = (byte) (0x01 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40);
        data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), bitmask);
    }

    /**
     * Makes a new team which we will
     * add players to hide the name tags
     */
    private PlayerTeam addToTeam(ServerPlayer corpse) {
        PlayerTeam team = new PlayerTeam(new Scoreboard(), corpse.getName().toString());
        team.setNameTagVisibility(Team.Visibility.NEVER);
        team.getPlayers().add(corpse.getName().getString());

        return team;
    }

    /**
     * sends all relevant packets to all players
     * which will show them the corpse
     */
    private void sendPackets(ServerPlayer corpse, PlayerTeam team) {
        //send packets
        for (Player on : Bukkit.getOnlinePlayers()) {
            ServerPlayerConnection connection = ((CraftPlayer) on).getHandle().connection;

            //showing the corpse
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, corpse));
            connection.send(new ClientboundAddPlayerPacket(corpse));
            connection.send(new ClientboundSetEntityDataPacket(corpse.getId(), corpse.getEntityData().getNonDefaultValues()));

            //adding the team to hide the name tag
            connection.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));

            //rotating the corpse randomly
            connection.send(new ClientboundMoveEntityPacket.Rot(corpse.getId(),
                    (byte) ((player.getLocation().getY()) - 1.7 - player.getLocation().getY() * 32),
                    (byte) 0,
                    false));

            //rotating the corpses head randomly
            byte headPosition = (byte) (RandomUtils.nextBoolean() ? ((RandomUtils.nextFloat(0F, 80F) * 256f) / 360f)
                    : ((RandomUtils.nextFloat(280F, 360F) * 256f) / 360f));
            connection.send(new ClientboundRotateHeadPacket(corpse, headPosition));
        }
    }

    private static void removeCorpse(PlayerCorpse corpseRemove) {
        for (Player online : Bukkit.getOnlinePlayers())
            sendRemovePackets(online, corpseRemove.getNMSCorpse());

        corpseRemove.getNameTag().remove();
    }


    private static void sendRemovePackets(Player player, ServerPlayer corpse) {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        ServerPlayerConnection connection = craftPlayer.getHandle().connection;

        connection.send(new ClientboundRemoveEntitiesPacket(corpse.getId()));
        connection.send(new ClientboundPlayerInfoRemovePacket(List.of(corpse.getUUID())));
    }

    private ServerPlayer getNMSCorpse() {
        return NMSCorpse;
    }

}
