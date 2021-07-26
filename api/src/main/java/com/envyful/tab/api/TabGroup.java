package com.envyful.tab.api;

/**
 *
 * An interface representing a group of players on the tablist
 *
 */
public interface TabGroup {

    /**
     *
     * The group's identifier
     *
     * @return The identifier
     */
    String getIdentifier();

    /**
     *
     * The weight (priority) of the tab group. The higher the weight the higher the priority (i.e. higher weights get
     * put first)
     *
     * @return The weight
     */
    int getWeight();

    /**
     *
     * Gets the permission required to be a member of this tab group
     *
     * @return The permission required
     */
    String getPermission();

    /**
     *
     * Gets the tab group's prefix
     *
     * @return The prefix for the group
     */
    String getPrefix();

    /**
     *
     * Gets the tab group's suffix
     *
     * @return The suffix for the tab group
     */
    String getSuffix();

}
