package eeu.other;

import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

public class Formula {
    @Nullable
    public Consume[] input;
    @Nullable
    public ItemStack[] outputItems;
    @Nullable
    public LiquidStack[] outputLiquids;
    public float craftTime = 60f;
    public int[] liquidOutputDirections = {-1};
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float warmupSpeed = 0.019f;

    public void setInput(Consume[] input) {
        this.input = input;
    }

    public Consume[] getInputs() {
        return input;
    }
    public void setOutput(ItemStack[] outputItems) {
        this.outputItems = outputItems;
    }
    public void setOutput(LiquidStack[] outputLiquids){
        this.outputLiquids = outputLiquids;
    }
    public ItemStack[] getOutputItems() {
        return outputItems;
    }
    public LiquidStack[] getOutputLiquids(){
        return outputLiquids;
    }
    public void set(Consume[] in, ItemStack[] outputItems, LiquidStack[] outputLiquids) {
        input = in;
        this.outputItems = outputItems;
        this.outputLiquids = outputLiquids;
    }
    public void apply(Block block){
        for(var c : input){
            c.apply(block);
        }
    }
    public void update(Building build){
        for(var c : input){
            c.update(build);
        }
    }
    public void trigger(Building build){
        for(var c : input){
            c.trigger(build);
        }
    }
    public void display(Stats stats, Block block){
        stats.timePeriod = craftTime;
        for (var c : input){
            c.display(stats);
        }
        if((block.hasItems && block.itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquids != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }
    }
    public void build(Building build, Table table){
        table.pane(t->{
            for (var c : input) {
                c.build(build, t);
            }
        });
    }
}
