package net.evmodder.UnplaceableHeads;

import java.io.InputStream;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import net.evmodder.EvLib.bukkit.HeadUtils;
import net.evmodder.EvLib.bukkit.YetAnotherProfile;
import net.evmodder.DropHeads.DropHeads;
import net.evmodder.EvLib.TextUtils;
import net.evmodder.EvLib.bukkit.ConfigUtils;
import net.evmodder.EvLib.bukkit.EvPlugin;

public final class UnplaceableHeads extends EvPlugin implements Listener{
	private String PREVENT_PLACE_MSG;

	private DropHeads dropheadsPlugin;
	private DropHeads getDropHeadsPlugin(){
		if(dropheadsPlugin == null) dropheadsPlugin = (DropHeads)getServer().getPluginManager().getPlugin("DropHeads");
		return dropheadsPlugin;
	}
	@Override public void reloadConfig(){
		// Same as EvPlugin, except we use "DropHeads.getPlugin()" for config dir
		ConfigUtils.updateConfigDirName(getDropHeadsPlugin());
		InputStream defaultConfig = getClass().getResourceAsStream("/config.yml");
		config = ConfigUtils.loadConfig(this, "config-"+getName()+".yml", defaultConfig, /*notifyIfNew=*/true);
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
		final YetAnotherProfile profile = YetAnotherProfile.fromSkullMeta((SkullMeta)headItem.getItemMeta());
		if(profile == null) return;

		if(evt.getPlayer().hasPermission("dropheads.canplacehead")) return;
		evt.getPlayer().sendMessage(PREVENT_PLACE_MSG);
		evt.setCancelled(true);
	}
}