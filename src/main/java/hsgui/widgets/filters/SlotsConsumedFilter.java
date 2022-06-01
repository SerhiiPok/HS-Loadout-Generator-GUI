package hsgui.widgets.filters;

import HuntShowdownLib.Character.ePerk;
import HuntShowdownLib.InGameTypes.GameItems.EMeleeWeapon;
import HuntShowdownLib.InGameTypes.GameItems.EProjectileWeapon;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemGroup;
import hsgui.App;
import javafx.scene.control.CheckBox;

import java.util.Optional;

public class SlotsConsumedFilter extends CheckBox implements FilterWidget<Boolean> {

    @Override
    public Optional<Filter<ItemGroup>> getWeaponsFilter() {

        return Optional.of(new Filter<ItemGroup> () {
            @Override
            public boolean test(ItemGroup iItems) {
                boolean consumeAll = getFilterValue();
                int maxSlots = App.mainViewController.containsPerk(ePerk.Quartermaster) ? 5 : 4;

                int slotsActuallyConsumed = iItems.asList()
                        .stream()
                        .mapToInt(i -> {
                            return i instanceof EProjectileWeapon ?
                                    ((EProjectileWeapon)i).slotsConsumed().toInt() :
                                    ((EMeleeWeapon)i).slotsConsumed().toInt();
                        })
                        .sum();
                return consumeAll ?
                        (slotsActuallyConsumed == maxSlots) :
                        (slotsActuallyConsumed <= maxSlots);
            }
        });
    }

    @Override
    public Boolean getFilterValue() {
        return this.isSelected();
    }

    @Override
    public void setFilterValue(Boolean value) {
        this.setSelected(value);
    }
}
