/* Atlas
 * Coded by Kevin Pietrow
 * Marist/IBM Joint Study
 * 
 * This module is designed to flood the flow table of a switch with flows, 
 * and see how the switch reacts.
 * 
 * Circa 2013, Marist College
 */







package net.floodlightcontroller.switchoverflow;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;


import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;
import net.floodlightcontroller.virtualnetwork.NoOp;

import org.jboss.netty.channel.Channel;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.util.HexString;
import org.openflow.util.U16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IFloodlightProviderService.Role;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.Controller;
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
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;
import net.floodlightcontroller.virtualnetwork.NoOp;




public class SwitchOverflowTest implements IFloodlightModule,
		IOFMessageListener, IOFSwitch {
	
	

	// Our dependencies
	protected static IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi;
	protected HashSet<IOFSwitch> connectedSwitches;
	

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
    public static void pushFLows() {
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
    	
    	/*
    	 *Working code to get a switche's dpid
    	 * a
    	 */
    	/*
    	Map<Long, IOFSwitch> activeswitches = floodlightProvider.getSwitches();
    	String currentswitch = (activeswitches.get(new Long(1)).toString());
    	int index = currentswitch.indexOf("DPID");
    	System.out.println(index);
    	String dpid = currentswitch.substring(index + 5, index + 28);
    	System.out.println(dpid);
    	*/
    	
    	
    	
    	
    	/*
    	Collection<IOFSwitch> hi = activeswitches.values();
    	Object yo = hi.toArray();
    	String therethere = Arrays.deepToString((Object[]) yo);
    	int index = therethere.indexOf("DPID");
    	System.out.println(index);
    	//37
    		// 42
    		// 65
    	//45 in b/t
    	
    	String dpid = therethere.substring(index + 5, index + 28);
    
    
    
    		for(int i = 0; i < 500; i++)
    		{
	
    			OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
    			OFMatch ofMatch = new OFMatch();
    			fm.setMatch(ofMatch);
	
    			String id = String.valueOf((int) i);
		
    			sfp.addFlow(id, fm, dpid);
    		}
    		
    		System.out.println(activeswitches);
    		System.out.println(hi);
    		System.out.println(yo);
    		System.out.println(therethere);
		*/
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




	@Override
	public void setFloodlightProvider(Controller controller) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void setThreadPoolService(IThreadPoolService threadPool) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void setChannel(Channel channel) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void write(OFMessage m, FloodlightContext bc) throws IOException {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void write(List<OFMessage> msglist, FloodlightContext bc)
			throws IOException {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void disconnectOutputStream() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public int getBuffers() {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int getActions() {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int getCapabilities() {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public byte getTables() {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public void setFeaturesReply(OFFeaturesReply featuresReply) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public Collection<OFPhysicalPort> getEnabledPorts() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Collection<Short> getEnabledPortNumbers() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public OFPhysicalPort getPort(short portNumber) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public OFPhysicalPort getPort(String portName) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void setPort(OFPhysicalPort port) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void deletePort(short portNumber) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void deletePort(String portName) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public Collection<OFPhysicalPort> getPorts() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public boolean portEnabled(short portName) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean portEnabled(String portName) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean portEnabled(OFPhysicalPort port) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public String getStringId() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public SocketAddress getInetAddress() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Map<Object, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Date getConnectedSince() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public int getNextTransactionId() {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public Future<List<OFStatistics>> getStatistics(OFStatisticsRequest request)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Future<OFFeaturesReply> querySwitchFeaturesReply()
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void deliverOFFeaturesReply(OFMessage reply) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void cancelFeaturesReply(int transactionId) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void setConnected(boolean connected) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public Role getHARole() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void setHARole(Role role, boolean haRoleReplyReceived) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void deliverStatisticsReply(OFMessage reply) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void cancelStatisticsReply(int transactionId) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void cancelAllStatisticsReplies() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean hasAttribute(String name) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public boolean attributeEquals(String name, Object other) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public Object removeAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void clearAllFlowMods() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean updateBroadcastCache(Long entry, Short port) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public Map<Short, Long> getPortBroadcastHits() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void sendStatsQuery(OFStatisticsRequest request, int xid,
			IOFMessageListener caller) throws IOException {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public Lock getListenerReadLock() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Lock getListenerWriteLock() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void setSwitchProperties(OFDescriptionStatistics description) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public OFPortType getPortType(short port_num) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public boolean isFastPort(short port_num) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public List<Short> getUplinkPorts() {
		// TODO Auto-generated method stub
		return null;
	}

}