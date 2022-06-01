package hsgui.widgets.filters;

import HuntShowdownLib.InGameTypes.GameItems.*;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemProperties.eAmmoSpecialEffect;
import HuntShowdownLib.InGameTypes.GameItems.ItemProperties.eMeleeDamageType;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import hsgui.App;
import hsgui.widgets.MeleeEquipGroup;
import hsgui.widgets.MeleeEquipType;

import java.util.Optional;

public class MeleeEquipFilter extends MeleeEquipGroup implements FilterWidget<MeleeEquipType>{

    private static final ItemGroup allItems = new ItemGroup(App.allItems.asList());
    private static final String WINFIELD = "winfield";
    private static final String KNUCKLE_KNIFE = "knuckle_knife";
    private static final String KNIFE = "knife";
    private static final String DUSTERS = "dusters";
    private static final int FULL_HUNTER_HP = 150;
    private static final int REFERENCE_STANDARD_FIREARM_LIGHT_MELEE_DAMAGE = ((EEquippable)allItems.get(WINFIELD)).getMeleeDamageLight();
    private static final int REFERENCE_STANDARD_FIREARM_HEAVY_MELEE_DAMAGE = ((EEquippable)allItems.get(WINFIELD)).getMeleeDamageHeavy();
    private static final int REFERENCE_LIGHT_RASP_DAMAGE = ((EEquippable)allItems.get(KNUCKLE_KNIFE)).getMeleeDamageLight();
    private static final int REFERENCE_HEAVY_SMALL_KNIFE_DAMAGE = ((EEquippable)allItems.get(KNIFE)).getMeleeDamageHeavy();
    private static final int REFERENCE_HEAVY_DUSTERS_DAMAGE = ((EEquippable)allItems.get(DUSTERS)).getMeleeDamageHeavy();

    // either a tool/consumable that is a melee weapon, or a main weapon that is a melee weapon, or a main projectile weapon
    // that is not a melee weapon but deals melee damage higher than a certain threshold (guns with bayonetts)
    private boolean hasAnyMelee(Loadout loadout) {
        for (EEquippable equipped : loadout.getEquippedItems()) {
            if (equipped instanceof EMeleeWeapon
                    ||
                    (equipped instanceof EProjectileWeapon
                        &&
                        (equipped.getMeleeDamageLight() > REFERENCE_STANDARD_FIREARM_LIGHT_MELEE_DAMAGE ||
                                equipped.getMeleeDamageHeavy() > REFERENCE_STANDARD_FIREARM_HEAVY_MELEE_DAMAGE)
                    )
            ) {
                return true;
            }
        }
        return false;
    }

    // possesses a weapon (proj/melee) whose melee damage is higher than the heavy charge of a small knife
    // assuming small knife one-taps dogs when heavy charged
    private boolean canDealWithDogs(Loadout loadout) {
        for (EEquippable equipped : loadout.getEquippedItems()) {
            if ((equipped instanceof EMeleeWeapon || equipped instanceof EProjectileWeapon)
                    &&
                    (equipped.getMeleeDamageHeavy() >= REFERENCE_HEAVY_SMALL_KNIFE_DAMAGE ||
                            equipped.getMeleeDamageLight() >= REFERENCE_HEAVY_SMALL_KNIFE_DAMAGE)) {
                return true;
            }
        }
        return false;
    }

    // - a weapon with light blunt charge > rasp
    // - a weapon with heavy blunt charge > dusters
    // - a weapon with poisonous projectiles
    // - a weapon with choke projectiles
    private boolean canDealWithImmolators(Loadout loadout) {
        for (EEquippable equipped : loadout.getEquippedItems()) {
            // poisonous projectiles?
            if (equipped instanceof EProjectileWeapon) {
                for (IAmmo ammoType : ((EProjectileWeapon)equipped).Ammo()) {
                    if (ammoType.specialEffect() == eAmmoSpecialEffect.Poisoning) {
                        return true;
                    }
                }
            }

            // sufficient melee damage ?
            if ((equipped instanceof EProjectileWeapon || equipped instanceof EMeleeWeapon) &&
                    (
                            (equipped.getMeleeDamageLight() >= REFERENCE_LIGHT_RASP_DAMAGE &&
                                    equipped.getLightMeleeAttackDamageType() == eMeleeDamageType.Blunt)
                                    ||
                            (equipped.getMeleeDamageHeavy() >= REFERENCE_HEAVY_DUSTERS_DAMAGE &&
                                    equipped.getHeavyMeleeAttackDamageType() == eMeleeDamageType.Blunt))) {
                return true;
            }
        }
        return false;
    }

    // possesses a melee weapon that one taps enemy hunters to the body
    private boolean canPVP(Loadout loadout) {
        for (EEquippable equipped : loadout.getEquippedItems()) {
            if (equipped.getMeleeDamageHeavy() >= FULL_HUNTER_HP || equipped.getMeleeDamageLight() >= FULL_HUNTER_HP) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Filter<Loadout>> getLoadoutFilter() {
        if (this.getValue() == null) {return Optional.empty();}

        switch (this.getValue()) {
            case Some:
                return Optional.of(new Filter<Loadout>() {
                    @Override
                    public boolean test(Loadout loadout) {
                        return hasAnyMelee(loadout);
                    }
                });
            case Ai:
                return Optional.of(new Filter<Loadout>() {
                    @Override
                    public boolean test(Loadout loadout) {
                        return canDealWithDogs(loadout) && canDealWithImmolators(loadout);
                    }
                });
            case Pvp:
                return Optional.of(new Filter<Loadout>() {
                    @Override
                    public boolean test(Loadout loadout) {
                        return canPVP(loadout);
                    }
                });
            case Ultra:
                return Optional.of(new Filter<Loadout>() {
                    @Override
                    public boolean test(Loadout loadout) {
                        return canDealWithDogs(loadout) && canDealWithImmolators(loadout) && canPVP(loadout);
                    }
                });
            default:
                return Optional.empty();
        }
    }

    @Override
    public MeleeEquipType getFilterValue() {
        return this.getValue();
    }

    @Override
    public void setFilterValue(MeleeEquipType value) {
        this.setValue(value);
    }

}
