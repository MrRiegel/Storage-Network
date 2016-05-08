package mrriegel.storagenetwork.tile;

import mrriegel.storagenetwork.config.ConfigHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TileItemBox extends AbstractFilterTile {

	private BlockPos master;
	private InventoryBasic inv = new InventoryBasic(null, false, ConfigHandler.itemBoxCapacity);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		master = new Gson().fromJson(compound.getString("master"), new TypeToken<BlockPos>() {
		}.getType());
		readInventory(compound);
	}

	public void readInventory(NBTTagCompound compound) {
		NBTTagList invList = compound.getTagList("box", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < invList.tagCount(); i++) {
			NBTTagCompound stackTag = invList.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getSizeInventory()) {
				inv.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("master", new Gson().toJson(master));
		writeInventory(compound);
	}

	public void writeInventory(NBTTagCompound compound) {
		NBTTagList invList = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(stackTag);
				invList.appendTag(stackTag);
			}
		}
		compound.setTag("box", invList);
	}

	@Override
	public BlockPos getMaster() {
		return master;
	}

	@Override
	public void setMaster(BlockPos master) {
		this.master = master;
	}

	public InventoryBasic getInv() {
		return inv;
	}

	public void setInv(InventoryBasic inv) {
		this.inv = inv;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void onChunkUnload() {
		if (master != null && worldObj.getChunkFromBlockCoords(master).isLoaded() && worldObj.getTileEntity(master) instanceof TileMaster)
			((TileMaster) worldObj.getTileEntity(master)).refreshNetwork();
	}

	@Override
	public IFluidHandler getFluidHandler() {
		return null;
	}

	@Override
	public IInventory getInventory() {
		return getInv();
	}

	@Override
	public BlockPos getSource() {
		return pos;
	}

	@Override
	public boolean isFluid() {
		return false;
	}

	@Override
	public EnumFacing getInventoryFace() {
		return EnumFacing.values()[0];
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		super.deserializeNBT(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		// TODO Auto-generated method stub
		return super.serializeNBT();
	}

}