package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Presets.ContainItem;
import HuntShowdownLib.InGameTypes.GameItems.ItemGroup;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.util.Optional;

public class ContainingToolFilter extends CheckBox implements FilterWidget<Boolean> {

    private Property<String> itemId = new SimpleStringProperty();

    public void setItemId(String val) {
        itemId.setValue(val);
    }

    public String getItemId() {return itemId.getValue();}

    @Override
    public Optional<Filter<ItemGroup>> getToolsFilter() {
        return getFilterValue() ?
        Optional.of(new ContainItem(itemId.getValue())) :
        Optional.empty();
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
