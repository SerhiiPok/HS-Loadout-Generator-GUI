package hsgui.filters;

import HuntShowdownLib.InGameTypes.GameItems.EEquippable;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;

import java.util.HashMap;
import java.util.Map;

public class EfficientMeleeFilter extends Filter<Loadout> {

    // things to check!
    // 1. at most one weapon or tool or consumable with qualifying damage per damage type ... which means
    // ... if we have weapon X with qualifying damage of type 'Blunt' then no other weapon or tool or consumable
    // ... with qualifying blunt damage is desired in the loadout !

    final int MINIMUM_ACCEPTABLE_BLUNT_DAMAGE = 50;
    final int MINIMUM_ACCEPTABLE_PIERCING_OR_CUTTING_DAMAGE = 100;

    @Override
    public boolean test(Loadout loadout) {

        Map<String,EEquippable> qualifyingBluntMeleeDamageWeapons = new HashMap<>();
        Map<String,EEquippable> qualifyingCutOrPiercingMeleeDamageWeapons = new HashMap<>();

        for (EEquippable equippedItem : loadout.getEquippedItems()) {

            switch (equippedItem.getLightMeleeAttackDamageType()) {
                case Cut:
                case Pierce:
                    if (equippedItem.getMeleeDamageLight() >= MINIMUM_ACCEPTABLE_PIERCING_OR_CUTTING_DAMAGE) {
                        qualifyingCutOrPiercingMeleeDamageWeapons.put(equippedItem.id(), equippedItem);
                    }
                    break;
                case Blunt:
                    if (equippedItem.getMeleeDamageLight() >= MINIMUM_ACCEPTABLE_BLUNT_DAMAGE) {
                        qualifyingBluntMeleeDamageWeapons.put(equippedItem.id(), equippedItem);
                    }
                    break;
            }

            switch (equippedItem.getHeavyMeleeAttackDamageType()) {
                case Cut:
                case Pierce:
                    if (equippedItem.getMeleeDamageHeavy() >= MINIMUM_ACCEPTABLE_PIERCING_OR_CUTTING_DAMAGE) {
                        qualifyingCutOrPiercingMeleeDamageWeapons.put(equippedItem.id(), equippedItem);
                    }
                    break;
                case Blunt:
                    if (equippedItem.getMeleeDamageHeavy() >= MINIMUM_ACCEPTABLE_BLUNT_DAMAGE) {
                        qualifyingBluntMeleeDamageWeapons.put(equippedItem.id(), equippedItem);
                    }
                    break;
            }

        }

        if (qualifyingBluntMeleeDamageWeapons.size() > 1 || qualifyingCutOrPiercingMeleeDamageWeapons.size() > 1) {
            return false;
        }

        return true;
    }

}
