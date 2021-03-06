package mrriegel.storagenetwork.tile;

import java.util.List;

import javax.vecmath.Vector2d;

import mrriegel.storagenetwork.GuiHandler.GuiID;
import mrriegel.storagenetwork.ModConfig;
import mrriegel.storagenetwork.StorageNetwork;
import mrriegel.storagenetwork.item.ItemItemFilter;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import com.google.common.collect.Lists;

public class TileItemAttractor extends TileNetworkPart implements ITickable {

	public ItemStack filter;

	@Override
	public void update() {
		if (ModConfig.STOPTICK)
			return;
		if (!worldObj.isBlockPowered(pos) && !worldObj.isRemote && getNetworkCore() != null && getNetworkCore().network != null) {
			int range = ModConfig.rangeItemAttractor;
			List<EntityItem> list = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB((getX() + .5) - range, (getY() + .5) - range, (getZ() + .5) - range, getX() + .5 + range, getY() + .5 + range, getZ() + .5 + range));
			for (EntityItem ei : list) {
				if (ei.isDead || ei.ticksExisted < 10 || !ItemItemFilter.canTransferItem(filter, ei.getEntityItem()) || !getNetworkCore().consumeRF(ei.getEntityItem().stackSize * 5, true))
					continue;
				if (ModConfig.teleportItems) {
					if (insert(ei))
						break;
				} else {
					Vec3d vec = new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).normalize().scale(0.12);
					if (Math.abs(ei.motionX) < 0.01 && Math.abs(ei.motionZ) < 0.01 && new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).lengthVector() > .9 && new Vector2d(ei.posX - (getX() + .5), ei.posZ - (getZ() + .5)).length() > 0.3)
						ei.motionY = 0.1;
					ei.motionX = vec.xCoord;
					ei.motionZ = vec.zCoord;
					if (worldObj.getTotalWorldTime() % 2 == 0/* && !(Math.abs(ei.motionX) < 0.01 && Math.abs(ei.motionZ) < 0.01)*/)
						for (EntityPlayerMP player : worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(ei.posX - 17, ei.posY - 17, ei.posZ - 17, ei.posX + 17, ei.posY + 17, ei.posZ + 17)))
							player.connection.sendPacket(new SPacketEntityVelocity(ei));
					if (new Vec3d(getX() + .5 - ei.posX, getY() + .5 - ei.posY, getZ() + .5 - ei.posZ).lengthVector() < .9)
						if (insert(ei))
							break;
				}
			}

		}
	}

	private boolean insert(EntityItem ei) {
		if (getNetworkCore().consumeRF(ei.getEntityItem().stackSize * 5, true)) {
			ItemStack stack = ei.getEntityItem().copy();
			ItemStack rest = getNetworkCore().network.insertItem(stack, null, false);
			getNetworkCore().consumeRF((rest == null ? stack.stackSize : stack.stackSize - rest.stackSize) * 5, false);
			if (rest == null)
				ei.setDead();
			else
				ei.setEntityItemStack(rest);
			return true;
		}
		return false;
	}

	@Override
	public List<ItemStack> getDroppingItems() {
		return Lists.newArrayList(filter);
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(StorageNetwork.instance, GuiID.ITEM_ATTRACTOR.ordinal(), worldObj, getX(), getY(), getZ());
		return true;
	}

}
