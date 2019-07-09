package me.libraryaddict.disguise.utilities.packets.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.LibsDisguises;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.EntityPose;
import me.libraryaddict.disguise.utilities.DisguiseUtilities;
import me.libraryaddict.disguise.utilities.packets.LibsPackets;
import me.libraryaddict.disguise.utilities.packets.PacketsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PacketListenerMain extends PacketAdapter {
    public PacketListenerMain(LibsDisguises plugin, ArrayList<PacketType> packetsToListen) {
        super(plugin, ListenerPriority.HIGH, packetsToListen);
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        if (event.isCancelled())
            return;

        final Player observer = event.getPlayer();

        if (observer.getName().contains("UNKNOWN[")) // If the player is temporary
            return;

        // First get the entity, the one sending this packet

        int entityId = event.getPacket().getIntegers().read(Server.COLLECT == event.getPacketType() ? 1 : 0);

        final Disguise disguise = DisguiseUtilities.getDisguise(observer, entityId);

        // If the entity is the same as the sender. Don't disguise!
        // Prevents problems and there is no advantage to be gained.
        // Or if they are null and there's no disguise
        if (disguise == null || disguise.getEntity() == observer) {
            return;
        }

        LibsPackets packets;

        try {
            packets = PacketsManager.getPacketsHandler()
                    .transformPacket(event.getPacket(), disguise, observer, disguise.getEntity());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            event.setCancelled(true);
            return;
        }

        if (packets.isUnhandled()) {
            return;
        }

        packets.setSpawnPacketCheck(event.getPacketType());

        event.setCancelled(true);

        try {
            for (PacketContainer packet : packets.getPackets()) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(observer, packet, false);
            }

            packets.sendDelayed(observer);
        }
        catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}