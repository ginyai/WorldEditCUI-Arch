package org.enginehub.worldeditcui.util;

/**
 * Observer for {@link Observable}
 * 
 * @author Adam Mummery-Smith
 */
public interface Observer
{
	public abstract void notifyChanged(Observable<?> source);
}
