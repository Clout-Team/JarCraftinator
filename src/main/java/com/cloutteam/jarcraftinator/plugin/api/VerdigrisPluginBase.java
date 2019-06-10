package com.cloutteam.jarcraftinator.plugin.api;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.logging.LogLevel;
import com.cloutteam.jarcraftinator.logging.PluginLogger;

@VerdigrisPlugin(name = "Unknown", version = "0.0.0", author = "Unknown", description = "Plugin without metadata definition.")
public abstract class VerdigrisPluginBase {

    private boolean isEnabled = false;
    private PluginLogger logger;

    // Used to handle loading the plugin internally.
    public final boolean _onEnable(){
        logger = new PluginLogger(JARCraftinator.getLogger(), this);

        final String nameTag = getName() + " v" + getVersion() + " by " + getAuthor();
        getLogger().log("Enabling " + nameTag + ".");

        isEnabled = onEnable();
        if(isEnabled) getLogger().log("Enabled " + nameTag + ".");
        else getLogger().log("Failed to enable " + nameTag + ".", LogLevel.ERROR);

        return isEnabled;
    }

    // Used to handle unloading the plugin internally.
    public final void _onDisable(){
        onDisable();
        isEnabled = false;
    }

    /**
     * Called when the plugin is loaded so you can enable your plugin.
     *
     * If #onEnable returns true, the plugin is considered successfully enabled,
     * otherwise it is considered that the plugin failed to enable correctly.
     *
     * @return whether the plugin was enabled successfully.
     */
    public boolean onEnable(){
        return false;
    }

    /**
     * Called when the plugin is disabled.
     * You should use this to clean up.
     */
    public void onDisable(){

    }

    /**
     * Returns a logger specifically for the plugin.
     * This logger prepends the plugin's prefix to log output.
     *
     * @return a logger specifically for the plugin.
     */
    public PluginLogger getLogger() {
        return this.logger;
    }

    private VerdigrisPlugin getPluginAnnotation(){
        return this.getClass().getAnnotation(VerdigrisPlugin.class);
    }

    /**
     * Returns the plugin's name as it was registered in the plugin.
     *
     * @return the plugin's name.
     */
    public final String getName(){
        return getPluginAnnotation().name();
    }

    /**
     * Returns the plugin's version as it was registered in the plugin.
     *
     * @return the plugin's version.
     */
    public final String getVersion(){
        return getPluginAnnotation().version();
    }

    /**
     * Returns the plugin's author as it was registered in the plugin.
     *
     * @return the plugin's author.
     */
    public final String getAuthor(){
        return getPluginAnnotation().author();
    }

    /**
     * Returns the plugin's description as it was registered in the plugin.
     *
     * @return the plugin's description.
     */
    public final String getDescription(){
        return getPluginAnnotation().author();
    }

    /**
     * Returns whether or not the plugin has been enabled successfully.
     *
     * @return plugin status.
     */
    public final boolean isEnabled(){
        return isEnabled;
    }

    @Override
    public final boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null) return false;
        if(!(obj instanceof VerdigrisPluginBase)) return false;

        return getName().equals(((VerdigrisPluginBase) obj).getName());
    }

    @Override
    public final int hashCode(){
        return getName().hashCode();
    }

}