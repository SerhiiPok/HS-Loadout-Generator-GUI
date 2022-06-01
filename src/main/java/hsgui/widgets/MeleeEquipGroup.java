package hsgui.widgets;

public class MeleeEquipGroup extends SimpleToggleGroup<MeleeEquipType> {

    @Override
    MeleeEquipType buttonTextToValue(String text) {
        switch (text) {
            case "Some":
                return MeleeEquipType.Some;
            case "AI":
                return MeleeEquipType.Ai;
            case "PVP":
                return MeleeEquipType.Pvp;
            case "Ultra":
                return MeleeEquipType.Ultra;
        }
        return null;
    }

    @Override
    String valueToButtonText(MeleeEquipType val) {
        switch (val) {
            case Some:
                return "Some";
            case Ai:
                return "AI";
            case Pvp:
                return "PVP";
            case Ultra:
                return "Ultra";
        }
        return null;
    }
}
