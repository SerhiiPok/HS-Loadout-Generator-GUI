package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.EEquippable;
import HuntShowdownLib.InGameTypes.GameItems.EProjectileWeapon;
import HuntShowdownLib.InGameTypes.GameItems.IItem;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemGroup;
import HuntShowdownLib.InGameTypes.GameItems.ItemProperties.eAmmoSpecialEffect;
import HuntShowdownLib.InGameTypes.GameItems.ItemProperties.eMeleeDamageType;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import hsgui.App;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EfficientMeleeFilter extends CheckBox implements FilterWidget<Boolean> {

    private static final ItemGroup allItems = new ItemGroup(App.allItems.asList());
    private static final String KNUCKLE_KNIFE = "knuckle_knife";
    private static final String KNIFE = "knife";
    private static final String DUSTERS = "dusters";
    private static final int REFERENCE_LIGHT_RASP_DAMAGE = ((EEquippable)allItems.get(KNUCKLE_KNIFE)).getMeleeDamageLight();
    private static final int REFERENCE_HEAVY_SMALL_KNIFE_DAMAGE = ((EEquippable)allItems.get(KNIFE)).getMeleeDamageHeavy();
    private static final int REFERENCE_HEAVY_DUSTERS_DAMAGE = ((EEquippable)allItems.get(DUSTERS)).getMeleeDamageHeavy();

    private enum MeleeProfile {
        dogsOrBetter,
        Immolators;
    }

    private List<MeleeProfile> getMeleeProfiles(EEquippable item) {
        List<MeleeProfile> meleeProfiles = new ArrayList<>();

        if (
                (item.getLightMeleeAttackDamageType() == eMeleeDamageType.Blunt && item.getMeleeDamageLight() >= REFERENCE_LIGHT_RASP_DAMAGE) ||
                (item.getHeavyMeleeAttackDamageType() == eMeleeDamageType.Blunt && item.getMeleeDamageHeavy() >= REFERENCE_HEAVY_DUSTERS_DAMAGE) ||
                (item instanceof EProjectileWeapon && ((EProjectileWeapon)item).Ammo().asList().stream().anyMatch(ammo -> ammo.specialEffect() == eAmmoSpecialEffect.Poisoning))
        ) {
            meleeProfiles.add(MeleeProfile.Immolators);
        }

        if (
                (item.getMeleeDamageLight() >= REFERENCE_HEAVY_SMALL_KNIFE_DAMAGE) ||
                (item.getMeleeDamageHeavy() >= REFERENCE_HEAVY_SMALL_KNIFE_DAMAGE)
        ) {
            meleeProfiles.add(MeleeProfile.dogsOrBetter);
        }

        return meleeProfiles;
    }

    private boolean inconsistentMelee(ItemGroup group) {

        ArrayList<MeleeProfile> groupHasMeleeProfiles = new ArrayList<>();

        for (IItem item : group) {

            boolean redundant = true;

            List<MeleeProfile> meleeProfiles = getMeleeProfiles((EEquippable) item);
            if (meleeProfiles.size() == 0) {continue;}

            for (MeleeProfile meleeProfile : meleeProfiles) {
                if (!groupHasMeleeProfiles.contains(meleeProfile)) {
                    groupHasMeleeProfiles.add(meleeProfile);
                    redundant=false;
                }
            }

            if (redundant) {return redundant;}

        }
        return false;
    }

    @Override
    public Optional<Filter<ItemGroup>> getToolsFilter() {
        return getFilterValue() ?
        Optional.of(new Filter<ItemGroup>() {
            @Override
            public boolean test(ItemGroup iItems) {
                return !inconsistentMelee(iItems);
            }
        }) :
        Optional.empty();
    }

    @Override
    public Optional<Filter<ItemGroup>> getWeaponsFilter() {
        return getFilterValue() ?
        Optional.of(new Filter<ItemGroup>() {
            @Override
            public boolean test(ItemGroup iItems) {
                return !inconsistentMelee(iItems);
            }
        }) :
        Optional.empty();
    }

    @Override
    public Optional<Filter<Loadout>> getLoadoutFilter() {
        return getFilterValue() ?
        Optional.of(new Filter<Loadout>() {
            @Override
            public boolean test(Loadout loadout) {
                return !inconsistentMelee(new ItemGroup(loadout.getEquippedItems()));
            }
        }) :
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
