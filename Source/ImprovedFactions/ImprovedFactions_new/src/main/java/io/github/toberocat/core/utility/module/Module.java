package io.github.toberocat.core.utility.module;

public abstract class Module {

    protected String moduleName;
    /**
     * This gets called when the module gets loaded.
     * This is getting called when the user types /if reload
     */
    public abstract void OnLoad();

    /**
     * This gets called when the module gets unloaded.
     * This is getting called when the user types /if reload
     */
    public abstract void OnUnload();

}
