package eeu.parser;

import arc.func.Boolf;
import arc.func.Floatf;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.consumers.*;

public class ConsumesParser {
    public Seq<Consume> consumes = new Seq<>();

    public ConsumeLiquid consumeLiquid(Liquid liquid, float amount) {
        return consume(new ConsumeLiquid(liquid, amount));
    }

    public ConsumeLiquids consumeLiquids(LiquidStack... stacks) {
        return consume(new ConsumeLiquids(stacks));
    }

    /**
     * Creates a consumer which directly uses power without buffering it.
     *
     * @param powerPerTick The amount of power which is required each tick for 100% efficiency.
     * @return the created consumer object.
     */
    public ConsumePower consumePower(float powerPerTick) {
        return consume(new ConsumePower(powerPerTick, 0.0f, false));
    }

    /**
     * Creates a consumer which only consumes power when the condition is met.
     */
    public <T extends Building> ConsumePower consumePowerCond(float usage, Boolf<T> cons) {
        return consume(new ConsumePowerCondition(usage, (Boolf<Building>) cons));
    }

    /**
     * Creates a consumer that consumes a dynamic amount of power.
     */
    public <T extends Building> ConsumePower consumePowerDynamic(Floatf<T> usage) {
        return consume(new ConsumePowerDynamic((Floatf<Building>) usage));
    }

    /**
     * Creates a consumer which stores power.
     *
     * @param powerCapacity The maximum capacity in power units.
     */
    public ConsumePower consumePowerBuffered(float powerCapacity) {
        return consume(new ConsumePower(0f, powerCapacity, true));
    }

    public ConsumeItems consumeItem(Item item) {
        return consumeItem(item, 1);
    }

    public ConsumeItems consumeItem(Item item, int amount) {
        return consume(new ConsumeItems(new ItemStack[]{new ItemStack(item, amount)}));
    }

    public ConsumeItems consumeItems(ItemStack... items) {
        return consume(new ConsumeItems(items));
    }

    public ConsumeCoolant consumeCoolant(float amount) {
        return consume(new ConsumeCoolant(amount));
    }

    public <T extends Consume> T consume(T consume) {
        consumes.add(consume);
        return consume;
    }

    @Override
    public String toString() {
        return consumes.toString();
    }
}
