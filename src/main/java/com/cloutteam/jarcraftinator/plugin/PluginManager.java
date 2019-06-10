package com.cloutteam.jarcraftinator.plugin;

import com.cloutteam.jarcraftinator.exceptions.PluginUnloadedException;
import com.cloutteam.jarcraftinator.plugin.api.VerdigrisPluginBase;
import com.samjakob.verdigris.test.TestPlugin;

import java.util.ArrayList;
import java.util.List;

public class PluginManager {

    List<VerdigrisPluginBase> loadedPlugins;

    public PluginManager(){
        this.loadedPlugins = new ArrayList<>();


        // TODO: Auto-load plugins
        load(new TestPlugin());
        enable(new TestPlugin());
    }

    public void load(VerdigrisPluginBase plugin){
        loadedPlugins.add(plugin);
    }

    public void unload(VerdigrisPluginBase plugin){
        assetPluginLoaded(plugin);

        if(plugin.isEnabled()) disable(plugin);
        loadedPlugins.remove(plugin);
    }

    public void enable(VerdigrisPluginBase plugin){
        assetPluginLoaded(plugin);
        plugin._onEnable();
    }

    public void disable(VerdigrisPluginBase plugin){
        assetPluginLoaded(plugin);
        plugin._onDisable();
    }

    public List<VerdigrisPluginBase> getLoadedPlugins() {
        return loadedPlugins;
    }


    private void assetPluginLoaded(VerdigrisPluginBase plugin){
        if(!loadedPlugins.contains(plugin)) throw new PluginUnloadedException("The plugin you are trying to unload (" + plugin.getName() + ") is not loaded.");
    }

}
