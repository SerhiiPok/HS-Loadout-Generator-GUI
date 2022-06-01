package hsgui;

import HuntShowdownLib.Character.ePerk;
import HuntShowdownLib.InGameTypes.GameItems.*;
import HuntShowdownLib.InGameTypes.GameItems.ItemProperties.eItemCategory;
import HuntShowdownLib.InGameTypes.Loadout.AmmoPack;
import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import HuntShowdownLib.InGameTypes.Loadout.LoadoutMetrics;
import HuntShowdownLib.InGameTypes.Loadout.WeaponSlot;
import HuntShowdownLib.Random.RandomLoadoutGenerator;
import HuntShowdownLib.UtilityTypes.ItemCollection;
import hsgui.widgets.EasyIcon;
import hsgui.widgets.MeleeEquipType;
import hsgui.widgets.SpecBox;
import hsgui.widgets.filters.*;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.application.Platform.runLater;

public class MainViewController implements Initializable {

    // lazy available pool updater
    // will check if there were no GUI updates in the last n milliseconds and will recalculate hsgui.filters and show total pool information
    // in the progress bar
    /*static class StatusBarUpdater {

        private Thread runningThread;
        private MainViewController controller;

        private Thread createUpdateThread() {
            return new Thread(() -> {
                try {
                    Thread.sleep(400);
                } catch (Exception e) {
                    return;
                }
                this.controller.randomGen.applyFilters();
                runLater(() -> {updateStatusBar();});
            });
        }
        public StatusBarUpdater(MainViewController controller) {
            this.controller = controller;
        }

        public void updateStatusBar() {
            this.controller.statusBar.setText("Ready +++ Pool of candidate loadouts: " + String.valueOf(this.controller.randomGen.getActivePoolSize()));
        }

        public void updateStatusBarLater() {
            if (runningThread != null) {
                runningThread.interrupt();
            }
            runningThread = createUpdateThread();
            runningThread.start();
        }

    }*/

    static class SceneSetupService extends Service<Boolean> {

        MainViewController controller;

        public void setMainViewController(MainViewController controller) {
            this.controller = controller;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean> () {

                MainViewController controller;

                public Task<Boolean> init(MainViewController controller) {
                    this.controller = controller;
                    return this;
                }

                @Override
                protected Boolean call() throws Exception {
                    // set total item pools and random generation

                    controller.totalWeaponPool = App.allItems.filterApply(i -> {
                        return (i.itemCategory() == eItemCategory.MainWeapon);
                    });

                    controller.totalToolPool = App.allItems.filterApply(i -> {
                        return (i.itemCategory() == eItemCategory.Tool);
                    });

                    controller.totalConsumablePool = App.allItems.filterApply(i -> {
                        return (i.itemCategory() == eItemCategory.Consumable);
                    });

                    controller.randomGen = new RandomLoadoutGenerator(App.allItems);
                    controller.randomGenService = new RandomLoadoutGeneratorService(controller.randomGen);

                    controller.randomGenService.setOnSucceeded(workerStateEvent -> {
                        controller.hideLoadoutLoadingScreen();
                        if (controller.randomGenService.getValue() == null) {
                            // show 'nothing found' message
                        } else {
                            controller.equipLoadout(controller.randomGenService.getValue());
                        }
                    }); // hide loading screen and equip loadout when the task has finished successfully

                    /*controller.statusBarUpdater = new StatusBarUpdater(controller);*/

                    controller.initialComboBoxDataSetup();
                    controller.setUpActionCallbacks();
                    controller.setUpValueListeners();
                    return true;
                }
            }.init(this.controller);
        }
    }

    // --- filter controls ---
    @FXML
    protected MaxPriceFilter maxPriceFilter;
    @FXML
    protected MinPriceFilter minPriceFilter;
    @FXML
    protected RankFilter rankFilter;
    @FXML
    protected EfficientMeleeFilter efficientMeleeFilter;
    @FXML
    protected ContainingToolFilter equipChokesFilter;
    @FXML
    protected SlotsConsumedFilter slotsConsumedFilter;
    @FXML
    protected MeleeEquipFilter meleeEquipFilter;
    @FXML
    protected HealingCapacityFilter healingCapacityFilter;

    // --- loadout controls ---
    @FXML
    protected ComboBox primaryWeaponContainer;
    @FXML
    protected ToggleButton primaryWeaponDoubleWield;
    @FXML
    protected ComboBox primaryWeaponAmmo1;
    @FXML
    protected ComboBox primaryWeaponAmmo2;

