package hsgui;

import HuntShowdownLib.InGameTypes.Loadout.Loadout;
import HuntShowdownLib.Random.RandomLoadoutGenerator;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class RandomLoadoutGeneratorService extends Service<Loadout> {

    private RandomLoadoutGenerator randomGen;

    public RandomLoadoutGenerator getRandomGen() {
        return this.randomGen;
    }

    public void setRandomGen(RandomLoadoutGenerator randomGen) {
        this.randomGen = randomGen;
    }

    public RandomLoadoutGeneratorService() {
        this(null);
    }

    public RandomLoadoutGeneratorService(RandomLoadoutGenerator randomGen) {
        this.randomGen = randomGen;
    }

    @Override
    protected Task<Loadout> createTask() {
        return new Task<Loadout> () {

            private RandomLoadoutGenerator randomGen;

            public Task<Loadout> init(RandomLoadoutGenerator randomGen) {
                this.randomGen = randomGen;
                return this;
            }

            @Override
            protected Loadout call() throws Exception {
                return this.randomGen.next();
            }
        }.init(this.randomGen);
    }

}
