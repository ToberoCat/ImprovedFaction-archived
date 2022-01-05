package extension.dynmapextension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionRegistry;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionData;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import io.github.toberocat.improvedfactions.utility.Debugger;
import io.github.toberocat.improvedfactions.utility.Vector2;
import io.github.toberocat.improvedfactions.utility.configs.DataManager;

public class Main extends Extension implements Runnable {

	private MarkerAPI markerAPI;
	private Map<String, Location> homes = new HashMap<>();
	
	@Override
	protected ExtensionRegistry register() {
		return new ExtensionRegistry("DynmapExtension", "2.0", new String[] { "BETAv3.0.0", "BETAv4.0.0" });
	}

	@Override
	protected void OnEnable(ImprovedFactionsMain plugin) {
		DynmapAPI dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
		markerAPI = dynmap.getMarkerAPI();
	
		if (ImprovedFactionsMain.extensions.containsKey("HomeExtension")) {
			Faction.data.add(new FactionData() {
				
				@Override
				public void Save(Faction faction, DataManager dataManager) {
					
				}
				
				@Override
				public void Load(Faction faction, DataManager dataManager) {
					Location location = dataManager.getConfig().getLocation("f." + faction.getRegistryName() + ".home");
					if (location != null) homes.put(faction.getRegistryName(), location);
				}
			});
		}
		
		//Rerun this every minute
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 1200);
	}



	@Override
	public void run() {
		Debugger.LogInfo("Refreshing Dynmap data");
		runClaims();
		runHomes();
    }
	
	private void runClaims() {
		MarkerSet markerSet = markerAPI.getMarkerSet("Faction_Claims");
        if (markerSet == null) {
            markerSet = markerAPI.createMarkerSet("Faction_Claims", "Claims", null, false);
        }
        
        List<World> worlds = Bukkit.getWorlds();
        
        for (World world : worlds) {
        	for (int i = 0; i < ChunkUtils.claimedChunks.size(); i++) {
    			Vector2 vector = ChunkUtils.claimedChunks.get(i);

    			Chunk chunk = world.getChunkAt((int) vector.getX(), (int) vector.getY());
    			Faction faction = ChunkUtils.GetFactionClaimedChunk(chunk);
    			if (faction == null)
    				continue;
    			
    			String id = "Faction_Claims_" + i;
    			double[] d1 = { chunk.getBlock(0, 0, 0).getLocation().getX(),
    					chunk.getBlock(15, 0, 15).getLocation().getX() + 1 };
    			double[] d2 = { chunk.getBlock(0, 0, 0).getLocation().getZ(),
    					chunk.getBlock(15, 0, 15).getLocation().getZ() + 1 };
    			if (markerSet.findAreaMarkerByLabel(id) != null) {
    				//Update Chunk
    				AreaMarker marker = markerSet.findAreaMarkerByLabel(id);
    				
    				marker.setCornerLocations(d1,d2);
        			marker.setLabel(faction.getDisplayName());
        			marker.setDescription("<div><h1>" + faction.getDisplayName() + "</h1><h2>Motd: " + faction.getMotd() + "</h2></div>");
    			} else {
    				//Create new chunk
    				AreaMarker marker = markerSet.createAreaMarker(id, faction.getRegistryName(), false, world.getName(),
        					new double[0], new double[0], false);

    				if (marker == null) return;
    				
    				marker.setCornerLocations(d1,d2);
        			marker.setLabel(faction.getDisplayName());
        			marker.setDescription("<div><h1>" + faction.getDisplayName() + "</h1><h2>Motd: " + faction.getMotd() + "</h2></div>");
    			}
    		}
        }
	}
	
	private void runHomes() {
		if (ImprovedFactionsMain.extensions.containsKey("HomeExtension")) {
			
			MarkerSet markerSet = markerAPI.getMarkerSet("Faction_Homes");
			if (markerSet == null) {
				markerSet = markerAPI.createMarkerSet("Faction_Homes", "Homes", null, false);
			}
			
			for (String homeName : homes.keySet()) {
				try {
					Location location = homes.get(homeName);
					
					if (markerSet.findMarkerByLabel(homeName) != null) {
						//Update the Location
						markerSet.findMarkerByLabel(homeName).setLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
					} else {
						Marker marker = markerSet.createMarker(null, homeName, location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), markerAPI.getMarkerIcon("pin"), false);
						marker.setLabel(homeName + "'s home");
						marker.setMarkerIcon(markerAPI.getMarkerIcon("bed"));
						marker.setLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
