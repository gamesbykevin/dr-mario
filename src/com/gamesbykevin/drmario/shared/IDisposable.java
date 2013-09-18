package com.gamesbykevin.drmario.shared;

/**
 * This interface is to ensure proper resources are marked for garbage collection
 * @author GOD
 */
public interface IDisposable 
{
    /**
     * Recycle the appropriate objects for garbage collection
     */
    public void dispose();
}