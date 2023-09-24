package eeu.content;

import ExtraUtilities.content.EUItems;
import eeu.world.blocks.defense.CrispWall;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.Env;

public class EEUBlocks {
    public static Block crispWall, crispWallLarge;

    public static void load() {
        int wallHealthMultiplier = 4;
        /*crispWall = new CrispWall("crisp-steel-wall") {{
            requirements(Category.defense, ItemStack.with(EUItems.crispSteel, 6));
            health = 120 * wallHealthMultiplier;
            maxHandleDamage = health * 0.8f;
            restoreSpeed = maxHandleDamage * 0.1f / 60f;
            envDisabled |= Env.scorching;
        }};*/
        crispWallLarge = new CrispWall("crisp-steel-wall-large") {{
            requirements(Category.defense, ItemStack.with(EUItems.crispSteel, 24));
            size = 2;
            health = 120 * wallHealthMultiplier * 4;
            maxHandleDamage = health * 0.8f;
            restoreSpeed = maxHandleDamage * 0.1f / 60f;
            envDisabled |= Env.scorching;
        }};
        /*polarZone = new PowerTurret("polar-zone") {{

        }};*/
        /*clearer = new ExtraBreaker("clearer") {{
            requirements(Category.effect, with(EUItems.lightninAlloy, 20));
            size = 3;
            tileLength = 30;
            placeableLiquid = true;
            floating = true;
            alwaysUnlocked = true;
        }};*/
        /*test = new MultipleCrafter("test"){{
            requirements(Category.crafting, with(EUItems.lightninAlloy, 20));
            size = 3;
            itemCapacity = 30;
            liquidCapacity = 30;
            formulas.addFormula(new Formula(){{
                craftEffect = new WrapEffect(Fx.titanSmoke, Items.titanium.color);
                setInput(new Consume[]{
                        new ConsumeItems(ItemStack.with(Items.copper, 1)),
                        new ConsumeLiquid(Liquids.cryofluid, 0.1f),
                        new ConsumePower(0.1f, 0.0f, false)
                });
                setOutput(ItemStack.with(Items.titanium, 2));
                setOutput(LiquidStack.with(Liquids.water, 0.05f));
            }});
            formulas.addFormula(new Formula(){{
                craftEffect = new WrapEffect(Fx.titanSmoke, Items.coal.color);
                craftTime = 90;
                setInput(new Consume[]{
                        new ConsumeItems(ItemStack.with(Items.copper, 1)),
                        new ConsumeLiquid(Liquids.oil, 0.1f),
                });
                setOutput(ItemStack.with(Items.coal, 6));
            }});
            formulas.addFormula(new Formula(){{
                craftEffect = new WrapEffect(Fx.titanSmoke, Items.scrap.color);
                craftTime = 120;
                setInput(new Consume[]{
                        new ConsumeLiquid(Liquids.slag, 0.1f),
                });
                setOutput(ItemStack.with(Items.scrap, 6));
            }});
            formulas.addFormula(new Formula(){{
                craftEffect = Fx.ballfire;
                craftTime = 30;
                setInput(new Consume[]{
                        new ConsumeItemFlammable()
                });
                setOutput(ItemStack.with(Items.sand, 1));
            }});
            formulas.addFormula(new Formula(){{
                craftTime = 30;
                setInput(new Consume[]{
                        new ConsumeLiquid(Liquids.slag, 0.1f),
                });
                liquidOutputDirections = new int[]{
                  1,3
                };
                setOutput(LiquidStack.with(Liquids.arkycite, 0.05f, Liquids.cyanogen, 0.05f));
            }});
        }};*/
    }
}
