package com.toyz.MyTokens;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.*;

import com.toyz.MyTokens.BaseCommand.BaseCommand;
import com.toyz.MyTokens.Events.*;
import com.toyz.MyTokens.Tools.*;
import com.toyz.MyTokens.Utils.ConfigAccessor;

public class MyTokens extends JavaPlugin{
	public static Logger logger = null;
	public static ConfigAccessor UserTokens = null;
	public static ConfigAccessor TokenShop = null;
	public static MyTokens _plugin = null;
	public static Hashtable<Integer, ItemStack> Items = null;
	public static List<TokenBlock> DropBlocks = null;
	public static ConsoleCommandSender console = null;
	public static List<String> AdminHelpCommands = Arrays.asList(
			"&b/MyTokens reload &f- Reloads the plugin", 
			"&b/MyTokens give username amount &f- Gives said user a amount of tokens",
			"&b/MyTokens reset username &f- Resets users tokens to 0");
	
	public static List<String> PublicHelpCommands = Arrays.asList(
			"&b/myt &f- Opens Up the Tokens Shop", 
			"&b/myt give username amount &f- Gives said user a amount of tokens",
			"&b/myt bal &f- Shows your current token balance");
	
	//Enable Plugin
	public void onEnable() {
		console = Bukkit.getServer().getConsoleSender();
		logger = getLogger();
		_plugin = this;
		
		//Load some Configs!
		UserTokens = new ConfigAccessor(this, "Tokens.yml");
		TokenShop = new ConfigAccessor(this, "Shop.yml");
		
		//Save Defaults if needed
		UserTokens.saveDefaultConfig();
		TokenShop.saveDefaultConfig();
		saveDefaultConfig();
		
		//Build our list of items!
		Item I = new Item();
		Items = I.BuildItems();
		
		//Build are block list
		DropBlocks = BuildTokenBlocks.BlocksThatDrop(); 
		
		//Load Our Commands
		getCommand("myt").setExecutor(new BaseCommand());
		getCommand("mytokens").setExecutor(new BaseCommand());
		
		//Register the listeners
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerUse(), this);
		//pm.registerEvents(new EntityDeath(), this);
		
		//Start the auto saving
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				MyTokens.UserTokens.saveConfig();
			}
		}, 800L, 800L);
	}
	
	//Disable Plugin
	public void onDisable() {
		UserTokens.saveConfig();
		TokenShop.saveConfig();
		
		UserTokens = null;
		TokenShop = null;
	}
	
	public void Reload(){
		UserTokens.saveConfig();
		TokenShop.saveConfig();
		
		UserTokens = null;
		TokenShop = null;
		
		//Load some Configs!
		UserTokens = new ConfigAccessor(this, "Tokens.yml");
		TokenShop = new ConfigAccessor(this, "Shop.yml");
		reloadConfig();
		
		//Save Defaults if needed
		UserTokens.saveDefaultConfig();
		TokenShop.saveDefaultConfig();
		saveDefaultConfig();
				
		//Build our list of items!
		Item I = new Item();
		Items = I.BuildItems();
				
		//Build are block list
		DropBlocks = BuildTokenBlocks.BlocksThatDrop(); 
	}
}