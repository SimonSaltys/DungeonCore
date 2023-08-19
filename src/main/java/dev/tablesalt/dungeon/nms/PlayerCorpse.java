package dev.tablesalt.dungeon.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.menu.TBSMenu;
import dev.tablesalt.dungeon.util.EntityUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import lombok.Getter;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerCorpse {

    private static final List<PlayerCorpse> allCorpses = new ArrayList<>();

    @Getter
    protected final Player player;

    protected ServerPlayer NMSCorpse;

    @Getter
    protected final ItemStack[] mainContent = new ItemStack[56];

    @Getter
    protected final ItemStack[] armor = new ItemStack[10];

    @Getter
    protected TextDisplay nameTag;

    protected CorpseMenu lootableMenu;

    private final byte headRotation;

    private final byte bodyRotation;

    public PlayerCorpse(Player player) {
        this.player = player;
        lootableMenu = new CorpseMenu();

        //randomness and basic math to give the body some character
        this.headRotation = (byte) (RandomUtils.nextBoolean() ? ((RandomUtils.nextFloat(0F, 80F) * 256f) / 360f)
                : ((RandomUtils.nextFloat(280F, 360F) * 256f) / 360f));

        this.bodyRotation = (byte) ((player.getLocation().getY()) - 1.7 - player.getLocation().getY() * 32);

        //loot contents for the menu, we are deep copying here because we don't want references to the original itemstacks
        deepCopy(player.getInventory().getStorageContents(), mainContent);
        deepCopy(player.getInventory().getArmorContents(), armor);
        ArrayUtils.add(armor, player.getInventory().getItemInOffHand());
        allCorpses.add(this);
    }

    /**
     * Makes a corpse of the player.
     */
    public void makeCorpse() {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        ServerPlayer corpse = new ServerPlayer(serverPlayer.getServer(), serverPlayer.serverLevel().getLevel(), makeGameProfile(craftPlayer));
        this.NMSCorpse = corpse;

        setSkinOverlay();

        Location locationOnGround = getGroundLocation().add(0, 1, 0);
        corpse.setPos(locationOnGround.getX(), locationOnGround.getY(), locationOnGround.getZ());
        corpse.setPose(Pose.SLEEPING);

        PlayerTeam team = addToTeam(corpse);

        sendPackets(team);
        updateArmorOnBody();

        this.nameTag = EntityUtil.createTextDisplay(locationOnGround, " Name here");
        nameTag.getTransformation().getScale().set(1.0);
        CompMetadata.setMetadata(nameTag, Keys.DEAD_BODY_NAME, player.getName());
    }

    public void displayLootTo(Player player) {
        lootableMenu.displayOnlyTo(player);
    }

    public void updateArmorOnBody() {
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> updatedArmor = Arrays.asList(
                new Pair<>(net.minecraft.world.entity.EquipmentSlot.HEAD, getArmorToDisplay(org.bukkit.inventory.EquipmentSlot.HEAD)),
                new Pair<>(net.minecraft.world.entity.EquipmentSlot.CHEST, getArmorToDisplay(org.bukkit.inventory.EquipmentSlot.CHEST)),
                new Pair<>(net.minecraft.world.entity.EquipmentSlot.LEGS, getArmorToDisplay(org.bukkit.inventory.EquipmentSlot.LEGS)),
                new Pair<>(net.minecraft.world.entity.EquipmentSlot.FEET, getArmorToDisplay(org.bukkit.inventory.EquipmentSlot.FEET))
        );

        for (Player on : Bukkit.getOnlinePlayers()) {
            ServerPlayerConnection connection = ((CraftPlayer) on).getHandle().connection;
            connection.send(new ClientboundSetEquipmentPacket(NMSCorpse.getId(), updatedArmor));
        }
    }

    public static void removeAllCorpses() {

        Iterator<PlayerCorpse> itr = allCorpses.listIterator();
        while (itr.hasNext()) {
            PlayerCorpse corpse = itr.next();
            removeCorpseFromList(corpse, false);
            itr.remove();
        }

    }

    /**
     * passing in boolean flag here, so we don't get
     * a concurrent modification error.
     */
    private static void removeCorpseFromList(PlayerCorpse playerCorpse, boolean removeFromList) {
        if (removeFromList)
            allCorpses.remove(playerCorpse);

        for (Player online : Bukkit.getOnlinePlayers())
            sendRemovePackets(online, playerCorpse.getNMSCorpse());

        playerCorpse.getNameTag().remove();
    }

    public static void removeCorpse(PlayerCorpse corpseRemove) {
        removeCorpseFromList(corpseRemove, true);
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

    private void deepCopy(ItemStack[] from, ItemStack[] to) {
        int length = Math.min(from.length, to.length); // Use the smaller length to prevent out of bounds

        for (int i = 0; i < length; i++) {
            if (from[i] == null)
                continue;

            to[i] = from[i].clone();
        }
    }

    private GameProfile makeGameProfile(CraftPlayer player) {
        ServerPlayer craftPlayer = player.getHandle();

        //making textures
        Property textures = (Property) craftPlayer.getGameProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "body" + RandomUtils.nextInt(0, 100));
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

    /**
     * Sets the 3d parts of the minecraft players skin
     * to the corpse
     */
    private void setSkinOverlay() {
        SynchedEntityData data = NMSCorpse.getEntityData();
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
    private void sendPackets(PlayerTeam team) {
        //send packets
        for (Player on : Bukkit.getOnlinePlayers()) {
            ServerPlayerConnection connection = ((CraftPlayer) on).getHandle().connection;

            //showing the corpse
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, NMSCorpse));
            connection.send(new ClientboundAddPlayerPacket(NMSCorpse));
            connection.send(new ClientboundSetEntityDataPacket(NMSCorpse.getId(), NMSCorpse.getEntityData().getNonDefaultValues()));

            //adding the team to hide the name tag
            connection.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));

            //rotating the corpse randomly
            connection.send(new ClientboundMoveEntityPacket.Rot(NMSCorpse.getId(),
                    bodyRotation,
                    (byte) 0,
                    false));

            //rotating the corpses head randomly
            connection.send(new ClientboundRotateHeadPacket(NMSCorpse, headRotation));
        }
    }


    private net.minecraft.world.item.ItemStack getArmorToDisplay(org.bukkit.inventory.EquipmentSlot slot) {
        return CraftItemStack.asNMSCopy(TBSItemUtil.ArmorSlotMapper.getInstance().getItemStackTypeInArmor(armor, slot));
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

    protected class CorpseMenu extends TBSMenu {

        private static final CompMaterial fillerMaterial = CompMaterial.WHITE_STAINED_GLASS_PANE;

        protected static final SlotPair ARMOR_SLOTS = new SlotPair(0, 9);

        protected static final SlotPair MAIN_CONTENTS = new SlotPair(9, 36);

        protected static final SlotPair HOTBAR = new SlotPair(45, 54);

        private CorpseMenu() {
            setSize(9 * 6);
            setTitle(Keys.DEAD_BODY_NAME + player.getName());
        }

        protected void displayOnlyTo(Player possibleViewer) {
            if (getViewer() == null)
                displayTo(possibleViewer);
            else
                Common.tellNoPrefix(possibleViewer, MessageUtil.makeError(getViewer().getName()
                        + " is currently looting the body of " + player.getName()));
        }

        @Override
        protected void onDisplay(InventoryDrawer drawer) {
            for (int i = ARMOR_SLOTS.getStartingSlot(); i < ARMOR_SLOTS.getFinalSlot(); i++) {
                drawer.setItem(i, armor[i - ARMOR_SLOTS.getStartingSlot()]);
            }

            for (int i = MAIN_CONTENTS.getStartingSlot(); i < MAIN_CONTENTS.getFinalSlot(); i++) {
                drawer.setItem(i, mainContent[i]);
            }

            //separator for hotbar
            for (int i = 36; i <= 44; i++) {
                drawer.setItem(i, ItemCreator.of(fillerMaterial, " ").make());
            }

            for (int i = HOTBAR.getStartingSlot(); i < HOTBAR.getFinalSlot(); i++) {

                drawer.setItem(i, mainContent[i - HOTBAR.getStartingSlot()]);
            }
        }


        @Override
        protected boolean isActionAllowed(MenuClickLocation location, int slot, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {
            return clicked == null || clicked.getType() != fillerMaterial.toMaterial();
        }

        @Override
        protected void onMenuClose(Player player, Inventory inventory) {
            for (int i = ARMOR_SLOTS.getStartingSlot(); i < ARMOR_SLOTS.getFinalSlot(); i++) {
                armor[i] = inventory.getItem(i);
            }

            for (int i = MAIN_CONTENTS.getStartingSlot(); i < MAIN_CONTENTS.getFinalSlot(); i++) {
                mainContent[i] = inventory.getItem(i);
            }

            for (int i = HOTBAR.getStartingSlot(); i < HOTBAR.getFinalSlot(); i++) {
                mainContent[i] = inventory.getItem(i);
            }

            lootableMenu = newInstance();
            updateArmorOnBody();
        }

        @Override
        public CorpseMenu newInstance() {
            return new CorpseMenu();
        }

        protected static class SlotPair {

            private final Pair<Integer, Integer> pair;

            public SlotPair(Integer startingSlot, Integer finalSlot) {
                pair = new Pair<>(startingSlot, finalSlot);
            }


            public final Integer getStartingSlot() {
                return pair.getFirst();
            }

            public final Integer getFinalSlot() {
                return pair.getSecond();
            }

        }

    }
}
