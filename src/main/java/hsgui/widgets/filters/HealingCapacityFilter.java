package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import HuntShowdownLib.InGameTypes.Loadout.LoadoutMetrics;
import hsgui.App;
import hsgui.widgets.HealingCapacityGroup;

import java.util.Optional;

public class HealingCapacityFilter extends HealingCapacityGroup implements FilterWidget<Integer> {

    @Override
    public Optional<Filter<Loadout>> getLoadoutFilter() {
        if (this.getValue() == null) {return Optional.empty();}

        return Optional.of(new Filter<Loadout> () {
            private int minValue;

            public Filter<Loadout> init(int minValue) {
                this.minValue = minValue;
                return this;
            }

            @Override
            public boolean test(Loadout loadout) {
                LoadoutMetrics metrics = new LoadoutMetrics();
                metrics.setPerks(App.mainViewController.getPerks());
                return metrics.getHealingCapacity(loadout) >= this.minValue;
            }
        }.init(this.getValue().intValue()));
    }

    @Override
    public Integer getFilterValue() {
        return this.getValue() == null ? null : this.getValue().intValue();
    }

    @Override
    public void setFilterValue(Integer value) {
        this.setValue(value);
    }

}