    @FXML
    protected ComboBox secondaryWeaponContainer;
    @FXML
    protected ToggleButton secondaryWeaponDoubleWield;
    @FXML
    protected ComboBox secondaryWeaponAmmo1;
    @FXML
    protected ComboBox secondaryWeaponAmmo2;

    @FXML
    protected ComboBox tool1;
    @FXML
    protected ComboBox tool2;
    @FXML
    protected ComboBox tool3;
    @FXML
    protected ComboBox tool4;

    @FXML
    protected ComboBox consumable1;
    @FXML
    protected ComboBox consumable2;
    @FXML
    protected ComboBox consumable3;
    @FXML
    protected ComboBox consumable4;

    // --- perks controls ---
    @FXML
    protected CheckBox quartermaster;
    @FXML
    protected CheckBox frontiersman;
    @FXML
    protected CheckBox doctor;

    // --- specs controls ---
    @FXML
    protected SpecBox price;
    @FXML
    protected SpecBox requiredRank;
    @FXML
    protected SpecBox healingCapacity;
    @FXML
    protected SpecBox numberOfHeals;
    @FXML
    protected SpecBox cuttingMeleeDamage;
    @FXML
    protected SpecBox bluntMeleeDamage;
    @FXML
    protected SpecBox ammoSupply;
    @FXML
    protected SpecBox headshotRange;

    // --- random generation and status bar ---

    @FXML
    protected Button equipNextRandom;
    @FXML
    protected Label statusBar;
    @FXML
    protected HBox lockScreen;
    @FXML
    protected HBox loadingScreen;
    @FXML
    protected EasyIcon loadingWheel;

    // --- data ---
    private ItemCollection<IItem> totalWeaponPool;
    private ItemCollection<IItem> totalToolPool;
    private ItemCollection<IItem> totalConsumablePool;
    private RandomLoadoutGenerator randomGen;
    private RandomLoadoutGeneratorService randomGenService;
    /*private StatusBarUpdater statusBarUpdater;*/

    // --- loadout listening ---
    boolean lazySpecUpdate = false;

    // --- interface ---

    public boolean containsPerk(ePerk perk) {
        return getCurrentPerks().contains(perk);
    }

    public List<ePerk> getPerks() {
        return List.copyOf(getCurrentPerks());
    }

    // --- get valid choices of items in comboboxes

    private ItemCollection<IItem> getValidWeaponChoice(ComboBox forComboBox) {
        ComboBox other = forComboBox == primaryWeaponContainer ? secondaryWeaponContainer : primaryWeaponContainer;
        if (other.getValue() == null) {
            return totalWeaponPool;
        } else {
            int allowedTotalWeaponSlots = getCurrentPerks().contains(ePerk.Quartermaster) ? 5 : 4;
            int consumedSlots = other.getValue() instanceof EMeleeWeapon ?
                    ((EMeleeWeapon)other.getValue()).slotsConsumed().toInt() :
                    ((EProjectileWeapon)other.getValue()).slotsConsumed().toInt();
            int remainingSlots = allowedTotalWeaponSlots - consumedSlots;
            return totalWeaponPool.filterApply(i -> {
                return (i instanceof EMeleeWeapon ?
                        ((EMeleeWeapon) i).slotsConsumed().toInt() <= remainingSlots :
                        ((EProjectileWeapon) i).slotsConsumed().toInt() <= remainingSlots);
            });
        }
    }

    private ItemCollection<IItem> getValidToolChoice(ComboBox forComboBox) {
        List<IItem> equippedTools = getCurrentlyEquippedTools();
        ItemCollection<IItem> allowedTools = totalToolPool.filterApply(i -> {
            return !equippedTools.contains(i);
        });
        if (forComboBox.getValue() != null) {allowedTools.Append((IItem)forComboBox.getValue());}
        return allowedTools;
    }

    private ItemCollection<IAmmo> getValidAmmoChoice(ComboBox forComboBox) {
        return forComboBox == primaryWeaponAmmo1 || forComboBox == primaryWeaponAmmo2 ?
                getValidAmmoChoice((IItem)primaryWeaponContainer.getValue()) :
                getValidAmmoChoice((IItem)secondaryWeaponContainer.getValue());
    }

