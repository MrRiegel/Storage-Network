package mrriegel.storagenetwork.block;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.storagenetwork.tile.TileEntityNetworkCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author canitzp
 */
public class BlockNetworkCore extends CommonBlockContainer<TileEntityNetworkCore> {

    public BlockNetworkCore() {
        super(Material.IRON, "block_network_core");
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(!worldIn.isRemote){
            TileEntityNetworkCore tile = (TileEntityNetworkCore) worldIn.getTileEntity(pos);
            tile.initializeNetwork();
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote){
            System.out.println(((TileEntityNetworkCore)worldIn.getTileEntity(pos)).network);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityNetworkCore();
    }

    @Override
    protected Class<? extends TileEntityNetworkCore> getTile() {
        return TileEntityNetworkCore.class;
    }

}
