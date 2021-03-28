package moe.kawaaii.DeathsWish.Items;

import moe.kawaaii.DeathsWish.DamageSources.SuicideDamage;
import moe.kawaaii.DeathsWish.MainClass;
import moe.kawaaii.DeathsWish.Interfaces.IPlayerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DemiseItem extends ToolItem {
    private String PATH;

    public DemiseItem(String path, int maxCount, ItemGroup group) {
        super(new ToolMaterial() {
            @Override
            public int getDurability() {
                return 33;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return 0;
            }

            @Override
            public float getAttackDamage() {
                return 0;
            }

            @Override
            public int getMiningLevel() {
                return 0;
            }

            @Override
            public int getEnchantability() {
                return 0;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return null;
            }
        }, new Item.Settings().maxCount(maxCount).group(group));

        this.PATH = path;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText(String.format("item.deaths_wish.%s.tooltip", this.PATH)));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.setDamage(0);
        ((IPlayerEntity) user).setKeepInventory(false);
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            ((ServerWorld) world).spawnParticles(ParticleTypes.EXPLOSION, user.getX(), user.getY(), user.getZ(), 25, 0, 0, 0, 0.1);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 1.0f, 1f);

            if (EnchantmentHelper.get(stack).get(MainClass.KEEP_INVENTORY) != null) {
                ((IPlayerEntity) user).setKeepInventory(true);
            }
        }

        if (!((PlayerEntity) user).isCreative()) {
            stack.decrement(1);
            // user.damage(new SuicideDamage(), Float.MAX_VALUE);
            user.damage(MainClass.DAMAGE_SOURCE, Float.MAX_VALUE);
        }
        return super.finishUsing(stack, world, user);
    }
}
