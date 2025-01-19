package com.northwesterndevelopment.fastbenchbackport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;

@Mod(
    modid = FastWorkbench.MODID,
    version = Tags.VERSION,
    name = "FastWorkbenchBackport",
    acceptedMinecraftVersions = "[1.7.10]")
public class FastWorkbench {

    public static final String MODID = "fastbenchbackport";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(
        clientSide = "com.northwesterndevelopment.fastbenchbackport.ClientProxy",
        serverSide = "com.northwesterndevelopment.fastbenchbackport.CommonProxy")
    public static com.northwesterndevelopment.fastbenchbackport.CommonProxy proxy;
}