    private ItemCollection<IAmmo> getValidAmmoChoice(IItem weapon) {
        if (weapon == null) {return new ItemCollection<IAmmo>();}
        return weapon instanceof EProjectileWeapon ?
                ((EProjectileWeapon)weapon).Ammo() :
                new ItemCollection<IAmmo>();
    }

    // --- actions on comboboxes

    private void nonDestructiveSort(ComboBox combo) {
        IItem selection = (IItem)combo.getValue();

        if (selection == null) {
            combo.getItems().sort((o1,o2) -> {
                return String.CASE_INSENSITIVE_ORDER.compare(((IItem)o1).name(), ((IItem)o2).name());
            });
        } else {
            List<Object> others = ( (List<Object>) combo.getItems() )
                    .stream()
                    .filter(item -> !item.equals(selection))
                    .collect(Collectors.toList());
            others.sort((o1,o2) -> {
                return String.CASE_INSENSITIVE_ORDER.compare( ((IItem) o1).name(), ((IItem) o2).name());
            });

            combo.getItems().removeIf(item -> others.contains(item));
            combo.getItems().addAll(others);
        }
    }

    private void setComboBoxChoiceTo(ComboBox referencedCombo, ItemCollection<? extends IItem> allowedItems) {
        List<? extends IItem> lAllowedItems = allowedItems.asList();

        referencedCombo.getItems().removeIf(item -> !lAllowedItems.contains(item));

        referencedCombo.getItems().addAll(
            lAllowedItems
            .stream()
            .filter(item -> !referencedCombo.getItems().contains(item))
            .collect(Collectors.toList())
        );

        nonDestructiveSort(referencedCombo);
    }

    private void clearAndDisableComboBox(ComboBox referencedCombo) {
        referencedCombo.setValue(null);
        referencedCombo.setDisable(true);
    }

    private void doWithAmmoComboBoxesOnWeaponChange(ComboBox forWeaponComboBox) {
        List<ComboBox> ammoBoxes = forWeaponComboBox == primaryWeaponContainer ?
                List.of(primaryWeaponAmmo1, primaryWeaponAmmo2)
                : List.of(secondaryWeaponAmmo1, secondaryWeaponAmmo2);
        if (forWeaponComboBox.getValue() == null || forWeaponComboBox.getValue() instanceof EMeleeWeapon) {
            ammoBoxes.forEach(box -> clearAndDisableComboBox(box));
        } else if (((EProjectileWeapon)forWeaponComboBox.getValue()).canBearMultiplAmmoTypes()) {
            ammoBoxes.forEach(box -> {
                box.setDisable(false);
                setComboBoxChoiceTo(box,getValidAmmoChoice(box));
                box.setValue(getStandardAmmoForWeapon((EProjectileWeapon)forWeaponComboBox.getValue()));
            });
        } else { // bears only one ammo type
            clearAndDisableComboBox(ammoBoxes.get(1));
            ammoBoxes.get(0).setDisable(false);
            setComboBoxChoiceTo(ammoBoxes.get(0), getValidAmmoChoice(ammoBoxes.get(0)));
            ammoBoxes.get(0).setValue(getStandardAmmoForWeapon((EProjectileWeapon)forWeaponComboBox.getValue()));
        }
    }

    private void doWithDoubleWieldButtonsOnChange() {
        int totalConsumedSlots = getCurrentlyEquippedWeapons()
                .stream()
                .mapToInt(i -> i instanceof EMeleeWeapon ?
                        ((EMeleeWeapon)i).slotsConsumed().toInt() :
                        ((EProjectileWeapon)i).slotsConsumed().toInt())
                .sum();
        boolean hasQuartermaster = getCurrentPerks().contains(ePerk.Quartermaster);

        // primary weapon
        List.of(primaryWeaponDoubleWield, secondaryWeaponDoubleWield).forEach(toggleBut -> {
            ComboBox weaponContainer = toggleBut == primaryWeaponDoubleWield ? primaryWeaponContainer : secondaryWeaponContainer;
            boolean slotDoubleWieldValid = weaponContainer.getValue() == null ?
                    false :
                    (weaponContainer.getValue() instanceof EMeleeWeapon ?
                            false :
                            ((EProjectileWeapon)weaponContainer.getValue()).canDoubleWield())
                            &&
                            (totalConsumedSlots < 4 || hasQuartermaster);
            toggleBut.setDisable(!slotDoubleWieldValid);
            toggleBut.setSelected(toggleBut.isSelected() && slotDoubleWieldValid);
        });
    }

