package mrriegel.storagenetwork.tile;

import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.util.GlobalBlockPos;
import mrriegel.storagenetwork.Network;
import mrriegel.storagenetwork.network.InventoryNetworkPart;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * @author canitzp
 */
public class TileEntityNetworkCore extends CommonTile{

	public Network network;

	public void initializeNetwork() {
		Network network = new Network();
		network.corePosition = new GlobalBlockPos(pos, worldObj.provider.getDimension());
        for(EnumFacing facing : EnumFacing.values()){
            BlockPos searchPos = this.getPos().offset(facing);
            if(!getWorld().isAirBlock(searchPos)){
                TileEntity tile = getWorld().getTileEntity(searchPos);
                if(tile != null){
					if(tile instanceof INetworkPart){
						network.addPart((INetworkPart) tile);
					} else if(tile instanceof IInventory){
						network.addPart(new InventoryNetworkPart(tile.getWorld(), searchPos, (IInventory) tile));
					}
                }
            }
        }
		this.network = network;
	}

}
