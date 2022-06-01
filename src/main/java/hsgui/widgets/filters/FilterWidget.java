package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.IItem;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemGroup;
import HuntShowdownLib.InGameTypes.Loadout.AmmoPack;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;

import java.util.Optional;

public interface FilterWidget<S> {

    public default Optional<Filter<IItem>> getPerItemFilter() {
        return Optional.empty();
    };

    public default Optional<Filter<AmmoPack>> getAmmoPackFilter() {
        return Optional.empty();
    };

    public default Optional<Filter<ItemGroup>> getConsumablesFilter() {
        return Optional.empty();
    };

    public default Optional<Filter<ItemGroup>> getToolsFilter() {
        return Optional.empty();
    }

    public default Optional<Filter<ItemGroup>> getWeaponsFilter() {
        return Optional.empty();
    }

    public default Optional<Filter<Loadout>> getLoadoutFilter() {
        return Optional.empty();
    }

    public String getId();

    public S getFilterValue();

    public void setFilterValue(S value);

}
