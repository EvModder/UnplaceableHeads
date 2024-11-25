package net.evmodder.UnplaceableHeads;

import java.io.InputStream;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import com.mojang.authlib.GameProfile;
import net.evmodder.EvLib.extras.HeadUtils;
import net.evmodder.EvLib.extras.TextUtils;
import net.evmodder.EvLib.EvPlugin;
import net.evmodder.EvLib.FileIO;

public final class UnplaceableHeads extends EvPlugin implements Listener{
	private String PREVENT_PLACE_MSG;

	@Override public void reloadConfig(){ // Same as EvPlugin, except we use "DropHeads.getPlugin()" instead of "this" when possible
		InputStream defaultConfig = getClass().getResourceAsStream("/config.yml");
		if(defaultConfig != null){
			Plugin dhPl = getServer().getPluginManager().getPlugin("DropHeads");;
			FileIO.verifyDir(dhPl != null ? dhPl : this);
			config = FileIO.loadConfig(this, "config-"+getName()+".yml", defaultConfig, /*notifyIfNew=*/true);
		}
	}

	@Override public void onEvEnable(){
		reloadConfig();
		PREVENT_PLACE_MSG = TextUtils.translateAlternateColorCodes('&',
				config.getString("prevent-head-placement-message", "&7[&6DropHeads&7]&c No permission to place head blocks"));
	}

	// This listener is only registered when 'prevent-head-placement' = true
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent evt){
		if(!HeadUtils.isPlayerHead(evt.getBlockPlaced().getType())) return;
		final ItemStack headItem = evt.getHand() == EquipmentSlot.HAND
				? evt.getPlayer().getInventory().getItemInMainHand()
				: evt.getPlayer().getInventory().getItemInOffHand();
		if(!headItem.hasItemMeta()) return;
		final SkullMeta meta = (SkullMeta) headItem.getItemMeta();
		final GameProfile profile = HeadUtils.getGameProfile(meta);
		if(profile == null) return;

		if(evt.getPlayer().hasPermission("dropheads.canplacehead")) return;
		evt.getPlayer().sendMessage(PREVENT_PLACE_MSG);
		evt.setCancelled(true);
	}
}