    // --- get info on equipped loadout, perks or equip loadout

    private Loadout getCurrentlyEquippedLoadout() {
        Loadout equippedLoadout = new Loadout();

        List.of(primaryWeaponContainer, secondaryWeaponContainer).forEach(
            weaponContainer -> {
                EEquippable weapon = (EEquippable)weaponContainer.getValue();
                ComboBox ammoContainer1 = weaponContainer == primaryWeaponContainer ? primaryWeaponAmmo1 : secondaryWeaponAmmo1;
                ComboBox ammoContainer2 = weaponContainer == primaryWeaponContainer ? primaryWeaponAmmo2 : secondaryWeaponAmmo2;
                boolean doubleWields = weaponContainer == primaryWeaponContainer ? primaryWeaponDoubleWield.isSelected() :
                        secondaryWeaponDoubleWield.isSelected();
                boolean doubleWieldsValid = false;

                IAmmo ammo1; IAmmo ammo2;
                ammo1 = ammo2 = null;
                if (weapon == null || weapon instanceof EMeleeWeapon) {
                } else {
                    EProjectileWeapon projWeapon = (EProjectileWeapon)weapon;
                    doubleWieldsValid = projWeapon.canDoubleWield();
                    if (!projWeapon.canBearMultiplAmmoTypes()) {
                        ammo1 = ammo2 = (IAmmo)ammoContainer1.getValue();
                    } else {
                        ammo1 = (IAmmo)ammoContainer1.getValue();
                        ammo2 = (IAmmo)ammoContainer2.getValue();
                    }
                }

                WeaponSlot loadoutSlot = weaponContainer == primaryWeaponContainer ?
                        equippedLoadout.primaryWeaponSlot :
                        equippedLoadout.secondaryWeaponSlot;

                if (weapon == null) {
                    return;
                } else {
                    loadoutSlot.setItem(weapon);
                    loadoutSlot.setDoubleWielding(doubleWieldsValid && doubleWields);
                    loadoutSlot.AmmoPack = new AmmoPack(ammo1, ammo2);
                }
            }
        );

        getCurrentlyEquippedTools().forEach(tool -> {
            equippedLoadout.equipTool((EEquippable)tool);
        });
        getCurrentlyEquippedConsumables().forEach(tool -> {
            equippedLoadout.equipConsumable((EEquippable)tool);
        });

        return equippedLoadout;
    }

    private List<IItem> getCurrentlyEquippedWeapons() {
        return List.of(primaryWeaponContainer,
                secondaryWeaponContainer)
                .stream()
                .filter(combo -> combo.getValue() != null)
                .map(combo -> (IItem)combo.getValue())
                .collect(Collectors.toList());
    }

    private List<IItem> getCurrentlyEquippedTools() {
        return List.of(tool1,tool2,tool3,tool4)
                .stream()
                .filter(combo -> combo.getValue() != null)
                .map(combo -> (IItem)combo.getValue())
                .collect(Collectors.toList());
    }

    private List<IItem> getCurrentlyEquippedConsumables() {
        return List.of(consumable1,consumable2,consumable3,consumable4)
                .stream()
                .filter(combo -> combo.getValue() != null)
                .map(combo -> (IItem)combo.getValue())
                .collect(Collectors.toList());
    }

    private List<ePerk> getCurrentPerks() {
        List<ePerk> perks = new ArrayList<>();
        if (quartermaster.isSelected()) {perks.add(ePerk.Quartermaster);}
        if (frontiersman.isSelected()) {perks.add(ePerk.Frontiersman);}
        if (doctor.isSelected()) {perks.add(ePerk.Doctor);}
        return perks;
    }

