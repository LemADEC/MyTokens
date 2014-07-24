package com.toyz.MyTokens.Events;

import java.util.*;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.toyz.MyTokens.MyTokens;
import com.toyz.MyTokens.Tools.Item;
import com.toyz.MyTokens.Tools.TokenBlock;
import com.toyz.MyTokens.Utils.MessageHelper;

public class BlockBreak implements Listener {
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player _player = e.getPlayer();

		if(!MyTokens._plugin.getConfig().getBoolean("modes.blockbreak")){
			return;
		}
		
		if(_player.getGameMode() == GameMode.CREATIVE){
			return;
		}
		
		//Handle config values
		ConfigurationSection dropitem = MyTokens._plugin.getConfig().getConfigurationSection("dropitem");
		ConfigurationSection dropmsg = MyTokens._plugin.getConfig().getConfigurationSection("dropmsg");
		
		//Let's do some checking now
		for (TokenBlock b : MyTokens.DropBlocks) {
			if ((b.getType() == e.getBlock().getType()) && (b.shouldDrop()))
			{
				int Drop = b.calculateDropAmount();
				
				if(dropitem.getBoolean("drop")){
					List<String> msgs = new ArrayList<String>();
					
					for(String msg : dropitem.getStringList("item.lore")){
						String f = msg;
						f = MessageHelper.Format(_player, f, Drop + "");
						msgs.add(f);
					}
					
					ItemStack droppedItem = Item.CreateItem(dropitem.getString("item.id"), dropitem.getString("item.name") + "  [" + Drop + "]", msgs, 0, true);
					org.bukkit.entity.Item i = _player.getWorld().dropItem(e.getBlock().getLocation(), droppedItem);
					i.setPickupDelay(dropitem.getInt("item.delay"));
					_player.sendMessage(MessageHelper.Format(_player, dropitem.getString("alert"), Drop + ""));
				}else if(dropmsg.getBoolean("say")){
					int Current = MyTokens.UserTokens.getConfig().getInt(_player.getUniqueId().toString());
					Current = Current + Drop;
					MyTokens.UserTokens.getConfig().set(_player.getUniqueId().toString(), Current);
					for(String msg : dropmsg.getStringList("messages")){
						String f = msg;
						f = MessageHelper.Format(_player, f, Drop + "");
						_player.sendMessage(f);
					}
				}
			}
		}
	}
}