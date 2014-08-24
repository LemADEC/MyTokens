package com.toyz.MyTokens.Events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.toyz.MyTokens.MyTokens;
import com.toyz.MyTokens.sql.SQLhandler;

public class PlayerUse implements Listener {
	 @EventHandler
	 public void onPlayerUse(PlayerInteractEvent e){
		 ConfigurationSection dropitem = MyTokens.getAPI().getConfig().getConfigurationSection("dropitem");
		 if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			 int id = 0;
			 try{
				 id = Integer.valueOf(dropitem.getString("item.id")).intValue();
			 }catch (Exception ex){
				 MyTokens.getAPI().getLogger().warning("Failed to create item with ID of " + id);
				 return;
		     }
			 
			 if(e.getPlayer().getInventory().getItemInHand().getType() == Material.getMaterial(id)){
				 String name = ChatColor.stripColor(e.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName());
				 if(name.contains(dropitem.getString("item.name"))){
					 name = name.replace(dropitem.getString("item.name"), "");
					 String amount = name.replace("[", "").replace("]", "");
					 amount = amount.trim();
					 int got = 0;
					 try{
						 got = Integer.valueOf(amount.trim()).intValue();
					 }catch (Exception ex){
						 MyTokens.getAPI().getLogger().warning("Failed to load token amount");
						 return;
				     }
					 
					 SQLhandler sql = MyTokens.getAPI().getSqlHandler();
					 int tokens = sql.GetBalance(e.getPlayer());
					 tokens = tokens + got;
					 
					 sql.SetBalance(e.getPlayer(), tokens);
					 
					 e.getPlayer().sendMessage(MyTokens.getAPI().getMessageHelper().format(e.getPlayer(), dropitem.getString("used"), amount));
					 int index = e.getPlayer().getInventory().getHeldItemSlot();
					 ItemStack i = e.getPlayer().getInventory().getItemInHand();
					 int count = i.getAmount();
					 if(count >= 1){
						 i.setAmount(count - 1);
						 e.getPlayer().getInventory().setItem(index, i);
					 }else{
						 e.getPlayer().getInventory().setItem(index, null);
					 }
					 e.getPlayer().updateInventory();
				 }
			 }
		 }
	 }
}