package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.IItem;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import hsgui.widgets.ConstrainedIntegerField;

import java.util.Optional;

public class RankFilter extends ConstrainedIntegerField implements FilterWidget<Integer> {

    @Override
    public Optional<Filter<IItem>> getPerItemFilter() {
        return Optional.of(new Filter<IItem> () {

            private int maxRank;

            public Filter<IItem> init(int maxRank) {
                this.maxRank = maxRank;
                return this;
            }

            @Override
            public boolean test(IItem iItem) {
                return iItem.minimumRequiredBloodlineRank().Rank() <= this.maxRank;
            }
        }.init(getFilterValue())
        );
    }

    @Override
    public Integer getFilterValue() {
        return this.getValue();
    }

    @Override
    public void setFilterValue(Integer value) {
        this.setValue(value);
    }
}
