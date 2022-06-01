package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.IItem;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Presets.PerGroupPriceFilter;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Presets.PerItemPriceFilter;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Presets.PerLoadoutPriceFilter;
import HuntShowdownLib.InGameTypes.GameItems.ItemGroup;
import HuntShowdownLib.InGameTypes.Loadout.AmmoPack;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import hsgui.widgets.ConstrainedIntegerField;

import java.util.List;
import java.util.Optional;

public class PriceFilter extends ConstrainedIntegerField implements FilterWidget<Integer> {

    @Override
    public Optional<Filter<IItem>> getPerItemFilter() {
        return direction == Direction.maxPrice ?
                Optional.of(new PerItemPriceFilter().lessThan(getFilterValue())) :
                Optional.of(new PerItemPriceFilter().greaterThan(0));
    }

    @Override
    public Optional<Filter<AmmoPack>> getAmmoPackFilter() {
        return direction == Direction.maxPrice ?
                Optional.of(new Filter<AmmoPack>() {
                    private int maxValue;

                    public Filter<AmmoPack> init(int maxValue) {
                        this.maxValue = maxValue;
                        return this;
                    }

                    @Override
                    public boolean test(AmmoPack ammoPack) {
                        return new ItemGroup(List.of( ammoPack.getFirstAmmo(), ammoPack.getSecondAmmo() )).totalPrice() <= this.maxValue;
                    }
                }.init(getFilterValue())) :
                Optional.of(new Filter<AmmoPack>() {

                    @Override
                    public boolean test(AmmoPack ammoPack) {
                        return true;
                    }
                });
    }

    @Override
    public Optional<Filter<ItemGroup>> getConsumablesFilter() {
        return direction == Direction.maxPrice ?
                Optional.of(new PerGroupPriceFilter().lessThan(getFilterValue())) :
                Optional.of(new PerGroupPriceFilter().greaterThan(0));
    }

    @Override
    public Optional<Filter<ItemGroup>> getToolsFilter() {
        return direction == Direction.maxPrice ?
                Optional.of(new PerGroupPriceFilter().lessThan(getFilterValue())) :
                Optional.of(new PerGroupPriceFilter().greaterThan(0));
    }

    @Override
    public Optional<Filter<ItemGroup>> getWeaponsFilter() {
        return direction == Direction.maxPrice ?
                Optional.of(new PerGroupPriceFilter().lessThan(getFilterValue())) :
                Optional.of(new PerGroupPriceFilter().greaterThan(0));
    }

    @Override
    public Optional<Filter<Loadout>> getLoadoutFilter() {
        return direction == Direction.maxPrice ?
                Optional.of(new PerLoadoutPriceFilter().lessThan(getFilterValue())) :
                Optional.of(new PerLoadoutPriceFilter().greaterThan(getFilterValue()));
    }

    @Override
    public Integer getFilterValue() {
        return this.getValue();
    }

    @Override
    public void setFilterValue(Integer value) {
        this.setValue(value);
    }

    enum Direction {
        maxPrice,
        minPrice;
    }

    Direction direction;

    public PriceFilter(Direction direction) {
        this.direction = direction;
    }

}