    private void equipLoadout(Loadout loadout) {

        lazySpecUpdate = true;

        // the values that are set in the comboboxes must be part of item lists of these comboboxes ! - IMPORTANT

        clearLoadout();
        initialComboBoxDataSetup();

        primaryWeaponContainer.setValue(loadout.primaryWeaponSlot.Item()); // --> triggers updates of ammo comboboxes and sets to standard ammo
        primaryWeaponAmmo1.setValue(loadout.primaryWeaponSlot.AmmoPack == null ?
                null :
                loadout.primaryWeaponSlot.AmmoPack.getFirstAmmo());
        if (loadout.primaryWeaponSlot.Item() instanceof EProjectileWeapon && ((EProjectileWeapon)loadout.primaryWeaponSlot.Item()).canBearMultiplAmmoTypes()) {
            primaryWeaponAmmo2.setValue(loadout.primaryWeaponSlot.AmmoPack == null ?
                    null :
                    loadout.primaryWeaponSlot.AmmoPack.getSecondAmmo());
        }
        primaryWeaponDoubleWield.setSelected(loadout.primaryWeaponSlot.isDoubleWielding());

        System.out.println("machine update thread: " + Thread.currentThread().getId());

        secondaryWeaponContainer.setValue(loadout.secondaryWeaponSlot.Item()); // --> triggers updates of ammo comboboxes and sets to standard ammo
        secondaryWeaponAmmo1.setValue(loadout.secondaryWeaponSlot.AmmoPack == null ?
                null :
                loadout.secondaryWeaponSlot.AmmoPack.getFirstAmmo());
        if (loadout.secondaryWeaponSlot.Item() instanceof EProjectileWeapon && ((EProjectileWeapon)loadout.secondaryWeaponSlot.Item()).canBearMultiplAmmoTypes()) {
            secondaryWeaponAmmo2.setValue(loadout.secondaryWeaponSlot.AmmoPack == null ?
                    null :
                    loadout.secondaryWeaponSlot.AmmoPack.getSecondAmmo());
        }
        secondaryWeaponDoubleWield.setSelected(loadout.secondaryWeaponSlot.isDoubleWielding());

        tool1.setValue(loadout.toolSlotA.Item());
        tool2.setValue(loadout.toolSlotB.Item());
        tool3.setValue(loadout.toolSlotC.Item());
        tool4.setValue(loadout.toolSlotD.Item());

        consumable1.setValue(loadout.consumableSlotA.Item());
        consumable2.setValue(loadout.consumableSlotB.Item());
        consumable3.setValue(loadout.consumableSlotC.Item());
        consumable4.setValue(loadout.consumableSlotD.Item());

        lazySpecUpdate = false;
        updateSpecs();
    }

    @FXML
    private void equipNextRandom() {
        randomGen.applyFilters();
        switch (randomGenService.getState()) {
            case RUNNING:
                randomGenService.cancel();
                hideLoadoutLoadingScreen();
                break;
            case READY:
                randomGenService.start();
                showLoadoutLoadingScreen();
                break;
            default:
                randomGenService.restart();
                showLoadoutLoadingScreen();
        }
    }

    // update interface or specs

    private void updateFilters() {
        // item space will not be recalculated after each filter update
        this.randomGen.setLazyFiltering(true);
        this.randomGen.clearFilters();

        for (FilterWidget<?> filterWidget : List.of(maxPriceFilter,minPriceFilter,rankFilter,
                efficientMeleeFilter,equipChokesFilter,slotsConsumedFilter,
                meleeEquipFilter,healingCapacityFilter)) {
            if (!filterWidget.getPerItemFilter().isEmpty()) {
                this.randomGen.addPerItemFilter(filterWidget.getPerItemFilter().get());
            }

            if (!filterWidget.getConsumablesFilter().isEmpty()) {
                this.randomGen.addConsumablesFilter(filterWidget.getConsumablesFilter().get());
            }

            if (!filterWidget.getToolsFilter().isEmpty()) {
                this.randomGen.addToolFilter(filterWidget.getToolsFilter().get());
            }

            if (!filterWidget.getWeaponsFilter().isEmpty()) {
                this.randomGen.addWeaponsFilter(filterWidget.getWeaponsFilter().get());
            }

            if (!filterWidget.getAmmoPackFilter().isEmpty()) {
                this.randomGen.addAmmoPackFilter(filterWidget.getAmmoPackFilter().get());
            }

            if (!filterWidget.getLoadoutFilter().isEmpty()) {
                this.randomGen.addPerLoadoutFilter(filterWidget.getLoadoutFilter().get());
            }
        }
        /*statusBarUpdater.updateStatusBarLater();*/
    }

