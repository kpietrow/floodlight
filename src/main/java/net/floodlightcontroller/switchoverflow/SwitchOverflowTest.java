package net.floodlightcontroller.switchoverflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;
import net.floodlightcontroller.virtualnetwork.NoOp;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;
import org.openflow.util.U16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.counter.ICounterStoreService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.loadbalancer.LBMember;
import net.floodlightcontroller.loadbalancer.LBPool;
import net.floodlightcontroller.loadbalancer.LBVip;
import net.floodlightcontroller.loadbalancer.LoadBalancer;
import net.floodlightcontroller.loadbalancer.LoadBalancerWebRoutable;
import net.floodlightcontroller.loadbalancer.MembersResource;
import net.floodlightcontroller.loadbalancer.MonitorsResource;
import net.floodlightcontroller.loadbalancer.PoolMemberResource;
import net.floodlightcontroller.loadbalancer.PoolsResource;
import net.floodlightcontroller.loadbalancer.VipsResource;
import net.floodlightcontroller.loadbalancer.LoadBalancer.IPClient;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;
import net.floodlightcontroller.virtualnetwork.NoOp;




public class SwitchOverflowTest implements IFloodlightModule,
		IOFMessageListener {
	
	protected static String dpid = "00:00:00:00:00:00:00:01";

	// Our dependencies
	protected static IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi;

	protected ICounterStoreService counterStore;
	protected OFMessageDamper messageDamper;
	protected IDeviceService deviceManager;
	protected IRoutingService routingEngine;
	protected ITopologyService topology;
	protected static IStaticFlowEntryPusherService sfp;
	
	
	
	 /**
     * used to push given route using static flow entry pusher
     * @param boolean inBound
     * @param Route route
     * @param IPClient client
     * @param LBMember member
     * @param long pinSwitch
     */
    public static void pushFLows(String dpid) {

 /*   	
    	List actionsTo = new ArrayList();
    	String id = String.valueOf((int) (Math.random()*10000));
    	
    	OFFlowMod fmTo = new OFFlowMod();
    	fmTo.setType(OFType.FLOW_MOD);
    	OFAction outputTo = new OFActionOutput((short) 2);
    	actionsTo.add(outputTo);
    	OFMatch mTo = new OFMatch();
    	mTo.setNetworkDestination(IPv4.toIPv4Address("10.0.0.2"));
    	mTo.setDataLayerType(Ethernet.TYPE_IPv4);
    	fmTo.setActions(actionsTo);
    	fmTo.setMatch(mTo);
    	
    	sfp.addFlow(id, fmTo, dpid);
    	*/
    	
    	for(int i = 0; i < 10000; i++)
    	{
    	
    		OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
    		OFMatch ofMatch = new OFMatch();
    		fm.setMatch(ofMatch);
    	
    		String id = String.valueOf((int) i);
    		
    		sfp.addFlow(id, fm, dpid);
    	
    		System.out.println("yo");
    	}

    }
    
    
    
    
	@Override
	public String getName() {
		return "SwitchOverflowTest";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = 
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IRestApiService.class);
        return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		sfp = context.getServiceImpl(IStaticFlowEntryPusherService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new WebResources());
	}

}