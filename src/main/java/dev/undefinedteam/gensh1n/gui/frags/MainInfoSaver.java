package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gensh1n.music.api.objs.model.SearchData;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;

public class MainInfoSaver {
    public static final int NULL = -1;

    public int tab_last_checked = NULL;
    public int category_last_checked = 0x888;
    public Module last_select_module;
    public Category last_select_category = Categories.Combat;
    public int pager_scroll = NULL;

    public String lastSearch = "";
    public SearchData lastSearchList;

}
