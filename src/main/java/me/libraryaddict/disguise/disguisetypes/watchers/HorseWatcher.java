package me.libraryaddict.disguise.disguisetypes.watchers;

import com.google.common.base.Optional;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Material;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.UUID;

public class HorseWatcher extends AgeableWatcher {

    public HorseWatcher(Disguise disguise) {
        super(disguise);
        setColor(Color.values()[new Random().nextInt(Color.values().length)]);
    }

    //TODO: Check and make sure Variants work
    public Variant getVariant() {
        return Variant.values()[(int) getValue(14, 0)];
    }

    public void setVariant(Variant variant) {
        setValue(14, variant.ordinal());
        sendData(14);
    }

    public Color getColor() {
        return Color.values()[((Integer) getValue(13, 0) & 0xFF)];
    }

    public ItemStack getHorseArmor() {
        int horseValue = getHorseArmorAsInt();
        switch (horseValue) {
            case 1:
                return new ItemStack(Material.getMaterial("IRON_BARDING"));
            case 2:
                return new ItemStack(Material.getMaterial("GOLD_BARDING"));
            case 3:
                return new ItemStack(Material.getMaterial("DIAMOND_BARDING"));
            default:
                break;
        }
        return null;
    }

    protected int getHorseArmorAsInt() {
        return (int) getValue(16, 0);
    }

    public Optional<UUID> getOwner() {
        return (Optional<UUID>) getValue(15, Optional.absent());
    }

    public Style getStyle() {
        return Style.values()[((int) getValue(13, 0) >>> 8)];
    }

    public boolean hasChest() {
        return isHorseFlag(8);
    }

    public boolean isBreedable() {
        return isHorseFlag(16);
    }

    public boolean isGrazing() {
        return isHorseFlag(32);
    }

    public boolean isMouthOpen() {
        return isHorseFlag(128);
    }

    public boolean isRearing() {
        return isHorseFlag(64);
    }

    public boolean isSaddled() {
        return isHorseFlag(4);
    }

    public boolean isTamed() {
        return isHorseFlag(2);
    }

    private boolean isHorseFlag(int i) {
        return ((Integer) getValue(12, (byte) 0) & i) != 0;
    }

    public void setCanBreed(boolean breed) {
        setHorseFlag(16, breed);
    }

    public void setCarryingChest(boolean chest) {
        setHorseFlag(8, chest);
    }

    public void setColor(Color color) {
        setValue(13, color.ordinal() & 0xFF | getStyle().ordinal() << 8);
        sendData(13);
    }

    private void setHorseFlag(int i, boolean flag) {
        int j = (int) getValue(12, 0);
        if (flag) {
            setValue(12, j | i);
        } else {
            setValue(12, j & ~i);
        }
        sendData(12);
    }

    public void setGrazing(boolean grazing) {
        setHorseFlag(32, grazing);
    }

    protected void setHorseArmor(int armor) {
        setValue(16, armor);
        sendData(16);
    }

    public void setHorseArmor(ItemStack item) {
        int value = 0;
        if (item != null) {
            Material mat = item.getType();
            if (mat == Material.IRON_BARDING) {
                value = 1;
            } else if (mat == Material.GOLD_BARDING) {
                value = 2;
            } else if (mat == Material.DIAMOND_BARDING) {
                value = 3;
            }
        }
        setHorseArmor(value);
    }

    public void setMouthOpen(boolean mouthOpen) {
        setHorseFlag(128, mouthOpen);
    }

    public void setOwner(Optional<UUID> uuid) {
        setValue(15, uuid);
        sendData(15);
    }

    public void setRearing(boolean rear) {
        setHorseFlag(64, rear);
    }

    public void setSaddled(boolean saddled) {
        setHorseFlag(4, saddled);
    }

    public void setStyle(Style style) {
        setValue(13, getColor().ordinal() & 0xFF | style.ordinal() << 8);
        sendData(13);
    }

    public void setTamed(boolean tamed) {
        setHorseFlag(2, tamed);
    }

}
