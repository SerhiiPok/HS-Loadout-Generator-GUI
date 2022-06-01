package hsgui.filters;

import HuntShowdownLib.InGameTypes.GameItems.EEquippable;
import HuntShowdownLib.InGameTypes.GameItems.ItemFiltering.Filter;
import HuntShowdownLib.InGameTypes.GameItems.ItemProperties.eMeleeDamageType;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import HuntShowdownLib.InGameTypes.Loadout.LoadoutMetrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AlwaysEquipMeleeFilter extends Filter<Loadout> {

    final int ACCEPTABLE_LIGHT_BLUNT_DAMAGE = 58;
    final int ACCEPTABLE_HEAVY_BLUNT_DAMAGE = 72;
    final int ACCEPTABLE_LIGHT_CUT_OR_PIERCING_DAMAGE = 52;
    final int ACCEPTABLE_HEAVY_CUT_OR_PIERCING_DAMAGE = 105;

    @Override
    public boolean test(Loadout loadout) {

        List<eMeleeDamageType> BLUNT = List.of(eMeleeDamageType.Blunt);
        List<eMeleeDamageType> CUT_OR_PIERCE = List.of(eMeleeDamageType.Cut, eMeleeDamageType.Pierce);

        LoadoutMetrics metrics = new LoadoutMetrics();

        Map<String, Integer> damageSpecs = new HashMap<>();

        Optional<EEquippable> bestLightBluntDamage = metrics.getBestLightMeleeDamage(loadout, BLUNT);
        Optional<EEquippable> bestHeavyBluntDamage = metrics.getBestHeavyMeleeDamage(loadout, BLUNT);
        Optional<EEquippable> bestLightCutOrPiercingDamage = metrics.getBestLightMeleeDamage(loadout, CUT_OR_PIERCE);
        Optional<EEquippable> bestHeavyCutOrPiercingDamage = metrics.getBestHeavyMeleeDamage(loadout, CUT_OR_PIERCE);

        damageSpecs.put("LightBlunt", bestLightBluntDamage.isEmpty() ? 0 : bestLightBluntDamage.get().getMeleeDamageLight());
        damageSpecs.put("LightCutOrPierce", bestLightCutOrPiercingDamage.isEmpty() ? 0 : bestLightCutOrPiercingDamage.get().getMeleeDamageLight());
        damageSpecs.put("HeavyBlunt", bestHeavyBluntDamage.isEmpty() ? 0 : bestHeavyBluntDamage.get().getMeleeDamageHeavy());
        damageSpecs.put("HeavyCutOrPierce", bestHeavyCutOrPiercingDamage.isEmpty() ? 0 : bestHeavyCutOrPiercingDamage.get().getMeleeDamageHeavy());

        if (damageSpecs.get("LightBlunt") >= ACCEPTABLE_LIGHT_BLUNT_DAMAGE ||
            damageSpecs.get("LightCutOrPierce") >= ACCEPTABLE_LIGHT_CUT_OR_PIERCING_DAMAGE ||
            damageSpecs.get("HeavyBlunt") >= ACCEPTABLE_HEAVY_BLUNT_DAMAGE ||
            damageSpecs.get("HeavyCutOrPierce") >= ACCEPTABLE_HEAVY_CUT_OR_PIERCING_DAMAGE) {
            return true;
        } else {
            return false;
        }
    }
}