    private void updateSpecs() {
        Loadout equippedLoadout = getCurrentlyEquippedLoadout();
        LoadoutMetrics metrics = new LoadoutMetrics();
        metrics.setPerks(getCurrentPerks());

        price.setValue(equippedLoadout.getTotalCost());
        requiredRank.setValue(equippedLoadout.getMinimumRequiredBloodlineRank().Rank());
        healingCapacity.setValue(metrics.getHealingCapacity(equippedLoadout));
        numberOfHeals.setValue(metrics.getNumberOfHeals(equippedLoadout));
        cuttingMeleeDamage.setValue(metrics.getBestCutOrPierceMeleeDamage(equippedLoadout));
        bluntMeleeDamage.setValue(metrics.getBestBluntMeleeDamage(equippedLoadout));
        ammoSupply.setValue(metrics.getTotalAmmoSupply(equippedLoadout));
        headshotRange.setValue(metrics.getBestHeadshotRange(equippedLoadout));
    }

    // other helpers
    private IAmmo getStandardAmmoForWeapon(EProjectileWeapon weapon) {
        return weapon.Ammo().filterApply(ammo -> ammo.price() == 0).at(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        SceneSetupService sceneSetupService;

        showProgramLoadingScreen();

        sceneSetupService = new SceneSetupService();
        sceneSetupService.setMainViewController(this);
        sceneSetupService.setOnSucceeded(e -> {
            hideProgramLoadingScreen();
            setUpDefaultFilters();
            equipNextRandom();
        });
        sceneSetupService.setOnFailed(e -> {
            // show startup failed message and exit...
        });

        HSItemsAPIService apiItemsService = new HSItemsAPIService();
        apiItemsService.setOnSucceeded(e -> {
            if (apiItemsService.getValue() != null) {
                App.allItems = apiItemsService.getValue();
            }
            sceneSetupService.start();
        });
        apiItemsService.setOnFailed(e -> {
            sceneSetupService.start();
        });

        apiItemsService.start();
    }

    private void setUpDefaultFilters() {
        maxPriceFilter.setValue(350);
        minPriceFilter.setValue(200);
        rankFilter.setValue(100);
        meleeEquipFilter.setValue(MeleeEquipType.Some);
    }

    private void hideLoadoutLoadingScreen() {

        equipNextRandom.setText("Random");

        // fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), lockScreen);
        fadeOut.setFromValue(1f);
        fadeOut.setToValue(0f);
        fadeOut.setOnFinished(e -> {
            runLater(() -> {
                lockScreen.setVisible(false);
            });
        });

        fadeOut.play();
    }

    private void showLoadoutLoadingScreen() {

        equipNextRandom.setText("Cancel...");

        lockScreen.setVisible(true);

        // wheel rotation
        RotateTransition rotation = new RotateTransition(Duration.millis(4000), loadingWheel);
        rotation.setByAngle(360f);
        rotation.setCycleCount(10000);

        // fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), lockScreen);
        fadeIn.setFromValue(0f);
        fadeIn.setToValue(1f);

        fadeIn.play();
        rotation.play();
    }

    private void hideProgramLoadingScreen() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1500), loadingScreen);

        fadeTransition.setDelay(Duration.millis(1000));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(e -> {
            loadingScreen.setVisible(false);
        });

        fadeTransition.play();
    }

    private void showProgramLoadingScreen() {
        loadingScreen.setVisible(true);
    }

    private void dataSetup() {

    }

    private void clearLoadout() {
        lazySpecUpdate = true;

        primaryWeaponContainer.setValue(null);
        primaryWeaponAmmo1.setValue(null);
        primaryWeaponAmmo2.setValue(null);
        secondaryWeaponContainer.setValue(null);
        secondaryWeaponAmmo1.setValue(null);
        secondaryWeaponAmmo2.setValue(null);
        tool1.setValue(null);
        tool2.setValue(null);
        tool3.setValue(null);
        tool4.setValue(null);
        consumable1.setValue(null);
        consumable2.setValue(null);
        consumable3.setValue(null);
        consumable4.setValue(null);

        lazySpecUpdate = false;
    }

    private void initialComboBoxDataSetup() {
        setComboBoxChoiceTo(primaryWeaponContainer, totalWeaponPool);
        setComboBoxChoiceTo(secondaryWeaponContainer, totalWeaponPool);
        setComboBoxChoiceTo(tool1, totalToolPool);
        setComboBoxChoiceTo(tool2, totalToolPool);
        setComboBoxChoiceTo(tool3, totalToolPool);
        setComboBoxChoiceTo(tool4, totalToolPool);
        setComboBoxChoiceTo(consumable1, totalConsumablePool);
        setComboBoxChoiceTo(consumable2, totalConsumablePool);
        setComboBoxChoiceTo(consumable3, totalConsumablePool);
        setComboBoxChoiceTo(consumable4, totalConsumablePool);
    }

    private void setUpValueListeners() {
        setUpFilterValueListeners();
        setUpLoadoutValueListeners();
        setUpPerksValueListeners();
        setUpStatsValueListeners();
    }

    private void setUpActionCallbacks() {
        // correct weapon selection
        List.of(primaryWeaponContainer, secondaryWeaponContainer).forEach(comboBox -> {
            comboBox.setOnMousePressed(mouseEvent -> {
                setComboBoxChoiceTo(comboBox, getValidWeaponChoice(comboBox));
            });
        });
        // correct tool selection
        List.of(tool1,tool2,tool3,tool4).forEach(comboBox -> {
            comboBox.setOnMousePressed(mouseEvent -> {
                setComboBoxChoiceTo(comboBox, getValidToolChoice(comboBox));
            });
        });
        // correct ammo selection
        List.of(primaryWeaponAmmo1, primaryWeaponAmmo2, secondaryWeaponAmmo1, secondaryWeaponAmmo2).forEach(comboBox -> {
            comboBox.setOnMousePressed(mouseEvent -> {
                setComboBoxChoiceTo(comboBox, getValidAmmoChoice(comboBox));
            });
        });
    }

    private void setUpFilterValueListeners() {
        List.of(maxPriceFilter, minPriceFilter, rankFilter).forEach(filter -> filter.valueProperty().addListener((x,old,nw)->updateFilters()));
        List.of(efficientMeleeFilter,slotsConsumedFilter,equipChokesFilter).forEach(filter->filter.selectedProperty().addListener((x,old,nw)->updateFilters()));
        meleeEquipFilter.addToggleChangeListener((x,old,nw) -> updateFilters());
        healingCapacityFilter.addToggleChangeListener((x,old,nw) -> updateFilters());
    }

    private void setUpLoadoutValueListeners() {

        // weapon change listener: update ammo (only if weapon changed) and enable/disable ammo box(es)
        List.of(primaryWeaponContainer, secondaryWeaponContainer).forEach(combo -> {
            combo.valueProperty().addListener((x,old,nw) -> {
                    if (old != null && nw != null && old.equals(nw)) {
                        return;
                    } else {
                        doWithAmmoComboBoxesOnWeaponChange(combo);
                    }

                doWithDoubleWieldButtonsOnChange();
            });
        });

        // any item change listener: update loadout info
        List.of(primaryWeaponContainer,secondaryWeaponContainer,
                primaryWeaponAmmo1, primaryWeaponAmmo2, secondaryWeaponAmmo1, secondaryWeaponAmmo2,
                tool1,tool2,tool3,tool4,
                consumable1,consumable2,consumable3,consumable4).forEach(
                        control -> {
                            control.valueProperty().addListener((x,old,nw) -> {
                                if (!lazySpecUpdate) {
                                    updateSpecs();
                                }
                            });
                        }
        );
        List.of(primaryWeaponDoubleWield,secondaryWeaponDoubleWield).forEach(
                control -> {
                    control.selectedProperty().addListener((x,old,nw) -> {
                        if (!lazySpecUpdate) {
                            updateSpecs();
                        }
                    });
                }
        );
    }

    private void setUpPerksValueListeners() {
        // quartermaster: enable/disable double-wielding buttons
        quartermaster.selectedProperty().addListener((x,old,nw) -> {
            if (old == nw) {return;}
            doWithDoubleWieldButtonsOnChange();
        });
        List.of(frontiersman,doctor).forEach(
                control -> {
                    control.selectedProperty().addListener((x,old,nw) -> {
                        updateSpecs();
                    });
                }
        );
    }

    private void setUpStatsValueListeners() {
        List.of(price,healingCapacity,cuttingMeleeDamage, ammoSupply).forEach(specBox -> {
                    specBox.textProperty().addListener(e -> {
                        Utils.playBackgroundTransition(specBox, Color.rgb(250,250,250,0.7), Color.rgb(80,80,80,0.0), 1000f);
                    });
                }
        );
        List.of(requiredRank,numberOfHeals,bluntMeleeDamage,headshotRange).forEach(specBox -> {
                    specBox.textProperty().addListener(e -> {
                        Utils.playBackgroundTransition(specBox, Color.rgb(250,250,250,0.7), Color.rgb(80,80,80,0.1), 1000f);
                    });
                }
        );
    }
}
